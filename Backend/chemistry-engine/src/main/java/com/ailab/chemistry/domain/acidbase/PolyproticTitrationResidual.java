package com.ailab.chemistry.domain.acidbase;

import java.math.BigDecimal;
import java.util.Objects;

public record PolyproticTitrationResidual(
        BigDecimal massBalanceResidual,
        BigDecimal chargeBalanceResidual
) {
    public PolyproticTitrationResidual {
        Objects.requireNonNull(massBalanceResidual, "massBalanceResidual must not be null");
        Objects.requireNonNull(chargeBalanceResidual, "chargeBalanceResidual must not be null");
    }
}
