package com.ailab.chemistry.controller;

import com.ailab.chemistry.api.ReactionKineticsService;
import com.ailab.chemistry.domain.kinetics.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chemistry/kinetics")
@Tag(name = "Reaction Kinetics", description = "Reaction rate laws, Arrhenius temperature dependence, integrated rate laws, and kinetic progress simulation")
@SecurityRequirement(name = "bearerAuth")
public class KineticsController {

    private final ReactionKineticsService kineticsService;

    public KineticsController(ReactionKineticsService kineticsService) {
        this.kineticsService = kineticsService;
    }

    @PostMapping("/rate")
    @Operation(summary = "Calculate reaction rate", description = "Compute reaction rate from reactant concentrations and rate constant r = k · [A]^m · [B]^n.")
    public RateEvaluationResult calculateRate(@Valid @RequestBody RateEvaluationRequest request) {
        return kineticsService.calculateRate(request);
    }

    @PostMapping("/integrated-law")
    @Operation(summary = "Calculate integrated rate law concentration", description = "Compute concentration at time t using 0th, 1st, or 2nd order integrated rate laws.")
    public IntegratedRateLawResult calculateIntegratedLaw(@Valid @RequestBody IntegratedRateLawRequest request) {
        return kineticsService.calculateIntegratedLaw(request);
    }

    @PostMapping("/half-life")
    @Operation(summary = "Calculate reaction half-life", description = "Compute reaction half-life t1/2 for specified reaction order and initial concentration.")
    public HalfLifeResult calculateHalfLife(@Valid @RequestBody IntegratedRateLawRequest request) {
        return kineticsService.calculateHalfLife(request);
    }

    @PostMapping("/arrhenius")
    @Operation(summary = "Calculate Arrhenius rate constant", description = "Compute temperature-dependent rate constant k(T) = A · exp(-Ea / (R·T)).")
    public ArrheniusResult calculateArrhenius(@Valid @RequestBody ArrheniusRequest request) {
        return kineticsService.calculateRateConstant(request);
    }

    @PostMapping("/progress")
    @Operation(summary = "Simulate kinetic progress over time", description = "Simulate reactant depletion and product accumulation over a discrete time grid.")
    public KineticProgressResult simulateProgress(@Valid @RequestBody KineticProgressRequest request) {
        return kineticsService.simulateProgress(request);
    }
}
