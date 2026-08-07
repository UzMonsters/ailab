package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;
import java.util.Objects;
import com.ailab.chemistry.domain.measurement.exception.NegativeQuantityException;
import com.ailab.chemistry.domain.measurement.exception.ScientificArithmeticException;

public final class MassConcentration implements Comparable<MassConcentration> {
    private final BigDecimal valueInGramsPerLiter;

    private MassConcentration(BigDecimal valueInGramsPerLiter) {
        if (valueInGramsPerLiter.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Mass concentration cannot be negative");
        }
        this.valueInGramsPerLiter = valueInGramsPerLiter;
    }

    public static MassConcentration of(BigDecimal value, MassConcentrationUnit unit) {
        Objects.requireNonNull(value, "Value must not be null");
        Objects.requireNonNull(unit, "Unit must not be null");
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Mass concentration value cannot be negative: " + value);
        }
        BigDecimal valueInGramsPerLiter = value.multiply(unit.getFactor(), ScientificMath.CALCULATION_CONTEXT);
        return new MassConcentration(valueInGramsPerLiter);
    }

    public static MassConcentration of(String valueStr, MassConcentrationUnit unit) {
        Objects.requireNonNull(valueStr, "Value string must not be null");
        return of(new BigDecimal(valueStr), unit);
    }

    public BigDecimal in(MassConcentrationUnit unit) {
        Objects.requireNonNull(unit, "Unit must not be null");
        return valueInGramsPerLiter.divide(unit.getFactor(), ScientificMath.CALCULATION_CONTEXT);
    }

    public MassConcentration add(MassConcentration other) {
        Objects.requireNonNull(other, "Other concentration must not be null");
        BigDecimal result = this.valueInGramsPerLiter.add(other.valueInGramsPerLiter, ScientificMath.CALCULATION_CONTEXT);
        return new MassConcentration(result);
    }

    public MassConcentration subtract(MassConcentration other) {
        Objects.requireNonNull(other, "Other concentration must not be null");
        BigDecimal result = this.valueInGramsPerLiter.subtract(other.valueInGramsPerLiter, ScientificMath.CALCULATION_CONTEXT);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Subtraction would result in a negative mass concentration: " + result);
        }
        return new MassConcentration(result);
    }

    public MassConcentration multiply(BigDecimal scalar) {
        Objects.requireNonNull(scalar, "Scalar must not be null");
        if (scalar.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Concentration multiplier must be non-negative: " + scalar);
        }
        BigDecimal result = this.valueInGramsPerLiter.multiply(scalar, ScientificMath.CALCULATION_CONTEXT);
        return new MassConcentration(result);
    }

    public MassConcentration divide(BigDecimal scalar) {
        Objects.requireNonNull(scalar, "Scalar must not be null");
        if (scalar.compareTo(BigDecimal.ZERO) == 0) {
            throw new ScientificArithmeticException("Division by zero");
        }
        if (scalar.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Concentration divisor must be positive: " + scalar);
        }
        BigDecimal result = this.valueInGramsPerLiter.divide(scalar, ScientificMath.CALCULATION_CONTEXT);
        return new MassConcentration(result);
    }

    @Override
    public int compareTo(MassConcentration other) {
        Objects.requireNonNull(other, "Other concentration must not be null");
        return this.valueInGramsPerLiter.compareTo(other.valueInGramsPerLiter);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MassConcentration that = (MassConcentration) o;
        return this.valueInGramsPerLiter.compareTo(that.valueInGramsPerLiter) == 0;
    }

    @Override
    public int hashCode() {
        return ScientificMath.scaleIndependentHashCode(valueInGramsPerLiter);
    }

    @Override
    public String toString() {
        return valueInGramsPerLiter.stripTrailingZeros().toPlainString() + " g/L";
    }
}
