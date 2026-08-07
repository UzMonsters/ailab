package com.ailab.chemistry.domain.acidbase;

import java.math.BigDecimal;
import java.util.Objects;

public record PolyproticResidual(
        BigDecimal massBalanceResidual,
        BigDecimal chargeBalanceResidual
) {
    public PolyproticResidual {
        Objects.requireNonNull(massBalanceResidual, "massBalanceResidual must not be null");
        Objects.requireNonNull(chargeBalanceResidual, "chargeBalanceResidual must not be null");
        massBalanceResidual = massBalanceResidual.abs();
        chargeBalanceResidual = chargeBalanceResidual.abs();
    }
}
