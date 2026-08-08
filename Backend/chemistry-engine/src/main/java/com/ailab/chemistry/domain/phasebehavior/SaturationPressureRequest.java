package com.ailab.chemistry.domain.phasebehavior;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.Temperature;

public record SaturationPressureRequest(
        String compoundCode,
        MatterState initialPhase,
        MatterState finalPhase,
        Temperature temperature
) {
}
