package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.measurement.Energy;
import com.ailab.chemistry.domain.measurement.Temperature;

import java.util.Objects;

public record SensibleHeatResult(
        ThermalSample sample,
        Temperature initialTemperature,
        Temperature finalTemperature,
        Energy heatTransferredJoules,
        CalorimetryStatus status,
        CalorimetryMethod method,
        String explanation) {
    public SensibleHeatResult {
        Objects.requireNonNull(sample, "sample must not be null");
        Objects.requireNonNull(initialTemperature, "initialTemperature must not be null");
        Objects.requireNonNull(finalTemperature, "finalTemperature must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(method, "method must not be null");
    }
}
