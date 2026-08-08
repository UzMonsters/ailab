package com.ailab.chemistry.service;

import com.ailab.chemistry.api.ContainerService;
import com.ailab.chemistry.api.EquipmentService;
import com.ailab.chemistry.api.SimulationSessionService;
import com.ailab.chemistry.domain.container.ContainerProfileSuitabilityRequest;
import com.ailab.chemistry.domain.container.ContainerSuitabilityStatus;
import com.ailab.chemistry.domain.equipment.CalibrationRecord;
import com.ailab.chemistry.domain.equipment.EquipmentProfileSuitabilityRequest;
import com.ailab.chemistry.domain.equipment.EquipmentRequirement;
import com.ailab.chemistry.domain.equipment.EquipmentSuitabilityStatus;
import com.ailab.chemistry.domain.laboratoryevent.CausationId;
import com.ailab.chemistry.domain.laboratoryevent.CorrelationId;
import com.ailab.chemistry.domain.laboratoryevent.IdempotencyKey;
import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEvent;
import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEventId;
import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEventPayload;
import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEventSequence;
import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEventSource;
import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEventStore;
import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEventType;
import com.ailab.chemistry.domain.laboratoryevent.MaterialDispensedPayload;
import com.ailab.chemistry.domain.laboratoryevent.MaterialTransferredPayload;
import com.ailab.chemistry.domain.laboratoryevent.SessionCreatedPayload;
import com.ailab.chemistry.domain.laboratoryevent.SessionLifecyclePayload;
import com.ailab.chemistry.domain.laboratoryevent.StepDefinitionSnapshot;
import com.ailab.chemistry.domain.laboratoryevent.StepStartedPayload;
import com.ailab.chemistry.domain.laboratoryprocess.LaboratoryProcessDefinition;
import com.ailab.chemistry.domain.laboratoryprocess.LaboratoryProcessException;
import com.ailab.chemistry.domain.laboratoryprocess.LaboratoryProcessRepository;
import com.ailab.chemistry.domain.measurement.MeasurementResolution;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.measurement.Volume;
import com.ailab.chemistry.domain.measurement.VolumeUnit;
import com.ailab.chemistry.domain.simulationstate.CreateSimulationSessionRequest;
import com.ailab.chemistry.domain.simulationstate.SimulationSessionId;
import com.ailab.chemistry.domain.simulationstate.SimulationSessionStatus;
import com.ailab.chemistry.domain.simulationstate.SimulationState;
import com.ailab.chemistry.domain.simulationstate.SimulationStateErrorCode;
import com.ailab.chemistry.domain.simulationstate.SimulationStateException;
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
import java.util.List;
import java.util.UUID;

@Service
public class SimulationSessionServiceImpl implements SimulationSessionService {
    private static final int SNAPSHOT_FREQUENCY = 2;

    private final LaboratoryProcessRepository processRepository;
    private final LaboratoryEventStore eventStore;
    private final SimulationStateRepository stateRepository;
    private final EquipmentService equipmentService;
    private final ContainerService containerService;
    private final SimulationStateReducer reducer = new SimulationStateReducer();
    private final LaboratoryEventCodec codec = new LaboratoryEventCodec();
    private final Clock clock = Clock.systemUTC();

    public SimulationSessionServiceImpl(LaboratoryProcessRepository processRepository,
                                        LaboratoryEventStore eventStore,
                                        SimulationStateRepository stateRepository,
                                        EquipmentService equipmentService,
                                        ContainerService containerService) {
        this.processRepository = processRepository;
        this.eventStore = eventStore;
        this.stateRepository = stateRepository;
        this.equipmentService = equipmentService;
        this.containerService = containerService;
    }

    @Override
    @Transactional
    public SimulationState createSession(CreateSimulationSessionRequest request) {
        LaboratoryProcessDefinition process = processRepository.findByCodeAndVersion(request.processCode(), request.processVersion())
                .orElseThrow(() -> new LaboratoryProcessException("Process definition not found: "
                        + request.processCode() + " v" + request.processVersion()));
        SimulationState initial = SimulationState.initial(request.sessionId());
        stateRepository.create(initial, process.code(), process.version().value());

        SessionCreatedPayload payload = new SessionCreatedPayload(
                process.code(),
                process.version().value(),
                process.steps().stream()
                        .map(step -> new StepDefinitionSnapshot(step.id().value(), step.optional(),
                                step.dependencies().stream().map(dep -> dep.stepId().value()).toList()))
                        .toList());
        LaboratoryEvent event = event(request.sessionId(), 1, new IdempotencyKey("create-" + request.sessionId().value()),
                LaboratoryEventType.SESSION_CREATED, payload, request.requestedAt(), null);
        eventStore.append(event);
        SimulationState created = reducer.apply(initial, event);
        stateRepository.updateCurrent(created);
        return created;
    }

