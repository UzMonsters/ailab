package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.Temperature;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record AdiabaticTemperatureRequest(
        String reactionCode,
        BigDecimal reactionExtentMoles,
        Temperature initialTemperature,
        Pressure pressure,
        List<InitialParticipantAmount> initialInventory,
        Calorimeter calorimeter,
        Map<String, MatterState> stateOverrides) {
    public AdiabaticTemperatureRequest {
        Objects.requireNonNull(reactionCode, "reactionCode must not be null");
        Objects.requireNonNull(reactionExtentMoles, "reactionExtentMoles must not be null");
        Objects.requireNonNull(initialTemperature, "initialTemperature must not be null");
        Objects.requireNonNull(initialInventory, "initialInventory must not be null");
        initialInventory = List.copyOf(initialInventory);
        stateOverrides = stateOverrides == null ? Map.of() : Map.copyOf(stateOverrides);
    }
}
