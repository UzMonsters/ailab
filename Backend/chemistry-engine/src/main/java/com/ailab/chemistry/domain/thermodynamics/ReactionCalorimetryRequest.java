package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.Temperature;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

public record ReactionCalorimetryRequest(
        String reactionCode,
        BigDecimal reactionExtentMoles,
        Temperature temperature,
        Pressure pressure,
        Map<String, MatterState> stateOverrides) {
    public ReactionCalorimetryRequest {
        Objects.requireNonNull(reactionCode, "reactionCode must not be null");
        Objects.requireNonNull(reactionExtentMoles, "reactionExtentMoles must not be null");
        Objects.requireNonNull(temperature, "temperature must not be null");
        stateOverrides = stateOverrides == null ? Map.of() : Map.copyOf(stateOverrides);
    }
}
