CREATE TABLE IF NOT EXISTS chemistry.laboratory_process_definitions (
    process_code TEXT NOT NULL,
    process_version INTEGER NOT NULL,
    status VARCHAR(32) NOT NULL CHECK (status IN ('DRAFT','PUBLISHED','TERMINAL','ARCHIVED')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (process_code, process_version)
);

CREATE TABLE IF NOT EXISTS chemistry.laboratory_process_steps (
    process_code TEXT NOT NULL,
    process_version INTEGER NOT NULL,
    step_id TEXT NOT NULL,
    step_type VARCHAR(32) NOT NULL,
    optional BOOLEAN NOT NULL DEFAULT FALSE,
    expected_duration_seconds NUMERIC(20, 8) NOT NULL CHECK (expected_duration_seconds >= 0),
    step_order INTEGER NOT NULL,
    PRIMARY KEY (process_code, process_version, step_id),
    FOREIGN KEY (process_code, process_version)
        REFERENCES chemistry.laboratory_process_definitions(process_code, process_version)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS chemistry.laboratory_process_step_dependencies (
    process_code TEXT NOT NULL,
    process_version INTEGER NOT NULL,
    step_id TEXT NOT NULL,
    depends_on_step_id TEXT NOT NULL,
    PRIMARY KEY (process_code, process_version, step_id, depends_on_step_id),
    FOREIGN KEY (process_code, process_version, step_id)
        REFERENCES chemistry.laboratory_process_steps(process_code, process_version, step_id)
        ON DELETE CASCADE,
    FOREIGN KEY (process_code, process_version, depends_on_step_id)
        REFERENCES chemistry.laboratory_process_steps(process_code, process_version, step_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS chemistry.laboratory_process_requirements (
    process_code TEXT NOT NULL,
    process_version INTEGER NOT NULL,
    step_id TEXT NOT NULL,
    requirement_id TEXT NOT NULL,
    requirement_type VARCHAR(32) NOT NULL CHECK (requirement_type IN ('MATERIAL','EQUIPMENT','CONTAINER','ENVIRONMENT')),
    requirement_payload JSONB NOT NULL,
    PRIMARY KEY (process_code, process_version, step_id, requirement_id),
    FOREIGN KEY (process_code, process_version, step_id)
        REFERENCES chemistry.laboratory_process_steps(process_code, process_version, step_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS chemistry.laboratory_process_ports (
    process_code TEXT NOT NULL,
    process_version INTEGER NOT NULL,
    step_id TEXT NOT NULL,
    port_id TEXT NOT NULL,
    port_direction VARCHAR(16) NOT NULL CHECK (port_direction IN ('INPUT','OUTPUT')),
    PRIMARY KEY (process_code, process_version, step_id, port_id, port_direction),
    FOREIGN KEY (process_code, process_version, step_id)
        REFERENCES chemistry.laboratory_process_steps(process_code, process_version, step_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS chemistry.simulation_sessions (
    session_id TEXT PRIMARY KEY,
    process_code TEXT NOT NULL,
    process_version INTEGER NOT NULL,
    status VARCHAR(32) NOT NULL CHECK (status IN ('CREATED','READY','RUNNING','PAUSED','COMPLETED','CANCELLED','FAILED')),
    current_version BIGINT NOT NULL CHECK (current_version >= 0),
    current_state JSONB NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    FOREIGN KEY (process_code, process_version)
        REFERENCES chemistry.laboratory_process_definitions(process_code, process_version)
);

CREATE TABLE IF NOT EXISTS chemistry.simulation_snapshots (
    snapshot_id BIGSERIAL PRIMARY KEY,
    session_id TEXT NOT NULL REFERENCES chemistry.simulation_sessions(session_id) ON DELETE CASCADE,
    state_version BIGINT NOT NULL CHECK (state_version >= 0),
    event_sequence BIGINT NOT NULL CHECK (event_sequence >= 1),
    snapshot_payload JSONB NOT NULL,
    checksum TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (session_id, event_sequence)
);
