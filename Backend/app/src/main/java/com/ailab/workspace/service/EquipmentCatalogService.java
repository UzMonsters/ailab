package com.ailab.workspace.service;

import com.ailab.workspace.dto.WorkspacePortDto;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EquipmentCatalogService {

    public record EquipmentSpecification(
            String id,
            String name,
            String category,
            Double capacityMl,
            String rendererKey,
            List<String> capabilities,
            List<WorkspacePortDto> ports,
            Map<String, Object> assets2d,
            Map<String, Object> limits
    ) {}

    private final Map<String, EquipmentSpecification> catalog = new LinkedHashMap<>();

    public EquipmentCatalogService() {
        registerDefaults();
    }

    private void registerDefaults() {
        // 1. Beaker 250mL
        catalog.put("beaker-250ml", new EquipmentSpecification(
                "beaker-250ml", "Beaker 250 mL", "CONTAINER", 250.0, "beaker_standard",
                List.of("CONTAINER", "LIQUID_HOLDING", "HEATING_RECEPTIVE"),
                List.of(
                        new WorkspacePortDto("INLET", "Top Rim", "FLUID", "INPUT", "standard-open-mouth", Map.of("x", 0.5, "y", 0.05), "anchor_rim", 250.0, true, false),
                        new WorkspacePortDto("OUTLET", "Spout", "FLUID", "OUTPUT", "spout", Map.of("x", 0.95, "y", 0.1), "anchor_spout", 250.0, true, false),
                        new WorkspacePortDto("THERMAL", "Bottom Base", "THERMAL", "BIDIRECTIONAL", "thermal-pad", Map.of("x", 0.5, "y", 0.95), "anchor_base", null, true, false),
                        new WorkspacePortDto("SENSOR", "Immersion Point", "SENSOR", "INPUT", "probe-receptive", Map.of("x", 0.5, "y", 0.5), "anchor_center", null, true, false)
                ),
                Map.of("svg", "/assets/equipment/beaker_250.svg", "width", 120, "height", 160),
                Map.of("maxTemperatureC", 500.0, "maxVolumeMl", 250.0)
        ));

        // 2. Beaker 500mL
        catalog.put("beaker-500ml", new EquipmentSpecification(
                "beaker-500ml", "Beaker 500 mL", "CONTAINER", 500.0, "beaker_standard",
                List.of("CONTAINER", "LIQUID_HOLDING", "HEATING_RECEPTIVE"),
                List.of(
                        new WorkspacePortDto("INLET", "Top Rim", "FLUID", "INPUT", "standard-open-mouth", Map.of("x", 0.5, "y", 0.05), "anchor_rim", 500.0, true, false),
                        new WorkspacePortDto("OUTLET", "Spout", "FLUID", "OUTPUT", "spout", Map.of("x", 0.95, "y", 0.1), "anchor_spout", 500.0, true, false),
                        new WorkspacePortDto("THERMAL", "Bottom Base", "THERMAL", "BIDIRECTIONAL", "thermal-pad", Map.of("x", 0.5, "y", 0.95), "anchor_base", null, true, false),
                        new WorkspacePortDto("SENSOR", "Immersion Point", "SENSOR", "INPUT", "probe-receptive", Map.of("x", 0.5, "y", 0.5), "anchor_center", null, true, false)
                ),
                Map.of("svg", "/assets/equipment/beaker_500.svg", "width", 150, "height", 200),
                Map.of("maxTemperatureC", 500.0, "maxVolumeMl", 500.0)
        ));

        // 3. Test Tube
        catalog.put("test-tube", new EquipmentSpecification(
                "test-tube", "Test Tube 50 mL", "CONTAINER", 50.0, "test_tube",
                List.of("CONTAINER", "LIQUID_HOLDING", "HEATING_RECEPTIVE"),
                List.of(
                        new WorkspacePortDto("INLET", "Mouth", "FLUID", "INPUT", "standard-open-mouth", Map.of("x", 0.5, "y", 0.05), "anchor_rim", 50.0, true, false),
                        new WorkspacePortDto("OUTLET", "Mouth", "FLUID", "OUTPUT", "standard-open-mouth", Map.of("x", 0.5, "y", 0.05), "anchor_rim", 50.0, true, false),
                        new WorkspacePortDto("THERMAL", "Tube Body", "THERMAL", "BIDIRECTIONAL", "thermal-pad", Map.of("x", 0.5, "y", 0.8), "anchor_body", null, true, false),
                        new WorkspacePortDto("SENSOR", "Immersion", "SENSOR", "INPUT", "probe-receptive", Map.of("x", 0.5, "y", 0.5), "anchor_center", null, true, false)
                ),
                Map.of("svg", "/assets/equipment/test_tube.svg", "width", 40, "height", 180),
                Map.of("maxTemperatureC", 400.0, "maxVolumeMl", 50.0)
        ));

        // 4. Erlenmeyer Flask 250mL
        catalog.put("erlenmeyer-flask", new EquipmentSpecification(
                "erlenmeyer-flask", "Erlenmeyer Flask 250 mL", "CONTAINER", 250.0, "erlenmeyer",
                List.of("CONTAINER", "LIQUID_HOLDING", "SWIRLABLE", "HEATING_RECEPTIVE"),
                List.of(
                        new WorkspacePortDto("INLET", "Neck", "FLUID", "INPUT", "standard-taper-24-40", Map.of("x", 0.5, "y", 0.05), "anchor_neck", 250.0, true, false),
                        new WorkspacePortDto("OUTLET", "Neck", "FLUID", "OUTPUT", "standard-taper-24-40", Map.of("x", 0.5, "y", 0.05), "anchor_neck", 250.0, true, false),
                        new WorkspacePortDto("THERMAL", "Bottom Base", "THERMAL", "BIDIRECTIONAL", "thermal-pad", Map.of("x", 0.5, "y", 0.95), "anchor_base", null, true, false),
                        new WorkspacePortDto("SENSOR", "Immersion Point", "SENSOR", "INPUT", "probe-receptive", Map.of("x", 0.5, "y", 0.6), "anchor_body", null, true, false)
                ),
                Map.of("svg", "/assets/equipment/erlenmeyer_250.svg", "width", 140, "height", 180),
                Map.of("maxTemperatureC", 500.0, "maxVolumeMl", 250.0)
        ));

        // 5. Bunsen Burner
        catalog.put("bunsen-burner", new EquipmentSpecification(
                "bunsen-burner", "Bunsen Burner", "HEATER", 0.0, "bunsen_burner",
                List.of("HEAT_SOURCE", "FLAME_GENERATOR"),
                List.of(
                        new WorkspacePortDto("GAS_INLET", "Gas Connector", "GAS", "INPUT", "tubing-barb", Map.of("x", 0.1, "y", 0.9), "anchor_gas", null, true, false),
                        new WorkspacePortDto("THERMAL", "Flame Top", "THERMAL", "OUTPUT", "thermal-pad", Map.of("x", 0.5, "y", 0.05), "anchor_flame", null, true, false)
                ),
                Map.of("svg", "/assets/equipment/bunsen_burner.svg", "width", 80, "height", 160),
                Map.of("maxPowerWatts", 1500.0, "maxTemperatureC", 1200.0)
        ));

        // 6. Hotplate Stirrer
        catalog.put("hotplate-stirrer", new EquipmentSpecification(
                "hotplate-stirrer", "Hotplate & Magnetic Stirrer", "HEATER", 0.0, "hotplate_stirrer",
                List.of("HEAT_SOURCE", "MAGNETIC_STIRRER"),
                List.of(
                        new WorkspacePortDto("THERMAL", "Heating Surface", "THERMAL", "OUTPUT", "thermal-pad", Map.of("x", 0.5, "y", 0.1), "anchor_surface", null, true, false),
                        new WorkspacePortDto("POWER", "Electrical Cord", "POWER", "INPUT", "iec-c13", Map.of("x", 0.05, "y", 0.8), "anchor_power", null, true, false)
                ),
                Map.of("svg", "/assets/equipment/hotplate.svg", "width", 160, "height", 100),
                Map.of("maxPowerWatts", 1200.0, "maxTemperatureC", 450.0)
        ));

        // 7. Digital Thermometer
        catalog.put("thermometer", new EquipmentSpecification(
                "thermometer", "Digital Temperature Probe", "SENSOR", 0.0, "thermometer",
                List.of("SENSOR", "TEMPERATURE_MEASURING"),
                List.of(
                        new WorkspacePortDto("SENSOR", "Probe Tip", "SENSOR", "OUTPUT", "temperature-probe", Map.of("x", 0.5, "y", 0.95), "anchor_tip", null, true, false)
                ),
                Map.of("svg", "/assets/equipment/thermometer.svg", "width", 30, "height", 180),
                Map.of("minTemperatureC", -50.0, "maxTemperatureC", 300.0, "accuracyC", 0.1)
        ));

        // 8. pH Meter
        catalog.put("ph-meter", new EquipmentSpecification(
                "ph-meter", "Digital pH Meter", "SENSOR", 0.0, "ph_meter",
                List.of("SENSOR", "PH_MEASURING"),
                List.of(
                        new WorkspacePortDto("SENSOR", "Electrode Bulb", "SENSOR", "OUTPUT", "ph-electrode", Map.of("x", 0.5, "y", 0.95), "anchor_bulb", null, true, false)
                ),
                Map.of("svg", "/assets/equipment/ph_meter.svg", "width", 40, "height", 180),
                Map.of("minPh", 0.0, "maxPh", 14.0, "accuracyPh", 0.01)
        ));

        // 9. Condenser (Liebig)
        catalog.put("condenser", new EquipmentSpecification(
                "condenser", "Liebig Condenser", "APPARATUS", 0.0, "condenser",
                List.of("CONDENSER", "COOLING"),
                List.of(
                        new WorkspacePortDto("VAPOR_INLET", "Top Joint", "FLUID", "INPUT", "standard-taper-24-40", Map.of("x", 0.5, "y", 0.05), "anchor_top", null, true, false),
                        new WorkspacePortDto("LIQUID_OUTLET", "Bottom Joint", "FLUID", "OUTPUT", "standard-taper-24-40", Map.of("x", 0.5, "y", 0.95), "anchor_bottom", null, true, false),
                        new WorkspacePortDto("WATER_INLET", "Coolant In", "FLUID", "INPUT", "tubing-barb", Map.of("x", 0.1, "y", 0.8), "anchor_water_in", null, true, false),
                        new WorkspacePortDto("WATER_OUTLET", "Coolant Out", "FLUID", "OUTPUT", "tubing-barb", Map.of("x", 0.9, "y", 0.2), "anchor_water_out", null, true, false)
                ),
                Map.of("svg", "/assets/equipment/condenser.svg", "width", 60, "height", 260),
                Map.of("maxVaporTemperatureC", 200.0)
        ));

        // 10. Funnel
        catalog.put("funnel", new EquipmentSpecification(
                "funnel", "Glass Funnel", "TOOL", 100.0, "funnel",
                List.of("TRANSFER", "FILTRATION"),
                List.of(
                        new WorkspacePortDto("INLET", "Wide Cone", "FLUID", "INPUT", "standard-open-mouth", Map.of("x", 0.5, "y", 0.1), "anchor_cone", 100.0, true, false),
                        new WorkspacePortDto("OUTLET", "Stem Outlet", "FLUID", "OUTPUT", "funnel-stem", Map.of("x", 0.5, "y", 0.95), "anchor_stem", 100.0, true, false)
                ),
                Map.of("svg", "/assets/equipment/funnel.svg", "width", 80, "height", 120),
                Map.of("maxVolumeMl", 100.0)
        ));

        // 11. Pipette 10mL
        catalog.put("pipette", new EquipmentSpecification(
                "pipette", "Graduated Pipette 10 mL", "TOOL", 10.0, "pipette",
                List.of("TRANSFER", "MEASURING_DISPENSE"),
                List.of(
                        new WorkspacePortDto("INLET", "Suction Top", "FLUID", "INPUT", "pipette-bulb-joint", Map.of("x", 0.5, "y", 0.05), "anchor_top", 10.0, true, false),
                        new WorkspacePortDto("OUTLET", "Fine Tip", "FLUID", "OUTPUT", "pipette-tip", Map.of("x", 0.5, "y", 0.95), "anchor_tip", 10.0, true, false)
                ),
                Map.of("svg", "/assets/equipment/pipette.svg", "width", 20, "height", 200),
                Map.of("maxVolumeMl", 10.0, "toleranceMl", 0.02)
        ));

        // 12. Burette 50mL
        catalog.put("burette", new EquipmentSpecification(
                "burette", "Titration Burette 50 mL", "TOOL", 50.0, "burette",
                List.of("TITRATION", "PRECISION_DISPENSE"),
                List.of(
                        new WorkspacePortDto("INLET", "Funnel Top", "FLUID", "INPUT", "standard-open-mouth", Map.of("x", 0.5, "y", 0.05), "anchor_top", 50.0, true, false),
                        new WorkspacePortDto("OUTLET", "Stopcock Tip", "FLUID", "OUTPUT", "precision-stopcock", Map.of("x", 0.5, "y", 0.95), "anchor_tip", 50.0, true, false)
                ),
                Map.of("svg", "/assets/equipment/burette.svg", "width", 30, "height", 300),
                Map.of("maxVolumeMl", 50.0, "toleranceMl", 0.05)
        ));
    }

    public List<EquipmentSpecification> getAll() {
        return new ArrayList<>(catalog.values());
    }

    public Optional<EquipmentSpecification> findById(String id) {
        if (id == null) return Optional.empty();
        String normalized = id.toLowerCase().trim();
        EquipmentSpecification exact = catalog.get(normalized);
        if (exact != null) return Optional.of(exact);

        // Fallback prefix matching (e.g. beaker, test-tube, burner, etc.)
        for (Map.Entry<String, EquipmentSpecification> entry : catalog.entrySet()) {
            if (normalized.contains(entry.getKey()) || entry.getKey().contains(normalized)) {
                return Optional.of(entry.getValue());
            }
        }
        return Optional.empty();
    }

    public Optional<WorkspacePortDto> findPort(String equipmentId, String portId) {
        return findById(equipmentId).flatMap(spec -> spec.ports.stream()
                .filter(p -> p.id().equalsIgnoreCase(portId))
                .findFirst());
    }
}
