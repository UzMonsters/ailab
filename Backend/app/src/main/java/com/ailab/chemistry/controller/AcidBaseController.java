package com.ailab.chemistry.controller;

import com.ailab.chemistry.api.AcidBaseEquilibriumService;
import com.ailab.chemistry.api.BufferCalculationService;
import com.ailab.chemistry.api.PolyproticTitrationService;
import com.ailab.chemistry.api.TitrationCalculationService;
import com.ailab.chemistry.domain.acidbase.*;
import com.ailab.chemistry.domain.measurement.MolarConcentration;
import com.ailab.chemistry.domain.measurement.MolarConcentrationUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/chemistry/acid-base")
@Tag(name = "Acid/Base & Titrations", description = "Acid-base equilibrium, pH, buffer calculations, and monoprotic/polyprotic titration curves")
@SecurityRequirement(name = "bearerAuth")
public class AcidBaseController {

    private final AcidBaseEquilibriumService equilibriumService;
    private final BufferCalculationService bufferService;
    private final TitrationCalculationService titrationService;
    private final PolyproticTitrationService polyproticTitrationService;

    public AcidBaseController(
            AcidBaseEquilibriumService equilibriumService,
            BufferCalculationService bufferService,
            TitrationCalculationService titrationService,
            PolyproticTitrationService polyproticTitrationService) {
        this.equilibriumService = equilibriumService;
        this.bufferService = bufferService;
        this.titrationService = titrationService;
        this.polyproticTitrationService = polyproticTitrationService;
    }

    @PostMapping("/water")
    @Operation(summary = "Calculate pure water autoionization", description = "Calculate pH and autoionization equilibrium Kw of pure water at specified temperature.")
    public AcidBaseResponse calculateWater(@RequestBody(required = false) TemperatureRequest request) {
        Temperature temp = (request != null && request.temperatureKelvin() != null)
                ? Temperature.of(request.temperatureKelvin(), TemperatureUnit.KELVIN)
                : Temperature.of("25.0", TemperatureUnit.CELSIUS);
        return AcidBaseResponse.from(equilibriumService.calculatePureWater(temp));
    }

    @PostMapping("/strong-acid")
    @Operation(summary = "Calculate strong acid pH", description = "Calculate pH and ion concentrations for a strong acid solution (e.g. HCl, HNO3).")
    public AcidBaseResponse calculateStrongAcid(@Valid @RequestBody AcidBaseRequest request) {
        return AcidBaseResponse.from(equilibriumService.calculateStrongAcid(request.speciesCode(), MolarConcentration.of(request.concentrationMolar(), MolarConcentrationUnit.MOL_PER_LITER), getTemperature(request)));
    }

    @PostMapping("/strong-base")
    @Operation(summary = "Calculate strong base pH", description = "Calculate pH and ion concentrations for a strong base solution (e.g. NaOH, KOH).")
    public AcidBaseResponse calculateStrongBase(@Valid @RequestBody AcidBaseRequest request) {
        return AcidBaseResponse.from(equilibriumService.calculateStrongBase(request.speciesCode(), MolarConcentration.of(request.concentrationMolar(), MolarConcentrationUnit.MOL_PER_LITER), getTemperature(request)));
    }

    @PostMapping("/weak-acid")
    @Operation(summary = "Calculate weak acid pH", description = "Calculate pH and ion concentrations for a weak acid solution using Ka dissociation equilibrium.")
    public AcidBaseResponse calculateWeakAcid(@Valid @RequestBody AcidBaseRequest request) {
        return AcidBaseResponse.from(equilibriumService.calculateWeakAcid(request.speciesCode(), MolarConcentration.of(request.concentrationMolar(), MolarConcentrationUnit.MOL_PER_LITER), getTemperature(request)));
    }

    @PostMapping("/weak-base")
    @Operation(summary = "Calculate weak base pH", description = "Calculate pH and ion concentrations for a weak base solution using Kb dissociation equilibrium.")
    public AcidBaseResponse calculateWeakBase(@Valid @RequestBody AcidBaseRequest request) {
        return AcidBaseResponse.from(equilibriumService.calculateWeakBase(request.speciesCode(), MolarConcentration.of(request.concentrationMolar(), MolarConcentrationUnit.MOL_PER_LITER), getTemperature(request)));
    }

