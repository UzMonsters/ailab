package com.ailab.chemistry.domain.phasebehavior;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.AmountOfSubstance;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.Temperature;

import java.util.List;

public record HeatingPathRequest(
        String compoundCode,
        AmountOfSubstance amount,
        MatterState initialPhase,
        MatterState finalPhase,
        Temperature initialTemperature,
        Temperature finalTemperature,
        Pressure pressure,
        List<HeatingPathSegmentSpec> segments
) {
    public HeatingPathRequest {
        segments = List.copyOf(segments);
    }
}
