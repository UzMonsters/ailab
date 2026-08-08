package com.ailab.chemistry.controller;

import com.ailab.chemistry.api.GasLawService;
import com.ailab.chemistry.domain.gas.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chemistry/gas")
@Tag(name = "Gas Laws & Phase Behavior", description = "Ideal gas law state calculations, gas mixture partial pressures, and state transformations")
@SecurityRequirement(name = "bearerAuth")
public class GasPhaseController {

    private final GasLawService gasLawService;

    public GasPhaseController(GasLawService gasLawService) {
        this.gasLawService = gasLawService;
    }

    @PostMapping("/state")
    @Operation(summary = "Calculate ideal gas state", description = "Compute missing thermodynamic variable (P, V, n, or T) using ideal gas law P·V = n·R·T.")
    public GasStateResult calculateState(@Valid @RequestBody GasStateRequest request) {
        return gasLawService.calculateState(request);
    }

    @PostMapping("/mixture")
    @Operation(summary = "Calculate gas mixture partial pressures", description = "Compute total pressure and component partial pressures using Dalton's law of partial pressures.")
    public GasMixtureResult calculateMixture(@Valid @RequestBody GasMixture request) {
        return gasLawService.calculateMixture(request);
    }

    @PostMapping("/transformation")
    @Operation(summary = "Calculate gas state transformation", description = "Compute final state after gas compression, expansion, heating, or cooling (P1·V1/T1 = P2·V2/T2).")
    public GasStateResult calculateTransformation(@Valid @RequestBody GasStateTransformation request) {
        return gasLawService.calculateTransformation(request);
    }
}
