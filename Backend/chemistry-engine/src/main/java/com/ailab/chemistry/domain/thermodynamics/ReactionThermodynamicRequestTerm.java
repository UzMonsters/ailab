package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.reaction.ReactionSide;
import com.ailab.chemistry.domain.reaction.ReactionSpeciesState;

import java.math.BigInteger;
import java.util.Objects;

public record ReactionThermodynamicRequestTerm(
        String compoundCode,
        String formula,
        ReactionSide side,
        BigInteger coefficient,
        ReactionSpeciesState speciesState) {

    public ReactionThermodynamicRequestTerm {
        Objects.requireNonNull(compoundCode, "compoundCode must not be null");
        Objects.requireNonNull(formula, "formula must not be null");
        Objects.requireNonNull(side, "side must not be null");
        Objects.requireNonNull(coefficient, "coefficient must not be null");
        if (coefficient.signum() <= 0) {
            throw new ThermodynamicException(ThermodynamicErrorCode.INVALID_REACTION_THERMODYNAMICS_REQUEST,
                    "Reaction coefficient must be positive");
        }
        speciesState = speciesState == null ? ReactionSpeciesState.UNKNOWN : speciesState;
    }
}
