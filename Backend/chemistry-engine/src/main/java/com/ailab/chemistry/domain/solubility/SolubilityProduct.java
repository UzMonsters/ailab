package com.ailab.chemistry.domain.solubility;

import java.math.BigDecimal;
import java.util.Objects;

public record SolubilityProduct(BigDecimal value) {
    public SolubilityProduct {
        Objects.requireNonNull(value, "Ksp value must not be null");
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new SolubilityException(SolubilityErrorCode.MISSING_KSP, "Ksp must be positive");
        }
    }
}
