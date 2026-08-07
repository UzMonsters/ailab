package com.ailab.chemistry.domain.equipment;

import com.ailab.chemistry.domain.measurement.MeasurementResolution;

import java.math.BigDecimal;
import java.util.Objects;

public record EquipmentRequirement(
        String capabilityType,
        String quantity,
        BigDecimal requestedValue,
        String unit,
        MeasurementResolution requiredResolution,
        boolean requireAccuracyOrUncertainty
) {
    public EquipmentRequirement {
        Objects.requireNonNull(capabilityType, "capabilityType must not be null");
        Objects.requireNonNull(quantity, "quantity must not be null");
        Objects.requireNonNull(requestedValue, "requestedValue must not be null");
        Objects.requireNonNull(unit, "unit must not be null");
    }
}
