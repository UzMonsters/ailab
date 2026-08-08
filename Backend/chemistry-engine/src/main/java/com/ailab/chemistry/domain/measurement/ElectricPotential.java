package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;
import java.util.Objects;

public final class ElectricPotential implements Comparable<ElectricPotential> {
    private final BigDecimal valueInVolts;

    private ElectricPotential(BigDecimal valueInVolts) {
        this.valueInVolts = valueInVolts;
    }

    public static ElectricPotential volts(BigDecimal value) {
        Objects.requireNonNull(value, "value must not be null");
        return new ElectricPotential(value);
    }

    public static ElectricPotential millivolts(BigDecimal value) {
        Objects.requireNonNull(value, "value must not be null");
        return new ElectricPotential(value.divide(new BigDecimal("1000"), ScientificMath.CALCULATION_CONTEXT));
    }

    public BigDecimal inVolts() {
        return valueInVolts;
    }

    public BigDecimal inMillivolts() {
        return valueInVolts.multiply(new BigDecimal("1000"), ScientificMath.CALCULATION_CONTEXT);
    }

    public ElectricPotential add(ElectricPotential other) {
        Objects.requireNonNull(other, "Other electric potential must not be null");
        return new ElectricPotential(valueInVolts.add(other.valueInVolts, ScientificMath.CALCULATION_CONTEXT));
    }

    public ElectricPotential subtract(ElectricPotential other) {
        Objects.requireNonNull(other, "Other electric potential must not be null");
        return new ElectricPotential(valueInVolts.subtract(other.valueInVolts, ScientificMath.CALCULATION_CONTEXT));
    }

    @Override
    public int compareTo(ElectricPotential other) {
        return valueInVolts.compareTo(other.valueInVolts);
    }
}
