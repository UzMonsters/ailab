package com.ailab.chemistry.domain.labenvironment;

import com.ailab.chemistry.domain.measurement.Temperature;

import java.util.Objects;

public record TemperatureRange(Temperature minimum, Temperature maximum) {
    public TemperatureRange {
        Objects.requireNonNull(minimum, "minimum must not be null");
        Objects.requireNonNull(maximum, "maximum must not be null");
        if (minimum.compareTo(maximum) > 0) {
            throw new EnvironmentException(EnvironmentErrorCode.INVALID_REQUIREMENT, "Temperature minimum cannot exceed maximum");
        }
    }

    public boolean contains(Temperature value) {
        return value.compareTo(minimum) >= 0 && value.compareTo(maximum) <= 0;
    }
}
