package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;
import java.util.Objects;

public final class PhValue {
    private final BigDecimal value;

    public PhValue(BigDecimal value) {
        if (value == null) {
            throw new IllegalArgumentException("pH value cannot be null");
        }
        this.value = value.stripTrailingZeros();
    }

    public static PhValue of(double value) {
        return new PhValue(BigDecimal.valueOf(value));
    }

    public static PhValue of(String value) {
        return new PhValue(new BigDecimal(value));
    }

    public BigDecimal getValue() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhValue phValue = (PhValue) o;
        return value.compareTo(phValue.value) == 0;
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
