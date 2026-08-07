package com.ailab.chemistry.domain.thermodynamics;

import java.math.BigDecimal;

public enum ThermodynamicSign {
    NEGATIVE,
    ZERO,
    POSITIVE;

    public static ThermodynamicSign of(BigDecimal value) {
        int comparison = value.compareTo(BigDecimal.ZERO);
        if (comparison < 0) {
            return NEGATIVE;
        }
        if (comparison > 0) {
            return POSITIVE;
        }
        return ZERO;
    }
}
