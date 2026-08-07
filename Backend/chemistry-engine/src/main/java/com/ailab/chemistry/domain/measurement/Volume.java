package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;
import java.util.Objects;
import com.ailab.chemistry.domain.measurement.exception.NegativeQuantityException;
import com.ailab.chemistry.domain.measurement.exception.ScientificArithmeticException;

public final class Volume implements Comparable<Volume> {
    private final BigDecimal valueInLiters;

    private Volume(BigDecimal valueInLiters) {
        if (valueInLiters.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Volume cannot be negative");
        }
        this.valueInLiters = valueInLiters;
    }

    public static Volume of(BigDecimal value, VolumeUnit unit) {
        Objects.requireNonNull(value, "Value must not be null");
        Objects.requireNonNull(unit, "Unit must not be null");
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Volume value cannot be negative: " + value);
        }
        BigDecimal valueInLiters = value.multiply(unit.getFactor(), ScientificMath.CALCULATION_CONTEXT);
        return new Volume(valueInLiters);
    }

    public static Volume of(String valueStr, VolumeUnit unit) {
        Objects.requireNonNull(valueStr, "Value string must not be null");
        return of(new BigDecimal(valueStr), unit);
    }

    public BigDecimal in(VolumeUnit unit) {
        Objects.requireNonNull(unit, "Unit must not be null");
        return valueInLiters.divide(unit.getFactor(), ScientificMath.CALCULATION_CONTEXT);
    }

    public Volume add(Volume other) {
        Objects.requireNonNull(other, "Other volume must not be null");
        BigDecimal result = this.valueInLiters.add(other.valueInLiters, ScientificMath.CALCULATION_CONTEXT);
        return new Volume(result);
    }

    public Volume subtract(Volume other) {
        Objects.requireNonNull(other, "Other volume must not be null");
        BigDecimal result = this.valueInLiters.subtract(other.valueInLiters, ScientificMath.CALCULATION_CONTEXT);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Subtraction would result in a negative volume: " + result);
        }
        return new Volume(result);
    }

    public Volume multiply(BigDecimal scalar) {
        Objects.requireNonNull(scalar, "Scalar must not be null");
        if (scalar.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Volume multiplier must be non-negative: " + scalar);
        }
        BigDecimal result = this.valueInLiters.multiply(scalar, ScientificMath.CALCULATION_CONTEXT);
        return new Volume(result);
    }

    public Volume divide(BigDecimal scalar) {
        Objects.requireNonNull(scalar, "Scalar must not be null");
        if (scalar.compareTo(BigDecimal.ZERO) == 0) {
            throw new ScientificArithmeticException("Division by zero");
        }
        if (scalar.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Volume divisor must be positive: " + scalar);
        }
        BigDecimal result = this.valueInLiters.divide(scalar, ScientificMath.CALCULATION_CONTEXT);
        return new Volume(result);
    }

    @Override
    public int compareTo(Volume other) {
        Objects.requireNonNull(other, "Other volume must not be null");
        return this.valueInLiters.compareTo(other.valueInLiters);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Volume volume = (Volume) o;
        return this.valueInLiters.compareTo(volume.valueInLiters) == 0;
    }

    @Override
    public int hashCode() {
        return ScientificMath.scaleIndependentHashCode(valueInLiters);
    }

    @Override
    public String toString() {
        return valueInLiters.stripTrailingZeros().toPlainString() + " L";
    }
}
