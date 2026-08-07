package com.ailab.chemistry.domain.equipment;

import java.util.List;
import java.util.Objects;

public record EquipmentReferenceProfile(
        String profileId,
        String datasetId,
        EquipmentType type,
        String displayName,
        EquipmentCondition condition,
        List<EquipmentCapability> capabilities,
        String provenance
) {
    public EquipmentReferenceProfile(
            String profileId,
            String datasetId,
            EquipmentType type,
            String displayName,
            EquipmentCondition condition,
            List<EquipmentCapability> capabilities
    ) {
        this(profileId, datasetId, type, displayName, condition, capabilities, "explicit reference profile");
    }

    public EquipmentReferenceProfile {
        Objects.requireNonNull(profileId, "profileId must not be null");
        Objects.requireNonNull(datasetId, "datasetId must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(displayName, "displayName must not be null");
        Objects.requireNonNull(condition, "condition must not be null");
        capabilities = List.copyOf(Objects.requireNonNull(capabilities, "capabilities must not be null"));
        Objects.requireNonNull(provenance, "provenance must not be null");
    }
}
