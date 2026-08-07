package com.ailab.chemistry.domain.laboratoryprocess;

public record ProcessValidationError(String code, String message, ProcessStepId stepId) {
    public ProcessValidationError {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Validation error code is required");
        }
    }
}
