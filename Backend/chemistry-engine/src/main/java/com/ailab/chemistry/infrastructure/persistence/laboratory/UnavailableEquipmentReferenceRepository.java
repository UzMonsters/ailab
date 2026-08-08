package com.ailab.chemistry.infrastructure.persistence.laboratory;

import com.ailab.chemistry.domain.equipment.EquipmentReferenceProfile;
import com.ailab.chemistry.domain.equipment.EquipmentReferenceRepository;

import java.util.List;
import java.util.Optional;

public final class UnavailableEquipmentReferenceRepository implements EquipmentReferenceRepository {
    @Override
    public Optional<EquipmentReferenceProfile> findByProfileId(String profileId) {
        throw new IllegalStateException("Production equipment reference repository is unavailable");
    }

    @Override
    public List<EquipmentReferenceProfile> findActive() {
        throw new IllegalStateException("Production equipment reference repository is unavailable");
    }
}
