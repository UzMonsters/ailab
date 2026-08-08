package com.ailab.chemistry.domain.measurement;

import com.ailab.chemistry.domain.measurement.exception.NegativeQuantityException;

import java.math.BigDecimal;
import java.util.Objects;

public record MeasurementResolution(BigDecimal value, String unit) {
    public MeasurementResolution {
        Objects.requireNonNull(value, "value must not be null");
        Objects.requireNonNull(unit, "unit must not be null");
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Measurement resolution cannot be negative: " + value);
        }
    }

    public static MeasurementResolution of(String value, String unit) {
        return new MeasurementResolution(new BigDecimal(value), unit);
    }
}
