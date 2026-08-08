package com.ailab.chemistry.domain.simulationengine;

import java.math.BigDecimal;

public record MaterialStateDelta(
        String vesselId,
        String compoundCode,
        BigDecimal quantityDelta,
        String unit,
        String physicalState
) {
    public MaterialStateDelta {
        if (vesselId == null || vesselId.isBlank() || compoundCode == null || compoundCode.isBlank()
                || quantityDelta == null || unit == null || unit.isBlank()
                || physicalState == null || physicalState.isBlank()) {
            throw new SimulationExecutionException(SimulationExecutionErrorCode.STATE_DELTA_INVARIANT_FAILED,
                    "Material delta requires vessel, compound, quantity, unit, and physical state");
        }
    }
}
