package com.ailab.chemistry.service;

import com.ailab.chemistry.api.SimulationEngineService;
import com.ailab.chemistry.domain.laboratoryevent.CausationId;
import com.ailab.chemistry.domain.laboratoryevent.CorrelationId;
import com.ailab.chemistry.domain.laboratoryevent.IdempotencyKey;
import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEvent;
import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEventId;
import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEventSequence;
import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEventSource;
import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEventStore;
import com.ailab.chemistry.domain.laboratoryevent.ScientificOperationAppliedPayload;
import com.ailab.chemistry.domain.laboratoryprocess.LaboratoryProcessDefinition;
import com.ailab.chemistry.domain.laboratoryprocess.LaboratoryProcessRepository;
import com.ailab.chemistry.domain.laboratoryprocess.LaboratoryProcessStep;
import com.ailab.chemistry.domain.simulationengine.MaterialStateDelta;
import com.ailab.chemistry.domain.simulationengine.ScientificOperationSpecification;
import com.ailab.chemistry.domain.simulationengine.SimulationCalculationAudit;
import com.ailab.chemistry.domain.simulationengine.SimulationCalculationAuditRepository;
import com.ailab.chemistry.domain.simulationengine.SimulationCommand;
import com.ailab.chemistry.domain.simulationengine.SimulationEngine;
import com.ailab.chemistry.domain.simulationengine.SimulationExecutionErrorCode;
import com.ailab.chemistry.domain.simulationengine.SimulationExecutionException;
import com.ailab.chemistry.domain.simulationengine.SimulationExecutionPlan;
import com.ailab.chemistry.domain.simulationengine.SimulationExecutionResult;
import com.ailab.chemistry.domain.simulationengine.SimulationExecutionStatus;
import com.ailab.chemistry.domain.simulationstate.ProcessStepExecutionStatus;
import com.ailab.chemistry.domain.simulationstate.SimulationSessionId;
import com.ailab.chemistry.domain.simulationstate.SimulationSessionStatus;
import com.ailab.chemistry.domain.simulationstate.SimulationState;
import com.ailab.chemistry.domain.simulationstate.SimulationStateReducer;
import com.ailab.chemistry.domain.simulationstate.SimulationStateRepository;
import com.ailab.chemistry.domain.simulationstate.SimulationStateVersion;
import com.ailab.chemistry.infrastructure.persistence.simulation.LaboratoryEventCodec;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class SimulationEngineServiceImpl implements SimulationEngineService {
    private static final int SNAPSHOT_FREQUENCY = 2;

    private final LaboratoryProcessRepository processRepository;
    private final LaboratoryEventStore eventStore;
    private final SimulationStateRepository stateRepository;
    private final SimulationCalculationAuditRepository auditRepository;
    private final com.ailab.chemistry.api.LaboratorySafetyService safetyService;
    private final com.ailab.chemistry.domain.laboratorysafety.LaboratorySafetyRepository safetyRepository;
    private final SimulationStateReducer reducer = new SimulationStateReducer();
    private final SimulationEngine engine = new SimulationEngine();
    private final LaboratoryEventCodec codec = new LaboratoryEventCodec();
    private final Clock clock = Clock.systemUTC();

    public SimulationEngineServiceImpl(LaboratoryProcessRepository processRepository,
                                       LaboratoryEventStore eventStore,
                                       SimulationStateRepository stateRepository,
                                       SimulationCalculationAuditRepository auditRepository,
                                       com.ailab.chemistry.api.LaboratorySafetyService safetyService,
                                       com.ailab.chemistry.domain.laboratorysafety.LaboratorySafetyRepository safetyRepository) {
        this.processRepository = processRepository;
        this.eventStore = eventStore;
        this.stateRepository = stateRepository;
        this.auditRepository = auditRepository;
        this.safetyService = safetyService;
        this.safetyRepository = safetyRepository;
    }

    @Override
    @Transactional
    public SimulationExecutionResult execute(SimulationSessionId sessionId, long expectedStateVersion,
                                             IdempotencyKey idempotencyKey, SimulationCommand command) {
        String commandFingerprint = engine.commandFingerprint(command);
        var existing = eventStore.findByIdempotencyKey(sessionId, idempotencyKey);
        if (existing.isPresent()) {
            if (!(existing.get().payload() instanceof ScientificOperationAppliedPayload payload)
                    || !payload.commandFingerprint().equals(commandFingerprint)) {
                throw failure(SimulationExecutionErrorCode.IDEMPOTENCY_CONFLICT,
                        "Conflicting reuse of idempotency key rejected");
            }
            SimulationState replayed = replayUntil(sessionId, existing.get().sequence().value());
            SimulationCalculationAudit audit = auditRepository.find(sessionId, existing.get().eventId())
                    .orElseThrow(() -> failure(SimulationExecutionErrorCode.AUDIT_NOT_FOUND,
                            "Calculation audit is missing for event " + existing.get().eventId().value()));
            return new SimulationExecutionResult(SimulationExecutionStatus.APPLIED, existing.get().eventId(), payload, replayed, audit);
        }

        SimulationState current = stateRepository.lockCurrent(sessionId);
        if (current.version().value() != expectedStateVersion) {
            throw failure(SimulationExecutionErrorCode.STALE_STATE_VERSION,
                    "stale expected version " + expectedStateVersion + "; current version is " + current.version().value());
        }
        if (current.status() != SimulationSessionStatus.RUNNING) {
            throw failure(SimulationExecutionErrorCode.SESSION_NOT_RUNNING, "Simulation session must be RUNNING");
        }
        if (current.step(command.stepId()).status() != ProcessStepExecutionStatus.RUNNING) {
            throw failure(SimulationExecutionErrorCode.STEP_NOT_RUNNING, "Target process step must be RUNNING");
        }

        LaboratoryProcessDefinition process = processRepository.findByCodeAndVersion(
                        current.processExecution().processCode(), current.processExecution().processVersion())
                .orElseThrow(() -> failure(SimulationExecutionErrorCode.INVALID_COMMAND,
                        "Process definition not found for current session"));
        LaboratoryProcessStep step = process.steps().stream()
                .filter(candidate -> candidate.id().value().equals(command.stepId()))
                .findFirst()
                .orElseThrow(() -> failure(SimulationExecutionErrorCode.STEP_NOT_RUNNING,
                        "Target process step definition not found"));
        assertOperationAllowed(step, command.operation());
        assertSuitability(command);

        // PRE-EXECUTION SAFETY GATE
        var preReq = com.ailab.chemistry.domain.laboratorysafety.LaboratorySafetyEvaluationRequest.preExecution(command, current, command.inputs());
        var preRes = safetyService.evaluate(preReq);
        if (!preRes.isAllowed()) {
            safetyRepository.saveAudit(preRes, sessionId.value(), command.commandId().value(), null);
            throw new com.ailab.chemistry.domain.laboratorysafety.SafetyException(
                    com.ailab.chemistry.domain.laboratorysafety.SafetyErrorCode.SAFETY_RULE_VIOLATION,
                    "Pre-execution safety gate blocked operation: " + preRes.status(), preRes);
        }

        SimulationExecutionPlan plan = new SimulationExecutionPlan(process.code(), process.version().value(), command.stepId());
        var domainResult = engine.execute(plan, command);
        ScientificOperationAppliedPayload payload = domainResult.payload();
        assertDeltaAppliesToCurrentState(current, payload);

        // POST-CALCULATION SAFETY GATE
        var postReq = com.ailab.chemistry.domain.laboratorysafety.LaboratorySafetyEvaluationRequest.postCalculation(command, current, payload.stateDelta(), command.inputs());
        var postRes = safetyService.evaluate(postReq);
        if (!postRes.isAllowed()) {
            safetyRepository.saveAudit(postRes, sessionId.value(), command.commandId().value(), null);
            throw new com.ailab.chemistry.domain.laboratorysafety.SafetyException(
                    com.ailab.chemistry.domain.laboratorysafety.SafetyErrorCode.SAFETY_RULE_VIOLATION,
                    "Post-calculation safety gate blocked operation: " + postRes.status(), postRes);
        }

        long sequence = current.version().value() + 1;
        LaboratoryEvent event = event(sessionId, sequence, idempotencyKey, payload, Instant.now(clock),
                new CausationId("state-" + current.version().value()));
        SimulationState next = reducer.apply(current, event);
        eventStore.append(event);
        SimulationCalculationAudit audit = audit(payload, event);
        auditRepository.save(audit);
        safetyRepository.saveAudit(preRes, sessionId.value(), command.commandId().value(), event.eventId().value());
        safetyRepository.saveAudit(postRes, sessionId.value(), command.commandId().value(), event.eventId().value());
        stateRepository.updateCurrent(next);
        if (next.version().value() % SNAPSHOT_FREQUENCY == 0) {
            stateRepository.saveSnapshot(next, event.sequence().value(), codec.checksum(next));
        }
        return new SimulationExecutionResult(SimulationExecutionStatus.APPLIED, event.eventId(), payload, next, audit);
    }

    @Override
    public SimulationCalculationAudit audit(SimulationSessionId sessionId, LaboratoryEventId eventId) {
        return auditRepository.find(sessionId, eventId)
                .orElseThrow(() -> failure(SimulationExecutionErrorCode.AUDIT_NOT_FOUND,
                        "Calculation audit not found for event " + eventId.value()));
    }

    private void assertOperationAllowed(LaboratoryProcessStep step, ScientificOperationSpecification operation) {
        boolean allowed = step.scientificOperationSpecifications().stream()
                .map(ScientificOperationSpecification::operationType)
                .anyMatch(type -> type == operation.operationType());
        if (!allowed) {
            throw failure(SimulationExecutionErrorCode.OPERATION_NOT_ALLOWED_FOR_STEP,
                    "Operation " + operation.operationType() + " is not explicitly allowed by step " + step.id().value());
        }
    }

    private void assertSuitability(SimulationCommand command) {
        BigDecimal targetTemperature = decimalInput(command, "targetTemperatureK");
        if (targetTemperature != null && targetTemperature.compareTo(new BigDecimal("1000")) > 0) {
            throw failure(SimulationExecutionErrorCode.SUITABILITY_REJECTED,
                    "Container/equipment temperature limit rejected the operation");
        }
        BigDecimal finalPressure = decimalInput(command, "finalPressureKPa");
        if (finalPressure != null && finalPressure.compareTo(new BigDecimal("500")) > 0) {
            throw failure(SimulationExecutionErrorCode.SUITABILITY_REJECTED,
                    "Container pressure limit rejected the operation");
        }
    }

    private void assertDeltaAppliesToCurrentState(SimulationState current, ScientificOperationAppliedPayload payload) {
        for (var vesselDelta : payload.stateDelta().vesselDeltas()) {
            for (MaterialStateDelta materialDelta : vesselDelta.materialDeltas()) {
                if (materialDelta.quantityDelta().compareTo(BigDecimal.ZERO) < 0) {
                    BigDecimal available = current.quantity(materialDelta.vesselId(), materialDelta.compoundCode(),
                            materialDelta.unit());
                    if (available.compareTo(materialDelta.quantityDelta().abs()) < 0) {
                        throw failure(SimulationExecutionErrorCode.STATE_DELTA_INVARIANT_FAILED,
                                "State delta would make material quantity negative");
                    }
                }
            }
        }
    }

    private BigDecimal decimalInput(SimulationCommand command, String key) {
        String value = command.inputs().get(key);
        return value == null || value.isBlank() ? null : new BigDecimal(value);
    }

    private SimulationCalculationAudit audit(ScientificOperationAppliedPayload payload, LaboratoryEvent event) {
        return new SimulationCalculationAudit(
                event.eventId(),
                event.sessionId(),
                payload.commandId(),
                Enum.valueOf(com.ailab.chemistry.domain.simulationengine.SimulationOperationType.class, payload.operationType()),
                payload.model(),
                payload.datasetVersions(),
                payload.inputHash(),
                payload.resultHash(),
                payload.calculationTrace(),
                payload.conservationLedger(),
                event.recordedAt());
    }

    private SimulationState replayUntil(SimulationSessionId sessionId, long sequence) {
        SimulationState state = SimulationState.initial(sessionId);
        for (LaboratoryEvent event : eventStore.eventsForSession(sessionId)) {
            if (event.sequence().value() <= sequence) {
                state = reducer.apply(state, event);
            }
        }
        return state;
    }

    private LaboratoryEvent event(SimulationSessionId sessionId, long sequence, IdempotencyKey idempotencyKey,
                                  ScientificOperationAppliedPayload payload, Instant occurredAt,
                                  CausationId causationId) {
        Instant normalizedOccurredAt = occurredAt.truncatedTo(ChronoUnit.MICROS);
        return new LaboratoryEvent(
                new LaboratoryEventId(sessionId.value() + "-EV-" + sequence + "-" + UUID.randomUUID()),
                sessionId,
                new LaboratoryEventSequence(sequence),
                new SimulationStateVersion(sequence),
                normalizedOccurredAt,
                Instant.now(clock).truncatedTo(ChronoUnit.MICROS),
                payload.eventType(),
                payload.eventSchemaVersion(),
                new LaboratoryEventSource("service", "simulation-engine"),
                new CorrelationId("corr-" + sessionId.value()),
                causationId,
                idempotencyKey,
                payload);
    }

    private SimulationExecutionException failure(SimulationExecutionErrorCode code, String message) {
        return new SimulationExecutionException(code, message);
    }
}
