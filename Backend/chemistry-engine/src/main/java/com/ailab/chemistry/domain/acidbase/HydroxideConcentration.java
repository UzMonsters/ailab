package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.measurement.MolarConcentration;
import com.ailab.chemistry.domain.measurement.MolarConcentrationUnit;

import java.math.BigDecimal;
import java.util.Objects;

public final class HydroxideConcentration {

    private final MolarConcentration concentration;

    public HydroxideConcentration(MolarConcentration concentration) {
        this.concentration = Objects.requireNonNull(concentration, "MolarConcentration must not be null");
        if (concentration.in(MolarConcentrationUnit.MOL_PER_LITER).compareTo(BigDecimal.ZERO) <= 0) {
            throw new AcidBaseCalculationException(AcidBaseCalculationErrorCode.NON_POSITIVE_CONCENTRATION, "Hydroxide concentration must be strictly positive (> 0)");
        }
    }

    public static HydroxideConcentration of(BigDecimal value) {
        return new HydroxideConcentration(MolarConcentration.of(value, MolarConcentrationUnit.MOL_PER_LITER));
    }

    public static HydroxideConcentration of(double value) {
        return new HydroxideConcentration(MolarConcentration.of(BigDecimal.valueOf(value), MolarConcentrationUnit.MOL_PER_LITER));
    }

    public MolarConcentration getConcentration() {
        return concentration;
    }

    public BigDecimal getValue() {
        return concentration.in(MolarConcentrationUnit.MOL_PER_LITER);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HydroxideConcentration that = (HydroxideConcentration) o;
        return concentration.equals(that.concentration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(concentration);
    }

    @Override
    public String toString() {
        return "[OH-] = " + getValue().toEngineeringString() + " mol/L";
    }
}
