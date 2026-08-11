package com.ailab.workspace.service;

import com.ailab.chemistry.api.CompoundCatalogService;
import com.ailab.chemistry.api.ElementCatalogService;
import com.ailab.chemistry.domain.equipment.EquipmentReferenceProfile;
import com.ailab.chemistry.domain.equipment.EquipmentReferenceRepository;
import com.ailab.chemistry.infrastructure.persistence.laboratory.UnavailableEquipmentReferenceRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class WorkspaceScienceAuthorityService {

    private static final Set<String> STANDARD_PORTS = Set.of("INLET", "OUTLET", "THERMAL");

    private final EquipmentReferenceRepository equipmentRepository;
    private final CompoundCatalogService compoundCatalogService;
    private final ElementCatalogService elementCatalogService;

    public WorkspaceScienceAuthorityService(
            ObjectProvider<EquipmentReferenceRepository> equipmentRepository,
            CompoundCatalogService compoundCatalogService,
            ElementCatalogService elementCatalogService) {
        EquipmentReferenceRepository repository = equipmentRepository.getIfAvailable();
        this.equipmentRepository = repository != null ? repository : new UnavailableEquipmentReferenceRepository();
        this.compoundCatalogService = compoundCatalogService;
        this.elementCatalogService = elementCatalogService;
    }

    public Map<String, Object> authoritativeEquipment(Map<String, Object> proposed) {
        String profileId = string(proposed.getOrDefault("profileId", proposed.get("equipmentProfileId")));
        Optional<EquipmentReferenceProfile> profile = profileId.isBlank()
                ? Optional.empty()
                : equipmentRepository.findByProfileId(profileId);

        if (profile.isEmpty()) {
            String equipmentType = string(proposed.get("equipmentType"));
            profile = resolveByType(equipmentType);
        }

        EquipmentReferenceProfile authoritative = profile.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Unknown laboratory equipment"));

        proposed.put("profileId", authoritative.profileId());
        proposed.put("equipmentType", authoritative.type().name());
        proposed.put("name", authoritative.displayName());
        containerProfileFor(authoritative).ifPresent(containerProfileId ->
                proposed.putIfAbsent("containerProfileId", containerProfileId));
        return proposed;
    }

    public void requireKnownMaterial(String materialId) {
        if (materialId == null || materialId.isBlank()) {
            throw new IllegalArgumentException("materialId is required");
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
        if (!STANDARD_PORTS.contains(port.toUpperCase(Locale.ROOT))) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Unknown equipment port: " + port);
        }
    }

    private Optional<EquipmentReferenceProfile> resolveByType(String equipmentType) {
        if (equipmentType == null || equipmentType.isBlank()) {
            return Optional.empty();
        }

        String normalized = equipmentType.toUpperCase(Locale.ROOT);
        List<EquipmentReferenceProfile> active = equipmentRepository.findActive();
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
