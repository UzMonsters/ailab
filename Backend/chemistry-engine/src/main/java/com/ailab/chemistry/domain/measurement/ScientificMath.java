package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;
import java.math.MathContext;

public final class ScientificMath {
    public static final MathContext CALCULATION_CONTEXT = MathContext.DECIMAL128;

    private ScientificMath() {}

    /**
     * Approximates equality check for two BigDecimal values using both absolute and relative tolerance.
     * The formula is:
     * |first - second| <= absoluteTolerance OR |first - second| / max(|first|, |second|) <= relativeTolerance
     */
    public static boolean isApproximatelyEqual(
            BigDecimal first,
            BigDecimal second,
            BigDecimal absoluteTolerance,
            BigDecimal relativeTolerance) {
        if (first == null || second == null || absoluteTolerance == null || relativeTolerance == null) {
            throw new IllegalArgumentException("Arguments must not be null");
        }
        if (absoluteTolerance.compareTo(BigDecimal.ZERO) < 0 || relativeTolerance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Tolerances must be non-negative");
        }

        BigDecimal diff = first.subtract(second).abs(CALCULATION_CONTEXT);
        if (diff.compareTo(absoluteTolerance) <= 0) {
            return true;
        }

        BigDecimal absFirst = first.abs(CALCULATION_CONTEXT);
        BigDecimal absSecond = second.abs(CALCULATION_CONTEXT);
        BigDecimal max = absFirst.compareTo(absSecond) > 0 ? absFirst : absSecond;

        if (max.compareTo(BigDecimal.ZERO) == 0) {
            return true;
        }

        BigDecimal relativeDiff = diff.divide(max, CALCULATION_CONTEXT);
        return relativeDiff.compareTo(relativeTolerance) <= 0;
    }

    public static int scaleIndependentHashCode(BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.hashCode();
        }
        return value.stripTrailingZeros().hashCode();
    }
}
