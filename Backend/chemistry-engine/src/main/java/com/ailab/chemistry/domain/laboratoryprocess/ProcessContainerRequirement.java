package com.ailab.chemistry.domain.laboratoryprocess;

import java.math.BigDecimal;

public record ProcessContainerRequirement(
        String requirementId,
        String profileId,
        BigDecimal requiredVolumeMl,
        boolean sealed,
        String compoundOrFamily,
        String physicalState
) {
    public ProcessContainerRequirement {
        if (requirementId == null || requirementId.isBlank()) {
            throw new IllegalArgumentException("Container requirement id is required");
        }
        if (requiredVolumeMl == null || requiredVolumeMl.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Container required volume must be non-negative");
        }
    }
}
