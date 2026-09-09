package com.ailab.common.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record ApiError(
        @JsonProperty("timestamp") Instant timestamp,
        @JsonProperty("status") int status,
        @JsonProperty("error") String error,
        @JsonProperty("message") String message,
        @JsonProperty("path") String path,
        @JsonProperty("instance") String instance,
        @JsonProperty("violations") List<FieldViolation> violations,
        @JsonProperty("fieldViolations") List<FieldViolation> fieldViolations,
        @JsonProperty("errors") Map<String, String> errors,
        @JsonProperty("correlationId") String correlationId,
        @JsonProperty("type") String type,
        @JsonProperty("title") String title,
        @JsonProperty("code") String code,
        @JsonProperty("detail") String detail,
        @JsonProperty("fieldErrors") List<FieldViolation> fieldErrors,
        @JsonProperty("traceId") String traceId
) {
    public ApiError(Instant timestamp, int status, String error, String message,
                    String path, List<FieldViolation> violations,
                    List<FieldViolation> fieldViolations,
                    Map<String, String> errors,
                    String correlationId) {
        this(
                timestamp,
                status,
                error,
                message,
                path,
                path,
                violations != null ? violations : List.of(),
                fieldViolations != null ? fieldViolations : List.of(),
                errors != null ? errors : Map.of(),
                correlationId,
                "https://api.aichemistry.local/problems/" + (error != null ? error.toLowerCase().replace(' ', '-').replace('_', '-') : "error"),
                error != null ? error : "Error",
                error != null ? error.toUpperCase().replace(' ', '_') : "ERROR",
                message,
                violations != null ? violations : List.of(),
                correlationId
        );
    }

    public ApiError(Instant timestamp, int status, String error, String message,
                    String path, List<FieldViolation> violations) {
        this(timestamp, status, error, message, path, violations, violations, Map.of(), null);
    }

    public static ApiError ofProblem(int status, String code, String title, String detail, String path, String correlationId, Map<String, String> errors) {
        List<FieldViolation> violations = new ArrayList<>();
        if (errors != null) {
            errors.forEach((k, v) -> violations.add(new FieldViolation(k, "INVALID", v)));
        }
        return new ApiError(
                Instant.now(),
                status,
                title,
                detail,
                path,
                path,
                violations,
                violations,
                errors != null ? errors : Map.of(),
                correlationId,
                "https://api.aichemistry.local/problems/" + (code != null ? code.toLowerCase().replace('_', '-') : "error"),
                title,
                code,
                detail,
                violations,
                correlationId
        );
    }

    public static ApiError ofProblemWithViolations(int status, String code, String title, String detail, String path, String correlationId, List<FieldViolation> violations) {
        List<FieldViolation> v = violations != null ? violations : List.of();
        return new ApiError(
                Instant.now(),
                status,
                title,
                detail,
                path,
                path,
                v,
                v,
                Map.of(),
                correlationId,
                "https://api.aichemistry.local/problems/" + (code != null ? code.toLowerCase().replace('_', '-') : "error"),
                title,
                code,
                detail,
                v,
                correlationId
        );
    }

    public record FieldViolation(
            @JsonProperty("field") String field,
            @JsonProperty("code") String code,
            @JsonProperty("message") String message
    ) {
        public FieldViolation(String field, String message) {
            this(field, "INVALID", message);
        }
    }
}
