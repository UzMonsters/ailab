package com.ailab.chemistry.infrastructure.persistence.laboratory;

import com.ailab.chemistry.domain.container.ContainerProfile;
import com.ailab.chemistry.domain.container.ContainerReferenceRepository;

import java.util.List;
import java.util.Optional;

public final class UnavailableContainerReferenceRepository implements ContainerReferenceRepository {
    @Override
    public Optional<ContainerProfile> findByProfileId(String profileId) {
        throw new IllegalStateException("Production container reference repository is unavailable");
    }

    @Override
    public List<ContainerProfile> findActive() {
        throw new IllegalStateException("Production container reference repository is unavailable");
    }
}
