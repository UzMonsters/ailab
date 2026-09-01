package com.ailab.common.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public record ApiError(
        @JsonProperty("timestamp") Instant timestamp,
        @JsonProperty("status") int status,
        @JsonProperty("error") String error,
        @JsonProperty("message") String message,
        @JsonProperty("path") String path,
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
                violations != null ? violations : List.of(),
                fieldViolations != null ? fieldViolations : List.of(),
                errors != null ? errors : Map.of(),
                correlationId,
                "https://errors.jasscience.dev/" + (error != null ? error.toLowerCase().replace(' ', '-') : "error"),
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
        return new ApiError(
                Instant.now(),
                status,
                title,
                detail,
                path,
                List.of(),
                List.of(),
                errors != null ? errors : Map.of(),
                correlationId,
                "https://errors.jasscience.dev/" + code.toLowerCase().replace('_', '-'),
                title,
                code,
                detail,
                List.of(),
                correlationId
        );
    }

    public record FieldViolation(String field, String message) {
    }
}
