package com.ailab.chemistry.domain.laboratoryevent;

import java.util.Map;

public record StepCompletedPayload(String stepId, boolean releaseResources, Map<String, String> explicitOutcome)
        implements LaboratoryEventPayload {
    public StepCompletedPayload {
        if (stepId == null || stepId.isBlank()) {
            throw new IllegalArgumentException("Step id is required");
        }
        explicitOutcome = Map.copyOf(explicitOutcome == null ? Map.of() : explicitOutcome);
    }

    @Override
    public LaboratoryEventType eventType() {
        return LaboratoryEventType.STEP_COMPLETED;
    }
}
