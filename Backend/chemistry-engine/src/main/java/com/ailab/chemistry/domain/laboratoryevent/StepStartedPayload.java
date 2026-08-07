package com.ailab.chemistry.domain.laboratoryevent;

import java.util.List;

public record StepStartedPayload(String stepId, List<String> exclusiveEquipmentProfileIds) implements LaboratoryEventPayload {
    public StepStartedPayload {
        if (stepId == null || stepId.isBlank()) {
            throw new IllegalArgumentException("Step id is required");
        }
        exclusiveEquipmentProfileIds = List.copyOf(exclusiveEquipmentProfileIds == null ? List.of() : exclusiveEquipmentProfileIds);
    }

    @Override
    public LaboratoryEventType eventType() {
        return LaboratoryEventType.STEP_STARTED;
    }
}
