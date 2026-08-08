package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;

import java.math.BigDecimal;
import java.util.Objects;

public record TemperatureValidityRange(Temperature minimum, Temperature maximum) {

    public TemperatureValidityRange {
        Objects.requireNonNull(minimum, "minimum must not be null");
        Objects.requireNonNull(maximum, "maximum must not be null");
        if (minimum.in(TemperatureUnit.KELVIN).compareTo(BigDecimal.ZERO) <= 0
                || maximum.in(TemperatureUnit.KELVIN).compareTo(BigDecimal.ZERO) <= 0
                || minimum.compareTo(maximum) > 0) {
            throw new TemperatureCorrectionException(TemperatureCorrectionErrorCode.INVALID_VALIDITY_RANGE,
                    "Temperature validity range must be positive and ordered");
        }
    }

    public boolean contains(Temperature temperature) {
        Objects.requireNonNull(temperature, "temperature must not be null");
        return temperature.compareTo(minimum) >= 0 && temperature.compareTo(maximum) <= 0;
    }
}
