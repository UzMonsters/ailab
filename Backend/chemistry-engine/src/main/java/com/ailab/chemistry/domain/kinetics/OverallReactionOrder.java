package com.ailab.chemistry.domain.kinetics;

import java.math.BigDecimal;
import java.util.Objects;

public record OverallReactionOrder(BigDecimal totalOrderValue) {
    public OverallReactionOrder {
        Objects.requireNonNull(totalOrderValue, "totalOrderValue must not be null");
        if (totalOrderValue.compareTo(BigDecimal.ZERO) < 0) {
            throw new KineticException(
                    KineticErrorCode.INVALID_ORDER,
                    "Overall reaction order cannot be negative: " + totalOrderValue);
        }
    }

    public static OverallReactionOrder of(int order) {
        return new OverallReactionOrder(BigDecimal.valueOf(order));
    }

    public static OverallReactionOrder of(BigDecimal order) {
        return new OverallReactionOrder(order);
    }
}
