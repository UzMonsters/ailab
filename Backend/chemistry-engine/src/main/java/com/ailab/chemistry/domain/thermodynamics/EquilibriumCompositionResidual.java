package com.ailab.chemistry.domain.thermodynamics;

import java.math.BigDecimal;
import java.util.Objects;

public record EquilibriumCompositionResidual(
        BigDecimal lnQ,
        BigDecimal lnK,
        BigDecimal absoluteResidual,
        BigDecimal relativeResidual,
        BigDecimal deltaGibbsKjPerMol,
        BigDecimal maxMassBalanceError) {
    public EquilibriumCompositionResidual {
        Objects.requireNonNull(lnQ, "lnQ must not be null");
        Objects.requireNonNull(lnK, "lnK must not be null");
        Objects.requireNonNull(absoluteResidual, "absoluteResidual must not be null");
        Objects.requireNonNull(relativeResidual, "relativeResidual must not be null");
        Objects.requireNonNull(deltaGibbsKjPerMol, "deltaGibbsKjPerMol must not be null");
        Objects.requireNonNull(maxMassBalanceError, "maxMassBalanceError must not be null");
    }
}
