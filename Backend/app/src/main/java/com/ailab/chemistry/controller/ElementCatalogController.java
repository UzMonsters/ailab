package com.ailab.chemistry.controller;

import com.ailab.chemistry.api.ElementCatalogService;
import com.ailab.chemistry.api.ElementDetails;
import com.ailab.chemistry.api.ElementPropertyDetails;
import com.ailab.chemistry.api.ElementPropertyService;
import com.ailab.chemistry.api.ElementSummary;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chemistry/elements")
@Tag(name = "Periodic Table Elements", description = "Periodic table element catalog and extended physical/chemical properties")
@SecurityRequirement(name = "bearerAuth")
public class ElementCatalogController {

    private final ElementCatalogService catalogService;
    private final ElementPropertyService propertyService;

    public ElementCatalogController(ElementCatalogService catalogService, ElementPropertyService propertyService) {
        this.catalogService = catalogService;
        this.propertyService = propertyService;
    }

    @GetMapping
    @Operation(summary = "List periodic table elements", description = "Retrieve summary listing of all periodic table elements.")
    public List<ElementSummary> listElements() {
        return catalogService.listElements();
    }

    @GetMapping("/{identifier}")
    @Operation(summary = "Get element details", description = "Retrieve element details by atomic number (e.g. 6) or chemical symbol (e.g. 'C', 'Fe').")
    public ElementDetails getElement(@PathVariable String identifier) {
        if (identifier.matches("\\d+")) {
            return catalogService.getByAtomicNumber(Integer.parseInt(identifier));
        }
        return catalogService.getBySymbol(identifier);
    }

    @GetMapping("/{identifier}/properties")
    @Operation(summary = "Get extended element properties", description = "Retrieve extended physical/chemical properties (electronegativity, ionization energy, atomic radius) by atomic number or symbol.")
    public ElementPropertyDetails getElementProperties(@PathVariable String identifier) {
        if (identifier.matches("\\d+")) {
            return propertyService.getByAtomicNumber(Integer.parseInt(identifier));
        }
        return propertyService.getBySymbol(identifier);
    }
}
