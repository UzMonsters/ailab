package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.Temperature;

import java.util.Map;

public record EquilibriumConstantRequest(
        String reactionCode,
        Temperature temperature,
        Pressure standardPressure,
        Map<String, MatterState> stateOverrides) {

    public EquilibriumConstantRequest {
        stateOverrides = stateOverrides == null ? Map.of() : Map.copyOf(stateOverrides);
    }
}
