package com.ailab.chemistry.domain.container;

import java.util.List;
import java.util.Objects;

public record ContainerProfile(
        String profileId,
        String datasetId,
        ContainerType type,
        ContainerMaterial material,
        ContainerClosureType closureType,
        ContainerMaterial closureMaterial,
        ContainerGeometry geometry,
        NominalCapacity nominalCapacity,
        MaximumWorkingVolume maximumWorkingVolume,
        ContainerTemperatureLimit temperatureLimit,
        ContainerPressureLimit pressureLimit,
        List<ContainerCompatibilityRecord> compatibilityRecords,
        String provenance
) {
    public ContainerProfile {
        Objects.requireNonNull(profileId, "profileId must not be null");
        Objects.requireNonNull(datasetId, "datasetId must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(material, "material must not be null");
        Objects.requireNonNull(closureType, "closureType must not be null");
        Objects.requireNonNull(nominalCapacity, "nominalCapacity must not be null");
        Objects.requireNonNull(maximumWorkingVolume, "maximumWorkingVolume must not be null");
        if (maximumWorkingVolume.volume().compareTo(nominalCapacity.volume()) > 0) {
            throw new ContainerException(ContainerErrorCode.INVALID_PROFILE, "Maximum working volume cannot exceed nominal capacity");
        }
        compatibilityRecords = compatibilityRecords == null ? List.of() : List.copyOf(compatibilityRecords);
        Objects.requireNonNull(provenance, "provenance must not be null");
    }
}
