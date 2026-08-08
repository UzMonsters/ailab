package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;
import java.util.Objects;
import com.ailab.chemistry.domain.measurement.exception.NegativeQuantityException;
import com.ailab.chemistry.domain.measurement.exception.ScientificArithmeticException;

public final class AmountOfSubstance implements Comparable<AmountOfSubstance> {
    private final BigDecimal valueInMoles;

    private AmountOfSubstance(BigDecimal valueInMoles) {
        if (valueInMoles.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Amount of substance cannot be negative");
        }
        this.valueInMoles = valueInMoles;
    }

    public static AmountOfSubstance of(BigDecimal value, AmountOfSubstanceUnit unit) {
        Objects.requireNonNull(value, "Value must not be null");
        Objects.requireNonNull(unit, "Unit must not be null");
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Amount of substance value cannot be negative: " + value);
        }
        BigDecimal valueInMoles = value.multiply(unit.getFactor(), ScientificMath.CALCULATION_CONTEXT);
        return new AmountOfSubstance(valueInMoles);
    }

    public static AmountOfSubstance of(String valueStr, AmountOfSubstanceUnit unit) {
        Objects.requireNonNull(valueStr, "Value string must not be null");
        return of(new BigDecimal(valueStr), unit);
    }

    public BigDecimal in(AmountOfSubstanceUnit unit) {
        Objects.requireNonNull(unit, "Unit must not be null");
        return valueInMoles.divide(unit.getFactor(), ScientificMath.CALCULATION_CONTEXT);
    }

    public AmountOfSubstance add(AmountOfSubstance other) {
        Objects.requireNonNull(other, "Other amount must not be null");
        BigDecimal result = this.valueInMoles.add(other.valueInMoles, ScientificMath.CALCULATION_CONTEXT);
        return new AmountOfSubstance(result);
    }

    public AmountOfSubstance subtract(AmountOfSubstance other) {
        Objects.requireNonNull(other, "Other amount must not be null");
        BigDecimal result = this.valueInMoles.subtract(other.valueInMoles, ScientificMath.CALCULATION_CONTEXT);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Subtraction would result in a negative amount: " + result);
        }
        return new AmountOfSubstance(result);
    }

    public AmountOfSubstance multiply(BigDecimal scalar) {
        Objects.requireNonNull(scalar, "Scalar must not be null");
        if (scalar.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Amount multiplier must be non-negative: " + scalar);
        }
        BigDecimal result = this.valueInMoles.multiply(scalar, ScientificMath.CALCULATION_CONTEXT);
        return new AmountOfSubstance(result);
    }

    public AmountOfSubstance divide(BigDecimal scalar) {
        Objects.requireNonNull(scalar, "Scalar must not be null");
        if (scalar.compareTo(BigDecimal.ZERO) == 0) {
            throw new ScientificArithmeticException("Division by zero");
        }
        if (scalar.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Amount divisor must be positive: " + scalar);
        }
        BigDecimal result = this.valueInMoles.divide(scalar, ScientificMath.CALCULATION_CONTEXT);
        return new AmountOfSubstance(result);
    }

    @Override
    public int compareTo(AmountOfSubstance other) {
        Objects.requireNonNull(other, "Other amount must not be null");
        return this.valueInMoles.compareTo(other.valueInMoles);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AmountOfSubstance that = (AmountOfSubstance) o;
        return this.valueInMoles.compareTo(that.valueInMoles) == 0;
    }

    @Override
    public int hashCode() {
        return ScientificMath.scaleIndependentHashCode(valueInMoles);
    }

    @Override
    public String toString() {
        return valueInMoles.stripTrailingZeros().toPlainString() + " mol";
    }
}
