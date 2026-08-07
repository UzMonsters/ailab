package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.measurement.Energy;
import com.ailab.chemistry.domain.measurement.Temperature;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public record ReactionCalorimetryResult(
        String reactionCode,
        BigDecimal reactionExtentMoles,
        Temperature temperature,
        BigDecimal standardReactionEnthalpyKjPerMol,
        Energy totalReactionHeatJoules,
        Energy heatToSurroundingsJoules,
        CalorimetryStatus status,
        String explanation,
        List<String> assumptions) {
    public ReactionCalorimetryResult {
        Objects.requireNonNull(reactionCode, "reactionCode must not be null");
        Objects.requireNonNull(reactionExtentMoles, "reactionExtentMoles must not be null");
        Objects.requireNonNull(temperature, "temperature must not be null");
        assumptions = assumptions == null ? List.of() : List.copyOf(assumptions);
    }
}
