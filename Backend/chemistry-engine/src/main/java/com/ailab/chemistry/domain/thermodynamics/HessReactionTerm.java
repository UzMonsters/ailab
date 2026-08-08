package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.equation.RationalNumber;

import java.util.Objects;

public record HessReactionTerm(
        String reactionCode,
        RationalNumber multiplier,
        ReactionThermodynamicVector vector,
        ReactionThermodynamicPropertySet properties) {

    public HessReactionTerm {
        Objects.requireNonNull(reactionCode, "reactionCode must not be null");
        Objects.requireNonNull(multiplier, "multiplier must not be null");
        Objects.requireNonNull(vector, "vector must not be null");
        Objects.requireNonNull(properties, "properties must not be null");
    }
}
