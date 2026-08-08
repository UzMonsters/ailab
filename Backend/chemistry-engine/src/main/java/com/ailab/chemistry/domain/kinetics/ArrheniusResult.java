package com.ailab.chemistry.domain.kinetics;

import com.ailab.chemistry.domain.measurement.Temperature;

import java.util.Objects;

public record ArrheniusResult(
        RateConstant calculatedRateConstant,
        Temperature targetTemperature,
        KineticCalculationMethod method,
        String explanation) {
    public ArrheniusResult {
        Objects.requireNonNull(calculatedRateConstant, "calculatedRateConstant must not be null");
        Objects.requireNonNull(targetTemperature, "targetTemperature must not be null");
        Objects.requireNonNull(method, "method must not be null");
    }
}
