package com.ailab.chemistry.controller;

import com.ailab.admin.catalog.AdminCatalogDraftEntity;
import com.ailab.admin.catalog.AdminCatalogDraftRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/chemistry/materials")
@Tag(name = "Materials Catalog", description = "Dynamic chemical materials and compounds tab")
@SecurityRequirement(name = "bearerAuth")
public class MaterialsCatalogController {

    private static final String PUBLISHED = "PUBLISHED";
    private static final List<String> MATERIAL_ENTITY_TYPES = List.of("MATERIAL", "SUBSTANCE");

    private final AdminCatalogDraftRepository catalogRepository;

    public MaterialsCatalogController(AdminCatalogDraftRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    @GetMapping
    @Operation(summary = "List chemical materials", description = "Retrieve material summary list (compounds and elements) with optional search query and physical phase filter.")
    public List<Map<String, Object>> listMaterials(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String phase,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        List<Map<String, Object>> result = new ArrayList<>();

        for (String entityType : MATERIAL_ENTITY_TYPES) {
            for (AdminCatalogDraftEntity entity : catalogRepository.findByEntityTypeAndStatus(entityType, PUBLISHED)) {
                Map<String, Object> material = toMaterialMap(entity);
                if (!matchesQuery(material, query) || !matchesPhase(material, phase)) {
                    continue;
                }
                result.add(material);
            }
        }

        int safePage = Math.max(0, page);
        int safeSize = Math.max(1, size);
        int from = Math.min(safePage * safeSize, result.size());
        int to = Math.min(from + safeSize, result.size());
        return result.subList(from, to);
    }

    private Map<String, Object> toMaterialMap(AdminCatalogDraftEntity entity) {
        Map<String, Object> data = entity.getData() != null ? entity.getData() : Map.of();
        String code = stringValue(data.getOrDefault("code", entity.getCode()));
        String name = firstString(data, "name", "displayName", "title", "label");
        if (name == null || name.isBlank()) {
            name = code;
        }
        String formula = firstString(data, "formula", "normalizedFormula", "symbol");
        String category = firstString(data, "category", "type");
        if (category == null || category.isBlank()) {
            category = entity.getEntityType();
        }
        String materialPhase = firstString(data, "phase", "state");

        Map<String, Object> m = new LinkedHashMap<>();
        m.putAll(data);
        m.put("materialId", code);
        m.put("code", code);
        m.put("name", name);
        if (formula != null) {
            m.put("formula", formula);
        }
        m.put("category", category);
        if (materialPhase != null) {
            m.put("phase", materialPhase.toLowerCase());
        }
        return m;
    }

    private boolean matchesQuery(Map<String, Object> material, String query) {
        if (query == null || query.isBlank()) {
            return true;
        }
        String q = query.toLowerCase();
        return String.valueOf(material.getOrDefault("materialId", "")).toLowerCase().contains(q)
                || String.valueOf(material.getOrDefault("code", "")).toLowerCase().contains(q)
                || String.valueOf(material.getOrDefault("name", "")).toLowerCase().contains(q)
                || String.valueOf(material.getOrDefault("formula", "")).toLowerCase().contains(q);
    }

    private boolean matchesPhase(Map<String, Object> material, String phase) {
        if (phase == null || phase.isBlank()) {
            return true;
        }
        return String.valueOf(material.getOrDefault("phase", "")).equalsIgnoreCase(phase);
    }

    private String firstString(Map<String, Object> data, String... keys) {
        for (String key : keys) {
            String value = stringValue(data.get(key));
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    private String stringValue(Object value) {
        return value != null ? String.valueOf(value) : null;
    }
}
