package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;
import java.util.Objects;
import com.ailab.chemistry.domain.measurement.exception.IncompatibleUnitException;
import com.ailab.chemistry.domain.measurement.exception.InvalidPercentageConcentrationException;
import com.ailab.chemistry.domain.measurement.exception.ScientificArithmeticException;

public final class PercentageConcentration implements Comparable<PercentageConcentration> {
    private final BigDecimal value;
    private final ConcentrationBasis basis;

    private PercentageConcentration(BigDecimal value, ConcentrationBasis basis) {
        if (value.compareTo(BigDecimal.ZERO) < 0 || value.compareTo(new BigDecimal("100")) > 0) {
            throw new InvalidPercentageConcentrationException("Percentage value must be between 0 and 100: " + value);
        }
        this.value = value;
        this.basis = Objects.requireNonNull(basis, "Concentration basis must not be null");
    }

    public static PercentageConcentration of(BigDecimal value, ConcentrationBasis basis) {
        Objects.requireNonNull(value, "Value must not be null");
        return new PercentageConcentration(value, basis);
    }

    public static PercentageConcentration of(String valueStr, ConcentrationBasis basis) {
        Objects.requireNonNull(valueStr, "Value string must not be null");
        return new PercentageConcentration(new BigDecimal(valueStr), basis);
    }

    public BigDecimal getValue() {
        return value;
    }

    public ConcentrationBasis getBasis() {
        return basis;
    }

    public PercentageConcentration add(PercentageConcentration other) {
        Objects.requireNonNull(other, "Other percentage concentration must not be null");
        if (this.basis != other.basis) {
            throw new IncompatibleUnitException("Cannot add percentage concentrations with different bases: " 
                    + this.basis + " and " + other.basis);
        }
        BigDecimal result = this.value.add(other.value, ScientificMath.CALCULATION_CONTEXT);
        return new PercentageConcentration(result, this.basis);
    }

    public PercentageConcentration subtract(PercentageConcentration other) {
        Objects.requireNonNull(other, "Other percentage concentration must not be null");
        if (this.basis != other.basis) {
            throw new IncompatibleUnitException("Cannot subtract percentage concentrations with different bases: " 
                    + this.basis + " and " + other.basis);
        }
        BigDecimal result = this.value.subtract(other.value, ScientificMath.CALCULATION_CONTEXT);
        return new PercentageConcentration(result, this.basis);
    }

    public PercentageConcentration multiply(BigDecimal scalar) {
        Objects.requireNonNull(scalar, "Scalar must not be null");
        if (scalar.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidPercentageConcentrationException("Multiplier must be non-negative");
        }
        BigDecimal result = this.value.multiply(scalar, ScientificMath.CALCULATION_CONTEXT);
        return new PercentageConcentration(result, this.basis);
    }

    public PercentageConcentration divide(BigDecimal scalar) {
        Objects.requireNonNull(scalar, "Scalar must not be null");
        if (scalar.compareTo(BigDecimal.ZERO) == 0) {
            throw new ScientificArithmeticException("Division by zero");
        }
        if (scalar.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidPercentageConcentrationException("Divisor must be positive");
        }
        BigDecimal result = this.value.divide(scalar, ScientificMath.CALCULATION_CONTEXT);
        return new PercentageConcentration(result, this.basis);
    }

    @Override
    public int compareTo(PercentageConcentration other) {
        Objects.requireNonNull(other, "Other percentage concentration must not be null");
        if (this.basis != other.basis) {
            throw new IncompatibleUnitException("Cannot compare percentage concentrations with different bases: "
                    + this.basis + " and " + other.basis);
        }
        return this.value.compareTo(other.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PercentageConcentration that = (PercentageConcentration) o;
        return this.basis == that.basis && this.value.compareTo(that.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(basis, ScientificMath.scaleIndependentHashCode(value));
    }

    @Override
    public String toString() {
        return value.stripTrailingZeros().toPlainString() + "% (" + basis.getSymbol() + ")";
    }
}
