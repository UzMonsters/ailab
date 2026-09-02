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
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class LaboratoryExceptionHandler {

    @ExceptionHandler(WorkspaceNotFoundException.class)
    ResponseEntity<ApiError> workspaceNotFound(WorkspaceNotFoundException ex, HttpServletRequest req) {
        return problem(HttpStatus.NOT_FOUND, "WORKSPACE_NOT_FOUND", "Workspace Not Found", ex.getMessage(), req, Map.of());
    }

    @ExceptionHandler(VersionConflictException.class)
    ResponseEntity<ApiError> workspaceVersionConflict(VersionConflictException ex, HttpServletRequest req) {
        return problem(HttpStatus.CONFLICT, "STATE_VERSION_CONFLICT", "State Version Conflict", ex.getMessage(), req, Map.of(
                "expectedVersion", Long.toString(ex.getExpectedVersion()),
                "actualVersion", Long.toString(ex.getActualVersion())));
    }

    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<ApiError> responseStatus(ResponseStatusException ex, HttpServletRequest req) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        if (status == null) status = HttpStatus.BAD_REQUEST;
        String reason = ex.getReason() != null ? ex.getReason() : status.getReasonPhrase();
        String code = status.name();
        if (reason.startsWith("PORT_") || reason.startsWith("INVALID_") || reason.startsWith("THERMAL_")
                || reason.startsWith("LAST_OWNER") || reason.startsWith("STALE_") || reason.startsWith("SHARE_")) {
            int colon = reason.indexOf(':');
            code = colon > 0 ? reason.substring(0, colon).trim() : reason;
        }
        return problem(status, code, status.getReasonPhrase(), reason, req, Map.of());
    }

    @ExceptionHandler(SafetyException.class)
    ResponseEntity<ApiError> unsafeScientificOperation(SafetyException ex, HttpServletRequest req) {
        return problem(HttpStatus.UNPROCESSABLE_ENTITY, "SAFETY_VIOLATION_" + ex.getErrorCode().name(), "Laboratory Safety Violation", ex.getMessage(), req, Map.of(
                "code", ex.getErrorCode().name()));
    }

    @ExceptionHandler(SimulationExecutionException.class)
    ResponseEntity<ApiError> simulationExecution(SimulationExecutionException ex, HttpServletRequest req) {
        HttpStatus status = ex.getErrorCode() == SimulationExecutionErrorCode.STALE_STATE_VERSION
                || ex.getErrorCode() == SimulationExecutionErrorCode.IDEMPOTENCY_CONFLICT
                ? HttpStatus.CONFLICT
                : HttpStatus.BAD_REQUEST;
        return problem(status, "SIMULATION_" + ex.getErrorCode().name(), "Simulation Execution Error", ex.getMessage(), req, Map.of("code", ex.getErrorCode().name()));
    }

    @ExceptionHandler(SimulationStateException.class)
    ResponseEntity<ApiError> simulationState(SimulationStateException ex, HttpServletRequest req) {
        HttpStatus status = ex.errorCode() == SimulationStateErrorCode.STALE_STATE_VERSION
                || ex.errorCode() == SimulationStateErrorCode.IDEMPOTENCY_CONFLICT
                ? HttpStatus.CONFLICT
                : HttpStatus.UNPROCESSABLE_ENTITY;
        return problem(status, "STATE_" + ex.errorCode().name(), "Simulation State Error", ex.getMessage(), req, Map.of("code", ex.errorCode().name()));
    }

    @ExceptionHandler(com.ailab.learning.exception.LevelNotFoundException.class)
    ResponseEntity<ApiError> levelNotFound(com.ailab.learning.exception.LevelNotFoundException ex, HttpServletRequest req) {
        return problem(HttpStatus.NOT_FOUND, "LEVEL_NOT_FOUND", "Level Not Found", ex.getMessage(), req, Map.of());
    }

    @ExceptionHandler(com.ailab.learning.exception.PrerequisiteNotMetException.class)
    ResponseEntity<ApiError> prerequisiteNotMet(com.ailab.learning.exception.PrerequisiteNotMetException ex, HttpServletRequest req) {
        return problem(HttpStatus.CONFLICT, "PREREQUISITE_NOT_MET", "Prerequisite Not Met", ex.getMessage(), req, Map.of(
                "requiredLevelId", ex.getRequiredLevelId() != null ? ex.getRequiredLevelId() : ""));
    }

    @ExceptionHandler(com.ailab.learning.exception.LevelVersionChangedException.class)
    ResponseEntity<ApiError> levelVersionChanged(com.ailab.learning.exception.LevelVersionChangedException ex, HttpServletRequest req) {
        return problem(HttpStatus.CONFLICT, "LEVEL_VERSION_CHANGED", "Level Version Changed", ex.getMessage(), req, Map.of(
                "expectedVersion", Long.toString(ex.getExpectedVersion()),
                "actualVersion", Long.toString(ex.getActualVersion())));
    }

    @ExceptionHandler(com.ailab.learning.exception.LearningStateVersionConflictException.class)
    ResponseEntity<ApiError> learningStateVersionConflict(com.ailab.learning.exception.LearningStateVersionConflictException ex, HttpServletRequest req) {
        return problem(HttpStatus.CONFLICT, "STATE_VERSION_CONFLICT", "State Version Conflict", ex.getMessage(), req, Map.of(
                "expectedVersion", Long.toString(ex.getExpectedVersion()),
                "actualVersion", Long.toString(ex.getActualVersion())));
    }

    @ExceptionHandler(com.ailab.learning.exception.StepRequirementNotMetException.class)
    ResponseEntity<ApiError> stepRequirementNotMet(com.ailab.learning.exception.StepRequirementNotMetException ex, HttpServletRequest req) {
        return problem(HttpStatus.UNPROCESSABLE_ENTITY, "STEP_REQUIREMENT_NOT_MET", "Step Requirement Not Met", ex.getMessage(), req, Map.of(
                "reason", ex.getReason() != null ? ex.getReason() : "",
                "hint", ex.getHint() != null ? ex.getHint() : ""));
    }

    private ResponseEntity<ApiError> problem(HttpStatus status, String code, String title, String message, HttpServletRequest req, Map<String, String> errors) {
        String correlationId = req.getHeader("X-Correlation-Id");
        ApiError err = ApiError.ofProblem(status.value(), code, title, message != null ? message : title, req.getRequestURI(), correlationId, errors);
        return ResponseEntity.status(status).body(err);
    }
}
