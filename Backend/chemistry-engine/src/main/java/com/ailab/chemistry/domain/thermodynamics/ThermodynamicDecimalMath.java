package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.measurement.ScientificMath;

import java.math.BigDecimal;

public final class ThermodynamicDecimalMath {
    public static final BigDecimal LN_10 = BigDecimal.valueOf(Math.log(10.0));
    private static final BigDecimal MIN_EXP = new BigDecimal("-745");
    private static final BigDecimal MAX_EXP = new BigDecimal("709");

    private ThermodynamicDecimalMath() {
    }

    public static BigDecimal ln(BigDecimal input) {
        requirePositive(input, "ln input");
        return finite(Math.log(input.doubleValue()), "ln");
    }

    public static BigDecimal log10(BigDecimal input) {
        requirePositive(input, "log10 input");
        return finite(Math.log10(input.doubleValue()), "log10");
    }

    public static BigDecimal exp(BigDecimal exponent) {
        requireFiniteInput(exponent, "exp exponent");
        if (exponent.compareTo(MIN_EXP) < 0 || exponent.compareTo(MAX_EXP) > 0) {
            throw new EquilibriumException(EquilibriumErrorCode.NUMERICAL_RANGE_EXCEEDED,
                    "exp exponent is outside the finite Java double output range");
        }
        return finite(Math.exp(exponent.doubleValue()), "exp");
    }

    public static BigDecimal pow10(BigDecimal exponent) {
        requireFiniteInput(exponent, "pow10 exponent");
        BigDecimal naturalExponent = exponent.multiply(LN_10, ScientificMath.CALCULATION_CONTEXT);
        return exp(naturalExponent);
    }

    public static boolean safelyExponentiable(BigDecimal naturalExponent) {
        return naturalExponent.compareTo(MIN_EXP) >= 0 && naturalExponent.compareTo(MAX_EXP) <= 0;
    }

    private static void requirePositive(BigDecimal input, String name) {
        requireFiniteInput(input, name);
        if (input.compareTo(BigDecimal.ZERO) <= 0) {
            throw new EquilibriumException(EquilibriumErrorCode.INVALID_ACTIVITY, name + " must be positive");
        }
    }

    private static void requireFiniteInput(BigDecimal input, String name) {
        if (input == null) {
            throw new EquilibriumException(EquilibriumErrorCode.INVALID_ACTIVITY, name + " must be present");
        }
        double value = input.doubleValue();
        if (!Double.isFinite(value)) {
            throw new EquilibriumException(EquilibriumErrorCode.NUMERICAL_RANGE_EXCEEDED, name + " is not finite");
        }
    }

    private static BigDecimal finite(double value, String operation) {
        if (!Double.isFinite(value)) {
            throw new EquilibriumException(EquilibriumErrorCode.NUMERICAL_RANGE_EXCEEDED,
                    operation + " produced a non-finite value");
        }
        return BigDecimal.valueOf(value).round(ScientificMath.CALCULATION_CONTEXT).stripTrailingZeros();
    }
}
