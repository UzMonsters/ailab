package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.element.MatterState;

import java.math.BigDecimal;
import java.util.Objects;

public record EquilibriumParticipantState(
        String compoundCode,
        MatterState state,
        String speciesCode,
        BigDecimal initialMoles,
        BigDecimal finalMoles,
        BigDecimal stoichiometricCoefficient,
        BigDecimal activity,
        BigDecimal partialPressureBar,
        BigDecimal concentrationMolPerLiter) {
    public EquilibriumParticipantState {
        Objects.requireNonNull(compoundCode, "compoundCode must not be null");
        Objects.requireNonNull(state, "state must not be null");
        Objects.requireNonNull(initialMoles, "initialMoles must not be null");
        Objects.requireNonNull(finalMoles, "finalMoles must not be null");
        Objects.requireNonNull(stoichiometricCoefficient, "stoichiometricCoefficient must not be null");
        Objects.requireNonNull(activity, "activity must not be null");
    }
}
