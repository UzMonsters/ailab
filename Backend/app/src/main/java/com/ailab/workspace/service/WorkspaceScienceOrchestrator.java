package com.ailab.workspace.service;

import com.ailab.workspace.domain.MeasurementEntity;
import com.ailab.workspace.dto.*;
import com.ailab.workspace.repository.MeasurementRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;

@Service
public class WorkspaceScienceOrchestrator {

    private final MeasurementRepository measurementRepository;
    private final EquipmentCatalogService equipmentCatalogService;

    public WorkspaceScienceOrchestrator(MeasurementRepository measurementRepository, EquipmentCatalogService equipmentCatalogService) {
        this.measurementRepository = measurementRepository;
        this.equipmentCatalogService = equipmentCatalogService;
    }

    public record ScientificExecutionOutcome(
            List<Map<String, Object>> mutatedItems,
            List<Map<String, Object>> mutatedConnections,
            List<MeasurementPointDto> measurements,
            List<String> safetyWarnings,
            List<CheckpointFactDto> checkpointFacts
    ) {}

    public ScientificExecutionOutcome processOperation(
            String workspaceId,
            String sessionId,
            String eventType,
            Map<String, Object> payload,
            List<Map<String, Object>> currentItems,
            List<Map<String, Object>> currentConnections
    ) {
        List<Map<String, Object>> items = deepCopyList(currentItems);
        List<Map<String, Object>> connections = deepCopyList(currentConnections);
        List<MeasurementPointDto> measurements = new ArrayList<>();
        List<String> safetyWarnings = new ArrayList<>();
        List<CheckpointFactDto> checkpointFacts = new ArrayList<>();

        switch (eventType) {
            case "MATERIAL_ADDED" -> handleMaterialAdded(payload, items, checkpointFacts, safetyWarnings);
            case "POUR", "TRANSFER" -> handlePour(payload, items, checkpointFacts, safetyWarnings);
            case "MIX", "STIR" -> handleMix(payload, items, checkpointFacts, safetyWarnings);
            case "HEAT_START", "HEAT" -> handleHeat(workspaceId, sessionId, payload, items, connections, measurements, checkpointFacts, safetyWarnings);
            case "COOL" -> handleCool(payload, items, checkpointFacts);
            case "MEASURE" -> handleMeasure(workspaceId, sessionId, payload, items, connections, measurements, checkpointFacts);
            case "CONNECT" -> handleConnect(payload, items, connections, checkpointFacts);
            case "DISCONNECT" -> handleDisconnect(payload, connections);
            default -> {
                // Scene only or passthrough
            }
        }

        return new ScientificExecutionOutcome(items, connections, measurements, safetyWarnings, checkpointFacts);
    }

