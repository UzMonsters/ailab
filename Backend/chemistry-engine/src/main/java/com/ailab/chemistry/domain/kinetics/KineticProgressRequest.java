package com.ailab.chemistry.domain.kinetics;

import com.ailab.chemistry.domain.measurement.Duration;
import com.ailab.chemistry.domain.measurement.Temperature;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

public record KineticProgressRequest(
        String reactionCode,
        KineticProfile profile,
        Map<String, BigDecimal> initialConcentrations,
        BigDecimal systemVolumeLiters,
        Duration totalDuration,
        Duration stepSize,
        Temperature temperature) {
    public KineticProgressRequest {
        Objects.requireNonNull(reactionCode, "reactionCode must not be null");
        Objects.requireNonNull(profile, "profile must not be null");
        Objects.requireNonNull(totalDuration, "totalDuration must not be null");
        initialConcentrations = initialConcentrations == null ? Map.of() : Map.copyOf(initialConcentrations);
        systemVolumeLiters = systemVolumeLiters == null ? BigDecimal.ONE : systemVolumeLiters;
        stepSize = stepSize == null ? Duration.of("1.0", com.ailab.chemistry.domain.measurement.DurationUnit.SECOND) : stepSize;
    }
}
