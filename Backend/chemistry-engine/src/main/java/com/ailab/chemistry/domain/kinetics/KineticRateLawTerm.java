package com.ailab.chemistry.domain.kinetics;

import com.ailab.chemistry.domain.element.MatterState;

import java.util.Objects;

public record KineticRateLawTerm(
        String compoundCode,
        MatterState state,
        ReactionOrder order) {
    public KineticRateLawTerm {
        Objects.requireNonNull(compoundCode, "compoundCode must not be null");
        Objects.requireNonNull(state, "state must not be null");
        Objects.requireNonNull(order, "order must not be null");
    }
}
