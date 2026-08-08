package com.ailab.chemistry.domain.solution;

import com.ailab.chemistry.domain.measurement.ScientificMath;

import java.math.BigDecimal;
import java.util.Objects;

public final class MoleFraction implements Comparable<MoleFraction> {

    private final BigDecimal value;

    public MoleFraction(BigDecimal value) {
        Objects.requireNonNull(value, "Mole fraction value must not be null");
        if (value.compareTo(BigDecimal.ZERO) < 0 || value.compareTo(BigDecimal.ONE) > 0) {
            throw new SolutionException(SolutionErrorCode.INVALID_FRACTION, "Mole fraction must be between 0 and 1: " + value);
        }
        this.value = value;
    }

    public static MoleFraction of(BigDecimal value) {
        return new MoleFraction(value);
    }

    public static MoleFraction of(String valueStr) {
        Objects.requireNonNull(valueStr, "Value string must not be null");
        return of(new BigDecimal(valueStr));
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public int compareTo(MoleFraction other) {
        Objects.requireNonNull(other, "Other mole fraction must not be null");
        return this.value.compareTo(other.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MoleFraction that = (MoleFraction) o;
        return this.value.compareTo(that.value) == 0;
    }

    @Override
    public int hashCode() {
        return ScientificMath.scaleIndependentHashCode(value);
    }

    @Override
    public String toString() {
        return value.stripTrailingZeros().toPlainString();
    }
}
