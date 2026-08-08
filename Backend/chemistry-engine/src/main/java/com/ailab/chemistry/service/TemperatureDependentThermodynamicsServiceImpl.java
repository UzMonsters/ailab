package com.ailab.chemistry.service;

import com.ailab.chemistry.api.ReactionCatalogService;
import com.ailab.chemistry.api.ReactionDetails;
import com.ailab.chemistry.api.ReactionThermodynamicsService;
import com.ailab.chemistry.api.TemperatureDependentThermodynamicsService;
import com.ailab.chemistry.api.ThermodynamicReferenceService;
import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.equation.RationalNumber;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.measurement.ScientificMath;
import com.ailab.chemistry.domain.reaction.ReactionSide;
import com.ailab.chemistry.domain.reaction.ReactionSpeciesState;
import com.ailab.chemistry.domain.thermodynamics.HeatCapacityCorrelation;
import com.ailab.chemistry.domain.thermodynamics.ReactionTemperatureCorrectionResult;
import com.ailab.chemistry.domain.thermodynamics.ReactionThermodynamicCoverage;
import com.ailab.chemistry.domain.thermodynamics.ReactionThermodynamicProperty;
import com.ailab.chemistry.domain.thermodynamics.ReactionThermodynamicResultProperty;
import com.ailab.chemistry.domain.thermodynamics.ReactionThermodynamicStatus;
import com.ailab.chemistry.domain.thermodynamics.ReactionThermodynamicsResult;
import com.ailab.chemistry.domain.thermodynamics.SpeciesTemperatureCorrection;
import com.ailab.chemistry.domain.thermodynamics.StandardStateConvention;
import com.ailab.chemistry.domain.thermodynamics.TemperatureCorrectionCoverage;
import com.ailab.chemistry.domain.thermodynamics.TemperatureCorrectionErrorCode;
import com.ailab.chemistry.domain.thermodynamics.TemperatureCorrectionException;
import com.ailab.chemistry.domain.thermodynamics.TemperatureCorrectionMethod;
import com.ailab.chemistry.domain.thermodynamics.TemperatureCorrectionStatus;
import com.ailab.chemistry.domain.thermodynamics.TemperatureCorrelationRepository;
import com.ailab.chemistry.domain.thermodynamics.TemperatureDependentPropertyResult;
import com.ailab.chemistry.domain.thermodynamics.TemperatureDependentThermodynamicsCalculator;
import com.ailab.chemistry.domain.thermodynamics.ThermodynamicPropertyType;
import com.ailab.chemistry.domain.thermodynamics.ThermodynamicReferenceConditions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TemperatureDependentThermodynamicsServiceImpl implements TemperatureDependentThermodynamicsService {
    private static final Temperature REFERENCE_TEMPERATURE = Temperature.of("298.15", TemperatureUnit.KELVIN);

    private final ReactionCatalogService reactionCatalogService;
    private final ThermodynamicReferenceService thermodynamicReferenceService;
    private final ReactionThermodynamicsService reactionThermodynamicsService;
    private final TemperatureCorrelationRepository temperatureCorrelationRepository;
    private final TemperatureDependentThermodynamicsCalculator calculator;

    public TemperatureDependentThermodynamicsServiceImpl(ReactionCatalogService reactionCatalogService,
                                                         ThermodynamicReferenceService thermodynamicReferenceService,
                                                         ReactionThermodynamicsService reactionThermodynamicsService,
                                                         TemperatureCorrelationRepository temperatureCorrelationRepository) {
        this.reactionCatalogService = Objects.requireNonNull(reactionCatalogService, "reactionCatalogService must not be null");
        this.thermodynamicReferenceService = Objects.requireNonNull(thermodynamicReferenceService, "thermodynamicReferenceService must not be null");
        this.reactionThermodynamicsService = Objects.requireNonNull(reactionThermodynamicsService, "reactionThermodynamicsService must not be null");
        this.temperatureCorrelationRepository = Objects.requireNonNull(temperatureCorrelationRepository, "temperatureCorrelationRepository must not be null");
        this.calculator = new TemperatureDependentThermodynamicsCalculator();
    }

    @Override
    public TemperatureDependentPropertyResult calculateSpeciesProperties(String compoundCode, MatterState state, Temperature targetTemperature) {
        HeatCapacityCorrelation correlation = temperatureCorrelationRepository.find(compoundCode, state, targetTemperature)
                .orElseThrow(() -> new TemperatureCorrectionException(TemperatureCorrectionErrorCode.MISSING_CORRELATION,
                        "Missing temperature correlation for " + compoundCode + "|" + state));
        BigDecimal referenceEntropy = referenceEntropy(compoundCode, state);
        return calculator.calculateSpecies(correlation, targetTemperature, referenceEntropy);
    }

    @Override
    public ReactionTemperatureCorrectionResult calculateReaction(String reactionCode, Temperature targetTemperature,
                                                                 Pressure pressure, Map<String, MatterState> stateOverrides) {
        Objects.requireNonNull(reactionCode, "reactionCode must not be null");
        Objects.requireNonNull(targetTemperature, "targetTemperature must not be null");
        Objects.requireNonNull(pressure, "pressure must not be null");
        Map<String, MatterState> overrides = stateOverrides == null ? Map.of() : Map.copyOf(stateOverrides);
        ReactionDetails reaction = reactionCatalogService.getByCode(reactionCode);
        ReactionThermodynamicsResult baseline = baseline(reactionCode, pressure, overrides);
        if (baseline.status() != ReactionThermodynamicStatus.CALCULABLE) {
            TemperatureCorrectionCoverage coverage = coverageForReactionTerms(reaction, targetTemperature, overrides, baseline.coverage());
            return incomplete(reactionCode, targetTemperature, pressure, coverage, baseline,
                    "Reference thermodynamic coverage is incomplete; no temperature correction was attempted.");
        }
        if (targetTemperature.equals(REFERENCE_TEMPERATURE)) {
            return new ReactionTemperatureCorrectionResult(reactionCode, targetTemperature, pressure,
                    TemperatureCorrectionStatus.CALCULABLE, TemperatureCorrectionCoverage.complete(reactionCode),
                    baseline.properties(), List.of(), baseline, TemperatureCorrectionMethod.REFERENCE_TEMPERATURE_REGRESSION,
                    "At 298.15 K Phase 8C delegates to the Phase 8B standard reaction calculation.");
        }

        List<String> missingCorrelations = new ArrayList<>();
        List<String> outOfRangeCorrelations = new ArrayList<>();
        List<String> missingPhysicalStates = new ArrayList<>();
        List<String> unsupportedStates = new ArrayList<>();
        List<SpeciesTemperatureCorrection> speciesCorrections = new ArrayList<>();
        BigDecimal enthalpyIncrement = BigDecimal.ZERO;
        BigDecimal entropyIncrement = BigDecimal.ZERO;
        BigDecimal heatCapacity = BigDecimal.ZERO;

        for (ReactionDetails.TermDetails term : reaction.terms()) {
            MatterState state = resolveState(term, overrides);
            RationalNumber signedCoefficient = signedCoefficient(term);
            if (state == MatterState.UNKNOWN) {
                missingPhysicalStates.add(term.compoundCode());
                continue;
            }
            if (state == null) {
                unsupportedStates.add(term.compoundCode() + "|" + term.speciesState());
                continue;
            }
            Optional<HeatCapacityCorrelation> correlation = temperatureCorrelationRepository.find(term.compoundCode(), state, targetTemperature);
            if (correlation.isEmpty()) {
                if (hasPhaseCorrelation(term.compoundCode(), state)) {
                    outOfRangeCorrelations.add(term.compoundCode() + "|" + state.name());
                    continue;
                }
                missingCorrelations.add(term.compoundCode() + "|" + state.name());
                continue;
            }
            TemperatureDependentPropertyResult species = calculator.calculateSpecies(
                    correlation.orElseThrow(), targetTemperature, referenceEntropy(term.compoundCode(), state));
            BigDecimal hContribution = scale(species.enthalpyIncrementKjPerMol(), signedCoefficient);
            BigDecimal sContribution = scale(species.entropyIncrementJPerMolKelvin(), signedCoefficient);
            BigDecimal cpContribution = scale(species.heatCapacityJPerMolKelvin(), signedCoefficient);
            enthalpyIncrement = enthalpyIncrement.add(hContribution, ScientificMath.CALCULATION_CONTEXT);
            entropyIncrement = entropyIncrement.add(sContribution, ScientificMath.CALCULATION_CONTEXT);
            heatCapacity = heatCapacity.add(cpContribution, ScientificMath.CALCULATION_CONTEXT);
            speciesCorrections.add(new SpeciesTemperatureCorrection(term.compoundCode(), state, signedCoefficient,
                    species, hContribution.stripTrailingZeros(), sContribution.stripTrailingZeros(),
                    cpContribution.stripTrailingZeros(), species.provenance()));
        }

        if (!missingCorrelations.isEmpty() || !outOfRangeCorrelations.isEmpty()
                || !missingPhysicalStates.isEmpty() || !unsupportedStates.isEmpty()) {
            TemperatureCorrectionCoverage coverage = TemperatureCorrectionCoverage.incomplete(reactionCode,
                    missingCorrelations.stream().distinct().sorted().toList(),
                    outOfRangeCorrelations.stream().distinct().sorted().toList(),
                    missingPhysicalStates.stream().distinct().sorted().toList(),
                    unsupportedStates.stream().distinct().sorted().toList());
            return incomplete(reactionCode, targetTemperature, pressure, coverage, baseline,
                    "Temperature-correlation coverage is incomplete; no missing term was defaulted.");
        }

        EnumMap<ReactionThermodynamicProperty, ReactionThermodynamicResultProperty> corrected =
                new EnumMap<>(ReactionThermodynamicProperty.class);
        BigDecimal h = baseline.property(ReactionThermodynamicProperty.STANDARD_REACTION_ENTHALPY).value()
                .add(enthalpyIncrement, ScientificMath.CALCULATION_CONTEXT).stripTrailingZeros();
        BigDecimal s = baseline.property(ReactionThermodynamicProperty.STANDARD_REACTION_ENTROPY).value()
                .add(entropyIncrement, ScientificMath.CALCULATION_CONTEXT).stripTrailingZeros();
        BigDecimal tKelvin = targetTemperature.in(TemperatureUnit.KELVIN);
        BigDecimal g = h.subtract(tKelvin.multiply(s, ScientificMath.CALCULATION_CONTEXT)
                        .divide(new BigDecimal("1000"), ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT)
                .stripTrailingZeros();
        corrected.put(ReactionThermodynamicProperty.STANDARD_REACTION_ENTHALPY,
                new ReactionThermodynamicResultProperty(ReactionThermodynamicProperty.STANDARD_REACTION_ENTHALPY, h, "kJ/mol"));
        corrected.put(ReactionThermodynamicProperty.STANDARD_REACTION_ENTROPY,
                new ReactionThermodynamicResultProperty(ReactionThermodynamicProperty.STANDARD_REACTION_ENTROPY, s, "J/(mol*K)"));
        corrected.put(ReactionThermodynamicProperty.STANDARD_REACTION_GIBBS_ENERGY,
                new ReactionThermodynamicResultProperty(ReactionThermodynamicProperty.STANDARD_REACTION_GIBBS_ENERGY, g, "kJ/mol"));
        corrected.put(ReactionThermodynamicProperty.STANDARD_REACTION_HEAT_CAPACITY,
                new ReactionThermodynamicResultProperty(ReactionThermodynamicProperty.STANDARD_REACTION_HEAT_CAPACITY,
                        heatCapacity.stripTrailingZeros(), "J/(mol*K)"));

        return new ReactionTemperatureCorrectionResult(reactionCode, targetTemperature, pressure,
                TemperatureCorrectionStatus.CALCULABLE, TemperatureCorrectionCoverage.complete(reactionCode),
                corrected, speciesCorrections, baseline, TemperatureCorrectionMethod.SHOMATE_TEMPERATURE_CORRECTION,
                "Reaction properties corrected by explicit phase-specific Shomate species increments.");
    }

    @Override
    public List<TemperatureCorrectionCoverage> evaluateCoverage(Temperature targetTemperature) {
        return reactionCatalogService.listReactions().stream()
                .sorted(Comparator.comparing(summary -> summary.reactionCode()))
                .map(summary -> calculateReaction(summary.reactionCode(), targetTemperature,
                        com.ailab.chemistry.domain.measurement.Pressure.of("1.000",
                                com.ailab.chemistry.domain.measurement.PressureUnit.BAR), Map.of()).coverage())
                .toList();
    }

    private ReactionThermodynamicsResult baseline(String reactionCode, Pressure pressure, Map<String, MatterState> stateOverrides) {
        return reactionThermodynamicsService.calculate(reactionCode,
                new ThermodynamicReferenceConditions(REFERENCE_TEMPERATURE, pressure, MatterState.GAS,
                        StandardStateConvention.IDEAL_GAS_STANDARD_STATE), stateOverrides);
    }

    private BigDecimal referenceEntropy(String compoundCode, MatterState state) {
        return thermodynamicReferenceService.findExact(compoundCode, ThermodynamicPropertyType.STANDARD_MOLAR_ENTROPY,
                        state, REFERENCE_TEMPERATURE, com.ailab.chemistry.domain.measurement.Pressure.of("1.000",
                                com.ailab.chemistry.domain.measurement.PressureUnit.BAR))
                .orElseThrow(() -> new TemperatureCorrectionException(TemperatureCorrectionErrorCode.MISSING_CORRELATION,
                        "Missing reference entropy for " + compoundCode + "|" + state))
                .value();
    }

    private TemperatureCorrectionCoverage coverageForReactionTerms(ReactionDetails reaction, Temperature targetTemperature,
                                                                  Map<String, MatterState> overrides,
                                                                  ReactionThermodynamicCoverage baselineCoverage) {
        List<String> missingCorrelations = new ArrayList<>();
        List<String> outOfRangeCorrelations = new ArrayList<>();
        List<String> missingPhysicalStates = new ArrayList<>(baselineCoverage.missingPhysicalStates());
        List<String> unsupportedStates = new ArrayList<>(baselineCoverage.unsupportedStates());
        for (ReactionDetails.TermDetails term : reaction.terms()) {
            MatterState state = resolveState(term, overrides);
            if (state == MatterState.UNKNOWN) {
                missingPhysicalStates.add(term.compoundCode());
            } else if (state == null) {
                unsupportedStates.add(term.compoundCode() + "|" + term.speciesState());
            } else if (temperatureCorrelationRepository.find(term.compoundCode(), state, targetTemperature).isEmpty()) {
                if (hasPhaseCorrelation(term.compoundCode(), state)) {
                    outOfRangeCorrelations.add(term.compoundCode() + "|" + state.name());
                } else {
                    missingCorrelations.add(term.compoundCode() + "|" + state.name());
                }
            }
        }
        missingCorrelations.addAll(baselineCoverage.missingPhaseSpecificRecords());
        return TemperatureCorrectionCoverage.incomplete(reaction.reactionCode(),
                missingCorrelations.stream().distinct().sorted().toList(),
                outOfRangeCorrelations.stream().distinct().sorted().toList(),
                missingPhysicalStates.stream().distinct().sorted().toList(),
                unsupportedStates.stream().distinct().sorted().toList());
    }

    private boolean hasPhaseCorrelation(String compoundCode, MatterState state) {
        return temperatureCorrelationRepository.findAll().stream()
                .anyMatch(correlation -> correlation.compoundCode().equalsIgnoreCase(compoundCode)
                        && correlation.state() == state);
    }

    private static TemperatureCorrectionCoverage fromBaselineCoverage(String reactionCode, ReactionThermodynamicCoverage coverage) {
        return TemperatureCorrectionCoverage.incomplete(reactionCode,
                coverage.missingPhaseSpecificRecords(),
                List.of(),
                coverage.missingPhysicalStates(),
                coverage.unsupportedStates());
    }

    private static ReactionTemperatureCorrectionResult incomplete(String reactionCode, Temperature targetTemperature,
                                                                  Pressure pressure, TemperatureCorrectionCoverage coverage,
                                                                  ReactionThermodynamicsResult baseline, String explanation) {
        return new ReactionTemperatureCorrectionResult(reactionCode, targetTemperature, pressure,
                TemperatureCorrectionStatus.INCOMPLETE_COVERAGE, coverage, Map.of(), List.of(), baseline,
                TemperatureCorrectionMethod.SHOMATE_TEMPERATURE_CORRECTION, explanation);
    }

    private static MatterState resolveState(ReactionDetails.TermDetails term, Map<String, MatterState> overrides) {
        MatterState override = overrides.getOrDefault(term.compoundCode() + "|" + term.termOrder(), overrides.get(term.compoundCode()));
        if (override != null) {
            return override;
        }
        return switch (term.speciesState()) {
            case GAS -> MatterState.GAS;
            case LIQUID -> MatterState.LIQUID;
            case SOLID -> MatterState.SOLID;
            case UNKNOWN -> MatterState.UNKNOWN;
            case AQUEOUS, DISSOLVED, MOLTEN -> null;
        };
    }

    private static RationalNumber signedCoefficient(ReactionDetails.TermDetails term) {
        RationalNumber coefficient = RationalNumber.of(term.coefficient());
        return term.side() == ReactionSide.REACTANT ? coefficient.negate() : coefficient;
    }

    private static BigDecimal scale(BigDecimal value, RationalNumber multiplier) {
        return value.multiply(new BigDecimal(multiplier.getNumerator()), ScientificMath.CALCULATION_CONTEXT)
                .divide(new BigDecimal(multiplier.getDenominator()), ScientificMath.CALCULATION_CONTEXT);
    }
}
