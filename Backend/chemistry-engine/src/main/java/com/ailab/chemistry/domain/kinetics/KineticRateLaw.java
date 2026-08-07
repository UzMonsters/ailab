package com.ailab.chemistry.domain.kinetics;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public record KineticRateLaw(List<KineticRateLawTerm> terms, OverallReactionOrder overallOrder) {
    public KineticRateLaw {
        Objects.requireNonNull(terms, "terms must not be null");
        terms = List.copyOf(terms);
        if (overallOrder == null) {
            BigDecimal sum = BigDecimal.ZERO;
            for (KineticRateLawTerm term : terms) {
                sum = sum.add(term.order().value());
            }
            overallOrder = OverallReactionOrder.of(sum);
        }
    }

    public static KineticRateLaw of(List<KineticRateLawTerm> terms) {
        return new KineticRateLaw(terms, null);
    }
}
