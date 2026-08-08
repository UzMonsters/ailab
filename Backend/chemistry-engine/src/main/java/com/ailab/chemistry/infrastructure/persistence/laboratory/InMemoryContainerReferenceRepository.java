package com.ailab.chemistry.infrastructure.persistence.laboratory;

import com.ailab.chemistry.domain.container.ContainerProfile;
import com.ailab.chemistry.domain.container.ContainerReferenceRepository;

import java.util.List;
import java.util.Optional;

public final class InMemoryContainerReferenceRepository implements ContainerReferenceRepository {
    private final List<ContainerProfile> profiles;

    public InMemoryContainerReferenceRepository(List<ContainerProfile> profiles) {
        this.profiles = List.copyOf(profiles);
    }

    @Override
    public Optional<ContainerProfile> findByProfileId(String profileId) {
        return profiles.stream().filter(p -> p.profileId().equals(profileId)).findFirst();
    }

    @Override
    public List<ContainerProfile> findActive() {
        return profiles;
    }
}
