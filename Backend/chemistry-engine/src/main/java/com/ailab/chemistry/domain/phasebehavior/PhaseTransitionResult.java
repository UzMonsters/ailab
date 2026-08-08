package com.ailab.chemistry.domain.phasebehavior;

import com.ailab.chemistry.domain.measurement.Energy;

public record PhaseTransitionResult(
        PhaseBehaviorStatus status,
        PhaseTransitionRecord record,
        Energy heat
) {
}
