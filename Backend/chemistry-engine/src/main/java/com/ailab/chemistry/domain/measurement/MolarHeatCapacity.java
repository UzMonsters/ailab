package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;
import java.util.Objects;

public final class MolarHeatCapacity {
    private final BigDecimal value;
    private final MolarHeatCapacityUnit unit;

    public MolarHeatCapacity(BigDecimal value, MolarHeatCapacityUnit unit) {
        Objects.requireNonNull(value, "Value cannot be null");
        Objects.requireNonNull(unit, "Unit cannot be null");
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Molar heat capacity must be positive: " + value);
        }
        this.value = value.stripTrailingZeros();
        this.unit = unit;
    }

    public static MolarHeatCapacity of(double value, MolarHeatCapacityUnit unit) {
        return new MolarHeatCapacity(BigDecimal.valueOf(value), unit);
    }

    public static MolarHeatCapacity of(String value, MolarHeatCapacityUnit unit) {
        return new MolarHeatCapacity(new BigDecimal(value), unit);
    }

    public BigDecimal getValue() { return value; }
    public MolarHeatCapacityUnit getUnit() { return unit; }

    public MolarHeatCapacity toCanonical() {
        if (unit == MolarHeatCapacityUnit.JOULE_PER_MOLE_KELVIN) return this;
        BigDecimal canonicalVal = value.multiply(unit.getFactorToCanonical());
        return new MolarHeatCapacity(canonicalVal, MolarHeatCapacityUnit.JOULE_PER_MOLE_KELVIN);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MolarHeatCapacity that = (MolarHeatCapacity) o;
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
