package com.ailab.chemistry.domain.phasebehavior;

public record PhaseTransitionProvenance(
        String sourceCode,
        String citation,
        String reuseTerms,
        PhaseTransitionEvidenceStatus evidenceStatus
) {
}
