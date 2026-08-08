package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;
import java.util.Objects;

public final class RefractiveIndex {
    private final BigDecimal value;

    public RefractiveIndex(BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Refractive index must be positive: " + value);
        }
        this.value = value.stripTrailingZeros();
    }

    public static RefractiveIndex of(double value) {
        return new RefractiveIndex(BigDecimal.valueOf(value));
    }

    public static RefractiveIndex of(String value) {
        return new RefractiveIndex(new BigDecimal(value));
    }

    public BigDecimal getValue() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefractiveIndex that = (RefractiveIndex) o;
        return value.compareTo(that.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value.stripTrailingZeros());
    }

    @Override
    public String toString() {
        return value.toPlainString();
    }
}
