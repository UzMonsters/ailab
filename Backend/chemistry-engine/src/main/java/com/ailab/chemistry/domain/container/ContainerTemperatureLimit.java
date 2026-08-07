package com.ailab.chemistry.domain.container;

import com.ailab.chemistry.domain.measurement.Temperature;

import java.util.Objects;

public record ContainerTemperatureLimit(Temperature minimum, Temperature maximum) {
    public ContainerTemperatureLimit {
        Objects.requireNonNull(minimum, "minimum must not be null");
        Objects.requireNonNull(maximum, "maximum must not be null");
        if (minimum.compareTo(maximum) > 0) {
            throw new ContainerException(ContainerErrorCode.INVALID_PROFILE, "Temperature limit minimum cannot exceed maximum");
        }
    }

    public boolean contains(Temperature temperature) {
        return temperature.compareTo(minimum) >= 0 && temperature.compareTo(maximum) <= 0;
    }
}
