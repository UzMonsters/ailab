package com.ailab.chemistry.domain.thermodynamics;

import java.math.BigDecimal;

public record NonstandardGibbsResult(
        String reactionCode,
        EquilibriumCalculationStatus status,
        EquilibriumConstantResult standardConstantResult,
        ReactionQuotient reactionQuotient,
        BigDecimal deltaGibbsStandardKjPerMol,
        BigDecimal deltaGibbsKjPerMol,
        ThermodynamicDirection direction,
        TemperatureCorrectionCoverage coverage,
        PhaseStabilityStatus phaseStabilityStatus,
        EquilibriumCalculationMethod method,
        String explanation) {
}
