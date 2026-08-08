package com.ailab.chemistry.domain.measurement;

import com.ailab.chemistry.domain.measurement.exception.NegativeQuantityException;

import java.math.BigDecimal;
import java.util.Objects;

public record MeasurementUncertainty(BigDecimal value, String unit) {
    public MeasurementUncertainty {
        Objects.requireNonNull(value, "value must not be null");
        Objects.requireNonNull(unit, "unit must not be null");
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Measurement uncertainty cannot be negative: " + value);
        }
    }

    public static MeasurementUncertainty of(String value, String unit) {
        return new MeasurementUncertainty(new BigDecimal(value), unit);
    }
}
