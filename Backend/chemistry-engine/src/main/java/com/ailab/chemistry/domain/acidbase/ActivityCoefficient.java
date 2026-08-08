package com.ailab.chemistry.domain.acidbase;

import java.math.BigDecimal;
import java.util.Objects;

public record ActivityCoefficient(
        String speciesCode,
        int charge,
        BigDecimal value
) {
    public ActivityCoefficient {
        if (speciesCode == null || speciesCode.isBlank()) {
            speciesCode = "CHARGE-" + charge;
        } else {
            speciesCode = speciesCode.trim();
        }
        Objects.requireNonNull(value, "value must not be null");
        if (value.compareTo(BigDecimal.ZERO) <= 0 || !Double.isFinite(value.doubleValue())) {
            throw new ActivityException(ActivityErrorCode.NON_FINITE_ACTIVITY_COEFFICIENT, "Activity coefficient must be positive and finite");
        }
    }
}
