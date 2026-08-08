package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;
import java.util.Objects;

public final class SpecificHeatCapacity {
    private final BigDecimal value;
    private final SpecificHeatCapacityUnit unit;

    public SpecificHeatCapacity(BigDecimal value, SpecificHeatCapacityUnit unit) {
        Objects.requireNonNull(value, "Value cannot be null");
        Objects.requireNonNull(unit, "Unit cannot be null");
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Specific heat capacity must be positive: " + value);
        }
        this.value = value.stripTrailingZeros();
        this.unit = unit;
    }

    public static SpecificHeatCapacity of(double value, SpecificHeatCapacityUnit unit) {
        return new SpecificHeatCapacity(BigDecimal.valueOf(value), unit);
    }

    public static SpecificHeatCapacity of(String value, SpecificHeatCapacityUnit unit) {
        return new SpecificHeatCapacity(new BigDecimal(value), unit);
    }

    public BigDecimal getValue() { return value; }
    public SpecificHeatCapacityUnit getUnit() { return unit; }

    public SpecificHeatCapacity toCanonical() {
        if (unit == SpecificHeatCapacityUnit.JOULE_PER_KILOGRAM_KELVIN) return this;
        BigDecimal canonicalVal = value.multiply(unit.getFactorToCanonical());
        return new SpecificHeatCapacity(canonicalVal, SpecificHeatCapacityUnit.JOULE_PER_KILOGRAM_KELVIN);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpecificHeatCapacity that = (SpecificHeatCapacity) o;
        return toCanonical().getValue().compareTo(that.toCanonical().getValue()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(toCanonical().getValue().stripTrailingZeros());
    }

    @Override
    public String toString() {
        return value.toPlainString() + " " + unit.getSymbol();
    }
}
