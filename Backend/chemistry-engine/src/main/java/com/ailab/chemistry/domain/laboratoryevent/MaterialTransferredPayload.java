package com.ailab.chemistry.domain.laboratoryevent;

import java.math.BigDecimal;

public record MaterialTransferredPayload(
        String sourceVesselId,
        String targetVesselId,
        String compoundCode,
        BigDecimal quantity,
        String unit,
        String physicalState,
        BigDecimal targetWorkingVolume
) implements LaboratoryEventPayload {
    public MaterialTransferredPayload {
        if (sourceVesselId == null || sourceVesselId.isBlank() || targetVesselId == null || targetVesselId.isBlank()
                || compoundCode == null || compoundCode.isBlank() || unit == null || unit.isBlank()
                || physicalState == null || physicalState.isBlank()) {
            throw new IllegalArgumentException("Source, target, compound, unit, and physical state are required");
        }
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Transferred quantity must be non-negative");
        }
        if (targetWorkingVolume == null || targetWorkingVolume.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Target working volume must be non-negative");
        }
    }

    @Override
    public LaboratoryEventType eventType() {
        return LaboratoryEventType.MATERIAL_TRANSFERRED;
    }
}
