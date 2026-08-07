package com.ailab.chemistry.domain.electrochemistry;

import com.ailab.chemistry.domain.measurement.ScientificMath;

import java.math.BigDecimal;
import java.util.Objects;

public record ElectrodePotential(BigDecimal inVolts) {
    public ElectrodePotential {
        Objects.requireNonNull(inVolts, "inVolts must not be null");
    }

    public static ElectrodePotential ofVolts(String volts) {
        return new ElectrodePotential(new BigDecimal(volts));
    }

    public ElectrodePotential subtract(ElectrodePotential other) {
        return new ElectrodePotential(inVolts.subtract(other.inVolts, ScientificMath.CALCULATION_CONTEXT));
    }
}
