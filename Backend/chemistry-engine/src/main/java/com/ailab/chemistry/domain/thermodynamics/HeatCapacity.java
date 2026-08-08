package com.ailab.chemistry.domain.thermodynamics;

import java.math.BigDecimal;
import java.util.Objects;

public record HeatCapacity(BigDecimal valueJoulesPerKelvin, String unit) {
    public HeatCapacity {
        Objects.requireNonNull(valueJoulesPerKelvin, "valueJoulesPerKelvin must not be null");
        if (valueJoulesPerKelvin.compareTo(BigDecimal.ZERO) < 0) {
            throw new CalorimetryException(
                    CalorimetryErrorCode.INVALID_HEAT_CAPACITY,
                    "Heat capacity cannot be negative: " + valueJoulesPerKelvin);
        }
        unit = unit == null ? "J/K" : unit;
    }

    public static HeatCapacity ofJoulesPerKelvin(BigDecimal value) {
        return new HeatCapacity(value, "J/K");
    }

    public static HeatCapacity ofJoulesPerKelvin(String valueStr) {
        return new HeatCapacity(new BigDecimal(valueStr), "J/K");
    }
}
