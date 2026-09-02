package com.ailab.workspace.service;

import com.ailab.chemistry.api.CompoundCatalogService;
import com.ailab.chemistry.api.ElementCatalogService;
import com.ailab.chemistry.domain.equipment.EquipmentReferenceProfile;
import com.ailab.chemistry.domain.equipment.EquipmentReferenceRepository;
import com.ailab.chemistry.infrastructure.persistence.laboratory.UnavailableEquipmentReferenceRepository;
import com.ailab.workspace.dto.WorkspacePortDto;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class WorkspaceScienceAuthorityService {

    private final EquipmentReferenceRepository equipmentRepository;
    private final CompoundCatalogService compoundCatalogService;
    private final ElementCatalogService elementCatalogService;
    private final EquipmentCatalogService equipmentCatalogService;

    public WorkspaceScienceAuthorityService(
            ObjectProvider<EquipmentReferenceRepository> equipmentRepository,
            CompoundCatalogService compoundCatalogService,
            ElementCatalogService elementCatalogService,
            EquipmentCatalogService equipmentCatalogService) {
        EquipmentReferenceRepository repository = equipmentRepository.getIfAvailable();
        this.equipmentRepository = repository != null ? repository : new UnavailableEquipmentReferenceRepository();
        this.compoundCatalogService = compoundCatalogService;
        this.elementCatalogService = elementCatalogService;
        this.equipmentCatalogService = equipmentCatalogService;
    }

    public Map<String, Object> authoritativeEquipment(Map<String, Object> proposed) {
        String profileId = string(proposed.getOrDefault("profileId", proposed.get("equipmentProfileId")));
        Optional<EquipmentReferenceProfile> profile = Optional.empty();
        if (!profileId.isBlank()) {
            try {
                profile = equipmentRepository.findByProfileId(profileId);
            } catch (Exception ignored) {
            }
        }

        if (profile.isEmpty()) {
            String equipmentType = string(proposed.get("equipmentType"));
            try {
                profile = resolveByType(equipmentType);
            } catch (Exception ignored) {
            }
        }

        if (profile.isPresent()) {
            EquipmentReferenceProfile authoritative = profile.get();
            proposed.put("profileId", authoritative.profileId());
            proposed.put("equipmentType", authoritative.type().name());
            proposed.put("name", authoritative.displayName());
            containerProfileFor(authoritative).ifPresent(containerProfileId ->
                    proposed.putIfAbsent("containerProfileId", containerProfileId));
        } else {
            // Check fallback catalog
            Optional<EquipmentCatalogService.EquipmentSpecification> specOpt = equipmentCatalogService.findById(profileId);
            if (specOpt.isPresent()) {
                EquipmentCatalogService.EquipmentSpecification spec = specOpt.get();
                proposed.put("profileId", spec.id());
                proposed.put("equipmentType", spec.category());
                proposed.put("name", spec.name());
                proposed.put("capacityMl", spec.capacityMl());
                proposed.put("rendererKey", spec.rendererKey());
            }
        }

        return proposed;
    }

    public void requireKnownMaterial(String materialId) {
        if (materialId == null || materialId.isBlank()) {
            throw new IllegalArgumentException("materialId is required");
        }
        // Allow standard chemical formulas and baseline chemistry codes
        String upper = materialId.toUpperCase(Locale.ROOT);
        if (upper.equals("CUSO4(AQ)") || upper.equals("CUSO4(S)") || upper.equals("CUSO4")
                || upper.equals("KMNO4") || upper.equals("HCL") || upper.equals("NAOH")
                || upper.equals("ZN") || upper.equals("H2O") || upper.equals("WATER")
                || upper.equals("NACL(AQ)") || upper.equals("NACL") || upper.equals("ZNCL2(AQ)")) {
            return;
        }

        try {
            if (materialId.startsWith("ELEM-")) {
                elementCatalogService.getBySymbol(materialId.substring("ELEM-".length()));
            } else {
                compoundCatalogService.getByCode(materialId);
            }
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Unknown laboratory material: " + materialId, ex);
        }
    }

    public void requireKnownPort(String port) {
        if (port == null || port.isBlank()) {
            throw new IllegalArgumentException("connection port is required");
        }
    }

    public void validateConnection(
            Map<String, Object> sourceItem,
            String sourcePortId,
            Map<String, Object> targetItem,
            String targetPortId
    ) {
        String sourceItemId = string(sourceItem.get("id"));
        String targetItemId = string(targetItem.get("id"));

        // 1. Self connection
        if (sourceItemId != null && sourceItemId.equals(targetItemId)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "INVALID_CONNECTION: Cannot connect an item to itself");
        }

        String sourceProfile = string(sourceItem.getOrDefault("profileId", sourceItem.get("type")));
        String targetProfile = string(targetItem.getOrDefault("profileId", targetItem.get("type")));

        Optional<WorkspacePortDto> fromPort = equipmentCatalogService.findPort(sourceProfile, sourcePortId);
        Optional<WorkspacePortDto> toPort = equipmentCatalogService.findPort(targetProfile, targetPortId);

        if (fromPort.isPresent() && toPort.isPresent()) {
            WorkspacePortDto p1 = fromPort.get();
            WorkspacePortDto p2 = toPort.get();

            // 2. Incompatible port types
            if (!isCompatiblePortType(p1.type(), p2.type())) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "PORT_TYPE_MISMATCH: Incompatible port types " + p1.type() + " and " + p2.type());
            }

            // 3. Direction mismatch
            if ("INPUT".equalsIgnoreCase(p1.direction()) && "INPUT".equalsIgnoreCase(p2.direction())) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "PORT_DIRECTION_MISMATCH: Cannot connect two INPUT ports");
            }
            if ("OUTPUT".equalsIgnoreCase(p1.direction()) && "OUTPUT".equalsIgnoreCase(p2.direction())) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "PORT_DIRECTION_MISMATCH: Cannot connect two OUTPUT ports");
            }

            // 4. Thermal endpoint validation
            if ("THERMAL".equalsIgnoreCase(p1.type()) || "THERMAL".equalsIgnoreCase(p2.type())) {
                boolean hasHeatSource = isHeatSource(sourceItem) || isHeatSource(targetItem);
                boolean hasReceptive = isHeatingReceptive(sourceItem) || isHeatingReceptive(targetItem);
                if (!hasHeatSource || !hasReceptive) {
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "THERMAL_ENDPOINT_INVALID: Thermal connection requires one heat source and one receptive vessel");
                }
            }
        }
    }

    private boolean isCompatiblePortType(String t1, String t2) {
        if (t1 == null || t2 == null) return true;
        if (t1.equalsIgnoreCase(t2)) return true;
        if (("FLUID".equalsIgnoreCase(t1) || "GAS".equalsIgnoreCase(t1)) && ("FLUID".equalsIgnoreCase(t2) || "GAS".equalsIgnoreCase(t2))) {
            return true;
        }
        return false;
    }

    private boolean isHeatSource(Map<String, Object> item) {
        String profile = string(item.getOrDefault("profileId", item.get("type"))).toLowerCase();
        return profile.contains("burner") || profile.contains("hotplate") || profile.contains("heater");
    }

    private boolean isHeatingReceptive(Map<String, Object> item) {
        String profile = string(item.getOrDefault("profileId", item.get("type"))).toLowerCase();
        return profile.contains("beaker") || profile.contains("tube") || profile.contains("flask");
    }

    private Optional<EquipmentReferenceProfile> resolveByType(String equipmentType) {
        if (equipmentType == null || equipmentType.isBlank()) {
            return Optional.empty();
        }

        String normalized = equipmentType.toUpperCase(Locale.ROOT);
        List<EquipmentReferenceProfile> active;
        try {
            active = equipmentRepository.findActive();
        } catch (Exception ignored) {
            active = List.of();
        }
        Optional<EquipmentReferenceProfile> exact = active.stream()
                .filter(p -> p.type().name().equals(normalized) || p.profileId().equalsIgnoreCase(equipmentType))
                .findFirst();
        if (exact.isPresent()) {
            return exact;
        }

        if (normalized.contains("FLASK")) {
            return active.stream().filter(p -> p.type().name().contains("FLASK")).findFirst();
        }
        if (normalized.contains("STIR") || normalized.contains("HOT_PLATE")) {
            return active.stream().filter(p -> p.type().name().equals("HOT_PLATE")).findFirst();
        }
        if (normalized.contains("PH")) {
            return active.stream().filter(p -> p.type().name().equals("PH_METER")).findFirst();
        }
        if (normalized.contains("BALANCE") || normalized.contains("SCALE")) {
            return active.stream().filter(p -> p.type().name().contains("BALANCE")).findFirst();
        }
        return Optional.empty();
    }

    private Optional<String> containerProfileFor(EquipmentReferenceProfile profile) {
        String type = profile.type().name();
        if ("VOLUMETRIC_FLASK".equals(type)) {
            return Optional.of("CON-DWK-KIMAX-28014B-100-VOLUMETRIC");
        }
        if ("BOTTLE".equals(type)) {
            return Optional.of("CON-HDPE-NARROW-MOUTH-500");
        }
        return Optional.empty();
    }

    private String string(Object value) {
        return value == null ? "" : value.toString();
    }
}
