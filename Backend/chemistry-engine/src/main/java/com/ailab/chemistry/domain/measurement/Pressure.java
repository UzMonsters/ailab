package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;
import java.util.Objects;
import com.ailab.chemistry.domain.measurement.exception.NegativeQuantityException;
import com.ailab.chemistry.domain.measurement.exception.ScientificArithmeticException;

public final class Pressure implements Comparable<Pressure> {
    private final BigDecimal valueInPascals;

    private Pressure(BigDecimal valueInPascals) {
        if (valueInPascals.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Absolute pressure cannot be negative");
        }
        this.valueInPascals = valueInPascals;
    }

    public static Pressure of(BigDecimal value, PressureUnit unit) {
        Objects.requireNonNull(value, "Value must not be null");
        Objects.requireNonNull(unit, "Unit must not be null");
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Absolute pressure value cannot be negative: " + value);
        }
        BigDecimal valueInPascals = value.multiply(unit.getFactor(), ScientificMath.CALCULATION_CONTEXT);
        return new Pressure(valueInPascals);
    }

    public static Pressure of(String valueStr, PressureUnit unit) {
        Objects.requireNonNull(valueStr, "Value string must not be null");
        return of(new BigDecimal(valueStr), unit);
    }

    public BigDecimal in(PressureUnit unit) {
        Objects.requireNonNull(unit, "Unit must not be null");
        return valueInPascals.divide(unit.getFactor(), ScientificMath.CALCULATION_CONTEXT);
    }

    public Pressure add(Pressure other) {
        Objects.requireNonNull(other, "Other pressure must not be null");
        BigDecimal result = this.valueInPascals.add(other.valueInPascals, ScientificMath.CALCULATION_CONTEXT);
        return new Pressure(result);
    }

    public Pressure subtract(Pressure other) {
        Objects.requireNonNull(other, "Other pressure must not be null");
        BigDecimal result = this.valueInPascals.subtract(other.valueInPascals, ScientificMath.CALCULATION_CONTEXT);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Subtraction would result in a negative absolute pressure: " + result);
        }
        return new Pressure(result);
    }

    public Pressure multiply(BigDecimal scalar) {
        Objects.requireNonNull(scalar, "Scalar must not be null");
        if (scalar.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Pressure multiplier must be non-negative: " + scalar);
        }
        BigDecimal result = this.valueInPascals.multiply(scalar, ScientificMath.CALCULATION_CONTEXT);
        return new Pressure(result);
    }

    public Pressure divide(BigDecimal scalar) {
        Objects.requireNonNull(scalar, "Scalar must not be null");
        if (scalar.compareTo(BigDecimal.ZERO) == 0) {
            throw new ScientificArithmeticException("Division by zero");
        }
        if (scalar.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Pressure divisor must be positive: " + scalar);
        }
        BigDecimal result = this.valueInPascals.divide(scalar, ScientificMath.CALCULATION_CONTEXT);
        return new Pressure(result);
    }

    @Override
    public int compareTo(Pressure other) {
        Objects.requireNonNull(other, "Other pressure must not be null");
        return this.valueInPascals.compareTo(other.valueInPascals);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pressure pressure = (Pressure) o;
        return this.valueInPascals.compareTo(pressure.valueInPascals) == 0;
    }

    @Override
    public int hashCode() {
        return ScientificMath.scaleIndependentHashCode(valueInPascals);
    }

    @Override
    public String toString() {
        return valueInPascals.stripTrailingZeros().toPlainString() + " Pa";
    }
}
