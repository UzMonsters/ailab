package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;
import java.util.Objects;
import com.ailab.chemistry.domain.measurement.exception.ScientificArithmeticException;

/**
 * Immutable Energy value object.
 * Supports signed values (positive, zero, and negative) to represent released/absorbed energy,
 * enthalpy change, or heat transfer.
 * Multipliers/divisors can be negative.
 */
public final class Energy implements Comparable<Energy> {
    private final BigDecimal valueInJoules;

    private Energy(BigDecimal valueInJoules) {
        this.valueInJoules = valueInJoules;
    }

    public static Energy of(BigDecimal value, EnergyUnit unit) {
        Objects.requireNonNull(value, "Value must not be null");
        Objects.requireNonNull(unit, "Unit must not be null");
        BigDecimal valueInJoules = value.multiply(unit.getFactor(), ScientificMath.CALCULATION_CONTEXT);
        return new Energy(valueInJoules);
    }

    public static Energy of(String valueStr, EnergyUnit unit) {
        Objects.requireNonNull(valueStr, "Value string must not be null");
        return of(new BigDecimal(valueStr), unit);
    }

    public BigDecimal in(EnergyUnit unit) {
        Objects.requireNonNull(unit, "Unit must not be null");
        return valueInJoules.divide(unit.getFactor(), ScientificMath.CALCULATION_CONTEXT);
    }

    public Energy add(Energy other) {
        Objects.requireNonNull(other, "Other energy must not be null");
        BigDecimal result = this.valueInJoules.add(other.valueInJoules, ScientificMath.CALCULATION_CONTEXT);
        return new Energy(result);
    }

    public Energy subtract(Energy other) {
        Objects.requireNonNull(other, "Other energy must not be null");
        BigDecimal result = this.valueInJoules.subtract(other.valueInJoules, ScientificMath.CALCULATION_CONTEXT);
        return new Energy(result);
    }

    public Energy multiply(BigDecimal scalar) {
        Objects.requireNonNull(scalar, "Scalar must not be null");
        // Energy can be multiplied by negative scalars
        BigDecimal result = this.valueInJoules.multiply(scalar, ScientificMath.CALCULATION_CONTEXT);
        return new Energy(result);
    }

    public Energy divide(BigDecimal scalar) {
        Objects.requireNonNull(scalar, "Scalar must not be null");
        if (scalar.compareTo(BigDecimal.ZERO) == 0) {
            throw new ScientificArithmeticException("Division by zero");
        }
        // Energy can be divided by negative scalars
        BigDecimal result = this.valueInJoules.divide(scalar, ScientificMath.CALCULATION_CONTEXT);
        return new Energy(result);
    }

    @Override
    public int compareTo(Energy other) {
        Objects.requireNonNull(other, "Other energy must not be null");
        return this.valueInJoules.compareTo(other.valueInJoules);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Energy energy = (Energy) o;
        return this.valueInJoules.compareTo(energy.valueInJoules) == 0;
    }

    @Override
    public int hashCode() {
        return ScientificMath.scaleIndependentHashCode(valueInJoules);
    }

    @Override
    public String toString() {
        return valueInJoules.stripTrailingZeros().toPlainString() + " J";
    }
}