    @PostMapping("/salt-hydrolysis")
    @Operation(summary = "Calculate salt hydrolysis pH", description = "Calculate pH and equilibrium concentrations for hydrolyzing salt solutions (e.g. NH4Cl, CH3COONa).")
    public AcidBaseResponse calculateSaltHydrolysis(@Valid @RequestBody AcidBaseRequest request) {
        return AcidBaseResponse.from(equilibriumService.calculateSaltHydrolysis(request.speciesCode(), MolarConcentration.of(request.concentrationMolar(), MolarConcentrationUnit.MOL_PER_LITER), getTemperature(request)));
    }

    @PostMapping("/buffer")
    @Operation(summary = "Calculate buffer capacity & pH", description = "Calculate buffer system pH and buffer capacity (beta) using Henderson-Hasselbalch equations.")
    public BufferCalculationResult calculateBuffer(@Valid @RequestBody BufferCalculationRequest request) {
        return bufferService.calculateBuffer(request);
    }

    @PostMapping("/buffer/preparation")
    @Operation(summary = "Calculate buffer preparation recipe", description = "Compute required masses/volumes of conjugate acid/base components to prepare a buffer at target pH.")
    public BufferPreparationResult calculateBufferPreparation(@Valid @RequestBody BufferPreparationRequest request) {
        return bufferService.calculatePreparation(request);
    }

    @PostMapping("/buffer/perturbation")
    @Operation(summary = "Calculate buffer acid/base perturbation", description = "Calculate pH change when strong acid or base is added to an existing buffer system.")
    public BufferPerturbationResult addStrongAcidOrBase(@Valid @RequestBody BufferPerturbationRequest request) {
        return bufferService.addStrongAcidOrBase(request);
    }

    @PostMapping("/titration/characteristic-points")
    @Operation(summary = "Calculate monoprotic titration curve points", description = "Compute key characteristic points (initial, half-equivalence, equivalence, excess titrant) on a monoprotic titration curve.")
    public TitrationCurveResult calculateTitrationPoints(@Valid @RequestBody TitrationRequest request) {
        return titrationService.calculateCharacteristicPoints(request);
    }

    @PostMapping("/polyprotic-titration/characteristic-points")
    @Operation(summary = "Calculate polyprotic titration curve points", description = "Compute characteristic equivalence points for multi-stage polyprotic acid/base titrations.")
    public PolyproticTitrationCurveResult calculatePolyproticTitrationPoints(@Valid @RequestBody PolyproticTitrationRequest request) {
        return polyproticTitrationService.calculateCharacteristicPoints(request);
    }

    private static Temperature getTemperature(AcidBaseRequest request) {
        return request.temperatureKelvin() != null ? Temperature.of(request.temperatureKelvin(), TemperatureUnit.KELVIN) : Temperature.of("25.0", TemperatureUnit.CELSIUS);
    }

    public record TemperatureRequest(BigDecimal temperatureKelvin) {}
    public record AcidBaseRequest(String speciesCode, BigDecimal concentrationMolar, BigDecimal temperatureKelvin) {}

    public record AcidBaseResponse(
            String systemType,
            BigDecimal ph,
            BigDecimal poh,
            BigDecimal hydroniumConcentration,
            BigDecimal hydroxideConcentration,
            BigDecimal kw,
            BigDecimal pKw,
            BigDecimal kActive,
            String calculationMethod,
            String solverStatus
    ) {
        public static AcidBaseResponse from(AcidBaseEquilibriumResult result) {
            return new AcidBaseResponse(
                    result.getSystemType().name(),
                    result.getPh().getValue(),
                    result.getPoh().getValue(),
                    result.getHydroniumConcentration().getValue(),
                    result.getHydroxideConcentration().getValue(),
                    result.getKw(),
                    result.getPKw(),
                    result.getKActive().orElse(null),
                    result.getCalculationMethod().name(),
                    result.getSolverStatus().name()
            );
        }
    }
}
