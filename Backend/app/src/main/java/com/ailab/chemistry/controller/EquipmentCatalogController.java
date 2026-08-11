package com.ailab.chemistry.controller;

import com.ailab.chemistry.domain.equipment.EquipmentReferenceProfile;
import com.ailab.chemistry.domain.equipment.EquipmentReferenceRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

import com.ailab.chemistry.infrastructure.persistence.laboratory.UnavailableEquipmentReferenceRepository;
import org.springframework.beans.factory.ObjectProvider;

@RestController
@RequestMapping("/api/v1/chemistry/equipment")
@Tag(name = "Equipment Catalog", description = "Dynamic laboratory apparatus, tools, equipment library, and port connections inspector")
@SecurityRequirement(name = "bearerAuth")
public class EquipmentCatalogController {

    private final EquipmentReferenceRepository equipmentRepository;

    public EquipmentCatalogController(ObjectProvider<EquipmentReferenceRepository> repositoryProvider) {
        EquipmentReferenceRepository repo = repositoryProvider.getIfAvailable();
        this.equipmentRepository = repo != null ? repo : new UnavailableEquipmentReferenceRepository();
    }

    @GetMapping
    @Operation(summary = "List equipment library", description = "Retrieve list of active laboratory equipment profiles with optional name/category filter and pagination.")
    public List<Map<String, Object>> listEquipment(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        List<EquipmentReferenceProfile> profiles = equipmentRepository.findActive();

        if (query != null && !query.isBlank()) {
            String q = query.toLowerCase();
            profiles = profiles.stream()
                    .filter(p -> p.displayName().toLowerCase().contains(q) || p.profileId().toLowerCase().contains(q))
                    .toList();
        }

        if (category != null && !category.isBlank()) {
            String cat = category.toLowerCase();
            profiles = profiles.stream()
                    .filter(p -> p.type().name().toLowerCase().contains(cat))
                    .toList();
        }

        return page(profiles, page, size).stream().map(this::toSummaryMap).toList();
    }

    @GetMapping("/{identifier}")
    @Operation(summary = "Get equipment details and ports", description = "Retrieve equipment specification, capabilities, and port configuration by profile identifier.")
    public Map<String, Object> getEquipmentDetails(@PathVariable String identifier) {
        EquipmentReferenceProfile profile = equipmentRepository.findByProfileId(identifier)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipment profile not found: " + identifier));
        return toDetailsMap(profile);
    }

    private Map<String, Object> toSummaryMap(EquipmentReferenceProfile profile) {
        return Map.of(
                "profileId", profile.profileId(),
                "displayName", profile.displayName(),
                "type", profile.type().name(),
                "condition", profile.condition().name(),
                "provenance", profile.provenance()
        );
    }

    private Map<String, Object> toDetailsMap(EquipmentReferenceProfile profile) {
        List<Map<String, Object>> ports = List.of(
                Map.of("id", "INLET", "name", "Fluid Inlet", "type", "FLUID", "direction", "INPUT"),
                Map.of("id", "OUTLET", "name", "Fluid Outlet", "type", "FLUID", "direction", "OUTPUT"),
                Map.of("id", "THERMAL", "name", "Heat Junction", "type", "THERMAL", "direction", "BIDIRECTIONAL")
        );

        return Map.of(
                "profileId", profile.profileId(),
                "displayName", profile.displayName(),
                "type", profile.type().name(),
                "condition", profile.condition().name(),
                "datasetId", profile.datasetId(),
                "capabilities", profile.capabilities().stream().map(com.ailab.chemistry.domain.equipment.EquipmentCapability::capabilityType).toList(),
                "ports", ports,
                "provenance", profile.provenance()
        );
    }

    private List<EquipmentReferenceProfile> page(List<EquipmentReferenceProfile> profiles, int page, int size) {
        int safePage = Math.max(0, page);
        int safeSize = Math.max(1, size);
        int from = Math.min(safePage * safeSize, profiles.size());
        int to = Math.min(from + safeSize, profiles.size());
        return profiles.subList(from, to);
    }
}
