package com.ailab.chemistry.controller;

import com.ailab.chemistry.api.*;
import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.PressureUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.thermodynamics.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/chemistry/thermodynamics")
@Tag(name = "Thermodynamics & Calorimetry", description = "Thermodynamic profiles, reaction thermodynamics, Hess law calculations, and calorimetry")
@SecurityRequirement(name = "bearerAuth")
public class ThermodynamicsController {

    private final ThermodynamicReferenceService referenceService;
    private final ReactionThermodynamicsService reactionThermodynamicsService;
    private final CalorimetryService calorimetryService;

    public ThermodynamicsController(
            ThermodynamicReferenceService referenceService,
            ReactionThermodynamicsService reactionThermodynamicsService,
            CalorimetryService calorimetryService) {
        this.referenceService = referenceService;
        this.reactionThermodynamicsService = reactionThermodynamicsService;
        this.calorimetryService = calorimetryService;
    }

    @GetMapping("/reference/{compoundCode}")
    @Operation(summary = "Get thermodynamic profile", description = "Retrieve standard thermodynamic profile (enthalpy of formation, entropy, Gibbs energy) for a compound code.")
    public ThermodynamicProfileDetails getProfile(@PathVariable String compoundCode) {
        return referenceService.getProfile(compoundCode);
    }

    @PostMapping("/calculate")
    @Operation(summary = "Calculate reaction thermodynamics", description = "Calculate standard or non-standard reaction enthalpy, entropy, Gibbs free energy, and equilibrium constant.")
    public ReactionThermodynamicsResult calculateReactionThermodynamics(@Valid @RequestBody ReactionThermodynamicsRequest request) {
        ThermodynamicReferenceConditions conditions = request.conditions() != null
                ? request.conditions()
                : new ThermodynamicReferenceConditions(
                Temperature.of(new BigDecimal("298.15"), TemperatureUnit.KELVIN),
                Pressure.of(new BigDecimal("100000"), PressureUnit.PASCAL),
                MatterState.LIQUID,
                StandardStateConvention.PURE_SUBSTANCE_STANDARD_STATE
        );
        return reactionThermodynamicsService.calculate(request.reactionCode(), conditions, request.stateOverrides());
    }

    @PostMapping("/hess-law")
    @Operation(summary = "Calculate Hess's Law reaction enthalpy", description = "Compute reaction enthalpy using Hess's Law from constituent target reaction steps.")
    public HessLawResult calculateHessLaw(@Valid @RequestBody HessLawRequest request) {
        return reactionThermodynamicsService.calculateHessLaw(request);
    }

    @PostMapping("/calorimetry/sensible-heat")
    @Operation(summary = "Calculate sensible heat transfer", description = "Compute heat required or released during temperature change Q = m·Cp·ΔT.")
    public SensibleHeatResult calculateSensibleHeat(@Valid @RequestBody SensibleHeatRequest request) {
        return calorimetryService.calculateSensibleHeat(request);
    }

    @PostMapping("/calorimetry/thermal-mixing")
    @Operation(summary = "Calculate thermal mixing final temperature", description = "Compute final equilibrium temperature resulting from mixing substances at different initial temperatures.")
    public ThermalMixingResult calculateThermalMixing(@Valid @RequestBody ThermalMixingRequest request) {
        return calorimetryService.calculateFinalTemperature(request);
    }

    @PostMapping("/calorimetry/reaction-heat")
    @Operation(summary = "Calculate reaction heat calorimetry", description = "Compute total heat released or absorbed during calorimetry experiment execution.")
    public ReactionCalorimetryResult calculateReactionHeat(@Valid @RequestBody ReactionCalorimetryRequest request) {
        return calorimetryService.calculateReactionHeat(request);
    }

    public record ReactionThermodynamicsRequest(
            String reactionCode,
            ThermodynamicReferenceConditions conditions,
            Map<String, MatterState> stateOverrides
    ) {}
}
