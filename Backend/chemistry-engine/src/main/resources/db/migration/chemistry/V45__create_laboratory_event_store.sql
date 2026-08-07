CREATE TABLE IF NOT EXISTS chemistry.simulation_events (
    event_id TEXT PRIMARY KEY,
    session_id TEXT NOT NULL REFERENCES chemistry.simulation_sessions(session_id) ON DELETE CASCADE,
    sequence_number BIGINT NOT NULL CHECK (sequence_number >= 1),
    state_version BIGINT NOT NULL CHECK (state_version >= 1),
    occurred_at TIMESTAMPTZ NOT NULL,
    recorded_at TIMESTAMPTZ NOT NULL,
    event_type VARCHAR(64) NOT NULL,
    schema_version INTEGER NOT NULL CHECK (schema_version >= 1),
    source_type TEXT NOT NULL,
    source_actor TEXT NOT NULL,
    correlation_id TEXT NOT NULL,
    causation_id TEXT,
    idempotency_key TEXT NOT NULL,
    payload_json JSONB NOT NULL,
    payload_fingerprint TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (session_id, sequence_number),
    UNIQUE (session_id, idempotency_key)
);

ALTER TABLE chemistry.simulation_snapshots
    ADD CONSTRAINT simulation_snapshots_event_sequence_fk
    FOREIGN KEY (session_id, event_sequence)
    REFERENCES chemistry.simulation_events(session_id, sequence_number)
    DEFERRABLE INITIALLY DEFERRED;
