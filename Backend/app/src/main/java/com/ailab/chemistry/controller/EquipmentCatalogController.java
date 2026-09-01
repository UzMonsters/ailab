package com.ailab.chemistry.controller;

import com.ailab.chemistry.domain.equipment.EquipmentReferenceProfile;
import com.ailab.chemistry.domain.equipment.EquipmentReferenceRepository;
import com.ailab.chemistry.infrastructure.persistence.laboratory.UnavailableEquipmentReferenceRepository;
import com.ailab.workspace.service.EquipmentCatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/api/v1/chemistry/equipment")
@Tag(name = "Equipment Catalog", description = "Dynamic laboratory apparatus, tools, equipment library, and typed port connections inspector")
@SecurityRequirement(name = "bearerAuth")
public class EquipmentCatalogController {

    private final EquipmentReferenceRepository equipmentRepository;
    private final EquipmentCatalogService equipmentCatalogService;

    public EquipmentCatalogController(
            ObjectProvider<EquipmentReferenceRepository> repositoryProvider,
            EquipmentCatalogService equipmentCatalogService
    ) {
        EquipmentReferenceRepository repo = repositoryProvider.getIfAvailable();
        this.equipmentRepository = repo != null ? repo : new UnavailableEquipmentReferenceRepository();
        this.equipmentCatalogService = equipmentCatalogService;
    }

    @GetMapping({"", "/catalog"})
    @Operation(summary = "List equipment library", description = "Retrieve list of active laboratory equipment profiles with typed ports, limits, and 2D/3D anchors.")
    public List<Map<String, Object>> listEquipment(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        List<Map<String, Object>> all = new ArrayList<>();

        // 1. Load active repository profiles
        List<EquipmentReferenceProfile> profiles = equipmentRepository.findActive();
        if (profiles != null) {
            for (EquipmentReferenceProfile p : profiles) {
                all.add(toSummaryMap(p));
            }
        }

        // 2. Load catalog service specs
        List<EquipmentCatalogService.EquipmentSpecification> specs = equipmentCatalogService.getAll();
        if (specs != null) {
            for (EquipmentCatalogService.EquipmentSpecification s : specs) {
                all.add(specToMap(s));
            }
        }

        // Filter
        if (query != null && !query.isBlank()) {
            String q = query.toLowerCase();
            all = all.stream()
                    .filter(m -> String.valueOf(m.getOrDefault("displayName", "")).toLowerCase().contains(q)
                            || String.valueOf(m.getOrDefault("name", "")).toLowerCase().contains(q)
                            || String.valueOf(m.getOrDefault("profileId", "")).toLowerCase().contains(q)
                            || String.valueOf(m.getOrDefault("id", "")).toLowerCase().contains(q))
                    .toList();
        }

        if (category != null && !category.isBlank()) {
            String cat = category.toLowerCase();
            all = all.stream()
                    .filter(m -> String.valueOf(m.getOrDefault("type", "")).toLowerCase().contains(cat)
                            || String.valueOf(m.getOrDefault("category", "")).toLowerCase().contains(cat))
                    .toList();
        }

        return page(all, page, size);
    }

    @GetMapping("/{identifier}")
    @Operation(summary = "Get equipment details and ports", description = "Retrieve equipment specification, capabilities, and typed port configuration by profile identifier.")
    public Map<String, Object> getEquipmentDetails(@PathVariable String identifier) {
        Optional<EquipmentReferenceProfile> profileOpt = equipmentRepository.findByProfileId(identifier);
        if (profileOpt.isPresent()) {
            return toDetailsMap(profileOpt.get());
        }

        Optional<EquipmentCatalogService.EquipmentSpecification> specOpt = equipmentCatalogService.findById(identifier);
        if (specOpt.isPresent()) {
            return specToMap(specOpt.get());
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipment profile not found: " + identifier);
    }

    private Map<String, Object> toSummaryMap(EquipmentReferenceProfile profile) {
        return Map.of(
                "profileId", profile.profileId(),
                "id", profile.profileId(),
                "displayName", profile.displayName(),
                "name", profile.displayName(),
                "type", profile.type().name(),
                "category", profile.type().name(),
                "condition", profile.condition().name(),
                "provenance", profile.provenance()
        );
    }

    private Map<String, Object> specToMap(EquipmentCatalogService.EquipmentSpecification spec) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("profileId", spec.id());
        m.put("id", spec.id());
        m.put("displayName", spec.name());
        m.put("name", spec.name());
        m.put("category", spec.category());
        m.put("type", spec.category());
        m.put("capacityMl", spec.capacityMl());
        m.put("rendererKey", spec.rendererKey());
        m.put("capabilities", spec.capabilities());
        m.put("ports", spec.ports());
        m.put("assets2d", spec.assets2d());
        m.put("limits", spec.limits());
        return m;
    }

    private Map<String, Object> toDetailsMap(EquipmentReferenceProfile profile) {
        List<Map<String, Object>> ports = List.of(
                Map.of("id", "INLET", "name", "Fluid Inlet", "type", "FLUID", "direction", "INPUT", "connector", "standard-open-mouth"),
                Map.of("id", "OUTLET", "name", "Fluid Outlet", "type", "FLUID", "direction", "OUTPUT", "connector", "spout"),
                Map.of("id", "THERMAL", "name", "Heat Junction", "type", "THERMAL", "direction", "BIDIRECTIONAL", "connector", "thermal-pad")
        );

        return Map.of(
                "profileId", profile.profileId(),
                "id", profile.profileId(),
                "displayName", profile.displayName(),
                "name", profile.displayName(),
                "type", profile.type().name(),
                "condition", profile.condition().name(),
                "datasetId", profile.datasetId(),
                "capabilities", profile.capabilities().stream().map(com.ailab.chemistry.domain.equipment.EquipmentCapability::capabilityType).toList(),
                "ports", ports,
                "provenance", profile.provenance()
        );
    }

    private <T> List<T> page(List<T> items, int page, int size) {
        int safePage = Math.max(0, page);
        int safeSize = Math.max(1, size);
        int from = Math.min(safePage * safeSize, items.size());
        int to = Math.min(from + safeSize, items.size());
        return items.subList(from, to);
    }
}
