package com.ailab.chemistry.domain.phasebehavior;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.MolarHeatCapacity;
import com.ailab.chemistry.domain.measurement.Temperature;

public record SensiblePhaseSegmentSpec(
        MatterState phase,
        Temperature startTemperature,
        Temperature endTemperature,
        MolarHeatCapacity heatCapacity
) implements HeatingPathSegmentSpec {
    @Override
    public MatterState initialPhase() {
        return phase;
    }

    @Override
    public MatterState finalPhase() {
        return phase;
    }
}
