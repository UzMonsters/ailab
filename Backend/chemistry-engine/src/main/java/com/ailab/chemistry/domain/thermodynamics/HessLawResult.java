package com.ailab.chemistry.domain.thermodynamics;

import java.util.List;
import java.util.Map;

public record HessLawResult(
        ReactionThermodynamicVector resultingVector,
        ReactionThermodynamicVector targetVector,
        Map<ReactionThermodynamicProperty, ReactionThermodynamicResultProperty> properties,
        List<HessReactionTerm> derivation,
        List<String> intermediateCancellations,
        ThermodynamicCalculationMethod method,
        String explanation) {

    public HessLawResult {
        properties = Map.copyOf(properties);
        derivation = List.copyOf(derivation);
        intermediateCancellations = List.copyOf(intermediateCancellations);
    }
}
