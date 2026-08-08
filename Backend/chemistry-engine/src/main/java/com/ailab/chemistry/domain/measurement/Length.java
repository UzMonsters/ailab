package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;
import java.util.Objects;
import com.ailab.chemistry.domain.measurement.exception.NegativeQuantityException;

public final class Length implements Comparable<Length> {
    private final BigDecimal valueInMeters;

    private Length(BigDecimal valueInMeters) {
        if (valueInMeters.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Length cannot be negative: " + valueInMeters);
        }
        this.valueInMeters = valueInMeters;
    }

    public static Length of(BigDecimal value, LengthUnit unit) {
        Objects.requireNonNull(value, "Value must not be null");
        Objects.requireNonNull(unit, "Unit must not be null");
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Length value cannot be negative: " + value);
        }
        BigDecimal valueInMeters = value.multiply(unit.getFactorToMeter(), ScientificMath.CALCULATION_CONTEXT);
        return new Length(valueInMeters);
    }

    public static Length of(String valueStr, LengthUnit unit) {
        Objects.requireNonNull(valueStr, "Value string must not be null");
        return of(new BigDecimal(valueStr), unit);
    }

    public BigDecimal in(LengthUnit unit) {
        Objects.requireNonNull(unit, "Unit must not be null");
        return valueInMeters.divide(unit.getFactorToMeter(), ScientificMath.CALCULATION_CONTEXT);
    }

    public Length add(Length other) {
        Objects.requireNonNull(other, "Other length must not be null");
        BigDecimal result = this.valueInMeters.add(other.valueInMeters, ScientificMath.CALCULATION_CONTEXT);
        return new Length(result);
    }

    public Length subtract(Length other) {
        Objects.requireNonNull(other, "Other length must not be null");
        BigDecimal result = this.valueInMeters.subtract(other.valueInMeters, ScientificMath.CALCULATION_CONTEXT);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Subtraction would result in a negative length: " + result);
        }
        return new Length(result);
    }

    public Length multiply(BigDecimal scalar) {
        Objects.requireNonNull(scalar, "Scalar must not be null");
        if (scalar.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Length multiplier must be non-negative: " + scalar);
        }
        BigDecimal result = this.valueInMeters.multiply(scalar, ScientificMath.CALCULATION_CONTEXT);
        return new Length(result);
    }

    public BigDecimal getValueInMeters() {
        return valueInMeters;
    }

    @Override
    public int compareTo(Length other) {
        return this.valueInMeters.compareTo(other.valueInMeters);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Length length = (Length) o;
        return valueInMeters.compareTo(length.valueInMeters) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(valueInMeters.stripTrailingZeros());
    }

    @Override
    public String toString() {
        return in(LengthUnit.PICOMETER).stripTrailingZeros().toPlainString() + " pm";
    }
}
