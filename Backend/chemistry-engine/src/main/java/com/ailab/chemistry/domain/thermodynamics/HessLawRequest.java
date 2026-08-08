package com.ailab.chemistry.domain.thermodynamics;

import java.util.List;
import java.util.Objects;

public record HessLawRequest(List<HessReactionTerm> reactionTerms, ReactionThermodynamicVector targetVector) {

    public HessLawRequest {
        Objects.requireNonNull(reactionTerms, "reactionTerms must not be null");
        Objects.requireNonNull(targetVector, "targetVector must not be null");
        reactionTerms = List.copyOf(reactionTerms);
    }
}
