package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;
import java.util.Objects;

public final class MolarEnergy {
    private final BigDecimal valueInJoulesPerMole;

    private MolarEnergy(BigDecimal valueInJoulesPerMole) {
        this.valueInJoulesPerMole = valueInJoulesPerMole.stripTrailingZeros();
    }

    public static MolarEnergy of(String value, MolarEnergyUnit unit) {
        Objects.requireNonNull(value, "Value must not be null");
        return of(new BigDecimal(value), unit);
    }

    public static MolarEnergy of(BigDecimal value, MolarEnergyUnit unit) {
        Objects.requireNonNull(value, "Value must not be null");
        Objects.requireNonNull(unit, "Unit must not be null");
        return new MolarEnergy(value.multiply(unit.getFactorToCanonical(), ScientificMath.CALCULATION_CONTEXT));
    }

    public BigDecimal in(MolarEnergyUnit unit) {
        Objects.requireNonNull(unit, "Unit must not be null");
        return valueInJoulesPerMole.divide(unit.getFactorToCanonical(), ScientificMath.CALCULATION_CONTEXT);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MolarEnergy that = (MolarEnergy) o;
        return valueInJoulesPerMole.compareTo(that.valueInJoulesPerMole) == 0;
    }

    @Override
    public int hashCode() {
        return ScientificMath.scaleIndependentHashCode(valueInJoulesPerMole);
    }

    @Override
    public String toString() {
        return in(MolarEnergyUnit.KILOJOULE_PER_MOLE).stripTrailingZeros().toPlainString() + " kJ/mol";
    }
}
