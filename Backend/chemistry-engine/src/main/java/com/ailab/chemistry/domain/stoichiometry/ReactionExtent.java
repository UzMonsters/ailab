package com.ailab.chemistry.domain.stoichiometry;

import com.ailab.chemistry.domain.measurement.AmountOfSubstance;
import com.ailab.chemistry.domain.measurement.AmountOfSubstanceUnit;
import com.ailab.chemistry.domain.measurement.ScientificMath;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

public final class ReactionExtent implements Comparable<ReactionExtent> {

    private final AmountOfSubstance extentInMoles;
    private final AmountOfSubstance lowerBoundInMoles;
    private final AmountOfSubstance upperBoundInMoles;

    public ReactionExtent(AmountOfSubstance extentInMoles, AmountOfSubstance lowerBoundInMoles, AmountOfSubstance upperBoundInMoles) {
        this.extentInMoles = Objects.requireNonNull(extentInMoles, "Extent in moles must not be null");
        this.lowerBoundInMoles = lowerBoundInMoles;
        this.upperBoundInMoles = upperBoundInMoles;
    }

    public ReactionExtent(AmountOfSubstance extentInMoles) {
        this(extentInMoles, null, null);
    }

    public static ReactionExtent calculate(AmountOfSubstance moles, BigInteger coefficient) {
        Objects.requireNonNull(moles, "Moles must not be null");
        Objects.requireNonNull(coefficient, "Coefficient must not be null");
        if (coefficient.compareTo(BigInteger.ZERO) <= 0) {
            throw new StoichiometryException(StoichiometryErrorCode.INVALID_COEFFICIENT, "Coefficient must be positive");
        }
        AmountOfSubstance extent = moles.divide(new BigDecimal(coefficient));
        return new ReactionExtent(extent);
    }

    public static ReactionExtent calculate(AmountOfSubstance moles, AmountOfSubstance lowerMoles, AmountOfSubstance upperMoles, BigInteger coefficient) {
        Objects.requireNonNull(moles, "Moles must not be null");
        Objects.requireNonNull(coefficient, "Coefficient must not be null");
        BigDecimal coeffDecimal = new BigDecimal(coefficient);
        AmountOfSubstance extent = moles.divide(coeffDecimal);
        AmountOfSubstance low = lowerMoles != null ? lowerMoles.divide(coeffDecimal) : null;
        AmountOfSubstance upp = upperMoles != null ? upperMoles.divide(coeffDecimal) : null;
        return new ReactionExtent(extent, low, upp);
    }

    public AmountOfSubstance getExtentInMoles() {
        return extentInMoles;
    }

    public AmountOfSubstance getLowerBoundInMoles() {
        return lowerBoundInMoles;
    }

    public AmountOfSubstance getUpperBoundInMoles() {
        return upperBoundInMoles;
    }

    public BigDecimal getValueInMoles() {
        return extentInMoles.in(AmountOfSubstanceUnit.MOLE);
    }

    @Override
    public int compareTo(ReactionExtent other) {
        Objects.requireNonNull(other, "Other extent must not be null");
        return this.extentInMoles.compareTo(other.extentInMoles);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReactionExtent that = (ReactionExtent) o;
        return extentInMoles.equals(that.extentInMoles);
    }

    @Override
    public int hashCode() {
        return extentInMoles.hashCode();
    }

    @Override
    public String toString() {
        return extentInMoles.toString() + " extent";
    }
}
