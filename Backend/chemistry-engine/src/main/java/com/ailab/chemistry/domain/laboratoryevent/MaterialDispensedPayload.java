package com.ailab.chemistry.domain.laboratoryevent;

import java.math.BigDecimal;

public record MaterialDispensedPayload(
        String vesselId,
        String containerProfileId,
        String compoundCode,
        BigDecimal quantity,
        String unit,
        String physicalState,
        BigDecimal vesselWorkingVolume
) implements LaboratoryEventPayload {
    public MaterialDispensedPayload {
        if (vesselId == null || vesselId.isBlank() || compoundCode == null || compoundCode.isBlank()
                || unit == null || unit.isBlank() || physicalState == null || physicalState.isBlank()) {
            throw new IllegalArgumentException("Vessel, compound, unit, and physical state are required");
        }
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Dispensed quantity must be non-negative");
        }
        if (vesselWorkingVolume == null || vesselWorkingVolume.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Vessel working volume must be non-negative");
        }
    }

    @Override
    public LaboratoryEventType eventType() {
        return LaboratoryEventType.MATERIAL_DISPENSED;
    }
}
