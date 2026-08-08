package com.ailab.chemistry.domain.equipment;

import java.util.List;
import java.util.Optional;

public interface EquipmentReferenceRepository {
    Optional<EquipmentReferenceProfile> findByProfileId(String profileId);

    List<EquipmentReferenceProfile> findActive();
}
