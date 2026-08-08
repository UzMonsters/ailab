package com.ailab.chemistry.domain.laboratoryevent;

public record CorrelationId(String value) {
    public CorrelationId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Correlation id is required");
        }
    }
}
