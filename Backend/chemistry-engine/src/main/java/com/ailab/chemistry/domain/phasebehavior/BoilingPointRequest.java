package com.ailab.chemistry.domain.phasebehavior;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.Pressure;

public record BoilingPointRequest(
        String compoundCode,
        MatterState initialPhase,
        MatterState finalPhase,
        Pressure externalPressure
) {
}
