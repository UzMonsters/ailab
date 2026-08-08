package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.measurement.Energy;

import java.util.Objects;

public record ThermalEnergyBalance(
        Energy totalHeatJoules,
        Energy residualJoules,
        boolean isBalanced) {
    public ThermalEnergyBalance {
        Objects.requireNonNull(totalHeatJoules, "totalHeatJoules must not be null");
        Objects.requireNonNull(residualJoules, "residualJoules must not be null");
    }
}
