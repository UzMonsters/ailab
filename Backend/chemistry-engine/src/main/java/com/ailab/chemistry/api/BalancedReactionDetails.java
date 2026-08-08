package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.reaction.ReactionSide;

import java.math.BigInteger;
import java.util.List;

public record BalancedReactionDetails(
        String inputEquation,
        String canonicalBalancedEquation,
        String reactionSignature,
        boolean isBalanced,
        List<BalancedTermDetails> terms
) {
    public record BalancedTermDetails(
            String formula,
            ReactionSide side,
            BigInteger coefficient
    ) {}
}
