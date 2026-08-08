package com.ailab.chemistry.controller;

import com.ailab.chemistry.api.ChemicalEquationService;
import com.ailab.chemistry.domain.equation.BalancedEquation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chemistry/equations")
@Tag(name = "Chemical Equations", description = "Chemical equation balancing with atom and charge conservation")
@SecurityRequirement(name = "bearerAuth")
public class ChemicalEquationController {

    private final ChemicalEquationService equationService;

    public ChemicalEquationController(ChemicalEquationService equationService) {
        this.equationService = equationService;
    }

    @PostMapping("/balance")
    @Operation(summary = "Balance chemical equation", description = "Automatically balance an unbalanced chemical equation (e.g. KMnO4 + HCl -> KCl + MnCl2 + H2O + Cl2) conserving mass and electrical charge.")
    public BalancedEquation balanceEquation(@Valid @RequestBody BalanceEquationRequest request) {
        return equationService.balanceEquation(request.equation());
    }

    public record BalanceEquationRequest(@NotBlank(message = "Equation string must not be blank") String equation) {}
}
