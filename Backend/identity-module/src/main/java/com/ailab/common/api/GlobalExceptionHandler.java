package com.ailab.common.api;

import com.ailab.auth.token.InvalidRefreshTokenException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<ApiError.FieldViolation> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> new ApiError.FieldViolation(e.getField(), "INVALID", e.getDefaultMessage()))
                .toList();
        return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR: Validation failed", req, violations);
    }

    @ExceptionHandler(BadCredentialsException.class)
    ResponseEntity<ApiError> unauthorized(BadCredentialsException ex, HttpServletRequest req) {
        return error(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED: Invalid credentials", req, List.of());
    }

    @ExceptionHandler({InsufficientAuthenticationException.class, AuthenticationCredentialsNotFoundException.class})
    ResponseEntity<ApiError> missingAuthentication(Exception ex, HttpServletRequest req) {
        return error(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED: Authentication required", req, List.of());
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    ResponseEntity<ApiError> invalidRefreshToken(InvalidRefreshTokenException ex, HttpServletRequest req) {
        return error(HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED: " + ex.getMessage(), req, List.of());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ApiError> badRequest(IllegalArgumentException ex, HttpServletRequest req) {
        String msg = ex.getMessage() != null ? ex.getMessage() : "Invalid argument";
        String codePrefix = msg.contains("locale") || msg.contains("Locale") ? "INVALID_LOCALE" : "VALIDATION_ERROR";
        return error(HttpStatus.BAD_REQUEST, codePrefix + ": " + msg, req, List.of());
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ApiError> forbidden(Exception ex, HttpServletRequest req) {
        return error(HttpStatus.FORBIDDEN, "FORBIDDEN: Access is forbidden", req, List.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ApiError> conflict(Exception ex, HttpServletRequest req) {
        return error(HttpStatus.CONFLICT, "SLUG_CONFLICT: Resource already exists or unique constraint violated", req, List.of());
    }

    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<ApiError> responseStatus(ResponseStatusException ex, HttpServletRequest req) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        if (status == null) status = HttpStatus.BAD_REQUEST;
        return error(status, ex.getReason(), req, List.of());
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiError> unexpected(Exception ex, HttpServletRequest req) {
        String correlationId = req.getHeader("X-Correlation-Id");
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = "err-" + java.util.UUID.randomUUID().toString().substring(0, 8);
        }
        log.error("Internal server error correlationId={}: {}", correlationId, ex.getMessage(), ex);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR: Internal server error", req, List.of());
    }

    private ResponseEntity<ApiError> error(HttpStatus status, String message, HttpServletRequest req, List<ApiError.FieldViolation> violations) {
        String correlationId = req.getHeader("X-Correlation-Id");
        String code = status.name();
        String detail = message != null ? message : status.getReasonPhrase();

        if (message != null && !message.isBlank()) {
            int colon = message.indexOf(':');
            if (colon > 0) {
                String potentialCode = message.substring(0, colon).trim();
                if (!potentialCode.contains(" ")) {
                    code = potentialCode;
                    detail = message.substring(colon + 1).trim();
                }
            } else if (!message.contains(" ")) {
                code = message.trim();
            }
        }

        String path = req.getRequestURI();
        String problemType = "https://api.aichemistry.local/problems/" + code.toLowerCase().replace('_', '-');
        String title = status.getReasonPhrase();

        ApiError apiError = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                detail,
                path,
                path,
                violations != null ? violations : List.of(),
                violations != null ? violations : List.of(),
                fieldErrorMap(violations),
                correlationId,
                problemType,
                title,
                code,
                detail,
                violations != null ? violations : List.of(),
                correlationId
        );

        return ResponseEntity.status(status).body(apiError);
    }

    private Map<String, String> fieldErrorMap(List<ApiError.FieldViolation> violations) {
        if (violations == null) return Map.of();
        return violations.stream().collect(java.util.stream.Collectors.toMap(
                ApiError.FieldViolation::field,
                ApiError.FieldViolation::message,
                (first, second) -> first));
    }
}
