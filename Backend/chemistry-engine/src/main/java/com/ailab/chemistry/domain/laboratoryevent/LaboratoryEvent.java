package com.ailab.chemistry.domain.laboratoryevent;

import com.ailab.chemistry.domain.simulationstate.SimulationSessionId;
import com.ailab.chemistry.domain.simulationstate.SimulationStateVersion;

import java.time.Instant;

public record LaboratoryEvent(
        LaboratoryEventId eventId,
        SimulationSessionId sessionId,
        LaboratoryEventSequence sequence,
        SimulationStateVersion stateVersion,
        Instant occurredAt,
        Instant recordedAt,
        LaboratoryEventType type,
        int schemaVersion,
        LaboratoryEventSource source,
        CorrelationId correlationId,
        CausationId causationId,
        IdempotencyKey idempotencyKey,
        LaboratoryEventPayload payload
) {
    public LaboratoryEvent {
        if (eventId == null || sessionId == null || sequence == null || stateVersion == null
                || occurredAt == null || recordedAt == null || type == null || source == null
                || correlationId == null || idempotencyKey == null || payload == null) {
            throw new IllegalArgumentException("Laboratory event metadata and typed payload are required");
        }
        if (schemaVersion < 1) {
            throw new IllegalArgumentException("Event schema version must be positive");
        }
    }
}
