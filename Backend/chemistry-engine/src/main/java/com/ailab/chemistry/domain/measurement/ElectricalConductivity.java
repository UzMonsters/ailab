package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;
import java.util.Objects;

public final class ElectricalConductivity {
    private final BigDecimal value;
    private final ElectricalConductivityUnit unit;

    public ElectricalConductivity(BigDecimal value, ElectricalConductivityUnit unit) {
        Objects.requireNonNull(value, "Value cannot be null");
        Objects.requireNonNull(unit, "Unit cannot be null");
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Electrical conductivity cannot be negative: " + value);
        }
        this.value = value.stripTrailingZeros();
        this.unit = unit;
    }

    public static ElectricalConductivity of(double value, ElectricalConductivityUnit unit) {
        return new ElectricalConductivity(BigDecimal.valueOf(value), unit);
    }

    public static ElectricalConductivity of(String value, ElectricalConductivityUnit unit) {
        return new ElectricalConductivity(new BigDecimal(value), unit);
    }

    public BigDecimal getValue() { return value; }
    public ElectricalConductivityUnit getUnit() { return unit; }

    public ElectricalConductivity toCanonical() {
        if (unit == ElectricalConductivityUnit.SIEMENS_PER_METER) return this;
        BigDecimal canonicalVal = value.multiply(unit.getFactorToCanonical());
        return new ElectricalConductivity(canonicalVal, ElectricalConductivityUnit.SIEMENS_PER_METER);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElectricalConductivity that = (ElectricalConductivity) o;
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
