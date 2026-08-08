package com.ailab.chemistry.domain.container;

import java.util.List;
import java.util.Optional;

public interface ContainerReferenceRepository {
    Optional<ContainerProfile> findByProfileId(String profileId);

    List<ContainerProfile> findActive();
}
