package com.ailab.chemistry.domain.laboratoryevent;

public record LaboratoryEventId(String value) {
    public LaboratoryEventId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Event id is required");
        }
    }
}
