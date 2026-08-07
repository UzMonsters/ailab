package com.ailab.chemistry.domain.gas;

import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.Temperature;

import java.util.Objects;

public record StandardConditionDefinition(String name, Temperature temperature, Pressure pressure) {
    public StandardConditionDefinition {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(temperature, "temperature must not be null");
        Objects.requireNonNull(pressure, "pressure must not be null");
    }
}
