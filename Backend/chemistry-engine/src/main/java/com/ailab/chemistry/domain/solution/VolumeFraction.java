package com.ailab.chemistry.domain.solution;

import com.ailab.chemistry.domain.measurement.ScientificMath;

import java.math.BigDecimal;
import java.util.Objects;

public final class VolumeFraction implements Comparable<VolumeFraction> {

    private final BigDecimal value;

    public VolumeFraction(BigDecimal value) {
        Objects.requireNonNull(value, "Volume fraction value must not be null");
        if (value.compareTo(BigDecimal.ZERO) < 0 || value.compareTo(BigDecimal.ONE) > 0) {
            throw new SolutionException(SolutionErrorCode.INVALID_FRACTION, "Volume fraction must be between 0 and 1: " + value);
        }
        this.value = value;
    }

    public static VolumeFraction of(BigDecimal value) {
        return new VolumeFraction(value);
    }

    public static VolumeFraction of(String valueStr) {
        Objects.requireNonNull(valueStr, "Value string must not be null");
        return of(new BigDecimal(valueStr));
    }

    public BigDecimal getValue() {
        return value;
    }

    public BigDecimal getVolumePercentage() {
        return value.multiply(new BigDecimal("100"), ScientificMath.CALCULATION_CONTEXT);
    }

    @Override
    public int compareTo(VolumeFraction other) {
        Objects.requireNonNull(other, "Other volume fraction must not be null");
        return this.value.compareTo(other.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VolumeFraction that = (VolumeFraction) o;
        return this.value.compareTo(that.value) == 0;
    }

    @Override
    public int hashCode() {
        return ScientificMath.scaleIndependentHashCode(value);
    }

    @Override
    public String toString() {
        return value.stripTrailingZeros().toPlainString() + " (" + getVolumePercentage().stripTrailingZeros().toPlainString() + "% v/v)";
    }
}
