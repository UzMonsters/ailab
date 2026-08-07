package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;

import java.math.BigDecimal;
import java.util.Objects;

public record Calorimeter(
        HeatCapacity heatCapacity,
        Temperature initialTemperature) {
    public Calorimeter {
        heatCapacity = heatCapacity == null ? HeatCapacity.ofJoulesPerKelvin(BigDecimal.ZERO) : heatCapacity;
        if (initialTemperature != null && initialTemperature.in(TemperatureUnit.KELVIN).compareTo(BigDecimal.ZERO) <= 0) {
            throw new CalorimetryException(
                    CalorimetryErrorCode.INVALID_TEMPERATURE,
                    "Calorimeter initial temperature must be positive");
        }
    }
}
