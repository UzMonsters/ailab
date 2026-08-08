package com.ailab.chemistry.domain.phasebehavior;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.AmountOfSubstance;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.Temperature;

public record PhaseTransitionRequest(
        String compoundCode,
        PhaseTransitionType transitionType,
        MatterState initialPhase,
        MatterState finalPhase,
        AmountOfSubstance amount,
        Temperature temperature,
        Pressure pressure
) {
}
