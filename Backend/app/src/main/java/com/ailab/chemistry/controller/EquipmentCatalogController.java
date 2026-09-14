package com.ailab.chemistry.controller;

import com.ailab.admin.catalog.AdminCatalogDraftEntity;
import com.ailab.admin.catalog.AdminCatalogDraftRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/api/v1/chemistry/equipment")
@Tag(name = "Equipment Catalog", description = "Dynamic laboratory apparatus, tools, equipment library, and typed port connections inspector")
@SecurityRequirement(name = "bearerAuth")
public class EquipmentCatalogController {

    private static final String ENTITY_TYPE = "EQUIPMENT";
    private static final String PUBLISHED = "PUBLISHED";

    private final AdminCatalogDraftRepository catalogRepository;

    public EquipmentCatalogController(AdminCatalogDraftRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    @GetMapping({"", "/catalog"})
    @Operation(summary = "List equipment library", description = "Retrieve list of active laboratory equipment profiles with typed ports, limits, and 2D/3D anchors.")
    public List<Map<String, Object>> listEquipment(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        List<Map<String, Object>> all = catalogRepository.findByEntityTypeAndStatus(ENTITY_TYPE, PUBLISHED).stream()
                .map(this::toEquipmentMap)
                .toList();

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
        AdminCatalogDraftEntity entity = catalogRepository.findByEntityTypeAndId(ENTITY_TYPE, identifier)
                .or(() -> catalogRepository.findByEntityTypeAndCode(ENTITY_TYPE, identifier))
                .filter(e -> PUBLISHED.equalsIgnoreCase(e.getStatus()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipment profile not found: " + identifier));
        return toEquipmentMap(entity);
    }

    private Map<String, Object> toEquipmentMap(AdminCatalogDraftEntity entity) {
        Map<String, Object> data = entity.getData() != null ? entity.getData() : Map.of();
        String code = stringValue(data.getOrDefault("code", entity.getCode()));
        String name = firstString(data, "displayName", "name", "title", "label");
        if (name == null || name.isBlank()) {
            name = code;
        }
        String category = firstString(data, "category", "type");

        Map<String, Object> m = new LinkedHashMap<>();
        m.putAll(data);
        m.put("profileId", code);
        m.put("id", code);
        m.put("code", code);
        m.put("displayName", name);
        m.put("name", name);
        if (category != null) {
            m.put("category", category);
            m.put("type", category);
        }
        m.putIfAbsent("ports", List.of());
        m.putIfAbsent("capabilities", List.of());
        return m;
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

    private <T> List<T> page(List<T> items, int page, int size) {
        int safePage = Math.max(0, page);
        int safeSize = Math.max(1, size);
        int from = Math.min(safePage * safeSize, items.size());
        int to = Math.min(from + safeSize, items.size());
        return items.subList(from, to);
    }
}
