package com.ailab.chemistry.domain.labenvironment;

import com.ailab.chemistry.domain.measurement.RelativeHumidity;

import java.util.Objects;

public record HumidityRange(RelativeHumidity minimum, RelativeHumidity maximum) {
    public HumidityRange {
        Objects.requireNonNull(minimum, "minimum must not be null");
        Objects.requireNonNull(maximum, "maximum must not be null");
        if (minimum.compareTo(maximum) > 0) {
            throw new EnvironmentException(EnvironmentErrorCode.INVALID_REQUIREMENT, "Humidity minimum cannot exceed maximum");
        }
    }

    public boolean contains(RelativeHumidity value) {
        return value.compareTo(minimum) >= 0 && value.compareTo(maximum) <= 0;
    }
}
