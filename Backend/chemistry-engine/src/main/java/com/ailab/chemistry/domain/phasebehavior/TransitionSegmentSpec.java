package com.ailab.chemistry.domain.phasebehavior;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.Temperature;

public record TransitionSegmentSpec(
        PhaseTransitionType transitionType,
        MatterState initialPhase,
        MatterState finalPhase,
        Temperature temperature
) implements HeatingPathSegmentSpec {
    @Override
    public Temperature startTemperature() {
        return temperature;
    }

    @Override
    public Temperature endTemperature() {
        return temperature;
    }
}
