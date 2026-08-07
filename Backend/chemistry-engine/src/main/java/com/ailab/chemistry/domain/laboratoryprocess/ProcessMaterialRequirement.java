package com.ailab.chemistry.domain.laboratoryprocess;

import java.math.BigDecimal;

public record ProcessMaterialRequirement(
        String requirementId,
        String compoundCode,
        BigDecimal quantity,
        String unit,
        String physicalState,
        boolean input,
        boolean output
) {
    public ProcessMaterialRequirement {
        if (requirementId == null || requirementId.isBlank()) {
            throw new IllegalArgumentException("Material requirement id is required");
        }
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Material quantity must be non-negative");
        }
    }
}
