package com.ailab.chemistry.domain.phasebehavior;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.Energy;
import com.ailab.chemistry.domain.measurement.Temperature;

public record HeatingPathSegment(
        String kind,
        MatterState initialPhase,
        MatterState finalPhase,
        Temperature startTemperature,
        Temperature endTemperature,
        Energy heat
) {
}
