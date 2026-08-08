package com.ailab.chemistry.domain.thermodynamics;

import java.math.BigDecimal;
import java.util.Optional;

public record StandardEquilibriumConstant(
        BigDecimal lnK,
        BigDecimal log10K,
        Optional<BigDecimal> directK,
        PhaseStabilityStatus phaseStabilityStatus) {

    public StandardEquilibriumConstant {
        directK = directK == null ? Optional.empty() : directK;
    }
}
