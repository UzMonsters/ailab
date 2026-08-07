package com.ailab.chemistry.service;

import com.ailab.chemistry.api.IonicActivityService;
import com.ailab.chemistry.api.ReactionCatalogService;
import com.ailab.chemistry.api.ReactionThermodynamicsService;
import com.ailab.chemistry.api.TemperatureDependentThermodynamicsService;
import com.ailab.chemistry.api.ThermodynamicEquilibriumService;
import com.ailab.chemistry.domain.acidbase.ActivityException;
import com.ailab.chemistry.domain.acidbase.ActivityModel;
import com.ailab.chemistry.domain.acidbase.IonicSpeciesConcentration;
import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.thermodynamics.ActivityBasis;
import com.ailab.chemistry.domain.thermodynamics.EquilibriumCalculationMethod;
import com.ailab.chemistry.domain.thermodynamics.EquilibriumCalculationStatus;
import com.ailab.chemistry.domain.thermodynamics.EquilibriumConstantResult;
import com.ailab.chemistry.domain.thermodynamics.NonstandardGibbsRequest;
import com.ailab.chemistry.domain.thermodynamics.NonstandardGibbsResult;
import com.ailab.chemistry.domain.thermodynamics.PhaseStabilityStatus;
import com.ailab.chemistry.domain.thermodynamics.ReactionQuotient;
import com.ailab.chemistry.domain.thermodynamics.ReactionTemperatureCorrectionResult;
import com.ailab.chemistry.domain.thermodynamics.ReactionThermodynamicCoverage;
import com.ailab.chemistry.domain.thermodynamics.ReactionThermodynamicProperty;
import com.ailab.chemistry.domain.thermodynamics.ReactionThermodynamicStatus;
import com.ailab.chemistry.domain.thermodynamics.ReactionThermodynamicsResult;
import com.ailab.chemistry.domain.thermodynamics.StandardEquilibriumConstant;
import com.ailab.chemistry.domain.thermodynamics.StandardStateConvention;
import com.ailab.chemistry.domain.thermodynamics.TemperatureCorrectionCoverage;
import com.ailab.chemistry.domain.thermodynamics.TemperatureCorrectionStatus;
import com.ailab.chemistry.domain.thermodynamics.ThermodynamicEquilibriumCalculator;
import com.ailab.chemistry.domain.thermodynamics.ThermodynamicReferenceConditions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class ThermodynamicEquilibriumServiceImpl implements ThermodynamicEquilibriumService {
    private static final Temperature REFERENCE_TEMPERATURE = Temperature.of("298.15", TemperatureUnit.KELVIN);

    private final ReactionCatalogService reactionCatalogService;
    private final ReactionThermodynamicsService reactionThermodynamicsService;
    private final TemperatureDependentThermodynamicsService temperatureDependentThermodynamicsService;
    @SuppressWarnings("unused")
    private final IonicActivityService ionicActivityService;
    private final ThermodynamicEquilibriumCalculator calculator = new ThermodynamicEquilibriumCalculator();

    public ThermodynamicEquilibriumServiceImpl(ReactionCatalogService reactionCatalogService,
                                               ReactionThermodynamicsService reactionThermodynamicsService,
                                               TemperatureDependentThermodynamicsService temperatureDependentThermodynamicsService,
                                               IonicActivityService ionicActivityService) {
        this.reactionCatalogService = Objects.requireNonNull(reactionCatalogService, "reactionCatalogService must not be null");
        this.reactionThermodynamicsService = Objects.requireNonNull(reactionThermodynamicsService, "reactionThermodynamicsService must not be null");
        this.temperatureDependentThermodynamicsService = Objects.requireNonNull(temperatureDependentThermodynamicsService,
                "temperatureDependentThermodynamicsService must not be null");
        this.ionicActivityService = ionicActivityService;
    }

    @Override
    public EquilibriumConstantResult calculateStandardConstant(String reactionCode, Temperature temperature,
                                                               Pressure standardPressure,
                                                               Map<String, MatterState> stateOverrides) {
        Map<String, MatterState> overrides = stateOverrides == null ? Map.of() : Map.copyOf(stateOverrides);
        PhaseStabilityStatus phaseStatus = overrides.isEmpty()
                ? PhaseStabilityStatus.PHASE_STABILITY_NOT_EVALUATED
                : PhaseStabilityStatus.PRESCRIBED_PHASE_ASSUMPTION;
        if (temperature.equals(REFERENCE_TEMPERATURE)) {
            return phase8b(reactionCode, temperature, standardPressure, overrides, phaseStatus);
        }
        return phase8c(reactionCode, temperature, standardPressure, overrides, phaseStatus);
    }

    @Override
    public NonstandardGibbsResult calculateNonstandardGibbs(NonstandardGibbsRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        TemperatureCorrectionCoverage daviesFailure = validateDaviesActivities(request);
        if (daviesFailure != null) {
            return new NonstandardGibbsResult(request.reactionCode(), EquilibriumCalculationStatus.INCOMPLETE_COVERAGE,
                    null, null, null, null, null, daviesFailure,
                    PhaseStabilityStatus.PHASE_STABILITY_NOT_EVALUATED, EquilibriumCalculationMethod.NONSTANDARD_GIBBS,
                    "AQUEOUS_DAVIES activity validation failed; no standard or nonstandard value was fabricated.");
        }
        EquilibriumConstantResult standard = calculateStandardConstant(request.reactionCode(), request.temperature(),
                request.standardPressure(), request.stateOverrides());
        if (standard.status() != EquilibriumCalculationStatus.CALCULABLE) {
            return new NonstandardGibbsResult(request.reactionCode(), EquilibriumCalculationStatus.INCOMPLETE_COVERAGE,
                    standard, null, null, null, null, standard.coverage(), standard.phaseStabilityStatus(),
                    EquilibriumCalculationMethod.NONSTANDARD_GIBBS,
                    "Standard thermodynamic reaction value is unavailable; nonstandard Gibbs energy was not calculated.");
        }
        if (request.activityInput().activities().stream().anyMatch(activity ->
                activity.basis() == ActivityBasis.AQUEOUS_DAVIES || activity.basis() == ActivityBasis.AQUEOUS_IDEAL)) {
            return new NonstandardGibbsResult(request.reactionCode(), EquilibriumCalculationStatus.INCOMPLETE_COVERAGE,
                    standard, null, standard.deltaGibbsStandardKjPerMol(), null, null,
                    TemperatureCorrectionCoverage.incomplete(request.reactionCode(), List.of(), List.of(), List.of(),
                            List.of("AQUEOUS_STANDARD_STATE_NOT_AVAILABLE")),
                    standard.phaseStabilityStatus(), EquilibriumCalculationMethod.NONSTANDARD_GIBBS,
                    "Aqueous activities were supplied, but no aqueous standard-state reaction thermodynamics are available.");
        }

        ReactionQuotient quotient = calculator.reactionQuotient(standard.reactionVector(), request.activityInput());
        NonstandardGibbsResult domain = calculator.nonstandardGibbs(standard.standardConstant(), quotient, request.temperature());
        return new NonstandardGibbsResult(request.reactionCode(), EquilibriumCalculationStatus.CALCULABLE, standard, quotient,
                standard.deltaGibbsStandardKjPerMol(), domain.deltaGibbsKjPerMol(), domain.direction(),
                standard.coverage(), standard.phaseStabilityStatus(), EquilibriumCalculationMethod.NONSTANDARD_GIBBS,
                "Delta G = Delta G standard + R*T*ln(Q); direction is thermodynamic only, not kinetic.");
    }

    private EquilibriumConstantResult phase8b(String reactionCode, Temperature temperature, Pressure standardPressure,
                                              Map<String, MatterState> overrides, PhaseStabilityStatus phaseStatus) {
        ReactionThermodynamicsResult reaction = reactionThermodynamicsService.calculate(reactionCode,
                new ThermodynamicReferenceConditions(temperature, standardPressure, MatterState.GAS,
                        StandardStateConvention.IDEAL_GAS_STANDARD_STATE), overrides);
        if (reaction.status() != ReactionThermodynamicStatus.CALCULABLE) {
            return incomplete(reactionCode, temperature, standardPressure, fromReactionCoverage(reactionCode, reaction.coverage()),
                    phaseStatus, EquilibriumCalculationMethod.STANDARD_GIBBS_PHASE8B);
        }
        BigDecimal deltaG = reaction.property(ReactionThermodynamicProperty.STANDARD_REACTION_GIBBS_ENERGY).value();
        StandardEquilibriumConstant constant = calculator.standardConstant(deltaG, temperature, phaseStatus);
        return new EquilibriumConstantResult(reactionCode, temperature, standardPressure,
                EquilibriumCalculationStatus.CALCULABLE, constant, deltaG, reaction.reactionVector(),
                TemperatureCorrectionCoverage.complete(reactionCode), phaseStatus,
                EquilibriumCalculationMethod.STANDARD_GIBBS_PHASE8B,
                "K standard calculated from Phase 8B standard reaction Gibbs energy.");
    }

    private EquilibriumConstantResult phase8c(String reactionCode, Temperature temperature, Pressure standardPressure,
                                              Map<String, MatterState> overrides, PhaseStabilityStatus phaseStatus) {
        ReactionTemperatureCorrectionResult reaction = temperatureDependentThermodynamicsService.calculateReaction(
                reactionCode, temperature, standardPressure, overrides);
        if (reaction.status() != TemperatureCorrectionStatus.CALCULABLE) {
            return incomplete(reactionCode, temperature, standardPressure, reaction.coverage(), phaseStatus,
                    EquilibriumCalculationMethod.STANDARD_GIBBS_PHASE8C);
        }
        BigDecimal deltaG = reaction.reactionProperties().get(ReactionThermodynamicProperty.STANDARD_REACTION_GIBBS_ENERGY).value();
        StandardEquilibriumConstant constant = calculator.standardConstant(deltaG, temperature, phaseStatus);
        return new EquilibriumConstantResult(reactionCode, temperature, standardPressure,
                EquilibriumCalculationStatus.CALCULABLE, constant, deltaG, reaction.referenceResult().reactionVector(),
                TemperatureCorrectionCoverage.complete(reactionCode), phaseStatus,
                EquilibriumCalculationMethod.STANDARD_GIBBS_PHASE8C,
                "K standard calculated from Phase 8C temperature-corrected standard Gibbs energy.");
    }

    private EquilibriumConstantResult incomplete(String reactionCode, Temperature temperature, Pressure standardPressure,
                                                 TemperatureCorrectionCoverage coverage, PhaseStabilityStatus phaseStatus,
                                                 EquilibriumCalculationMethod method) {
        return new EquilibriumConstantResult(reactionCode, temperature, standardPressure,
                EquilibriumCalculationStatus.INCOMPLETE_COVERAGE, null, null, null, coverage, phaseStatus, method,
                "Incomplete thermodynamic coverage; no equilibrium constant was fabricated.");
    }

    private static TemperatureCorrectionCoverage fromReactionCoverage(String reactionCode, ReactionThermodynamicCoverage coverage) {
        return TemperatureCorrectionCoverage.incomplete(reactionCode, coverage.missingPhaseSpecificRecords(), List.of(),
                coverage.missingPhysicalStates(), coverage.unsupportedStates());
    }

    private TemperatureCorrectionCoverage validateDaviesActivities(NonstandardGibbsRequest request) {
        if (ionicActivityService == null || request.activityInput() == null || request.activityInput().activities().stream()
                .noneMatch(activity -> activity.basis() == ActivityBasis.AQUEOUS_DAVIES)) {
            return null;
        }
        List<IonicSpeciesConcentration> species = request.activityInput().activities().stream()
                .filter(activity -> activity.basis() == ActivityBasis.AQUEOUS_DAVIES)
                .map(activity -> new IonicSpeciesConcentration(
                        activity.speciesCode() == null ? activity.compoundCode() : activity.speciesCode(),
                        activity.concentrationMolPerLiter(),
                        activity.ionicCharge() == null ? 0 : activity.ionicCharge()))
                .toList();
        try {
            ionicActivityService.calculateActivities(species, request.temperature(), "COMP-H2O", ActivityModel.DAVIES);
            return null;
        } catch (ActivityException ex) {
            return TemperatureCorrectionCoverage.incomplete(request.reactionCode(), List.of(), List.of(), List.of(),
                    List.of("AQUEOUS_DAVIES_VALIDITY_FAILURE|" + ex.getErrorCode()));
        }
    }
}
