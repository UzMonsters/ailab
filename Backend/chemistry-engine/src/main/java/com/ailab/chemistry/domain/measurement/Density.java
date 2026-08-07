package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;
import java.util.Objects;
import com.ailab.chemistry.domain.measurement.exception.NegativeQuantityException;

public final class Density implements Comparable<Density> {
    private final BigDecimal valueInKgPerM3;

    private Density(BigDecimal valueInKgPerM3) {
        if (valueInKgPerM3.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NegativeQuantityException("Density must be strictly positive (> 0): " + valueInKgPerM3);
        }
        this.valueInKgPerM3 = valueInKgPerM3;
    }

    public static Density of(BigDecimal value, DensityUnit unit) {
        Objects.requireNonNull(value, "Value must not be null");
        Objects.requireNonNull(unit, "Unit must not be null");
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NegativeQuantityException("Density value must be strictly positive (> 0): " + value);
        }
        BigDecimal valueInKgPerM3 = value.multiply(unit.getFactorToKgPerM3(), ScientificMath.CALCULATION_CONTEXT);
        return new Density(valueInKgPerM3);
    }

    public static Density of(String valueStr, DensityUnit unit) {
        Objects.requireNonNull(valueStr, "Value string must not be null");
        return of(new BigDecimal(valueStr), unit);
    }

    public BigDecimal in(DensityUnit unit) {
        Objects.requireNonNull(unit, "Unit must not be null");
        return valueInKgPerM3.divide(unit.getFactorToKgPerM3(), ScientificMath.CALCULATION_CONTEXT);
    }

    public BigDecimal getValueInKgPerM3() {
        return valueInKgPerM3;
    }

    @Override
    public int compareTo(Density other) {
        return this.valueInKgPerM3.compareTo(other.valueInKgPerM3);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Density density = (Density) o;
        return valueInKgPerM3.compareTo(density.valueInKgPerM3) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(valueInKgPerM3.stripTrailingZeros());
    }

    @Override
    public String toString() {
        return in(DensityUnit.KILOGRAM_PER_CUBIC_METER).stripTrailingZeros().toPlainString() + " kg/m³";
    }
}
