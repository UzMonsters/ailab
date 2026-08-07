package com.ailab.chemistry.domain.phasebehavior;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.Temperature;

public sealed interface HeatingPathSegmentSpec permits SensiblePhaseSegmentSpec, TransitionSegmentSpec {
    MatterState initialPhase();
    MatterState finalPhase();
    Temperature startTemperature();
    Temperature endTemperature();
}
