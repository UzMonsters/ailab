CREATE TABLE IF NOT EXISTS chemistry.laboratory_safety_rules (
    rule_id TEXT NOT NULL,
    rule_version INTEGER NOT NULL,
    rule_type TEXT NOT NULL,
    severity TEXT NOT NULL,
    evaluation_stage TEXT NOT NULL,
    operation_types JSONB NOT NULL DEFAULT '[]'::jsonb,
    required_input_fields JSONB NOT NULL DEFAULT '[]'::jsonb,
    condition_field TEXT NOT NULL,
    condition_operator TEXT NOT NULL,
    condition_target_value TEXT NOT NULL,
    condition_parameters JSONB NOT NULL DEFAULT '{}'::jsonb,
    source_identifier TEXT NOT NULL,
    source_citation TEXT NOT NULL,
    source_version_date TEXT NOT NULL,
    evidence_status TEXT NOT NULL,
    effective_version INTEGER NOT NULL DEFAULT 1,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (rule_id, rule_version)
);

CREATE TABLE IF NOT EXISTS chemistry.simulation_safety_audits (
    audit_id BIGSERIAL PRIMARY KEY,
    session_id TEXT NOT NULL REFERENCES chemistry.simulation_sessions(session_id),
    command_id TEXT NOT NULL,
    event_id TEXT REFERENCES chemistry.simulation_events(event_id),
    evaluation_stage TEXT NOT NULL,
    status TEXT NOT NULL,
    evaluated_rule_versions JSONB NOT NULL,
    violations JSONB NOT NULL,
    warnings JSONB NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
