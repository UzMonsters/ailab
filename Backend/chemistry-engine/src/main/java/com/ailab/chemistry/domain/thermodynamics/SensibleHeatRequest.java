package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.measurement.Temperature;

import java.util.Objects;

public record SensibleHeatRequest(
        ThermalSample sample,
        Temperature finalTemperature,
        CalorimetryMethod method) {
    public SensibleHeatRequest {
        Objects.requireNonNull(sample, "sample must not be null");
        Objects.requireNonNull(finalTemperature, "finalTemperature must not be null");
        Objects.requireNonNull(method, "method must not be null");
    }
}
