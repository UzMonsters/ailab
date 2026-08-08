package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.Volume;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record EquilibriumCompositionRequest(
        String reactionCode,
        Temperature temperature,
        Pressure standardPressure,
        EquilibriumCompositionMethod method,
        List<InitialParticipantAmount> initialAmounts,
        Pressure totalPressure,
        Volume volume,
        BigDecimal inertGasMoles,
        List<InitialParticipantAmount> spectatorIons,
        Map<String, MatterState> stateOverrides) {
    public EquilibriumCompositionRequest {
        Objects.requireNonNull(reactionCode, "reactionCode must not be null");
        Objects.requireNonNull(temperature, "temperature must not be null");
        Objects.requireNonNull(method, "method must not be null");
        Objects.requireNonNull(initialAmounts, "initialAmounts must not be null");
        initialAmounts = List.copyOf(initialAmounts);
        spectatorIons = spectatorIons == null ? List.of() : List.copyOf(spectatorIons);
        stateOverrides = stateOverrides == null ? Map.of() : Map.copyOf(stateOverrides);
    }
}
