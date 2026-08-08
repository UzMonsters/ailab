package com.ailab.common.api;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<ApiError.FieldViolation> violations = ex.getBindingResult().getFieldErrors().stream().map(e -> new ApiError.FieldViolation(e.getField(), e.getDefaultMessage())).toList();
        return error(HttpStatus.BAD_REQUEST, "Validation failed", req, violations);
    }

    @ExceptionHandler(BadCredentialsException.class)
    ResponseEntity<ApiError> unauthorized(BadCredentialsException ex, HttpServletRequest req) {
        return error(HttpStatus.UNAUTHORIZED, "Invalid credentials", req, List.of());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ApiError> badRequest(IllegalArgumentException ex, HttpServletRequest req) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage(), req, List.of());
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ApiError> forbidden(Exception ex, HttpServletRequest req) {
        return error(HttpStatus.FORBIDDEN, "Insufficient privileges", req, List.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ApiError> conflict(Exception ex, HttpServletRequest req) {
        return error(HttpStatus.CONFLICT, "Username or email is already taken", req, List.of());
    }

    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<ApiError> responseStatus(ResponseStatusException ex, HttpServletRequest req) {
        return error(HttpStatus.valueOf(ex.getStatusCode().value()), ex.getReason(), req, List.of());
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiError> unexpected(Exception ex, HttpServletRequest req) {
        ex.printStackTrace();
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error: " + ex.getMessage(), req, List.of());
    }

    private ResponseEntity<ApiError> error(HttpStatus status, String message, HttpServletRequest req, List<ApiError.FieldViolation> violations) {
        return ResponseEntity.status(status).body(new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message == null ? status.getReasonPhrase() : message, req.getRequestURI(), violations));
    }
}
