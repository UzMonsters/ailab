package com.ailab.common.api;

import com.ailab.chemistry.domain.laboratorysafety.SafetyException;
import com.ailab.chemistry.domain.simulationengine.SimulationExecutionErrorCode;
import com.ailab.chemistry.domain.simulationengine.SimulationExecutionException;
import com.ailab.chemistry.domain.simulationstate.SimulationStateErrorCode;
import com.ailab.chemistry.domain.simulationstate.SimulationStateException;
import com.ailab.workspace.exception.VersionConflictException;
import com.ailab.workspace.exception.WorkspaceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class LaboratoryExceptionHandler {

    @ExceptionHandler(WorkspaceNotFoundException.class)
    ResponseEntity<ApiError> workspaceNotFound(WorkspaceNotFoundException ex, HttpServletRequest req) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage(), req, Map.of());
    }

    @ExceptionHandler(VersionConflictException.class)
    ResponseEntity<ApiError> workspaceVersionConflict(VersionConflictException ex, HttpServletRequest req) {
        return error(HttpStatus.CONFLICT, ex.getMessage(), req, Map.of(
                "expectedVersion", Long.toString(ex.getExpectedVersion()),
                "actualVersion", Long.toString(ex.getActualVersion())));
    }

    @ExceptionHandler(SafetyException.class)
    ResponseEntity<ApiError> unsafeScientificOperation(SafetyException ex, HttpServletRequest req) {
        return error(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), req, Map.of(
                "code", ex.getErrorCode().name()));
    }

    @ExceptionHandler(SimulationExecutionException.class)
    ResponseEntity<ApiError> simulationExecution(SimulationExecutionException ex, HttpServletRequest req) {
        HttpStatus status = ex.getErrorCode() == SimulationExecutionErrorCode.STALE_STATE_VERSION
                || ex.getErrorCode() == SimulationExecutionErrorCode.IDEMPOTENCY_CONFLICT
                ? HttpStatus.CONFLICT
                : HttpStatus.BAD_REQUEST;
        return error(status, ex.getMessage(), req, Map.of("code", ex.getErrorCode().name()));
    }

    @ExceptionHandler(SimulationStateException.class)
    ResponseEntity<ApiError> simulationState(SimulationStateException ex, HttpServletRequest req) {
        HttpStatus status = ex.errorCode() == SimulationStateErrorCode.STALE_STATE_VERSION
                || ex.errorCode() == SimulationStateErrorCode.IDEMPOTENCY_CONFLICT
                ? HttpStatus.CONFLICT
                : HttpStatus.UNPROCESSABLE_ENTITY;
        return error(status, ex.getMessage(), req, Map.of("code", ex.errorCode().name()));
    }

    private ResponseEntity<ApiError> error(HttpStatus status, String message, HttpServletRequest req, Map<String, String> errors) {
        String correlationId = req.getHeader("X-Correlation-Id");
        return ResponseEntity.status(status).body(new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message == null ? status.getReasonPhrase() : message,
                req.getRequestURI(),
                List.of(),
                List.of(),
                errors,
                correlationId == null || correlationId.isBlank() ? null : correlationId));
    }
}
