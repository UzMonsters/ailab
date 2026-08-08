package com.ailab.chemistry.domain.laboratoryevent;

import java.time.Instant;

public record LaboratoryEventMetadata(
        Instant occurredAt,
        Instant recordedAt,
        int schemaVersion,
        LaboratoryEventSource source,
        CorrelationId correlationId,
        CausationId causationId,
        IdempotencyKey idempotencyKey
) {
    public LaboratoryEventMetadata {
        if (occurredAt == null || recordedAt == null || schemaVersion < 1
                || source == null || correlationId == null || idempotencyKey == null) {
            throw new IllegalArgumentException("Event metadata is incomplete");
        }
    }
}
