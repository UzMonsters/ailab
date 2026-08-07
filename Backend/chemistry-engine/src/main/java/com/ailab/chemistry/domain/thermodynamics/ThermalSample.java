package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.AmountOfSubstance;
import com.ailab.chemistry.domain.measurement.Mass;
import com.ailab.chemistry.domain.measurement.MolarHeatCapacity;
import com.ailab.chemistry.domain.measurement.SpecificHeatCapacity;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;

import java.math.BigDecimal;
import java.util.Objects;

public record ThermalSample(
        String sampleId,
        MatterState state,
        Mass mass,
        AmountOfSubstance amount,
        SpecificHeatCapacity specificHeatCapacity,
        MolarHeatCapacity molarHeatCapacity,
        Temperature initialTemperature) {
    public ThermalSample {
        Objects.requireNonNull(sampleId, "sampleId must not be null");
        Objects.requireNonNull(state, "state must not be null");
        Objects.requireNonNull(initialTemperature, "initialTemperature must not be null");
        if (initialTemperature.in(TemperatureUnit.KELVIN).compareTo(BigDecimal.ZERO) <= 0) {
            throw new CalorimetryException(
                    CalorimetryErrorCode.INVALID_TEMPERATURE,
                    "Initial temperature must be positive: " + initialTemperature);
        }
        if (mass == null && amount == null) {
            throw new CalorimetryException(
                    CalorimetryErrorCode.INVALID_MASS_OR_AMOUNT,
                    "Thermal sample must specify either mass or amount of substance");
        }
    }
}
