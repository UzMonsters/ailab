package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.measurement.Temperature;

import java.util.List;

public record EquilibriumCompositionResult(
        String reactionCode,
        Temperature temperature,
        EquilibriumCompositionStatus status,
        EquilibriumExtent extent,
        List<EquilibriumParticipantState> finalComposition,
        ReactionQuotient reactionQuotient,
        StandardEquilibriumConstant equilibriumConstant,
        EquilibriumCompositionResidual residual,
        TemperatureCorrectionCoverage coverage,
        PhaseStabilityStatus phaseStabilityStatus,
        EquilibriumCompositionMethod method,
        String explanation,
        List<String> assumptions) {
    public EquilibriumCompositionResult {
        finalComposition = finalComposition == null ? List.of() : List.copyOf(finalComposition);
        assumptions = assumptions == null ? List.of() : List.copyOf(assumptions);
    }
}
