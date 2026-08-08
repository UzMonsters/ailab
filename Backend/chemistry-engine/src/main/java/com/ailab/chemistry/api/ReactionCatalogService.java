package com.ailab.chemistry.api;

import java.util.List;
import java.util.UUID;

public interface ReactionCatalogService {

    ReactionDetails getById(UUID reactionId);

    ReactionDetails getByCode(String reactionCode);

    List<ReactionSummary> findByReactant(String compoundCode);

    List<ReactionSummary> findByProduct(String compoundCode);

    List<ReactionSummary> findInvolvingCompound(String compoundCode);

    List<ReactionSummary> findByReactionType(String reactionTypeCode);

    List<ReactionSummary> findReversible();

    List<ReactionSummary> listReactions();

    BalancedReactionDetails validateAndBalance(String equation);
}
