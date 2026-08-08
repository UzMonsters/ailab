package com.ailab.chemistry.domain.laboratoryevent;

import java.math.BigDecimal;

public record SampleTakenPayload(
        String sourceVesselId,
        String sampleId,
        String compoundCode,
        BigDecimal quantity,
        String unit,
        String physicalState
) implements LaboratoryEventPayload {
    public SampleTakenPayload {
        if (sourceVesselId == null || sourceVesselId.isBlank() || sampleId == null || sampleId.isBlank()
                || compoundCode == null || compoundCode.isBlank() || unit == null || unit.isBlank()
                || physicalState == null || physicalState.isBlank()) {
            throw new IllegalArgumentException("Source, sample, compound, unit, and state are required");
        }
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Sample quantity must be non-negative");
        }
    }

    @Override
    public LaboratoryEventType eventType() {
        return LaboratoryEventType.SAMPLE_TAKEN;
    }
}
