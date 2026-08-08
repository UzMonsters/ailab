package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;
import java.util.Objects;
import com.ailab.chemistry.domain.measurement.exception.NegativeQuantityException;
import com.ailab.chemistry.domain.measurement.exception.ScientificArithmeticException;

public final class Mass implements Comparable<Mass> {
    private final BigDecimal valueInGrams;

    private Mass(BigDecimal valueInGrams) {
        if (valueInGrams.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Mass cannot be negative");
        }
        this.valueInGrams = valueInGrams;
    }

    public static Mass of(BigDecimal value, MassUnit unit) {
        Objects.requireNonNull(value, "Value must not be null");
        Objects.requireNonNull(unit, "Unit must not be null");
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Mass value cannot be negative: " + value);
        }
        BigDecimal valueInGrams = value.multiply(unit.getFactor(), ScientificMath.CALCULATION_CONTEXT);
        return new Mass(valueInGrams);
    }

    public static Mass of(String valueStr, MassUnit unit) {
        Objects.requireNonNull(valueStr, "Value string must not be null");
        return of(new BigDecimal(valueStr), unit);
    }

    public BigDecimal in(MassUnit unit) {
        Objects.requireNonNull(unit, "Unit must not be null");
        return valueInGrams.divide(unit.getFactor(), ScientificMath.CALCULATION_CONTEXT);
    }

    public Mass add(Mass other) {
        Objects.requireNonNull(other, "Other mass must not be null");
        BigDecimal result = this.valueInGrams.add(other.valueInGrams, ScientificMath.CALCULATION_CONTEXT);
        return new Mass(result);
    }

    public Mass subtract(Mass other) {
        Objects.requireNonNull(other, "Other mass must not be null");
        BigDecimal result = this.valueInGrams.subtract(other.valueInGrams, ScientificMath.CALCULATION_CONTEXT);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Subtraction would result in a negative mass: " + result);
        }
        return new Mass(result);
    }

    public Mass multiply(BigDecimal scalar) {
        Objects.requireNonNull(scalar, "Scalar must not be null");
        if (scalar.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Mass multiplier must be non-negative: " + scalar);
        }
        BigDecimal result = this.valueInGrams.multiply(scalar, ScientificMath.CALCULATION_CONTEXT);
        return new Mass(result);
    }

    public Mass divide(BigDecimal scalar) {
        Objects.requireNonNull(scalar, "Scalar must not be null");
        if (scalar.compareTo(BigDecimal.ZERO) == 0) {
            throw new ScientificArithmeticException("Division by zero");
        }
        if (scalar.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Mass divisor must be positive: " + scalar);
        }
        BigDecimal result = this.valueInGrams.divide(scalar, ScientificMath.CALCULATION_CONTEXT);
        return new Mass(result);
    }

    @Override
    public int compareTo(Mass other) {
        Objects.requireNonNull(other, "Other mass must not be null");
        return this.valueInGrams.compareTo(other.valueInGrams);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mass mass = (Mass) o;
        return this.valueInGrams.compareTo(mass.valueInGrams) == 0;
    }

    @Override
    public int hashCode() {
        return ScientificMath.scaleIndependentHashCode(valueInGrams);
    }

    @Override
    public String toString() {
        return valueInGrams.stripTrailingZeros().toPlainString() + " g";
    }
}
