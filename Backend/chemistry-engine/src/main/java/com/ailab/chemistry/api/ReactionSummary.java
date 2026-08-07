package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.reaction.ReactionDirectionality;

import java.util.List;
import java.util.UUID;

public record ReactionSummary(
        UUID reactionId,
        String reactionCode,
        String primaryName,
        String canonicalEquation,
        ReactionDirectionality directionality,
        List<String> typeCodes,
        int reactantCount,
        int productCount,
        boolean hasCatalyst,
        boolean hasConditions
) {}
