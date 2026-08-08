package com.ailab.chemistry.domain.simulationstate;

import java.math.BigDecimal;

public record MaterialPortion(String compoundCode, BigDecimal quantity, String unit, String physicalState, String sourceEventId) {
    public MaterialPortion {
        if (compoundCode == null || compoundCode.isBlank() || unit == null || unit.isBlank()
                || physicalState == null || physicalState.isBlank()) {
            throw new IllegalArgumentException("Material compound, unit, and state are required");
        }
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new SimulationStateException(SimulationStateErrorCode.MATERIAL_QUANTITY_NEGATIVE,
                    "Material quantity cannot become negative");
        }
        sourceEventId = sourceEventId == null ? "" : sourceEventId;
    }

    public String key() {
        return compoundCode + "|" + unit + "|" + physicalState;
    }
}
