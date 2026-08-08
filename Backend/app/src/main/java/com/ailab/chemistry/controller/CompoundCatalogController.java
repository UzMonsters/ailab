package com.ailab.chemistry.controller;

import com.ailab.chemistry.api.CompoundCatalogService;
import com.ailab.chemistry.api.CompoundDetails;
import com.ailab.chemistry.api.CompoundPhysicalPropertyDetails;
import com.ailab.chemistry.api.CompoundPhysicalPropertyService;
import com.ailab.chemistry.api.CompoundSummary;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chemistry/compounds")
@Tag(name = "Compound Catalog", description = "Chemical compound catalog, search, and physical property lookup")
@SecurityRequirement(name = "bearerAuth")
public class CompoundCatalogController {

    private final CompoundCatalogService catalogService;
    private final CompoundPhysicalPropertyService propertyService;

    public CompoundCatalogController(CompoundCatalogService catalogService, CompoundPhysicalPropertyService propertyService) {
        this.catalogService = catalogService;
        this.propertyService = propertyService;
    }

    @GetMapping
    @Operation(summary = "Search or list compounds", description = "List all compounds or filter by name query, normalized formula, or composition formula.")
    public List<CompoundSummary> searchCompounds(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String formula,
            @RequestParam(required = false) String composition) {
        if (name != null && !name.isBlank()) {
            return catalogService.searchByName(name);
        }
        if (formula != null && !formula.isBlank()) {
            return catalogService.findByNormalizedFormula(formula);
        }
        if (composition != null && !composition.isBlank()) {
            return catalogService.findByCompositionFormula(composition);
        }
        return catalogService.listCompounds();
    }

    @GetMapping("/{identifier}")
    @Operation(summary = "Get compound details", description = "Retrieve detailed compound metadata by UUID or compound code (e.g. 'CMP-WATER', 'CMP-COPPER-SULFATE-PENTAHYDRATE').")
    public CompoundDetails getCompound(@PathVariable String identifier) {
        if (isUuid(identifier)) {
            return catalogService.getById(UUID.fromString(identifier));
        }
        return catalogService.getByCode(identifier);
    }

    @GetMapping("/{identifier}/properties")
    @Operation(summary = "Get compound physical properties", description = "Retrieve physical properties (melting point, boiling point, density, heat capacity) by UUID or compound code.")
    public CompoundPhysicalPropertyDetails getCompoundProperties(@PathVariable String identifier) {
        if (isUuid(identifier)) {
            return propertyService.getByCompoundId(UUID.fromString(identifier));
        }
        return propertyService.getByCompoundCode(identifier);
    }

    private static boolean isUuid(String value) {
        try {
            UUID.fromString(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
