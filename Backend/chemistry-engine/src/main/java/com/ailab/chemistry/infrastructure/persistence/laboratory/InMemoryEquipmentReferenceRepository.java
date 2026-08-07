package com.ailab.chemistry.infrastructure.persistence.laboratory;

import com.ailab.chemistry.domain.equipment.EquipmentReferenceProfile;
import com.ailab.chemistry.domain.equipment.EquipmentReferenceRepository;

import java.util.List;
import java.util.Optional;

public final class InMemoryEquipmentReferenceRepository implements EquipmentReferenceRepository {
    private final List<EquipmentReferenceProfile> profiles;

    public InMemoryEquipmentReferenceRepository(List<EquipmentReferenceProfile> profiles) {
        this.profiles = List.copyOf(profiles);
    }

    @Override
    public Optional<EquipmentReferenceProfile> findByProfileId(String profileId) {
        return profiles.stream().filter(p -> p.profileId().equals(profileId)).findFirst();
    }

    @Override
    public List<EquipmentReferenceProfile> findActive() {
        return profiles;
    }
}
