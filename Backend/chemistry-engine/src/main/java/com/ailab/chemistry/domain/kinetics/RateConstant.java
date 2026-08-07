package com.ailab.chemistry.domain.kinetics;

import java.math.BigDecimal;
import java.util.Objects;

public record RateConstant(BigDecimal value, RateConstantDimension dimension) {
    public RateConstant {
        Objects.requireNonNull(value, "value must not be null");
        Objects.requireNonNull(dimension, "dimension must not be null");
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new KineticException(
                    KineticErrorCode.INVALID_RATE_CONSTANT,
                    "Rate constant value must be positive: " + value);
        }
    }

    public static RateConstant of(double value, RateConstantDimension dimension) {
        return new RateConstant(BigDecimal.valueOf(value), dimension);
    }

    public static RateConstant of(String valueStr, RateConstantDimension dimension) {
        return new RateConstant(new BigDecimal(valueStr), dimension);
    }
}
