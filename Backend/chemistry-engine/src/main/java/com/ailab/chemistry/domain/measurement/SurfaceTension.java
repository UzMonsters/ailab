package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;
import java.util.Objects;

public final class SurfaceTension {
    private final BigDecimal value;
    private final SurfaceTensionUnit unit;

    public SurfaceTension(BigDecimal value, SurfaceTensionUnit unit) {
        Objects.requireNonNull(value, "Value cannot be null");
        Objects.requireNonNull(unit, "Unit cannot be null");
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Surface tension must be positive: " + value);
        }
        this.value = value.stripTrailingZeros();
        this.unit = unit;
    }

    public static SurfaceTension of(double value, SurfaceTensionUnit unit) {
        return new SurfaceTension(BigDecimal.valueOf(value), unit);
    }

    public static SurfaceTension of(String value, SurfaceTensionUnit unit) {
        return new SurfaceTension(new BigDecimal(value), unit);
    }

    public BigDecimal getValue() { return value; }
    public SurfaceTensionUnit getUnit() { return unit; }

    public SurfaceTension toCanonical() {
        if (unit == SurfaceTensionUnit.NEWTON_PER_METER) return this;
        BigDecimal canonicalVal = value.multiply(unit.getFactorToCanonical());
        return new SurfaceTension(canonicalVal, SurfaceTensionUnit.NEWTON_PER_METER);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SurfaceTension that = (SurfaceTension) o;
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
