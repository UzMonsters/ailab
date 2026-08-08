package com.ailab.chemistry.domain.laboratoryevent;

public record IdempotencyKey(String value) {
    public IdempotencyKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Idempotency key is required");
        }
    }
}
