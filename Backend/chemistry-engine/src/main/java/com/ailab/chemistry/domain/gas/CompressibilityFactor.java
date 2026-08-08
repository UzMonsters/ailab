package com.ailab.chemistry.domain.gas;

import java.math.BigDecimal;
import java.util.Objects;

public record CompressibilityFactor(BigDecimal value) {
    public CompressibilityFactor {
        Objects.requireNonNull(value, "value must not be null");
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new GasLawException(GasLawErrorCode.INVALID_COMPRESSIBILITY_FACTOR, "Compressibility factor must be positive");
        }
        value = value.stripTrailingZeros();
    }

    public static CompressibilityFactor of(String value) {
        return new CompressibilityFactor(new BigDecimal(value));
    }

    public static CompressibilityFactor ideal() {
        return new CompressibilityFactor(BigDecimal.ONE);
    }
}
