package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.equation.RationalNumber;
import com.ailab.chemistry.domain.measurement.ScientificMath;
import com.ailab.chemistry.domain.reaction.ReactionSide;
import com.ailab.chemistry.domain.reaction.ReactionSpeciesState;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class ReactionThermodynamicsCalculator {

    public ReactionThermodynamicsResult calculate(ReactionThermodynamicsRequest request) {
        List<String> missingPhysicalStates = new ArrayList<>();
        List<String> unsupportedStates = new ArrayList<>();
        List<String> missingRecords = new ArrayList<>();
        List<ReactionThermodynamicVectorTerm> vectorTerms = new ArrayList<>();

        for (ReactionThermodynamicRequestTerm term : request.terms()) {
            MatterState state = toMatterState(term.speciesState());
            RationalNumber signedCoefficient = signedCoefficient(term);
            if (state == MatterState.UNKNOWN) {
                missingPhysicalStates.add(term.compoundCode());
                continue;
            }
            if (state == null) {
                unsupportedStates.add(term.compoundCode() + "|" + term.speciesState());
                missingRecords.add(term.compoundCode() + "|" + term.speciesState().name());
                continue;
            }
            vectorTerms.add(new ReactionThermodynamicVectorTerm(term.compoundCode(), state, signedCoefficient));
            if (!request.recordSets().containsKey(recordKey(term.compoundCode(), state))) {
                missingRecords.add(term.compoundCode() + "|" + term.speciesState().name());
            }
        }

        ReactionThermodynamicVector vector = ReactionThermodynamicVector.of(vectorTerms);
        if (!missingPhysicalStates.isEmpty() || !unsupportedStates.isEmpty() || !missingRecords.isEmpty()) {
            return incomplete(request, missingRecords, missingPhysicalStates, unsupportedStates, vector);
        }

        EnumMap<ReactionThermodynamicProperty, BigDecimal> totals = new EnumMap<>(ReactionThermodynamicProperty.class);
        for (ReactionThermodynamicProperty property : ReactionThermodynamicProperty.values()) {
            totals.put(property, BigDecimal.ZERO);
        }
        List<ReactionThermodynamicTerm> resultTerms = new ArrayList<>();

        for (ReactionThermodynamicRequestTerm term : request.terms()) {
            MatterState state = toMatterState(term.speciesState());
            ReactionThermodynamicRecordSet records = request.recordSets().get(recordKey(term.compoundCode(), state));
            validateStandardState(records);
            RationalNumber signedCoefficient = signedCoefficient(term);
            EnumMap<ReactionThermodynamicProperty, ReactionThermodynamicResultProperty> contributions =
                    new EnumMap<>(ReactionThermodynamicProperty.class);

            for (Map.Entry<ReactionThermodynamicProperty, ReactionThermodynamicSourceProperty> entry : records.sourceProperties().entrySet()) {
                BigDecimal contribution = scale(entry.getValue().value(), signedCoefficient).stripTrailingZeros();
                totals.put(entry.getKey(), totals.get(entry.getKey()).add(contribution, ScientificMath.CALCULATION_CONTEXT));
                contributions.put(entry.getKey(), new ReactionThermodynamicResultProperty(entry.getKey(), contribution, entry.getKey().unitSymbol()));
            }

            resultTerms.add(new ReactionThermodynamicTerm(term.compoundCode(), term.formula(), state, signedCoefficient,
                    records.sourceProperties(), contributions, records.sourceProperties().values().stream()
                    .map(ReactionThermodynamicSourceProperty::provenance)
                    .distinct()
                    .toList()));
        }

        EnumMap<ReactionThermodynamicProperty, ReactionThermodynamicResultProperty> properties =
                new EnumMap<>(ReactionThermodynamicProperty.class);
        for (ReactionThermodynamicProperty property : ReactionThermodynamicProperty.values()) {
            properties.put(property, new ReactionThermodynamicResultProperty(property,
                    totals.get(property).stripTrailingZeros(), property.unitSymbol()));
        }

        return new ReactionThermodynamicsResult(request.reactionCode(), request.equation(), ReactionThermodynamicStatus.CALCULABLE,
                ReactionThermodynamicCoverage.complete(request.reactionCode()), properties, resultTerms, vector,
                ThermodynamicCalculationMethod.FORMATION_SUM,
                "Calculated per canonical stoichiometric reaction as written using product-positive and reactant-negative formation sums.");
    }

    private ReactionThermodynamicsResult incomplete(ReactionThermodynamicsRequest request, List<String> missingRecords,
                                                    List<String> missingPhysicalStates, List<String> unsupportedStates,
                                                    ReactionThermodynamicVector vector) {
        ReactionThermodynamicCoverage coverage = ReactionThermodynamicCoverage.incomplete(request.reactionCode(), List.of(), List.of(),
                missingRecords.stream().distinct().sorted().toList(),
                missingPhysicalStates.stream().distinct().sorted().toList(),
                unsupportedStates.stream().distinct().sorted().toList());
        return new ReactionThermodynamicsResult(request.reactionCode(), request.equation(), ReactionThermodynamicStatus.INCOMPLETE_COVERAGE,
                coverage, Map.of(), List.of(), vector, ThermodynamicCalculationMethod.FORMATION_SUM,
                "Incomplete thermodynamic coverage; no missing value was defaulted to zero.");
    }

    private static RationalNumber signedCoefficient(ReactionThermodynamicRequestTerm term) {
        RationalNumber coefficient = RationalNumber.of(term.coefficient());
        return term.side() == ReactionSide.REACTANT ? coefficient.negate() : coefficient;
    }

    private static BigDecimal scale(BigDecimal value, RationalNumber multiplier) {
        return value.multiply(new BigDecimal(multiplier.getNumerator()), ScientificMath.CALCULATION_CONTEXT)
                .divide(new BigDecimal(multiplier.getDenominator()), ScientificMath.CALCULATION_CONTEXT);
    }

    private static String recordKey(String compoundCode, MatterState state) {
        return compoundCode + "|" + state.name();
    }

    private static MatterState toMatterState(ReactionSpeciesState state) {
        return switch (state) {
            case GAS -> MatterState.GAS;
            case LIQUID -> MatterState.LIQUID;
            case SOLID -> MatterState.SOLID;
            case UNKNOWN -> MatterState.UNKNOWN;
            case AQUEOUS, DISSOLVED, MOLTEN -> null;
        };
    }

    private static void validateStandardState(ReactionThermodynamicRecordSet records) {
        StandardStateConvention expected = switch (records.state()) {
            case GAS -> StandardStateConvention.IDEAL_GAS_STANDARD_STATE;
            case LIQUID -> StandardStateConvention.PURE_SUBSTANCE_STANDARD_STATE;
            case SOLID -> StandardStateConvention.SOLID_REFERENCE_STATE;
            default -> throw new ThermodynamicException(ThermodynamicErrorCode.CONFLICTING_STANDARD_STATE_CONVENTION,
                    "Unsupported thermodynamic reference state " + records.state());
        };
        if (records.conditions().standardStateConvention() != expected) {
            throw new ThermodynamicException(ThermodynamicErrorCode.CONFLICTING_STANDARD_STATE_CONVENTION,
                    "Reference record standard-state convention does not match its phase");
        }
    }
}
