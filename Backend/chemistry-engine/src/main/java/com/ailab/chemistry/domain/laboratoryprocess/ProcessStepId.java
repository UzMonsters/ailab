package com.ailab.chemistry.domain.laboratoryprocess;

public record ProcessStepId(String value) {
    public ProcessStepId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Process step id is required");
        }
    }
}
