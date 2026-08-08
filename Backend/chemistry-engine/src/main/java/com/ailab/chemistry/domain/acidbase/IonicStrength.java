package com.ailab.chemistry.domain.acidbase;

import java.math.BigDecimal;
import java.util.Objects;

public record IonicStrength(BigDecimal value) {
    public IonicStrength {
        Objects.requireNonNull(value, "value must not be null");
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new ActivityException(ActivityErrorCode.NEGATIVE_CONCENTRATION, "Ionic strength must not be negative");
        }
    }
}
