package com.ailab.chemistry.domain.solubility;

import java.math.BigDecimal;
import java.util.Objects;

public record SolubilityResidual(
        BigDecimal solubilityProductResidual,
        BigDecimal ionBalanceResidual
) {
    public SolubilityResidual {
        Objects.requireNonNull(solubilityProductResidual, "solubilityProductResidual must not be null");
        Objects.requireNonNull(ionBalanceResidual, "ionBalanceResidual must not be null");
    }
}
