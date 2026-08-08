package com.ailab.chemistry.domain.phasebehavior;

import com.ailab.chemistry.domain.element.MatterState;

public record PhaseTransitionRecord(
        String recordId,
        String compoundCode,
        PhaseTransitionType transitionType,
        MatterState initialPhase,
        MatterState finalPhase,
        PhaseTransitionConditions conditions,
        TransitionEnthalpy enthalpy,
        PhaseTransitionProvenance provenance
) {
}
