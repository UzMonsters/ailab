package com.ailab.chemistry.domain.laboratoryevent;

public record StepFailedPayload(String stepId, String reason) implements LaboratoryEventPayload {
    public StepFailedPayload {
        if (stepId == null || stepId.isBlank()) {
            throw new IllegalArgumentException("Step id is required");
        }
    }

    @Override
    public LaboratoryEventType eventType() {
        return LaboratoryEventType.STEP_FAILED;
    }
}
