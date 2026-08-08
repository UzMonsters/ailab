package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.measurement.Temperature;

import java.util.List;
import java.util.Objects;

public record ThermalMixingResult(
        List<ThermalSample> samples,
        Calorimeter calorimeter,
        Temperature finalTemperature,
        ThermalEnergyBalance energyBalance,
        CalorimetryStatus status,
        CalorimetryMethod method,
        String explanation,
        List<String> assumptions) {
    public ThermalMixingResult {
        samples = samples == null ? List.of() : List.copyOf(samples);
        assumptions = assumptions == null ? List.of() : List.copyOf(assumptions);
    }
}
