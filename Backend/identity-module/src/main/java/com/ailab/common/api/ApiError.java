package com.ailab.common.api;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record ApiError(Instant timestamp, int status, String error, String message,
                       String path, List<FieldViolation> violations,
                       List<FieldViolation> fieldViolations,
                       Map<String, String> errors,
                       String correlationId) {
    public ApiError(Instant timestamp, int status, String error, String message,
                    String path, List<FieldViolation> violations) {
        this(timestamp, status, error, message, path, violations, violations, Map.of(), null);
    }

    public record FieldViolation(String field, String message) {
    }
}
