package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;
import java.util.Objects;

public final class MolarEntropy {
    private final BigDecimal valueInJoulesPerMoleKelvin;

    private MolarEntropy(BigDecimal valueInJoulesPerMoleKelvin) {
        this.valueInJoulesPerMoleKelvin = valueInJoulesPerMoleKelvin.stripTrailingZeros();
    }

    public static MolarEntropy of(String value, MolarEntropyUnit unit) {
        Objects.requireNonNull(value, "Value must not be null");
        return of(new BigDecimal(value), unit);
    }

    public static MolarEntropy of(BigDecimal value, MolarEntropyUnit unit) {
        Objects.requireNonNull(value, "Value must not be null");
        Objects.requireNonNull(unit, "Unit must not be null");
        return new MolarEntropy(value.multiply(unit.getFactorToCanonical(), ScientificMath.CALCULATION_CONTEXT));
    }

    public BigDecimal in(MolarEntropyUnit unit) {
        Objects.requireNonNull(unit, "Unit must not be null");
        return valueInJoulesPerMoleKelvin.divide(unit.getFactorToCanonical(), ScientificMath.CALCULATION_CONTEXT);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MolarEntropy that = (MolarEntropy) o;
        return valueInJoulesPerMoleKelvin.compareTo(that.valueInJoulesPerMoleKelvin) == 0;
    }

    @Override
    public int hashCode() {
        return ScientificMath.scaleIndependentHashCode(valueInJoulesPerMoleKelvin);
    }

    @Override
    public String toString() {
        return valueInJoulesPerMoleKelvin.stripTrailingZeros().toPlainString() + " J/(mol*K)";
    }
}
