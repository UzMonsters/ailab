package com.ailab.chemistry.domain.kinetics;

import com.ailab.chemistry.domain.element.MatterState;

import java.math.BigDecimal;
import java.util.Objects;

public record SpeciesRate(
        String compoundCode,
        MatterState state,
        BigDecimal stoichiometricCoefficient,
        BigDecimal rateMolarPerSecond) {
    public SpeciesRate {
        Objects.requireNonNull(compoundCode, "compoundCode must not be null");
        Objects.requireNonNull(state, "state must not be null");
        Objects.requireNonNull(stoichiometricCoefficient, "stoichiometricCoefficient must not be null");
        Objects.requireNonNull(rateMolarPerSecond, "rateMolarPerSecond must not be null");
    }
}
