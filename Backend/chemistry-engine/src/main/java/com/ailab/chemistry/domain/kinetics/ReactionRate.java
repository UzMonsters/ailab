package com.ailab.chemistry.domain.kinetics;

import java.math.BigDecimal;
import java.util.Objects;

public record ReactionRate(BigDecimal value, String unit) {
    public ReactionRate {
        Objects.requireNonNull(value, "value must not be null");
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new KineticException(
                    KineticErrorCode.INVALID_CONCENTRATION,
                    "Reaction rate cannot be negative: " + value);
        }
        unit = unit == null ? "mol/(L*s)" : unit;
    }

    public static ReactionRate ofMolarPerSecond(BigDecimal value) {
        return new ReactionRate(value, "mol/(L*s)");
    }
}
