package com.ailab.chemistry.domain.phasebehavior;

import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.Temperature;

public record PhaseBoundaryPoint(
        String compoundCode,
        Temperature temperature,
        Pressure pressure,
        String boundaryType,
        PhaseTransitionProvenance provenance
) {
}
