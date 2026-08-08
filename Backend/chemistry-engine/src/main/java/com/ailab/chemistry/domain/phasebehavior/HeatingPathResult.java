package com.ailab.chemistry.domain.phasebehavior;

import com.ailab.chemistry.domain.measurement.Energy;

import java.util.List;

public record HeatingPathResult(
        PhaseBehaviorStatus status,
        List<HeatingPathSegment> segments,
        Energy totalHeat,
        Energy segmentHeatSum
) {
}
