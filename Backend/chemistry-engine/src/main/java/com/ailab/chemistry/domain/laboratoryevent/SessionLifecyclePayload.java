package com.ailab.chemistry.domain.laboratoryevent;

public record SessionLifecyclePayload(String reason) implements LaboratoryEventPayload {
    @Override
    public LaboratoryEventType eventType() {
        return LaboratoryEventType.SESSION_STARTED;
    }
}
