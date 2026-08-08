package com.ailab.chemistry.domain.thermodynamics;

import java.math.BigDecimal;
import java.util.Objects;

public record EquilibriumExtent(
        BigDecimal extent,
        ExtentBounds bounds,
        boolean atForwardBound,
        boolean atReverseBound) {
    public EquilibriumExtent {
        Objects.requireNonNull(extent, "extent must not be null");
        Objects.requireNonNull(bounds, "bounds must not be null");
    }
}