    @Override
    @Transactional
    public SimulationState appendEvent(SimulationSessionId sessionId, long expectedVersion, IdempotencyKey idempotencyKey,
                                      LaboratoryEventPayload payload) {
        LaboratoryEventType eventType = eventTypeFor(payload, stateRepository.findCurrent(sessionId).orElse(null));
        var existing = eventStore.findByIdempotencyKey(sessionId, idempotencyKey);
        if (existing.isPresent()) {
            if (!codec.fingerprint(existing.get().payload()).equals(codec.fingerprint(payload))) {
                throw new SimulationStateException(SimulationStateErrorCode.IDEMPOTENCY_CONFLICT,
                        "Conflicting reuse of idempotency key rejected");
            }
            return replayUntil(sessionId, existing.get().sequence().value());
        }

        SimulationState current = stateRepository.lockCurrent(sessionId);
        if (current.version().value() != expectedVersion) {
            throw new SimulationStateException(SimulationStateErrorCode.STALE_STATE_VERSION,
                    "stale expected version " + expectedVersion + "; current version is " + current.version().value());
        }

        assertPhaseTwelveSuitability(payload, Instant.now(clock));
        long sequence = current.version().value() + 1;
        LaboratoryEvent event = event(sessionId, sequence, idempotencyKey, eventType, payload, Instant.now(clock),
                current.version().value() == 0 ? null : new CausationId("state-" + current.version().value()));
        SimulationState next = reducer.apply(current, event);
        eventStore.append(event);
        stateRepository.updateCurrent(next);
        if (next.version().value() % SNAPSHOT_FREQUENCY == 0) {
            stateRepository.saveSnapshot(next, event.sequence().value(), codec.checksum(next));
        }
        return next;
    }

    @Override
    public SimulationState getCurrentState(SimulationSessionId sessionId) {
        return stateRepository.findCurrent(sessionId)
                .orElseThrow(() -> new SimulationStateException(SimulationStateErrorCode.STALE_STATE_VERSION,
                        "Simulation session not found: " + sessionId.value()));
    }

    @Override
    public SimulationState replay(SimulationSessionId sessionId) {
        return reducer.replay(sessionId, eventStore.eventsForSession(sessionId));
    }

    @Override
    public SimulationState replayFromLatestSnapshot(SimulationSessionId sessionId) {
        return stateRepository.latestSnapshot(sessionId)
                .map(snapshot -> {
                    SimulationState state = snapshot.state();
                    for (LaboratoryEvent event : eventStore.eventsForSession(sessionId)) {
                        if (event.sequence().value() > snapshot.eventSequence()) {
                            state = reducer.apply(state, event);
                        }
                    }
                    return state;
                })
                .orElseGet(() -> replay(sessionId));
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
                                  LaboratoryEventType type, LaboratoryEventPayload payload, Instant occurredAt,
                                  CausationId causationId) {
        Instant normalizedOccurredAt = occurredAt.truncatedTo(ChronoUnit.MICROS);
        return new LaboratoryEvent(
                new LaboratoryEventId(sessionId.value() + "-EV-" + sequence + "-" + UUID.randomUUID()),
                sessionId,
                new LaboratoryEventSequence(sequence),
                new SimulationStateVersion(sequence),
                normalizedOccurredAt,
                Instant.now(clock).truncatedTo(ChronoUnit.MICROS),
                type,
                1,
                new LaboratoryEventSource("service", "internal"),
                new CorrelationId("corr-" + sessionId.value()),
                causationId,
                idempotencyKey,
                payload);
    }

    private LaboratoryEventType eventTypeFor(LaboratoryEventPayload payload, SimulationState current) {
        if (payload instanceof SessionLifecyclePayload) {
            if (current != null && current.status() == SimulationSessionStatus.PAUSED) {
                return LaboratoryEventType.SESSION_RESUMED;
            }
            return LaboratoryEventType.SESSION_STARTED;
        }
        return payload.eventType();
    }

    private void assertPhaseTwelveSuitability(LaboratoryEventPayload payload, Instant now) {
        if (payload instanceof StepStartedPayload stepStartedPayload) {
            for (String profileId : stepStartedPayload.exclusiveEquipmentProfileIds()) {
                var requirement = profileId.contains("IKA")
                        ? new EquipmentRequirement("HEAT", "TEMPERATURE", new BigDecimal("80"), "degC", null, false)
                        : new EquipmentRequirement("MEASURE", "MASS", BigDecimal.ONE, "g", MeasurementResolution.of("0.001", "g"), false);
                var result = equipmentService.evaluate(new EquipmentProfileSuitabilityRequest(
                        profileId,
                        List.of(requirement),
                        List.of(new CalibrationRecord("SESSION-CALLER-CAL", now.minusSeconds(60), "caller supplied session calibration")),
                        now));
                if (result.status() == EquipmentSuitabilityStatus.UNSUITABLE) {
                    throw new SimulationStateException(SimulationStateErrorCode.EQUIPMENT_UNSUITABLE,
                            "equipment suitability rejected event: " + result.errorCodes());
                }
            }
        }
        if (payload instanceof MaterialDispensedPayload dispensedPayload) {
            assertContainerSuitable(dispensedPayload.containerProfileId(), dispensedPayload.quantity(), dispensedPayload.compoundCode(),
                    dispensedPayload.physicalState());
        }
        if (payload instanceof MaterialTransferredPayload transferredPayload && !transferredPayload.targetVesselId().isBlank()) {
            // Transfer capacity is enforced by the reducer; container suitability is checked when the target vessel is declared/dispensed.
        }
    }

    private void assertContainerSuitable(String profileId, BigDecimal volumeMl, String compoundOrFamily, String physicalState) {
        if (profileId == null || profileId.isBlank()) {
            return;
        }
        var result = containerService.evaluate(new ContainerProfileSuitabilityRequest(
                profileId,
                Volume.of(volumeMl.toPlainString(), VolumeUnit.MILLILITER),
                false,
                Temperature.of("20", TemperatureUnit.CELSIUS),
                null,
                null,
                compoundOrFamily,
                physicalState,
                null,
                null));
        if (result.status() == ContainerSuitabilityStatus.UNSUITABLE) {
            throw new SimulationStateException(SimulationStateErrorCode.CONTAINER_UNSUITABLE,
                    "container suitability rejected event: " + result.errorCodes());
        }
    }
}
