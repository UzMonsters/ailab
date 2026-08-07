package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.measurement.Energy;
import com.ailab.chemistry.domain.measurement.Temperature;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public record AdiabaticTemperatureResult(
        String reactionCode,
        BigDecimal reactionExtentMoles,
        Temperature initialTemperature,
        Temperature finalTemperature,
        Energy reactionHeatJoules,
        ThermalEnergyBalance energyBalance,
        CalorimetryStatus status,
        String explanation,
        List<String> assumptions) {
    public AdiabaticTemperatureResult {
        Objects.requireNonNull(reactionCode, "reactionCode must not be null");
        Objects.requireNonNull(reactionExtentMoles, "reactionExtentMoles must not be null");
        Objects.requireNonNull(initialTemperature, "initialTemperature must not be null");
        assumptions = assumptions == null ? List.of() : List.copyOf(assumptions);
    }
}
