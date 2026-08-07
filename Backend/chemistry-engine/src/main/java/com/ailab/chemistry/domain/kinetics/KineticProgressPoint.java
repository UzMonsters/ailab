package com.ailab.chemistry.domain.kinetics;

import com.ailab.chemistry.domain.measurement.Duration;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

public record KineticProgressPoint(
        Duration time,
        BigDecimal extentMoles,
        BigDecimal extentMolar,
        ReactionRate reactionRate,
        Map<String, BigDecimal> concentrations) {
    public KineticProgressPoint {
        Objects.requireNonNull(time, "time must not be null");
        Objects.requireNonNull(extentMoles, "extentMoles must not be null");
        Objects.requireNonNull(extentMolar, "extentMolar must not be null");
        Objects.requireNonNull(reactionRate, "reactionRate must not be null");
        concentrations = concentrations == null ? Map.of() : Map.copyOf(concentrations);
    }
}
