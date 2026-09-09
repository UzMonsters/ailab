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

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class LaboratoryExceptionHandler {

    @ExceptionHandler(WorkspaceNotFoundException.class)
    ResponseEntity<ApiError> workspaceNotFound(WorkspaceNotFoundException ex, HttpServletRequest req) {
        return problem(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", "Workspace Not Found", ex.getMessage(), req, Map.of());
    }

    @ExceptionHandler(VersionConflictException.class)
    ResponseEntity<ApiError> workspaceVersionConflict(VersionConflictException ex, HttpServletRequest req) {
        List<ApiError.FieldViolation> violations = List.of(
                new ApiError.FieldViolation("version", "STALE", "Expected " + ex.getExpectedVersion() + ", actual " + ex.getActualVersion())
        );
        return problemWithViolations(HttpStatus.CONFLICT, "VERSION_CONFLICT", "Version Conflict", ex.getMessage(), req, violations);
    }

    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<ApiError> responseStatus(ResponseStatusException ex, HttpServletRequest req) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        if (status == null) status = HttpStatus.BAD_REQUEST;
        String reason = ex.getReason() != null ? ex.getReason() : status.getReasonPhrase();
        String code = status.name();
        String detail = reason;

        int colon = reason.indexOf(':');
        if (colon > 0) {
            String potentialCode = reason.substring(0, colon).trim();
            if (!potentialCode.contains(" ")) {
                code = potentialCode;
                detail = reason.substring(colon + 1).trim();
            }
        } else if (!reason.contains(" ")) {
            code = reason.trim();
        }

        return problem(status, code, status.getReasonPhrase(), detail, req, Map.of());
    }

    @ExceptionHandler(SafetyException.class)
    ResponseEntity<ApiError> unsafeScientificOperation(SafetyException ex, HttpServletRequest req) {
        return problem(HttpStatus.UNPROCESSABLE_ENTITY, "SAFETY_VIOLATION", "Laboratory Safety Violation", ex.getMessage(), req, Map.of(
                "code", ex.getErrorCode().name()));
    }

    @ExceptionHandler(SimulationExecutionException.class)
    ResponseEntity<ApiError> simulationExecution(SimulationExecutionException ex, HttpServletRequest req) {
        HttpStatus status = ex.getErrorCode() == SimulationExecutionErrorCode.STALE_STATE_VERSION
                || ex.getErrorCode() == SimulationExecutionErrorCode.IDEMPOTENCY_CONFLICT
                ? HttpStatus.CONFLICT
                : HttpStatus.BAD_REQUEST;
        String code = ex.getErrorCode() == SimulationExecutionErrorCode.STALE_STATE_VERSION
                ? "VERSION_CONFLICT"
                : "SIMULATION_" + ex.getErrorCode().name();
        return problem(status, code, "Simulation Execution Error", ex.getMessage(), req, Map.of("code", ex.getErrorCode().name()));
    }

    @ExceptionHandler(SimulationStateException.class)
    ResponseEntity<ApiError> simulationState(SimulationStateException ex, HttpServletRequest req) {
        HttpStatus status = ex.errorCode() == SimulationStateErrorCode.STALE_STATE_VERSION
                || ex.errorCode() == SimulationStateErrorCode.IDEMPOTENCY_CONFLICT
                ? HttpStatus.CONFLICT
                : HttpStatus.UNPROCESSABLE_ENTITY;
        String code = ex.errorCode() == SimulationStateErrorCode.STALE_STATE_VERSION
                ? "VERSION_CONFLICT"
                : "STATE_" + ex.errorCode().name();
        return problem(status, code, "Simulation State Error", ex.getMessage(), req, Map.of("code", ex.errorCode().name()));
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
        List<ApiError.FieldViolation> violations = List.of(
                new ApiError.FieldViolation("version", "STALE", "Expected " + ex.getExpectedVersion() + ", actual " + ex.getActualVersion())
        );
        return problemWithViolations(HttpStatus.CONFLICT, "VERSION_CONFLICT", "Version Conflict", ex.getMessage(), req, violations);
    }

    @ExceptionHandler(com.ailab.learning.exception.LearningStateVersionConflictException.class)
    ResponseEntity<ApiError> learningStateVersionConflict(com.ailab.learning.exception.LearningStateVersionConflictException ex, HttpServletRequest req) {
        List<ApiError.FieldViolation> violations = List.of(
                new ApiError.FieldViolation("stateVersion", "STALE", "Expected " + ex.getExpectedVersion() + ", actual " + ex.getActualVersion())
        );
        return problemWithViolations(HttpStatus.CONFLICT, "VERSION_CONFLICT", "Version Conflict", ex.getMessage(), req, violations);
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

    private ResponseEntity<ApiError> problemWithViolations(HttpStatus status, String code, String title, String message, HttpServletRequest req, List<ApiError.FieldViolation> violations) {
        String correlationId = req.getHeader("X-Correlation-Id");
        ApiError err = ApiError.ofProblemWithViolations(status.value(), code, title, message != null ? message : title, req.getRequestURI(), correlationId, violations);
        return ResponseEntity.status(status).body(err);
    }
}
