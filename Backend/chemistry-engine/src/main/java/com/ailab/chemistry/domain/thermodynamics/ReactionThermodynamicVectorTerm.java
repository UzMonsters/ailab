package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.equation.RationalNumber;

import java.util.Objects;

public record ReactionThermodynamicVectorTerm(String compoundCode, MatterState state, RationalNumber coefficient) {

    public ReactionThermodynamicVectorTerm {
        Objects.requireNonNull(compoundCode, "compoundCode must not be null");
        Objects.requireNonNull(state, "state must not be null");
        Objects.requireNonNull(coefficient, "coefficient must not be null");
    }

    public String key() {
        return compoundCode + "|" + state.name();
    }
}
