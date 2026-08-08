package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.equation.RationalNumber;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public record ReactionThermodynamicsResult(
        String reactionCode,
        String equation,
        ReactionThermodynamicStatus status,
        ReactionThermodynamicCoverage coverage,
        Map<ReactionThermodynamicProperty, ReactionThermodynamicResultProperty> properties,
        List<ReactionThermodynamicTerm> terms,
        ReactionThermodynamicVector reactionVector,
        ThermodynamicCalculationMethod method,
        String explanation) {

    public ReactionThermodynamicsResult {
        properties = Map.copyOf(properties);
        terms = List.copyOf(terms);
    }

    public ReactionThermodynamicResultProperty property(ReactionThermodynamicProperty property) {
        return properties.get(property);
    }

    public HessReactionTerm toHessReactionTerm(long numerator, long denominator) {
        return new HessReactionTerm(reactionCode, RationalNumber.of(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator)),
                reactionVector, new ReactionThermodynamicPropertySet(properties));
    }
}
