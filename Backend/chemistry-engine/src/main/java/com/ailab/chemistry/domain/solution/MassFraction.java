package com.ailab.chemistry.domain.solution;

import com.ailab.chemistry.domain.measurement.ScientificMath;

import java.math.BigDecimal;
import java.util.Objects;

public final class MassFraction implements Comparable<MassFraction> {

    private final BigDecimal value;

    public MassFraction(BigDecimal value) {
        Objects.requireNonNull(value, "Mass fraction value must not be null");
        if (value.compareTo(BigDecimal.ZERO) < 0 || value.compareTo(BigDecimal.ONE) > 0) {
            throw new SolutionException(SolutionErrorCode.INVALID_FRACTION, "Mass fraction must be between 0 and 1: " + value);
        }
        this.value = value;
    }

    public static MassFraction of(BigDecimal value) {
        return new MassFraction(value);
    }

    public static MassFraction of(String valueStr) {
        Objects.requireNonNull(valueStr, "Value string must not be null");
        return of(new BigDecimal(valueStr));
    }

    public BigDecimal getValue() {
        return value;
    }

    public BigDecimal getMassPercentage() {
        return value.multiply(new BigDecimal("100"), ScientificMath.CALCULATION_CONTEXT);
    }

    @Override
    public int compareTo(MassFraction other) {
        Objects.requireNonNull(other, "Other mass fraction must not be null");
        return this.value.compareTo(other.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MassFraction that = (MassFraction) o;
        return this.value.compareTo(that.value) == 0;
    }

    @Override
    public int hashCode() {
        return ScientificMath.scaleIndependentHashCode(value);
    }

    @Override
    public String toString() {
        return value.stripTrailingZeros().toPlainString() + " (" + getMassPercentage().stripTrailingZeros().toPlainString() + "%)";
    }
}
