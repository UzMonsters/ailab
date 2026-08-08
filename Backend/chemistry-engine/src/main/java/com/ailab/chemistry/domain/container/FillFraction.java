package com.ailab.chemistry.domain.container;

import java.math.BigDecimal;
import java.util.Objects;

public record FillFraction(BigDecimal value) {
    public FillFraction {
        Objects.requireNonNull(value, "value must not be null");
        if (value.compareTo(BigDecimal.ZERO) < 0 || value.compareTo(BigDecimal.ONE) > 0) {
            throw new ContainerException(ContainerErrorCode.INVALID_PROFILE, "Fill fraction must be from 0 through 1");
        }
    }
}
