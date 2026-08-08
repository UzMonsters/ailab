package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;
import java.util.Objects;

public final class DynamicViscosity {
    private final BigDecimal value;
    private final DynamicViscosityUnit unit;

    public DynamicViscosity(BigDecimal value, DynamicViscosityUnit unit) {
        Objects.requireNonNull(value, "Value cannot be null");
        Objects.requireNonNull(unit, "Unit cannot be null");
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Dynamic viscosity must be positive: " + value);
        }
        this.value = value.stripTrailingZeros();
        this.unit = unit;
    }

    public static DynamicViscosity of(double value, DynamicViscosityUnit unit) {
        return new DynamicViscosity(BigDecimal.valueOf(value), unit);
    }

    public static DynamicViscosity of(String value, DynamicViscosityUnit unit) {
        return new DynamicViscosity(new BigDecimal(value), unit);
    }

    public BigDecimal getValue() { return value; }
    public DynamicViscosityUnit getUnit() { return unit; }

    public DynamicViscosity toCanonical() {
        if (unit == DynamicViscosityUnit.PASCAL_SECOND) return this;
        BigDecimal canonicalVal = value.multiply(unit.getFactorToCanonical());
        return new DynamicViscosity(canonicalVal, DynamicViscosityUnit.PASCAL_SECOND);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DynamicViscosity that = (DynamicViscosity) o;
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