    private void handleMaterialAdded(Map<String, Object> payload, List<Map<String, Object>> items, List<CheckpointFactDto> facts, List<String> warnings) {
        String itemId = string(payload.get("itemId"));
        String materialId = string(payload.get("materialId"));
        double amountMl = doubleValue(payload.getOrDefault("amountMl", payload.get("amount")));
        String phase = string(payload.getOrDefault("phase", "liquid"));

        Map<String, Object> item = findItem(items, itemId);
        if (item == null) return;

        double cap = doubleValue(item.getOrDefault("capacityMl", 250.0));
        double currentVol = doubleValue(item.getOrDefault("volumeMl", 0.0));
        double nextVol = currentVol + amountMl;
        if (cap > 0 && nextVol > cap) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY, "Material addition exceeds vessel capacity");
        }

        item.put("volumeMl", nextVol);
        item.put("liquidLevel", cap > 0 ? (nextVol / cap) : 0.0);
        item.put("materialId", materialId);
        item.put("phase", phase.toLowerCase(Locale.ROOT));

        List<Map<String, Object>> contents = getOrCreateContents(item);
        Double customConc = payload.containsKey("concentrationMolar") ? doubleValue(payload.get("concentrationMolar")) : null;
        Map<String, Object> newPortion = createPortion(materialId, amountMl, phase, customConc);
        contents.add(newPortion);

        recomputeMixtureAndAppearance(item, facts, warnings);
        facts.add(CheckpointFactDto.of("MATERIAL_ADDED", itemId, "Added " + amountMl + " mL of " + materialId + " to " + itemId));
    }

    private void handlePour(Map<String, Object> payload, List<Map<String, Object>> items, List<CheckpointFactDto> facts, List<String> warnings) {
        String sourceId = string(payload.getOrDefault("sourceItemId", payload.get("sourceId")));
        String targetId = string(payload.getOrDefault("targetItemId", payload.get("targetId")));
        double amount = doubleValue(payload.getOrDefault("amountMl", payload.getOrDefault("amount", 20.0)));

        Map<String, Object> source = findItem(items, sourceId);
        Map<String, Object> target = findItem(items, targetId);
        if (source == null || target == null) return;

        double sourceVol = doubleValue(source.get("volumeMl"));
        if (amount > sourceVol) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY, "Transfer amount exceeds source vessel contents");
        }

        double targetVol = doubleValue(target.get("volumeMl"));
        double targetCap = doubleValue(target.getOrDefault("capacityMl", 250.0));
        if (targetCap > 0 && (targetVol + amount) > targetCap) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY, "Transfer exceeds target vessel capacity");
        }

        double actualPour = amount;
        if (actualPour <= 0.0) return;

        // Subtract from source
        double newSourceVol = Math.max(0.0, sourceVol - actualPour);
        source.put("volumeMl", newSourceVol);
        double sourceCap = doubleValue(source.getOrDefault("capacityMl", 250.0));
        source.put("liquidLevel", sourceCap > 0 ? (newSourceVol / sourceCap) : 0.0);
        List<Map<String, Object>> sourceContents = getOrCreateContents(source);
        List<Map<String, Object>> transferredContents = extractProportionalContents(sourceContents, actualPour, sourceVol);
        if (newSourceVol == 0) {
            source.put("materialId", null);
            source.put("appearance", Map.of("color", "transparent", "opacity", 0.0, "bubbles", false));
        } else {
            recomputeMixtureAndAppearance(source, facts, warnings);
        }

        // Add to target
        double newTargetVol = targetVol + actualPour;
        target.put("volumeMl", newTargetVol);
        target.put("liquidLevel", targetCap > 0 ? (newTargetVol / targetCap) : 0.0);
        List<Map<String, Object>> targetContents = getOrCreateContents(target);
        targetContents.addAll(transferredContents);

        recomputeMixtureAndAppearance(target, facts, warnings);
        facts.add(CheckpointFactDto.of("POUR_COMPLETED", targetId, "Poured " + actualPour + " mL from " + sourceId + " to " + targetId));
    }

    private void handleMix(Map<String, Object> payload, List<Map<String, Object>> items, List<CheckpointFactDto> facts, List<String> warnings) {
        String itemId = string(payload.get("itemId"));
        Map<String, Object> item = findItem(items, itemId);
        if (item == null) return;

        item.put("operation", "MIX");
        recomputeMixtureAndAppearance(item, facts, warnings);
        facts.add(CheckpointFactDto.of("MIX_COMPLETED", itemId, "Mixture thoroughly stirred in " + itemId));
    }

    private void handleHeat(
            String workspaceId,
            String sessionId,
            Map<String, Object> payload,
            List<Map<String, Object>> items,
            List<Map<String, Object>> connections,
            List<MeasurementPointDto> measurements,
            List<CheckpointFactDto> facts,
            List<String> warnings
    ) {
        String itemId = string(payload.get("itemId"));
        double targetTemp = doubleValue(payload.getOrDefault("targetTemperatureC", payload.getOrDefault("temperatureC", 60.0)));
        Map<String, Object> item = findItem(items, itemId);
        if (item == null) return;

        // Check thermal connection or heating source
        boolean hasThermalAttachment = hasThermalConnection(itemId, connections) || isHeater(item);
        if (!hasThermalAttachment) {
            warnings.add("Warning: Vessel " + itemId + " is heated without a valid thermal connection to a burner or hotplate.");
        }

        double currentTemp = doubleValue(item.getOrDefault("temperatureC", 20.0));
        double newTemp = Math.min(100.0, Math.max(currentTemp, targetTemp)); // Boiling limit for aqueous
        item.put("temperatureC", newTemp);
        item.put("operation", "HEAT");

        if (newTemp >= 100.0) {
            warnings.add("Boiling point reached in " + itemId + ". Vaporization active.");
        }

        // Record temperature measurement if thermometer is connected
        String thermometerId = findConnectedSensor(itemId, "temperature-probe", connections);
        if (thermometerId != null) {
            MeasurementEntity m = new MeasurementEntity(
                    UUID.randomUUID().toString(), sessionId, workspaceId, thermometerId, itemId,
                    "TEMPERATURE", BigDecimal.valueOf(newTemp).setScale(2, RoundingMode.HALF_UP), "°C", Instant.now()
            );
            measurementRepository.save(m);
            measurements.add(new MeasurementPointDto(m.getId(), "TEMPERATURE", m.getValue(), "°C", thermometerId, itemId, m.getRecordedAt()));
            facts.add(CheckpointFactDto.of("MEASUREMENT_RECORDED", itemId, "Recorded temperature: " + newTemp + " °C"));
        }

        facts.add(CheckpointFactDto.of("TARGET_TEMPERATURE_REACHED", itemId, "Vessel heated to " + newTemp + " °C"));
    }

    private void handleCool(Map<String, Object> payload, List<Map<String, Object>> items, List<CheckpointFactDto> facts) {
        String itemId = string(payload.get("itemId"));
        Map<String, Object> item = findItem(items, itemId);
        if (item == null) return;

        double currentTemp = doubleValue(item.getOrDefault("temperatureC", 20.0));
        double newTemp = Math.max(20.0, currentTemp - 20.0);
        item.put("temperatureC", newTemp);
        item.put("operation", "COOL");
        facts.add(CheckpointFactDto.of("COOL_COMPLETED", itemId, "Vessel cooled to " + newTemp + " °C"));
    }

    private void handleMeasure(
            String workspaceId,
            String sessionId,
            Map<String, Object> payload,
            List<Map<String, Object>> items,
            List<Map<String, Object>> connections,
            List<MeasurementPointDto> measurements,
            List<CheckpointFactDto> facts
    ) {
        String sensorId = string(payload.getOrDefault("sensorItemId", payload.get("sensorId")));
        String targetId = string(payload.getOrDefault("targetItemId", payload.get("targetId")));
        String kind = string(payload.getOrDefault("kind", "TEMPERATURE")).toUpperCase();

        Map<String, Object> target = findItem(items, targetId);
        if (target == null) return;

        BigDecimal val;
        String unit;

        if ("PH".equals(kind)) {
            double ph = computeSolutionPh(target);
            val = BigDecimal.valueOf(ph).setScale(2, RoundingMode.HALF_UP);
            unit = "pH";
        } else if ("MASS".equals(kind)) {
            double vol = doubleValue(target.get("volumeMl"));
            val = BigDecimal.valueOf(vol * 1.05).setScale(2, RoundingMode.HALF_UP); // approx density
            unit = "g";
        } else {
            double temp = doubleValue(target.getOrDefault("temperatureC", 20.0));
            val = BigDecimal.valueOf(temp).setScale(2, RoundingMode.HALF_UP);
            unit = "°C";
        }

        MeasurementEntity m = new MeasurementEntity(
                UUID.randomUUID().toString(), sessionId, workspaceId, sensorId, targetId, kind, val, unit, Instant.now()
        );
        measurementRepository.save(m);
        measurements.add(new MeasurementPointDto(m.getId(), kind, val, unit, sensorId, targetId, m.getRecordedAt()));
        facts.add(CheckpointFactDto.of("MEASUREMENT_RECORDED", targetId, "Measured " + kind + " = " + val + " " + unit));
    }

    private void handleConnect(Map<String, Object> payload, List<Map<String, Object>> items, List<Map<String, Object>> connections, List<CheckpointFactDto> facts) {
        String fromId = string(payload.getOrDefault("sourceItemId", payload.get("fromItemId")));
        String fromPort = string(payload.getOrDefault("sourcePort", payload.get("fromPortId")));
        String toId = string(payload.getOrDefault("targetItemId", payload.get("toItemId")));
        String toPort = string(payload.getOrDefault("targetPort", payload.get("toPortId")));
        String connId = string(payload.getOrDefault("id", "conn-" + UUID.randomUUID().toString().substring(0, 8)));

        Map<String, Object> conn = new LinkedHashMap<>();
        conn.put("id", connId);
        conn.put("fromItemId", fromId);
        conn.put("fromPortId", fromPort);
        conn.put("toItemId", toId);
        conn.put("toPortId", toPort);
        conn.put("type", inferConnectionType(fromPort, toPort));
        conn.put("status", "CONNECTED");
        connections.add(conn);

        facts.add(CheckpointFactDto.of("CONNECTION_COMPLETED", toId, "Connected " + fromId + ":" + fromPort + " -> " + toId + ":" + toPort));
    }

    private void handleDisconnect(Map<String, Object> payload, List<Map<String, Object>> connections) {
        String connId = string(payload.get("id"));
        if (connId != null) {
            connections.removeIf(c -> connId.equals(c.get("id")));
        } else {
            String fromId = string(payload.get("fromItemId"));
            String toId = string(payload.get("toItemId"));
            connections.removeIf(c -> Objects.equals(fromId, c.get("fromItemId")) && Objects.equals(toId, c.get("toItemId")));
        }
    }

    // ==========================================
    // 5 BASELINE CHEMISTRY SCENARIOS ENGINE
    // ==========================================
    public void recomputeMixtureAndAppearance(Map<String, Object> item, List<CheckpointFactDto> facts, List<String> warnings) {
        List<Map<String, Object>> contents = getOrCreateContents(item);
        if (contents.isEmpty()) {
            item.put("appearance", Map.of("color", "transparent", "opacity", 0.0, "bubbles", false));
            item.put("phase", "LIQUID");
            return;
        }

        // Check if solutes can react or dissolve
        boolean hasCuSO4Aqueous = hasCode(contents, "CuSO4(aq)", "MAT-CUSO4-AQ", "CUSO4_AQ");
        boolean hasCuSO4Solid = hasCode(contents, "CuSO4(s)", "MAT-CUSO4-S", "CUSO4_S", "CuSO4");
        boolean hasWater = hasCode(contents, "H2O", "COMP-H2O", "WATER", "H2O(l)");
        boolean hasKMnO4 = hasCode(contents, "KMnO4", "KMnO4(s)", "MAT-KMNO4", "KMNO4");
        boolean hasHCl = hasCode(contents, "HCl", "HCl(aq)", "MAT-HCL", "COMP-HCL");
        boolean hasNaOH = hasCode(contents, "NaOH", "NaOH(aq)", "MAT-NAOH", "COMP-NAOH");
        boolean hasZn = hasCode(contents, "Zn", "Zn(s)", "MAT-ZN", "ZINC");

        // Scenario 1: CuSO4(aq) + H2O (Dilution)
        if (hasCuSO4Aqueous && hasWater) {
            double totalVol = doubleValue(item.get("volumeMl"));
            double soluteMols = calculateTotalMoles(contents, "CuSO4");
            if (soluteMols <= 0.0) soluteMols = 0.05; // 0.05 mol baseline
            double molarity = totalVol > 0 ? (soluteMols / (totalVol / 1000.0)) : 0.1;

            // Merge into single homogeneous solution
            contents.clear();
            contents.add(Map.of(
                    "materialCode", "CuSO4(aq)",
                    "name", "Copper(II) Sulfate Solution",
                    "amountMl", totalVol,
                    "concentrationMolar", molarity,
                    "phase", "LIQUID",
                    "mixtureState", "HOMOGENEOUS"
            ));

            // Compute dynamic diluted blue color based on concentration
            String blueHex = interpolateBlueColor(molarity);
            item.put("appearance", Map.of("color", blueHex, "opacity", Math.min(0.9, 0.4 + molarity * 0.4), "bubbles", false));
            item.put("phase", "LIQUID");
            item.put("materialId", "CuSO4(aq)");
            facts.add(CheckpointFactDto.of("DILUTION_COMPLETED", string(item.get("id")), "Diluted CuSO4(aq) to " + String.format(Locale.ROOT, "%.3f", molarity) + " M"));
            return;
        }

        // Scenario 2: CuSO4(s) + H2O (Dissolution)
        if (hasCuSO4Solid && hasWater) {
            double totalVol = doubleValue(item.get("volumeMl"));
            double solidMoles = calculateSolidMoles(contents, "CuSO4", 159.609);
            if (solidMoles <= 0.0) solidMoles = 0.05;
            double molarity = totalVol > 0 ? (solidMoles / (totalVol / 1000.0)) : 0.5;

            contents.clear();
            contents.add(Map.of(
                    "materialCode", "CuSO4(aq)",
                    "name", "Copper(II) Sulfate Solution",
                    "amountMl", totalVol,
                    "concentrationMolar", molarity,
                    "phase", "LIQUID",
                    "mixtureState", "HOMOGENEOUS"
            ));
            String blueHex = interpolateBlueColor(molarity);
            item.put("appearance", Map.of("color", blueHex, "opacity", 0.85, "bubbles", false));
            item.put("phase", "LIQUID");
            item.put("materialId", "CuSO4(aq)");
            facts.add(CheckpointFactDto.of("DISSOLUTION_COMPLETED", string(item.get("id")), "CuSO4(s) completely dissolved into " + String.format(Locale.ROOT, "%.3f", molarity) + " M solution"));
            return;
        }

        // Scenario 3: KMnO4(s) + H2O (Dissolution into purple solution)
        if (hasKMnO4 && hasWater) {
            double totalVol = doubleValue(item.get("volumeMl"));
            double solidMoles = calculateSolidMoles(contents, "KMnO4", 158.034);
            if (solidMoles <= 0.0) solidMoles = 0.005;
            double molarity = totalVol > 0 ? (solidMoles / (totalVol / 1000.0)) : 0.1;

            contents.clear();
            contents.add(Map.of(
                    "materialCode", "KMnO4(aq)",
                    "name", "Potassium Permanganate Solution",
                    "amountMl", totalVol,
                    "concentrationMolar", molarity,
                    "phase", "LIQUID",
                    "mixtureState", "HOMOGENEOUS"
            ));
            item.put("appearance", Map.of("color", "#7E22CE", "opacity", Math.min(1.0, 0.6 + molarity * 3.0), "bubbles", false));
            item.put("phase", "LIQUID");
            item.put("materialId", "KMnO4(aq)");
            facts.add(CheckpointFactDto.of("DISSOLUTION_COMPLETED", string(item.get("id")), "KMnO4 dissolved into " + String.format(Locale.ROOT, "%.3f", molarity) + " M homogeneous purple solution"));
            return;
        }

        // Scenario 4: HCl + NaOH (Neutralization & Exothermic Heat)
        if (hasHCl && hasNaOH) {
            double totalVol = Math.max(1.0, doubleValue(item.get("volumeMl")));
            double molHcl = calculateTotalMoles(contents, "HCl");
            double molNaoh = calculateTotalMoles(contents, "NaOH");
            if (molHcl <= 0.0) molHcl = 0.025;
            if (molNaoh <= 0.0) molNaoh = 0.025;

            double rxnMoles = Math.min(molHcl, molNaoh);
            // Delta H = -57.3 kJ/mol
            double deltaHJoules = rxnMoles * 57300.0;
            double massGrams = totalVol * 1.0;
            double deltaT = (deltaHJoules) / (massGrams * 4.184);

            double curTemp = doubleValue(item.getOrDefault("temperatureC", 25.0));
            double nextTemp = Math.round((curTemp + deltaT) * 100.0) / 100.0;
            item.put("temperatureC", nextTemp);

            double saltMolarity = rxnMoles / (totalVol / 1000.0);
            contents.clear();
            contents.add(new LinkedHashMap<>(Map.of(
                    "materialCode", "NaCl(aq)",
                    "name", "Sodium Chloride Solution",
                    "amountMl", totalVol,
                    "concentrationMolar", saltMolarity,
                    "phase", "LIQUID",
                    "mixtureState", "HOMOGENEOUS"
            )));

            double resultingPh = 7.00;
            if (molHcl > molNaoh) {
                double excessAcid = (molHcl - molNaoh) / (totalVol / 1000.0);
                resultingPh = Math.max(0.0, -Math.log10(excessAcid));
                contents.add(new LinkedHashMap<>(Map.of(
                        "materialCode", "HCl(aq)",
                        "name", "Excess Hydrochloric Acid",
                        "amountMl", totalVol,
                        "concentrationMolar", excessAcid,
                        "phase", "LIQUID",
                        "mixtureState", "HOMOGENEOUS"
                )));
            } else if (molNaoh > molHcl) {
                double excessBase = (molNaoh - molHcl) / (totalVol / 1000.0);
                resultingPh = Math.min(14.0, 14.0 + Math.log10(excessBase));
                contents.add(new LinkedHashMap<>(Map.of(
                        "materialCode", "NaOH(aq)",
                        "name", "Excess Sodium Hydroxide",
                        "amountMl", totalVol,
                        "concentrationMolar", excessBase,
                        "phase", "LIQUID",
                        "mixtureState", "HOMOGENEOUS"
                )));
            }

            item.put("appearance", Map.of("color", "#E0F2FE", "opacity", 0.4, "bubbles", false));
            item.put("phase", "LIQUID");
            item.put("materialId", "NaCl(aq)");
            facts.add(CheckpointFactDto.of("NEUTRALIZATION_COMPLETED", string(item.get("id")),
                    String.format(Locale.ROOT, "HCl + NaOH neutralized -> NaCl(aq) + H2O (Temp: %.1f °C, pH: %.2f)", nextTemp, resultingPh)));
            return;
        }

        // Scenario 5: Zn + HCl (Single Displacement, Gas Bubbles & Safety Alert)
        if (hasZn && hasHCl) {
            double totalVol = Math.max(1.0, doubleValue(item.get("volumeMl")));
            double molZn = calculateSolidMoles(contents, "Zn", 65.38);
            double molHcl = calculateTotalMoles(contents, "HCl");
            if (molZn <= 0.0) molZn = 0.015;
            if (molHcl <= 0.0) molHcl = 0.030;

            // Zn + 2HCl -> ZnCl2 + H2
            double rxnMoles = Math.min(molZn, molHcl / 2.0);
            if (rxnMoles > 0) {
                double deltaHJoules = rxnMoles * 153900.0;
                double massGrams = totalVol * 1.0;
                double deltaT = (deltaHJoules) / (massGrams * 4.184);
                double curTemp = doubleValue(item.getOrDefault("temperatureC", 20.0));
                double nextTemp = Math.round((curTemp + deltaT) * 100.0) / 100.0;
                item.put("temperatureC", nextTemp);

                double saltMolarity = rxnMoles / (totalVol / 1000.0);
                double h2VolumeLiters = rxnMoles * 24.45;

                contents.clear();
                contents.add(new LinkedHashMap<>(Map.of(
                        "materialCode", "ZnCl2(aq)",
                        "name", "Zinc Chloride Solution",
                        "amountMl", totalVol,
                        "concentrationMolar", saltMolarity,
                        "phase", "LIQUID",
                        "mixtureState", "HOMOGENEOUS"
                )));

                item.put("appearance", Map.of("color", "#CBD5E1", "opacity", 0.5, "bubbles", true, "gas", "H2"));
                item.put("phase", "LIQUID");
                item.put("materialId", "ZnCl2(aq)");
                warnings.add(String.format(Locale.ROOT, "SAFETY WARNING: Hydrogen gas (H2) evolved (%.2f L). Flammable gas risk in %s", h2VolumeLiters, item.get("id")));
                facts.add(CheckpointFactDto.of("GAS_EVOLVED", string(item.get("id")),
                        String.format(Locale.ROOT, "Zn + 2HCl -> ZnCl2 + H2(g) (Evolved %.2f L H2, Temp: %.1f °C)", h2VolumeLiters, nextTemp)));
                return;
            }
        }

        // Generic single solute / liquid appearance
        String firstMat = string(contents.get(0).get("materialCode"));
        String color = inferDefaultColor(firstMat);
        item.put("appearance", Map.of("color", color, "opacity", 0.7, "bubbles", false));
        item.put("materialId", firstMat);
    }

    private double calculateSolidMoles(List<Map<String, Object>> contents, String prefix, double molarMass) {
        for (Map<String, Object> c : contents) {
            String mat = string(c.get("materialCode"));
            if (mat != null && mat.toUpperCase().contains(prefix.toUpperCase())) {
                double amount = doubleValue(c.getOrDefault("amountMl", c.get("amount")));
                if (amount > 0) {
                    return amount / molarMass;
                }
            }
        }
        return 0.0;
    }

    private String interpolateBlueColor(double molarity) {
        if (molarity >= 1.0) return "#1D4ED8"; // Deep Royal Blue
        if (molarity >= 0.5) return "#2563EB"; // Blue
        if (molarity >= 0.2) return "#3B82F6"; // Medium Blue
        if (molarity >= 0.05) return "#60A5FA"; // Light Blue
        return "#93C5FD"; // Very Pale Sky Blue
    }

    private String inferDefaultColor(String mat) {
        if (mat == null) return "transparent";
        String lower = mat.toLowerCase();
        if (lower.contains("water") || lower.contains("h2o") || lower.contains("nacl")) return "#E0F2FE";
        if (lower.contains("cuso4")) return "#3B82F6";
        if (lower.contains("kmno4")) return "#7E22CE";
        if (lower.contains("fecl3")) return "#D97706";
        if (lower.contains("hcl") || lower.contains("naoh")) return "#F8FAFC";
        return "#60A5FA";
    }

    private double computeSolutionPh(Map<String, Object> item) {
        List<Map<String, Object>> contents = getOrCreateContents(item);
        if (contents.isEmpty()) return 7.0;
        for (Map<String, Object> c : contents) {
            String code = string(c.get("materialCode")).toLowerCase();
            if (code.contains("hcl")) {
                if (c.containsKey("concentrationMolar")) {
                    double conc = doubleValue(c.get("concentrationMolar"));
                    return Math.max(0.0, Math.round(-Math.log10(Math.max(1e-7, conc)) * 100.0) / 100.0);
                }
                return 1.2;
            }
            if (code.contains("naoh")) {
                if (c.containsKey("concentrationMolar")) {
                    double conc = doubleValue(c.get("concentrationMolar"));
                    return Math.min(14.0, Math.round((14.0 + Math.log10(Math.max(1e-7, conc))) * 100.0) / 100.0);
                }
                return 13.0;
            }
            if (code.contains("cuso4")) return 4.5;
            if (code.contains("nacl")) return 7.0;
        }
        return 7.0;
    }

    private boolean hasThermalConnection(String itemId, List<Map<String, Object>> connections) {
        for (Map<String, Object> c : connections) {
            String from = string(c.get("fromItemId"));
            String to = string(c.get("toItemId"));
            String type = string(c.get("type"));
            if ((itemId.equals(from) || itemId.equals(to)) && "THERMAL".equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;
    }

    private String findConnectedSensor(String targetId, String connector, List<Map<String, Object>> connections) {
        for (Map<String, Object> c : connections) {
            String from = string(c.get("fromItemId"));
            String to = string(c.get("toItemId"));
            String type = string(c.get("type"));
            if (targetId.equals(to) && "SENSOR".equalsIgnoreCase(type)) {
                return from;
            }
            if (targetId.equals(from) && "SENSOR".equalsIgnoreCase(type)) {
                return to;
            }
        }
        return null;
    }

    private boolean isHeater(Map<String, Object> item) {
        String type = string(item.get("type"));
        String profileId = string(item.get("profileId"));
        return "HEATER".equalsIgnoreCase(type) || (profileId != null && (profileId.contains("burner") || profileId.contains("hotplate")));
    }

    private String inferConnectionType(String fromPort, String toPort) {
        if ("THERMAL".equalsIgnoreCase(fromPort) || "THERMAL".equalsIgnoreCase(toPort)) return "THERMAL";
        if ("SENSOR".equalsIgnoreCase(fromPort) || "SENSOR".equalsIgnoreCase(toPort)) return "SENSOR";
        if ("GAS_INLET".equalsIgnoreCase(fromPort) || "GAS_INLET".equalsIgnoreCase(toPort)) return "GAS";
        return "FLUID";
    }

    private boolean hasCode(List<Map<String, Object>> contents, String... codes) {
        for (Map<String, Object> c : contents) {
            String mat = string(c.get("materialCode"));
            for (String code : codes) {
                if (mat != null && mat.equalsIgnoreCase(code)) return true;
            }
        }
        return false;
    }

    private double calculateTotalMoles(List<Map<String, Object>> contents, String prefix) {
        for (Map<String, Object> c : contents) {
            String mat = string(c.get("materialCode"));
            if (mat != null && mat.toUpperCase().contains(prefix.toUpperCase())) {
                double conc = doubleValue(c.getOrDefault("concentrationMolar", 1.0));
                double volMl = doubleValue(c.getOrDefault("amountMl", 50.0));
                return Math.max(conc, 1.0) * (volMl / 1000.0);
            }
        }
        return 0.05;
    }

    private List<Map<String, Object>> extractProportionalContents(List<Map<String, Object>> sourceContents, double extractAmount, double totalSourceVol) {
        List<Map<String, Object>> extracted = new ArrayList<>();
        if (totalSourceVol <= 0) return extracted;
        double ratio = extractAmount / totalSourceVol;

        for (Map<String, Object> portion : sourceContents) {
            double portionAmount = doubleValue(portion.get("amountMl"));
            double taken = portionAmount * ratio;
            portion.put("amountMl", Math.max(0.0, portionAmount - taken));

            Map<String, Object> clone = new LinkedHashMap<>(portion);
            clone.put("amountMl", taken);
            extracted.add(clone);
        }
        return extracted;
    }

    private Map<String, Object> createPortion(String materialId, double amountMl, String phase, Double concentrationMolar) {
        Map<String, Object> portion = new LinkedHashMap<>();
        portion.put("materialCode", materialId);
        portion.put("name", materialId);
        portion.put("amountMl", amountMl);
        if (concentrationMolar != null) {
            portion.put("concentrationMolar", concentrationMolar);
        } else if (materialId != null && materialId.equalsIgnoreCase("HCl")) {
            portion.put("concentrationMolar", 0.063); // Standard bench 0.063 M HCl -> pH 1.20
        } else if (materialId != null && materialId.equalsIgnoreCase("NaOH")) {
            portion.put("concentrationMolar", 0.1); // Standard bench 0.1 M NaOH -> pH 13.00
        } else {
            portion.put("concentrationMolar", 1.0);
        }
        portion.put("phase", phase);
        portion.put("mixtureState", "HOMOGENEOUS");
        return portion;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getOrCreateContents(Map<String, Object> item) {
        Object existing = item.get("contents");
        if (existing instanceof List<?> list) {
            return (List<Map<String, Object>>) list;
        }
        List<Map<String, Object>> newList = new ArrayList<>();
        item.put("contents", newList);
        return newList;
    }

    private Map<String, Object> findItem(List<Map<String, Object>> items, String id) {
        if (id == null) return null;
        for (Map<String, Object> item : items) {
            if (id.equals(item.get("id"))) return item;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> deepCopyList(List<Map<String, Object>> original) {
        if (original == null) return new ArrayList<>();
        List<Map<String, Object>> copy = new ArrayList<>();
        for (Map<String, Object> item : original) {
            copy.add(new LinkedHashMap<>(item));
        }
        return copy;
    }

    private String string(Object obj) {
        return obj != null ? obj.toString() : null;
    }

    private double doubleValue(Object obj) {
        if (obj instanceof Number n) return n.doubleValue();
        if (obj instanceof String s) {
            try { return Double.parseDouble(s); } catch (Exception ignored) {}
        }
        return 0.0;
    }
}
