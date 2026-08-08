package com.ailab.chemistry.domain.kinetics;

import java.util.List;
import java.util.Objects;

public record RateEvaluationResult(
        ReactionRate reactionRate,
        List<SpeciesRate> speciesRates,
        OverallReactionOrder overallOrder,
        KineticCalculationMethod method,
        String explanation) {
    public RateEvaluationResult {
        Objects.requireNonNull(reactionRate, "reactionRate must not be null");
        speciesRates = speciesRates == null ? List.of() : List.copyOf(speciesRates);
        Objects.requireNonNull(overallOrder, "overallOrder must not be null");
        Objects.requireNonNull(method, "method must not be null");
    }
}
