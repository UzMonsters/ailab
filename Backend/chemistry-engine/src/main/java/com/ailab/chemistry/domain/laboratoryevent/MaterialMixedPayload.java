package com.ailab.chemistry.domain.laboratoryevent;

public record MaterialMixedPayload(String vesselId, String bookkeepingNote) implements LaboratoryEventPayload {
    public MaterialMixedPayload {
        if (vesselId == null || vesselId.isBlank()) {
            throw new IllegalArgumentException("Vessel id is required");
        }
    }

    @Override
    public LaboratoryEventType eventType() {
        return LaboratoryEventType.MATERIAL_MIXED;
    }
}
