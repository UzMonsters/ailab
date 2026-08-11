package com.ailab.chemistry.controller;

import com.ailab.chemistry.api.CompoundCatalogService;
import com.ailab.chemistry.api.CompoundSummary;
import com.ailab.chemistry.api.ElementCatalogService;
import com.ailab.chemistry.api.ElementSummary;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/chemistry/materials")
@Tag(name = "Materials Catalog", description = "Dynamic chemical materials and compounds tab")
@SecurityRequirement(name = "bearerAuth")
public class MaterialsCatalogController {

    private final CompoundCatalogService compoundCatalogService;
    private final ElementCatalogService elementCatalogService;

    public MaterialsCatalogController(CompoundCatalogService compoundCatalogService, ElementCatalogService elementCatalogService) {
        this.compoundCatalogService = compoundCatalogService;
        this.elementCatalogService = elementCatalogService;
    }

    @GetMapping
    @Operation(summary = "List chemical materials", description = "Retrieve material summary list (compounds and elements) with optional search query and physical phase filter.")
    public List<Map<String, Object>> listMaterials(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String phase,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        List<Map<String, Object>> result = new ArrayList<>();

        List<CompoundSummary> compounds = compoundCatalogService.listCompounds();
        for (CompoundSummary c : compounds) {
            if (query != null && !query.isBlank() && !c.getPrimaryName().toLowerCase().contains(query.toLowerCase())
                    && !c.getNormalizedFormula().toLowerCase().contains(query.toLowerCase())) {
                continue;
            }
            String p = "liquid";
            if (phase != null && !phase.isBlank() && !p.equalsIgnoreCase(phase)) {
                continue;
            }
            result.add(Map.of(
                    "materialId", c.getCompoundCode(),
                    "name", c.getPrimaryName(),
                    "formula", c.getNormalizedFormula(),
                    "category", "COMPOUND",
                    "phase", p
            ));
        }

        List<ElementSummary> elements = elementCatalogService.listElements();
        for (ElementSummary e : elements) {
            if (query != null && !query.isBlank() && !e.getName().toLowerCase().contains(query.toLowerCase())
                    && !e.getSymbol().toLowerCase().contains(query.toLowerCase())) {
                continue;
            }
            String p = "solid";
            if (phase != null && !phase.isBlank() && !p.equalsIgnoreCase(phase)) {
                continue;
            }
            result.add(Map.of(
                    "materialId", "ELEM-" + e.getSymbol(),
                    "name", e.getName(),
                    "formula", e.getSymbol(),
                    "category", "ELEMENT",
                    "phase", p
            ));
        }

        int safePage = Math.max(0, page);
        int safeSize = Math.max(1, size);
        int from = Math.min(safePage * safeSize, result.size());
        int to = Math.min(from + safeSize, result.size());
        return result.subList(from, to);
    }
}
