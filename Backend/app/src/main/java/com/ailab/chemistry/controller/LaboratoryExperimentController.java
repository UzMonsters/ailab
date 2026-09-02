package com.ailab.chemistry.controller;

import com.ailab.chemistry.api.SimulationEngineService;
import com.ailab.chemistry.api.SimulationSessionService;
import com.ailab.chemistry.domain.laboratoryevent.IdempotencyKey;
import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEventId;
import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEventPayload;
import com.ailab.chemistry.domain.simulationengine.SimulationCalculationAudit;
import com.ailab.chemistry.domain.simulationengine.SimulationCommand;
import com.ailab.chemistry.domain.simulationengine.SimulationExecutionResult;
import com.ailab.chemistry.domain.simulationstate.CreateSimulationSessionRequest;
import com.ailab.chemistry.domain.simulationstate.SimulationSessionId;
import com.ailab.chemistry.domain.simulationstate.SimulationState;
import com.ailab.workspace.dto.MeasurementPointDto;
import com.ailab.workspace.service.LaboratoryAccessService;
import com.ailab.workspace.service.MeasurementService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chemistry/experiments")
@Tag(name = "Laboratory Experiments & Simulation", description = "Virtual experiment lifecycle, scientific operations, simulation execution, safety checks, audit, measurements, and replay")
@SecurityRequirement(name = "bearerAuth")
public class LaboratoryExperimentController {

    private final SimulationSessionService sessionService;
    private final SimulationEngineService engineService;
    private final LaboratoryAccessService accessService;
    private final MeasurementService measurementService;

    public LaboratoryExperimentController(
            SimulationSessionService sessionService,
            SimulationEngineService engineService,
            LaboratoryAccessService accessService,
            MeasurementService measurementService
    ) {
        this.sessionService = sessionService;
        this.engineService = engineService;
        this.accessService = accessService;
        this.measurementService = measurementService;
    }

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank() || "anonymousUser".equalsIgnoreCase(auth.getName())) {
            throw new org.springframework.security.authentication.InsufficientAuthenticationException("User must be authenticated");
        }
        return auth.getName();
    }

    @PostMapping
    @Operation(summary = "Create experiment simulation session", description = "Initialize a new virtual laboratory experiment session with initial apparatus, containers, and environmental conditions.")
    public SimulationState createExperiment(@Valid @RequestBody CreateSimulationSessionRequest request) {
        return sessionService.createSession(request);
    }

    @GetMapping("/{sessionId}")
    @Operation(summary = "Get experiment current state", description = "Retrieve current state, reactants, apparatus, temperature, pressure, and version of an active experiment session.")
    public SimulationState getExperimentState(@PathVariable String sessionId) {
        accessService.verifyExperimentAccess(sessionId, getCurrentUserId());
        return sessionService.getCurrentState(new SimulationSessionId(sessionId));
    }

    @PostMapping("/{sessionId}/operations")
    @Operation(summary = "Execute scientific operation on experiment", description = "Execute a scientific operation (MIX, HEAT, COOL, PRESSURE_CHANGE, TITRATE) on an active experiment with real-time safety evaluation.")
    public SimulationExecutionResult executeOperation(
            @PathVariable String sessionId,
            @Valid @RequestBody SimulationOperationRequest request) {
        accessService.verifyExperimentAccess(sessionId, getCurrentUserId());
        IdempotencyKey key = request.idempotencyKey() != null && !request.idempotencyKey().isBlank()
                ? new IdempotencyKey(request.idempotencyKey())
                : new IdempotencyKey(UUID.randomUUID().toString());
        return engineService.execute(
                new SimulationSessionId(sessionId),
                request.expectedStateVersion(),
                key,
                request.command()
        );
    }

    @PostMapping("/{sessionId}/events")
    @Operation(summary = "Append laboratory event", description = "Append a discrete laboratory event payload (e.g. ADD_REAGENT, CONTAINER_SEAL) to an experiment session.")
    public SimulationState appendEvent(
            @PathVariable String sessionId,
            @Valid @RequestBody AppendEventRequest request) {
        accessService.verifyExperimentAccess(sessionId, getCurrentUserId());
        IdempotencyKey key = request.idempotencyKey() != null && !request.idempotencyKey().isBlank()
                ? new IdempotencyKey(request.idempotencyKey())
                : new IdempotencyKey(UUID.randomUUID().toString());
        return sessionService.appendEvent(
                new SimulationSessionId(sessionId),
                request.expectedVersion(),
                key,
                request.payload()
        );
    }

    @PostMapping("/{sessionId}/replay")
    @Operation(summary = "Replay experiment simulation session", description = "Replay all events in an experiment session from initial snapshot deterministically.")
    public SimulationState replayExperiment(@PathVariable String sessionId) {
        accessService.verifyExperimentAccess(sessionId, getCurrentUserId());
        return sessionService.replay(new SimulationSessionId(sessionId));
    }

    @GetMapping("/{sessionId}/audit/{eventId}")
    @Operation(summary = "Get calculation audit for event", description = "Retrieve immutable calculation audit log entry (inputs, formulas used, safety rule evaluations, outputs) for a specific event.")
    public SimulationCalculationAudit getCalculationAudit(
            @PathVariable String sessionId,
            @PathVariable String eventId) {
        accessService.verifyExperimentAccess(sessionId, getCurrentUserId());
        return engineService.audit(
                new SimulationSessionId(sessionId),
                new LaboratoryEventId(eventId)
        );
    }

    @GetMapping("/{sessionId}/measurements")
    @Operation(summary = "Get experiment measurements", description = "Query sensor time-series records (temperature, pH, mass) for an active or completed experiment session.")
    public List<MeasurementPointDto> getExperimentMeasurements(
            @PathVariable String sessionId,
            @RequestParam(required = false) String kind,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(defaultValue = "100") int limit) {
        accessService.verifyExperimentAccess(sessionId, getCurrentUserId());
        return measurementService.getMeasurements(sessionId, null, kind, from, to, limit);
    }

    public record SimulationOperationRequest(
            long expectedStateVersion,
            String idempotencyKey,
            @NotNull SimulationCommand command
    ) {}

    public record AppendEventRequest(
            long expectedVersion,
            String idempotencyKey,
            @NotNull LaboratoryEventPayload payload
    ) {}
}
