package com.ailab.chemistry.domain.thermodynamics;

import java.util.List;
import java.util.Objects;

public record ThermalMixingRequest(
        List<ThermalSample> samples,
        Calorimeter calorimeter,
        CalorimetryMethod method) {
    public ThermalMixingRequest {
        Objects.requireNonNull(samples, "samples must not be null");
        if (samples.size() < 2) {
            throw new CalorimetryException(
                    CalorimetryErrorCode.INVALID_MASS_OR_AMOUNT,
                    "Thermal mixing requires at least 2 thermal samples");
        }
        samples = List.copyOf(samples);
        Objects.requireNonNull(method, "method must not be null");
    }
}
