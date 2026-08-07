package com.ailab.chemistry.domain.thermodynamics;

import java.math.BigDecimal;
import java.util.Objects;

public record ExtentBounds(
        BigDecimal minExtent,
        BigDecimal maxExtent,
        String limitingReactantCode,
        String limitingProductCode) {
    public ExtentBounds {
        Objects.requireNonNull(minExtent, "minExtent must not be null");
        Objects.requireNonNull(maxExtent, "maxExtent must not be null");
        if (minExtent.compareTo(maxExtent) > 0) {
            throw new EquilibriumCompositionException(
                    EquilibriumCompositionErrorCode.NO_VALID_ROOT,
                    "minExtent (" + minExtent + ") cannot exceed maxExtent (" + maxExtent + ")");
        }
    }
}
