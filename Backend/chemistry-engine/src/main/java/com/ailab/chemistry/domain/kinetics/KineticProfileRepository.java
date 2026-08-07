package com.ailab.chemistry.domain.kinetics;

import java.util.List;
import java.util.Optional;

public interface KineticProfileRepository {
    Optional<KineticProfile> findByProfileId(String profileId);
    List<KineticProfile> findByReactionCode(String reactionCode);
    List<KineticProfile> findAll();
}
