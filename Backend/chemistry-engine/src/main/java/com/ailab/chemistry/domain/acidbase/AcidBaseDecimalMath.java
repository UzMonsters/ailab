package com.ailab.chemistry.domain.acidbase;

import java.math.BigDecimal;
import java.math.MathContext;

public final class AcidBaseDecimalMath {
    public static final MathContext MATH_CONTEXT = MathContext.DECIMAL128;
    static final BigDecimal MIN_SUPPORTED_RATIO = new BigDecimal("1e-12");
    static final BigDecimal MAX_SUPPORTED_RATIO = new BigDecimal("1e12");
    static final BigDecimal ROUND_TRIP_RELATIVE_TOLERANCE = new BigDecimal("1e-12");

    private AcidBaseDecimalMath() {
    }

    public static BigDecimal log10(BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BufferException(BufferErrorCode.NUMERICALLY_UNSAFE_REQUEST, "log10 input must be positive");
        }
        return finite(Math.log10(value.doubleValue()), "log10");
    }

    public static BigDecimal tenPower(BigDecimal exponent) {
        if (exponent == null) {
            throw new BufferException(BufferErrorCode.NUMERICALLY_UNSAFE_REQUEST, "10^x exponent must not be null");
        }
        double result = Math.pow(10.0, exponent.doubleValue());
        if (result <= 0.0) {
            throw new BufferException(BufferErrorCode.NUMERICALLY_UNSAFE_REQUEST, "10^x result must be positive");
        }
        return finite(result, "10^x");
    }

    private static BigDecimal finite(double value, String operation) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new BufferException(BufferErrorCode.NUMERICALLY_UNSAFE_REQUEST, operation + " produced a non-finite result");
        }
        return BigDecimal.valueOf(value);
    }
}
