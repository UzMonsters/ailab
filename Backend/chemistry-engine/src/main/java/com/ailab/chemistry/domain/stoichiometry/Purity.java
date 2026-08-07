package com.ailab.chemistry.domain.stoichiometry;

import com.ailab.chemistry.domain.measurement.ScientificMath;

import java.math.BigDecimal;
import java.util.Objects;

public final class Purity {

    public static final Purity PURE = new Purity(new BigDecimal("100"));

    private final BigDecimal percentage;

    private Purity(BigDecimal percentage) {
        Objects.requireNonNull(percentage, "Percentage must not be null");
        if (percentage.compareTo(BigDecimal.ZERO) <= 0 || percentage.compareTo(new BigDecimal("100")) > 0) {
            throw new StoichiometryException(StoichiometryErrorCode.INVALID_PURITY,
                    "Purity percentage must be strictly greater than 0% and at most 100%: " + percentage);
        }
        this.percentage = percentage;
    }

    public static Purity ofPercentage(BigDecimal percentage) {
        return new Purity(percentage);
    }

    public static Purity ofPercentage(String percentageStr) {
        Objects.requireNonNull(percentageStr, "Percentage string must not be null");
        return ofPercentage(new BigDecimal(percentageStr));
    }

    public static Purity ofFraction(BigDecimal fraction) {
        Objects.requireNonNull(fraction, "Fraction must not be null");
        return ofPercentage(fraction.multiply(new BigDecimal("100"), ScientificMath.CALCULATION_CONTEXT));
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public BigDecimal getFraction() {
        return percentage.divide(new BigDecimal("100"), ScientificMath.CALCULATION_CONTEXT);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Purity purity = (Purity) o;
        return percentage.compareTo(purity.percentage) == 0;
    }

    @Override
    public int hashCode() {
        return ScientificMath.scaleIndependentHashCode(percentage);
    }

    @Override
    public String toString() {
        return percentage.stripTrailingZeros().toPlainString() + "%";
    }
}
