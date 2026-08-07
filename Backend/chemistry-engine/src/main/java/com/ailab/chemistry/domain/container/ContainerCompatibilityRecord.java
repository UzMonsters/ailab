package com.ailab.chemistry.domain.container;

import java.util.Objects;

public record ContainerCompatibilityRecord(
        String compoundOrFamily,
        String physicalState,
        ContainerMaterial containerMaterial,
        ContainerMaterial closureMaterial,
        CompatibilityStatus status,
        CompatibilityCondition concentrationCondition,
        ContainerTemperatureLimit temperatureLimit,
        CompatibilityCondition contactDurationLimit,
        String source,
        String evidenceStatus
) {
    public ContainerCompatibilityRecord {
        Objects.requireNonNull(compoundOrFamily, "compoundOrFamily must not be null");
        Objects.requireNonNull(physicalState, "physicalState must not be null");
        Objects.requireNonNull(containerMaterial, "containerMaterial must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(source, "source must not be null");
        Objects.requireNonNull(evidenceStatus, "evidenceStatus must not be null");
    }
}
