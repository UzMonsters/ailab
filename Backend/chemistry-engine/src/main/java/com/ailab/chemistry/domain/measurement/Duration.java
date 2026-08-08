package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;
import java.util.Objects;
import com.ailab.chemistry.domain.measurement.exception.NegativeQuantityException;
import com.ailab.chemistry.domain.measurement.exception.ScientificArithmeticException;

/**
 * Immutable Duration value object representing elapsed simulation time.
 * Framework-independent and separate from java.time types.
 * Value in seconds must be non-negative.
 */
public final class Duration implements Comparable<Duration> {
    private final BigDecimal valueInSeconds;

    private Duration(BigDecimal valueInSeconds) {
        if (valueInSeconds.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Duration cannot be negative");
        }
        this.valueInSeconds = valueInSeconds;
    }

    public static Duration of(BigDecimal value, DurationUnit unit) {
        Objects.requireNonNull(value, "Value must not be null");
        Objects.requireNonNull(unit, "Unit must not be null");
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Duration value cannot be negative: " + value);
        }
        BigDecimal valueInSeconds = value.multiply(unit.getFactor(), ScientificMath.CALCULATION_CONTEXT);
        return new Duration(valueInSeconds);
    }

    public static Duration of(String valueStr, DurationUnit unit) {
        Objects.requireNonNull(valueStr, "Value string must not be null");
        return of(new BigDecimal(valueStr), unit);
    }

    public BigDecimal in(DurationUnit unit) {
        Objects.requireNonNull(unit, "Unit must not be null");
        return valueInSeconds.divide(unit.getFactor(), ScientificMath.CALCULATION_CONTEXT);
    }

    public Duration add(Duration other) {
        Objects.requireNonNull(other, "Other duration must not be null");
        BigDecimal result = this.valueInSeconds.add(other.valueInSeconds, ScientificMath.CALCULATION_CONTEXT);
        return new Duration(result);
    }

    public Duration subtract(Duration other) {
        Objects.requireNonNull(other, "Other duration must not be null");
        BigDecimal result = this.valueInSeconds.subtract(other.valueInSeconds, ScientificMath.CALCULATION_CONTEXT);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Subtraction would result in a negative duration: " + result);
        }
        return new Duration(result);
    }

    public Duration multiply(BigDecimal scalar) {
        Objects.requireNonNull(scalar, "Scalar must not be null");
        if (scalar.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Duration multiplier must be non-negative: " + scalar);
        }
        BigDecimal result = this.valueInSeconds.multiply(scalar, ScientificMath.CALCULATION_CONTEXT);
        return new Duration(result);
    }

    public Duration divide(BigDecimal scalar) {
        Objects.requireNonNull(scalar, "Scalar must not be null");
        if (scalar.compareTo(BigDecimal.ZERO) == 0) {
            throw new ScientificArithmeticException("Division by zero");
        }
        if (scalar.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Duration divisor must be positive: " + scalar);
        }
        BigDecimal result = this.valueInSeconds.divide(scalar, ScientificMath.CALCULATION_CONTEXT);
        return new Duration(result);
    }

    @Override
    public int compareTo(Duration other) {
        Objects.requireNonNull(other, "Other duration must not be null");
        return this.valueInSeconds.compareTo(other.valueInSeconds);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Duration duration = (Duration) o;
        return this.valueInSeconds.compareTo(duration.valueInSeconds) == 0;
    }

    @Override
    public int hashCode() {
        return ScientificMath.scaleIndependentHashCode(valueInSeconds);
    }

    @Override
    public String toString() {
        return valueInSeconds.stripTrailingZeros().toPlainString() + " s";
    }
}
