package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.equation.RationalNumber;
import com.ailab.chemistry.domain.measurement.ScientificMath;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public final class HessLawCalculator {

    public HessLawResult calculate(HessLawRequest request) {
        ReactionThermodynamicVector resultingVector = ReactionThermodynamicVector.of(List.of());
        EnumMap<ReactionThermodynamicProperty, BigDecimal> totals = new EnumMap<>(ReactionThermodynamicProperty.class);
        for (ReactionThermodynamicProperty property : ReactionThermodynamicProperty.values()) {
            totals.put(property, BigDecimal.ZERO);
        }

        List<String> cancellationCandidates = new ArrayList<>();
        for (HessReactionTerm term : request.reactionTerms()) {
            ReactionThermodynamicVector scaled = term.vector().scale(term.multiplier());
            if (scaled.hasOpposingUncancelledStates()) {
                throw new ThermodynamicException(ThermodynamicErrorCode.HESS_STATE_INCOMPATIBLE_CANCELLATION,
                        "Hess term " + term.reactionCode() + " tries to cancel formula-identical species across different states");
            }
            cancellationCandidates.addAll(resultingVector.keys());
            resultingVector = resultingVector.add(scaled);
            for (ReactionThermodynamicProperty property : ReactionThermodynamicProperty.values()) {
                var value = term.properties().properties().get(property).value();
                totals.put(property, totals.get(property).add(scale(value, term.multiplier()), ScientificMath.CALCULATION_CONTEXT));
            }
        }

        if (resultingVector.hasOpposingUncancelledStates()) {
            throw new ThermodynamicException(ThermodynamicErrorCode.HESS_STATE_INCOMPATIBLE_CANCELLATION,
                    "Hess combination leaves state-incompatible opposing species");
        }

        if (!resultingVector.equals(request.targetVector())) {
            throw new ThermodynamicException(ThermodynamicErrorCode.HESS_TARGET_MISMATCH,
                    "Hess vector does not equal requested target equation");
        }

        EnumMap<ReactionThermodynamicProperty, ReactionThermodynamicResultProperty> properties =
                new EnumMap<>(ReactionThermodynamicProperty.class);
        for (ReactionThermodynamicProperty property : ReactionThermodynamicProperty.values()) {
            properties.put(property, new ReactionThermodynamicResultProperty(property,
                    totals.get(property).stripTrailingZeros(), property.unitSymbol()));
        }

        List<String> finalKeys = resultingVector.keys();
        List<String> cancellations = cancellationCandidates.stream()
                .filter(key -> request.reactionTerms().stream()
                        .flatMap(term -> term.vector().scale(term.multiplier()).terms().stream())
                        .anyMatch(vectorTerm -> vectorTerm.key().equals(key)))
                .distinct()
                .filter(key -> !finalKeys.contains(key))
                .sorted()
                .toList();

        return new HessLawResult(resultingVector, request.targetVector(), properties, request.reactionTerms(), cancellations,
                ThermodynamicCalculationMethod.HESS_LAW_EXACT_VECTOR_SUM,
                "Hess's law combination used exact rational reaction-vector algebra; decimal arithmetic is used only for property sums.");
    }

    private static BigDecimal scale(BigDecimal value, RationalNumber multiplier) {
        return value.multiply(new BigDecimal(multiplier.getNumerator()), ScientificMath.CALCULATION_CONTEXT)
                .divide(new BigDecimal(multiplier.getDenominator()), ScientificMath.CALCULATION_CONTEXT);
    }
}
