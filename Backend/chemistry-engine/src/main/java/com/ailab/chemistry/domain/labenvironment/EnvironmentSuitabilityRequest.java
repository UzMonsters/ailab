package com.ailab.chemistry.domain.labenvironment;

import java.util.Objects;

public record EnvironmentSuitabilityRequest(
        LaboratoryEnvironmentSnapshot snapshot,
        EnvironmentalRequirement requirement
) {
    public EnvironmentSuitabilityRequest {
        Objects.requireNonNull(snapshot, "snapshot must not be null");
        Objects.requireNonNull(requirement, "requirement must not be null");
    }
}
