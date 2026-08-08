package com.ailab.chemistry.domain.laboratoryevent;

public record LaboratoryEventSource(String sourceType, String actor) {
    public LaboratoryEventSource {
        if (sourceType == null || sourceType.isBlank()) {
            throw new IllegalArgumentException("Event source type is required");
        }
        actor = actor == null ? "" : actor;
    }
}
