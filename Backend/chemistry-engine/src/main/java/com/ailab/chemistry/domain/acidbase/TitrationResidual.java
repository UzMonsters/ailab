package com.ailab.chemistry.domain.acidbase;

import java.math.BigDecimal;
import java.util.Objects;

public record TitrationResidual(
        BigDecimal massBalanceResidual,
        BigDecimal chargeBalanceResidual
) {
    public TitrationResidual {
        Objects.requireNonNull(massBalanceResidual, "massBalanceResidual must not be null");
        Objects.requireNonNull(chargeBalanceResidual, "chargeBalanceResidual must not be null");
    }

    public static TitrationResidual zero() {
        return new TitrationResidual(BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public BigDecimal getMassBalanceResidual() {
        return massBalanceResidual;
    }

    public BigDecimal getChargeBalanceResidual() {
        return chargeBalanceResidual;
    }
}
