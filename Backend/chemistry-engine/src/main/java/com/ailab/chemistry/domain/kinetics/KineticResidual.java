package com.ailab.chemistry.domain.kinetics;

import java.math.BigDecimal;
import java.util.Objects;

public record KineticResidual(
        BigDecimal maxMassBalanceError,
        BigDecimal maxAnalyticalError,
        boolean isBalanced) {
    public KineticResidual {
        Objects.requireNonNull(maxMassBalanceError, "maxMassBalanceError must not be null");
        maxAnalyticalError = maxAnalyticalError == null ? BigDecimal.ZERO : maxAnalyticalError;
    }
}
