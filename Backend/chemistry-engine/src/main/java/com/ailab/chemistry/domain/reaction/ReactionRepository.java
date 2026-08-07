package com.ailab.chemistry.domain.reaction;

import java.util.List;
import java.util.Optional;

public interface ReactionRepository {
    Optional<Reaction> findById(ReactionId id);

    Optional<Reaction> findByCode(ReactionCode code);

    List<Reaction> findByReactantCompoundCode(String compoundCode);

    List<Reaction> findByProductCompoundCode(String compoundCode);

    List<Reaction> findInvolvingCompoundCode(String compoundCode);

    List<Reaction> findByReactionTypeCode(ReactionTypeCode typeCode);

    List<Reaction> findReversible();

    List<Reaction> findAll();

    Reaction save(Reaction reaction);
}
