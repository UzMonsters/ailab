package com.ailab.chemistry.domain.solution;

import com.ailab.chemistry.domain.measurement.ScientificMath;

import java.math.BigDecimal;
import java.util.Objects;

public final class Molality implements Comparable<Molality> {

    private final BigDecimal valueInMolPerKg;

    private Molality(BigDecimal valueInMolPerKg) {
        Objects.requireNonNull(valueInMolPerKg, "Molality value must not be null");
        if (valueInMolPerKg.compareTo(BigDecimal.ZERO) < 0) {
            throw new SolutionException(SolutionErrorCode.INVALID_CONCENTRATION, "Molality cannot be negative: " + valueInMolPerKg);
        }
        this.valueInMolPerKg = valueInMolPerKg;
    }

    public static Molality of(BigDecimal value) {
        return new Molality(value);
    }

    public static Molality of(String valueStr) {
        Objects.requireNonNull(valueStr, "Value string must not be null");
        return of(new BigDecimal(valueStr));
    }

    public BigDecimal getValueInMolPerKg() {
        return valueInMolPerKg;
    }

    @Override
    public int compareTo(Molality other) {
        Objects.requireNonNull(other, "Other molality must not be null");
        return this.valueInMolPerKg.compareTo(other.valueInMolPerKg);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Molality molality = (Molality) o;
        return this.valueInMolPerKg.compareTo(molality.valueInMolPerKg) == 0;
    }

    @Override
    public int hashCode() {
        return ScientificMath.scaleIndependentHashCode(valueInMolPerKg);
    }

    @Override
    public String toString() {
        return valueInMolPerKg.stripTrailingZeros().toPlainString() + " mol/kg";
    }
}
