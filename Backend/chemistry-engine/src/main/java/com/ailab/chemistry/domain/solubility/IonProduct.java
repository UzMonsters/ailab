package com.ailab.chemistry.domain.solubility;

import java.math.BigDecimal;
import java.util.Objects;

public record IonProduct(BigDecimal value) {
    public IonProduct {
        Objects.requireNonNull(value, "ion product must not be null");
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new SolubilityException(SolubilityErrorCode.NEGATIVE_CONCENTRATION, "Ion product cannot be negative");
        }
    }
}
