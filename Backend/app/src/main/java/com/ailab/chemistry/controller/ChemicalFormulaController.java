package com.ailab.chemistry.controller;

import com.ailab.chemistry.api.ChemicalFormulaService;
import com.ailab.chemistry.domain.formula.ChemicalFormula;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chemistry/formulas")
@Tag(name = "Chemical Formulas", description = "Chemical formula parsing, element contribution, and stoichiometry analysis")
@SecurityRequirement(name = "bearerAuth")
public class ChemicalFormulaController {

    private final ChemicalFormulaService formulaService;

    public ChemicalFormulaController(ChemicalFormulaService formulaService) {
        this.formulaService = formulaService;
    }

    @PostMapping("/parse")
    @Operation(summary = "Parse chemical formula", description = "Parse raw chemical formula (e.g. CuSO4·5H2O, Fe3+, (NH4)2SO4) into structured element counts, charges, and hydrate stoichiometry.")
    public ChemicalFormula parseFormula(@Valid @RequestBody ParseFormulaRequest request) {
        return formulaService.parseFormula(request.formula());
    }

    public record ParseFormulaRequest(@NotBlank(message = "Formula string must not be blank") String formula) {}
}
