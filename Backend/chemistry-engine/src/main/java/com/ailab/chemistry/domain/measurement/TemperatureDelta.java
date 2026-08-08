package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;
import java.util.Objects;
import com.ailab.chemistry.domain.measurement.exception.ScientificArithmeticException;

public final class TemperatureDelta implements Comparable<TemperatureDelta> {
    private final BigDecimal valueInKelvins;

    private TemperatureDelta(BigDecimal valueInKelvins) {
        this.valueInKelvins = valueInKelvins;
    }

    public static TemperatureDelta of(BigDecimal value, TemperatureUnit unit) {
        Objects.requireNonNull(value, "Value must not be null");
        Objects.requireNonNull(unit, "Unit must not be null");
        // Celsius delta magnitude is exactly 1:1 with Kelvin delta magnitude
        return new TemperatureDelta(value);
    }

    public static TemperatureDelta of(String valueStr, TemperatureUnit unit) {
        Objects.requireNonNull(valueStr, "Value string must not be null");
        return of(new BigDecimal(valueStr), unit);
    }

    public static TemperatureDelta kelvin(BigDecimal value) {
        return of(value, TemperatureUnit.KELVIN);
    }

    public static TemperatureDelta celsius(BigDecimal value) {
        return of(value, TemperatureUnit.CELSIUS);
    }

    public BigDecimal in(TemperatureUnit unit) {
        Objects.requireNonNull(unit, "Unit must not be null");
        return valueInKelvins;
    }

    public TemperatureDelta add(TemperatureDelta other) {
        Objects.requireNonNull(other, "Other delta must not be null");
        BigDecimal result = this.valueInKelvins.add(other.valueInKelvins, ScientificMath.CALCULATION_CONTEXT);
        return new TemperatureDelta(result);
    }

    public TemperatureDelta subtract(TemperatureDelta other) {
        Objects.requireNonNull(other, "Other delta must not be null");
        BigDecimal result = this.valueInKelvins.subtract(other.valueInKelvins, ScientificMath.CALCULATION_CONTEXT);
        return new TemperatureDelta(result);
    }

    public TemperatureDelta multiply(BigDecimal scalar) {
        Objects.requireNonNull(scalar, "Scalar must not be null");
        BigDecimal result = this.valueInKelvins.multiply(scalar, ScientificMath.CALCULATION_CONTEXT);
        return new TemperatureDelta(result);
    }

    public TemperatureDelta divide(BigDecimal scalar) {
        Objects.requireNonNull(scalar, "Scalar must not be null");
        if (scalar.compareTo(BigDecimal.ZERO) == 0) {
            throw new ScientificArithmeticException("Division by zero");
        }
        BigDecimal result = this.valueInKelvins.divide(scalar, ScientificMath.CALCULATION_CONTEXT);
        return new TemperatureDelta(result);
    }

    @Override
    public int compareTo(TemperatureDelta other) {
        Objects.requireNonNull(other, "Other delta must not be null");
        return this.valueInKelvins.compareTo(other.valueInKelvins);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TemperatureDelta that = (TemperatureDelta) o;
        return this.valueInKelvins.compareTo(that.valueInKelvins) == 0;
    }

    @Override
    public int hashCode() {
        return ScientificMath.scaleIndependentHashCode(valueInKelvins);
    }

    @Override
    public String toString() {
        return valueInKelvins.stripTrailingZeros().toPlainString() + " K delta";
    }
}
