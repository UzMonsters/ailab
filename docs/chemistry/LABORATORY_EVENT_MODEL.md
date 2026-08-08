# Laboratory Event Model

Phase 13 adds immutable event types under:

```text
com.ailab.chemistry.domain.laboratoryevent
```

## Event Metadata

Every event carries:

- event id;
- session id;
- contiguous sequence number;
- state version;
- occurred-at timestamp;
- recorded-at timestamp;
- event type;
- schema version;
- source;
- correlation id;
- optional causation id;
- idempotency key;
- typed payload.

Payloads are domain records such as `SessionCreatedPayload`, `StepStartedPayload`, `MaterialDispensedPayload`, `MaterialTransferredPayload`, and `SampleTakenPayload`. Arbitrary JSON is not used as the domain model.

## Idempotency

The service checks idempotency keys before stale-version checks. Replaying the same key with the same payload returns the prior state. Reusing the key with a different payload is rejected as an idempotency conflict.

## Append-Only History

`simulation_events` is append-only and enforces unique `(session_id, sequence_number)` and unique `(session_id, idempotency_key)`. Events remain the authoritative history; snapshots are only acceleration data.

## Persistence Boundary

Event payload JSONB is allowed only at the JDBC boundary. The repository maps JSONB back into validated typed payload records before entering the reducer.
