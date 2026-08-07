package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;
import java.util.Objects;
import com.ailab.chemistry.domain.measurement.exception.NegativeQuantityException;
import com.ailab.chemistry.domain.measurement.exception.ScientificArithmeticException;

public final class MolarConcentration implements Comparable<MolarConcentration> {
    private final BigDecimal valueInMolsPerLiter;

    private MolarConcentration(BigDecimal valueInMolsPerLiter) {
        if (valueInMolsPerLiter.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Molar concentration cannot be negative");
        }
        this.valueInMolsPerLiter = valueInMolsPerLiter;
    }

    public static MolarConcentration of(BigDecimal value, MolarConcentrationUnit unit) {
        Objects.requireNonNull(value, "Value must not be null");
        Objects.requireNonNull(unit, "Unit must not be null");
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Molar concentration value cannot be negative: " + value);
        }
        BigDecimal valueInMolsPerLiter = value.multiply(unit.getFactor(), ScientificMath.CALCULATION_CONTEXT);
        return new MolarConcentration(valueInMolsPerLiter);
    }

    public static MolarConcentration of(String valueStr, MolarConcentrationUnit unit) {
        Objects.requireNonNull(valueStr, "Value string must not be null");
        return of(new BigDecimal(valueStr), unit);
    }

    public BigDecimal in(MolarConcentrationUnit unit) {
        Objects.requireNonNull(unit, "Unit must not be null");
        return valueInMolsPerLiter.divide(unit.getFactor(), ScientificMath.CALCULATION_CONTEXT);
    }

    public MolarConcentration add(MolarConcentration other) {
        Objects.requireNonNull(other, "Other concentration must not be null");
        BigDecimal result = this.valueInMolsPerLiter.add(other.valueInMolsPerLiter, ScientificMath.CALCULATION_CONTEXT);
        return new MolarConcentration(result);
    }

    public MolarConcentration subtract(MolarConcentration other) {
        Objects.requireNonNull(other, "Other concentration must not be null");
        BigDecimal result = this.valueInMolsPerLiter.subtract(other.valueInMolsPerLiter, ScientificMath.CALCULATION_CONTEXT);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Subtraction would result in a negative molar concentration: " + result);
        }
        return new MolarConcentration(result);
    }

    public MolarConcentration multiply(BigDecimal scalar) {
        Objects.requireNonNull(scalar, "Scalar must not be null");
        if (scalar.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Concentration multiplier must be non-negative: " + scalar);
        }
        BigDecimal result = this.valueInMolsPerLiter.multiply(scalar, ScientificMath.CALCULATION_CONTEXT);
        return new MolarConcentration(result);
    }

    public MolarConcentration divide(BigDecimal scalar) {
        Objects.requireNonNull(scalar, "Scalar must not be null");
        if (scalar.compareTo(BigDecimal.ZERO) == 0) {
            throw new ScientificArithmeticException("Division by zero");
        }
        if (scalar.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeQuantityException("Concentration divisor must be positive: " + scalar);
        }
        BigDecimal result = this.valueInMolsPerLiter.divide(scalar, ScientificMath.CALCULATION_CONTEXT);
        return new MolarConcentration(result);
    }

    @Override
    public int compareTo(MolarConcentration other) {
        Objects.requireNonNull(other, "Other concentration must not be null");
        return this.valueInMolsPerLiter.compareTo(other.valueInMolsPerLiter);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MolarConcentration that = (MolarConcentration) o;
        return this.valueInMolsPerLiter.compareTo(that.valueInMolsPerLiter) == 0;
    }

    @Override
    public int hashCode() {
        return ScientificMath.scaleIndependentHashCode(valueInMolsPerLiter);
    }

    @Override
    public String toString() {
        return valueInMolsPerLiter.stripTrailingZeros().toPlainString() + " mol/L";
    }
}
