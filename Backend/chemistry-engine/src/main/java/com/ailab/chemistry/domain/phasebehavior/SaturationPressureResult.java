package com.ailab.chemistry.domain.phasebehavior;

import com.ailab.chemistry.domain.measurement.Pressure;

public record SaturationPressureResult(
        PhaseBehaviorStatus status,
        AntoineCoefficientSet correlation,
        Pressure pressure
) {
}
