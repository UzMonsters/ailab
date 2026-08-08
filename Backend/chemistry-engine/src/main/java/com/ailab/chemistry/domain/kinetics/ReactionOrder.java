package com.ailab.chemistry.domain.kinetics;

import java.math.BigDecimal;
import java.util.Objects;

public record ReactionOrder(BigDecimal value) {
    public ReactionOrder {
        Objects.requireNonNull(value, "value must not be null");
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new KineticException(
                    KineticErrorCode.INVALID_ORDER,
                    "Reaction order cannot be negative: " + value);
        }
    }

    public static ReactionOrder of(int order) {
        return new ReactionOrder(BigDecimal.valueOf(order));
    }

    public static ReactionOrder of(double order) {
        return new ReactionOrder(BigDecimal.valueOf(order));
    }

    public static ReactionOrder of(String orderStr) {
        return new ReactionOrder(new BigDecimal(orderStr));
    }
}
