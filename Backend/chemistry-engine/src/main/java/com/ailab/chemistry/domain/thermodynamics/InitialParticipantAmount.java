package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.element.MatterState;

import java.math.BigDecimal;
import java.util.Objects;

public record InitialParticipantAmount(
        String compoundCode,
        MatterState state,
        BigDecimal moles,
        String speciesCode,
        Integer ionicCharge) {
    public InitialParticipantAmount {
        Objects.requireNonNull(compoundCode, "compoundCode must not be null");
        Objects.requireNonNull(state, "state must not be null");
        Objects.requireNonNull(moles, "moles must not be null");
        if (moles.compareTo(BigDecimal.ZERO) < 0) {
            throw new EquilibriumCompositionException(
                    EquilibriumCompositionErrorCode.INVALID_INITIAL_AMOUNTS,
                    "Participant initial moles cannot be negative: " + compoundCode + "=" + moles);
        }
    }

    public InitialParticipantAmount(String compoundCode, MatterState state, BigDecimal moles) {
        this(compoundCode, state, moles, null, null);
    }
}
