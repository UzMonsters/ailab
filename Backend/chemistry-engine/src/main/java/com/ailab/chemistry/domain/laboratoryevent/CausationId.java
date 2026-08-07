package com.ailab.chemistry.domain.laboratoryevent;

public record CausationId(String value) {
    public CausationId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Causation id is required when supplied");
        }
    }
}
