package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.Temperature;

import java.util.Map;

public record ReactionTemperatureCorrectionRequest(
        String reactionCode,
        Temperature targetTemperature,
        Pressure pressure,
        Map<String, MatterState> stateOverrides) {

    public ReactionTemperatureCorrectionRequest {
        stateOverrides = stateOverrides == null ? Map.of() : Map.copyOf(stateOverrides);
    }
}
