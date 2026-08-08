package com.ailab.chemistry.domain.acidbase;

import java.math.BigDecimal;
import java.util.Objects;

public record DistributionFraction(
        String speciesCode,
        int protonsRemaining,
        int charge,
        BigDecimal fraction,
        BigDecimal concentration
) {
    public DistributionFraction {
        if (speciesCode == null || speciesCode.isBlank()) {
            throw new PolyproticException(PolyproticErrorCode.INVALID_DISTRIBUTION_FRACTION, "Species code must not be blank");
        }
        speciesCode = speciesCode.trim();
        Objects.requireNonNull(fraction, "fraction must not be null");
        Objects.requireNonNull(concentration, "concentration must not be null");
        if (fraction.compareTo(BigDecimal.ZERO) < 0 || fraction.compareTo(BigDecimal.ONE) > 0) {
            throw new PolyproticException(PolyproticErrorCode.INVALID_DISTRIBUTION_FRACTION, "Distribution fraction must be between 0 and 1");
        }
    }
}
