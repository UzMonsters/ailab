package com.ailab.chemistry.domain.acidbase;

import java.math.BigDecimal;
import java.util.Objects;

public final class EquilibriumResidual {

    private final BigDecimal value;

    public EquilibriumResidual(BigDecimal value) {
        this.value = Objects.requireNonNull(value, "Residual value must not be null").abs();
    }

    public static EquilibriumResidual zero() {
        return new EquilibriumResidual(BigDecimal.ZERO);
    }

    public static EquilibriumResidual of(BigDecimal value) {
        return new EquilibriumResidual(value);
    }

    public BigDecimal getValue() {
        return value;
    }

    public boolean isBelowTolerance(BigDecimal tolerance) {
        return value.compareTo(tolerance) <= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EquilibriumResidual that = (EquilibriumResidual) o;
        return value.compareTo(that.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value.stripTrailingZeros());
    }

    @Override
    public String toString() {
        return value.toEngineeringString();
    }
}
