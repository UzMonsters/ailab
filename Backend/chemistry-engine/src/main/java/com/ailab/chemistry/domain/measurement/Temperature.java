package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;
import java.util.Objects;
import com.ailab.chemistry.domain.measurement.exception.BelowAbsoluteZeroException;
import com.ailab.chemistry.domain.measurement.exception.NegativeQuantityException;
import com.ailab.chemistry.domain.measurement.exception.ScientificArithmeticException;

public final class Temperature implements Comparable<Temperature> {
    private final BigDecimal valueInKelvin;

    private Temperature(BigDecimal valueInKelvin) {
        if (valueInKelvin.compareTo(BigDecimal.ZERO) < 0) {
            throw new BelowAbsoluteZeroException("Temperature cannot be below absolute zero (0 K)");
        }
        this.valueInKelvin = valueInKelvin;
    }

    public static Temperature of(BigDecimal value, TemperatureUnit unit) {
        Objects.requireNonNull(value, "Value must not be null");
        Objects.requireNonNull(unit, "Unit must not be null");
        
        BigDecimal kelvinValue;
        if (unit == TemperatureUnit.KELVIN) {
            kelvinValue = value;
        } else {
            kelvinValue = value.add(ScientificConstants.CELSIUS_TO_KELVIN_OFFSET, ScientificMath.CALCULATION_CONTEXT);
        }

        if (kelvinValue.compareTo(BigDecimal.ZERO) < 0) {
            throw new BelowAbsoluteZeroException("Temperature cannot be below absolute zero: " + value + " " + unit.getSymbol());
        }
        return new Temperature(kelvinValue);
    }

    public static Temperature of(String valueStr, TemperatureUnit unit) {
        Objects.requireNonNull(valueStr, "Value string must not be null");
        return of(new BigDecimal(valueStr), unit);
    }

    public BigDecimal in(TemperatureUnit unit) {
        Objects.requireNonNull(unit, "Unit must not be null");
        if (unit == TemperatureUnit.KELVIN) {
            return valueInKelvin;
        } else {
            return valueInKelvin.subtract(ScientificConstants.CELSIUS_TO_KELVIN_OFFSET, ScientificMath.CALCULATION_CONTEXT);
        }
    }

    public Temperature add(TemperatureDelta delta) {
        Objects.requireNonNull(delta, "Delta must not be null");
        BigDecimal result = this.valueInKelvin.add(delta.in(TemperatureUnit.KELVIN), ScientificMath.CALCULATION_CONTEXT);
        return new Temperature(result);
    }

    public Temperature subtract(TemperatureDelta delta) {
        Objects.requireNonNull(delta, "Delta must not be null");
        BigDecimal result = this.valueInKelvin.subtract(delta.in(TemperatureUnit.KELVIN), ScientificMath.CALCULATION_CONTEXT);
        return new Temperature(result);
    }

    public TemperatureDelta subtract(Temperature other) {
        Objects.requireNonNull(other, "Other absolute temperature must not be null");
        BigDecimal result = this.valueInKelvin.subtract(other.valueInKelvin, ScientificMath.CALCULATION_CONTEXT);
        return TemperatureDelta.kelvin(result);
    }

    public Temperature multiply(BigDecimal scalar) {
        Objects.requireNonNull(scalar, "Scalar must not be null");
        if (scalar.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Temperature multiplier must be non-negative: " + scalar);
        }
        BigDecimal result = this.valueInKelvin.multiply(scalar, ScientificMath.CALCULATION_CONTEXT);
        return new Temperature(result);
    }

    public Temperature divide(BigDecimal scalar) {
        Objects.requireNonNull(scalar, "Scalar must not be null");
        if (scalar.compareTo(BigDecimal.ZERO) == 0) {
            throw new ScientificArithmeticException("Division by zero");
        }
        if (scalar.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Temperature divisor must be positive: " + scalar);
        }
        BigDecimal result = this.valueInKelvin.divide(scalar, ScientificMath.CALCULATION_CONTEXT);
        return new Temperature(result);
    }

    @Override
    public int compareTo(Temperature other) {
        Objects.requireNonNull(other, "Other temperature must not be null");
        return this.valueInKelvin.compareTo(other.valueInKelvin);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Temperature that = (Temperature) o;
        return this.valueInKelvin.compareTo(that.valueInKelvin) == 0;
    }

    @Override
    public int hashCode() {
        return ScientificMath.scaleIndependentHashCode(valueInKelvin);
    }

    @Override
    public String toString() {
        return valueInKelvin.stripTrailingZeros().toPlainString() + " K";
    }
}
