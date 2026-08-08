package com.ailab.chemistry.domain.kinetics;

import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;

import java.math.BigDecimal;
import java.util.Objects;

public record ArrheniusRequest(
        ArrheniusParameters parameters,
        Temperature targetTemperature) {
    public ArrheniusRequest {
        Objects.requireNonNull(parameters, "parameters must not be null");
        Objects.requireNonNull(targetTemperature, "targetTemperature must not be null");
        if (targetTemperature.in(TemperatureUnit.KELVIN).compareTo(BigDecimal.ZERO) <= 0) {
            throw new KineticException(
                    KineticErrorCode.INVALID_TEMPERATURE,
                    "Target temperature must be positive: " + targetTemperature);
        }
    }
}
