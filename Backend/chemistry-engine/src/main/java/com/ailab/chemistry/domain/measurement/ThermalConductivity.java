package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;
import java.util.Objects;

public final class ThermalConductivity {
    private final BigDecimal value;
    private final ThermalConductivityUnit unit;

    public ThermalConductivity(BigDecimal value, ThermalConductivityUnit unit) {
        Objects.requireNonNull(value, "Value cannot be null");
        Objects.requireNonNull(unit, "Unit cannot be null");
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Thermal conductivity must be positive: " + value);
        }
        this.value = value.stripTrailingZeros();
        this.unit = unit;
    }

    public static ThermalConductivity of(double value, ThermalConductivityUnit unit) {
        return new ThermalConductivity(BigDecimal.valueOf(value), unit);
    }

    public static ThermalConductivity of(String value, ThermalConductivityUnit unit) {
        return new ThermalConductivity(new BigDecimal(value), unit);
    }

    public BigDecimal getValue() { return value; }
    public ThermalConductivityUnit getUnit() { return unit; }

    public ThermalConductivity toCanonical() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ThermalConductivity that = (ThermalConductivity) o;
        return value.compareTo(that.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value.stripTrailingZeros());
    }

    @Override
    public String toString() {
        return value.toPlainString() + " " + unit.getSymbol();
    }
}
