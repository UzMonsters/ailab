package com.ailab.chemistry.domain.labenvironment;

import com.ailab.chemistry.domain.measurement.Pressure;

import java.util.Objects;

public record PressureRange(Pressure minimum, Pressure maximum) {
    public PressureRange {
        Objects.requireNonNull(minimum, "minimum must not be null");
        Objects.requireNonNull(maximum, "maximum must not be null");
        if (minimum.compareTo(maximum) > 0) {
            throw new EnvironmentException(EnvironmentErrorCode.INVALID_REQUIREMENT, "Pressure minimum cannot exceed maximum");
        }
    }

    public boolean contains(Pressure value) {
        return value.compareTo(minimum) >= 0 && value.compareTo(maximum) <= 0;
    }
}
