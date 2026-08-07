CREATE TABLE IF NOT EXISTS chemistry.simulation_calculation_audits (
    event_id TEXT PRIMARY KEY REFERENCES chemistry.simulation_events(event_id),
    session_id TEXT NOT NULL REFERENCES chemistry.simulation_sessions(session_id),
    command_id TEXT NOT NULL,
    operation_type TEXT NOT NULL,
    model_identifier TEXT NOT NULL,
    model_version TEXT NOT NULL,
    dataset_versions JSONB NOT NULL,
    input_hash TEXT NOT NULL,
    result_hash TEXT NOT NULL,
    calculation_trace JSONB NOT NULL,
    conservation_ledger JSONB NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (session_id, command_id),
    UNIQUE (session_id, event_id)
);

ALTER TABLE chemistry.simulation_calculation_audits
    ADD CONSTRAINT simulation_calculation_audits_operation_type_check
    CHECK (operation_type IN (
        'STOICHIOMETRIC_REACTION',
        'EQUILIBRIUM_REACTION',
        'KINETIC_PROGRESS',
        'THERMAL_OPERATION',
        'GAS_STATE_CHANGE',
        'PHASE_TRANSITION',
        'ELECTROLYSIS',
        'BOOKKEEPING_MIX'
    ));

CREATE INDEX IF NOT EXISTS idx_simulation_calculation_audits_session
    ON chemistry.simulation_calculation_audits(session_id, created_at);
