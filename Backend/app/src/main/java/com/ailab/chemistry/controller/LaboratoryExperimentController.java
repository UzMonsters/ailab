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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chemistry/experiments")
@Tag(name = "Laboratory Experiments & Simulation", description = "Virtual experiment lifecycle, scientific operations, simulation execution, safety checks, audit, and replay")
@SecurityRequirement(name = "bearerAuth")
public class LaboratoryExperimentController {

    private final SimulationSessionService sessionService;
    private final SimulationEngineService engineService;

    public LaboratoryExperimentController(SimulationSessionService sessionService, SimulationEngineService engineService) {
        this.sessionService = sessionService;
        this.engineService = engineService;
    }

    @PostMapping
    @Operation(summary = "Create experiment simulation session", description = "Initialize a new virtual laboratory experiment session with initial apparatus, containers, and environmental conditions.")
    public SimulationState createExperiment(@Valid @RequestBody CreateSimulationSessionRequest request) {
        return sessionService.createSession(request);
    }

    @GetMapping("/{sessionId}")
    @Operation(summary = "Get experiment current state", description = "Retrieve current state, reactants, apparatus, temperature, pressure, and version of an active experiment session.")
    public SimulationState getExperimentState(@PathVariable UUID sessionId) {
        return sessionService.getCurrentState(new SimulationSessionId(sessionId.toString()));
    }

    @PostMapping("/{sessionId}/operations")
    @Operation(summary = "Execute scientific operation on experiment", description = "Execute a scientific operation (MIX, HEAT, COOL, PRESSURE_CHANGE, TITRATE) on an active experiment with real-time safety evaluation.")
    public SimulationExecutionResult executeOperation(
            @PathVariable UUID sessionId,
            @Valid @RequestBody SimulationOperationRequest request) {
        IdempotencyKey key = request.idempotencyKey() != null && !request.idempotencyKey().isBlank()
                ? new IdempotencyKey(request.idempotencyKey())
                : new IdempotencyKey(UUID.randomUUID().toString());
        return engineService.execute(
                new SimulationSessionId(sessionId.toString()),
                request.expectedStateVersion(),
                key,
                request.command()
        );
    }

    @PostMapping("/{sessionId}/events")
    @Operation(summary = "Append laboratory event", description = "Append a discrete laboratory event payload (e.g. ADD_REAGENT, CONTAINER_SEAL) to an experiment session.")
    public SimulationState appendEvent(
            @PathVariable UUID sessionId,
            @Valid @RequestBody AppendEventRequest request) {
        IdempotencyKey key = request.idempotencyKey() != null && !request.idempotencyKey().isBlank()
                ? new IdempotencyKey(request.idempotencyKey())
                : new IdempotencyKey(UUID.randomUUID().toString());
        return sessionService.appendEvent(
                new SimulationSessionId(sessionId.toString()),
                request.expectedVersion(),
                key,
                request.payload()
        );
    }

    @PostMapping("/{sessionId}/replay")
    @Operation(summary = "Replay experiment simulation session", description = "Replay all events in an experiment session from initial snapshot deterministically.")
    public SimulationState replayExperiment(@PathVariable UUID sessionId) {
        return sessionService.replay(new SimulationSessionId(sessionId.toString()));
    }

    @GetMapping("/{sessionId}/audit/{eventId}")
    @Operation(summary = "Get calculation audit for event", description = "Retrieve immutable calculation audit log entry (inputs, formulas used, safety rule evaluations, outputs) for a specific event.")
    public SimulationCalculationAudit getCalculationAudit(
            @PathVariable UUID sessionId,
            @PathVariable UUID eventId) {
        return engineService.audit(
                new SimulationSessionId(sessionId.toString()),
                new LaboratoryEventId(eventId.toString())
        );
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
