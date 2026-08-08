CREATE TABLE IF NOT EXISTS chemistry.phase_transition_dataset_versions (
    dataset_id VARCHAR(64) PRIMARY KEY,
    version VARCHAR(32) NOT NULL,
    immutable_snapshot BOOLEAN NOT NULL DEFAULT TRUE,
    description TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS chemistry.phase_behavior_source_documents (
    source_code VARCHAR(64) PRIMARY KEY,
    title TEXT NOT NULL,
    citation TEXT NOT NULL,
    reuse_terms TEXT NOT NULL,
    source_url TEXT,
    accessed_on DATE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS chemistry.phase_transition_records (
    record_id VARCHAR(96) PRIMARY KEY,
    dataset_id VARCHAR(64) NOT NULL REFERENCES chemistry.phase_transition_dataset_versions(dataset_id),
    compound_id UUID NOT NULL REFERENCES chemistry.compounds(id),
    compound_code VARCHAR(64) NOT NULL,
    transition_type VARCHAR(24) NOT NULL CHECK (transition_type IN ('FUSION', 'VAPORIZATION', 'SUBLIMATION')),
    initial_phase VARCHAR(16) NOT NULL CHECK (initial_phase IN ('SOLID', 'LIQUID', 'GAS')),
    final_phase VARCHAR(16) NOT NULL CHECK (final_phase IN ('SOLID', 'LIQUID', 'GAS')),
    temperature_k NUMERIC(12,5) NOT NULL CHECK (temperature_k > 0),
    pressure_pa NUMERIC(18,6) NOT NULL CHECK (pressure_pa > 0),
    original_value VARCHAR(64) NOT NULL,
    original_unit VARCHAR(64) NOT NULL,
    normalized_enthalpy_j_mol NUMERIC(18,6) NOT NULL CHECK (normalized_enthalpy_j_mol > 0),
    uncertainty VARCHAR(64),
    source_code VARCHAR(64) NOT NULL REFERENCES chemistry.phase_behavior_source_documents(source_code),
    citation TEXT NOT NULL,
    reuse_terms TEXT NOT NULL,
    evidence_status VARCHAR(40) NOT NULL CHECK (evidence_status IN ('SOURCED_REFERENCE_VALUE')),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_phase_transition_distinct_phases CHECK (initial_phase <> final_phase),
    CONSTRAINT uk_phase_transition_record UNIQUE (dataset_id, compound_code, transition_type, temperature_k, pressure_pa)
);

CREATE TABLE IF NOT EXISTS chemistry.antoine_correlations (
    correlation_id VARCHAR(96) PRIMARY KEY,
    dataset_id VARCHAR(64) NOT NULL REFERENCES chemistry.phase_transition_dataset_versions(dataset_id),
    compound_id UUID NOT NULL REFERENCES chemistry.compounds(id),
    compound_code VARCHAR(64) NOT NULL,
    initial_phase VARCHAR(16) NOT NULL CHECK (initial_phase IN ('LIQUID')),
    final_phase VARCHAR(16) NOT NULL CHECK (final_phase IN ('GAS')),
    coefficient_a NUMERIC(18,8) NOT NULL,
    coefficient_b NUMERIC(18,8) NOT NULL,
    coefficient_c NUMERIC(18,8) NOT NULL,
    temperature_unit VARCHAR(32) NOT NULL,
    pressure_unit VARCHAR(32) NOT NULL,
    min_temperature_k NUMERIC(12,5) NOT NULL CHECK (min_temperature_k > 0),
    max_temperature_k NUMERIC(12,5) NOT NULL CHECK (max_temperature_k > min_temperature_k),
    convention TEXT NOT NULL,
    source_code VARCHAR(64) NOT NULL REFERENCES chemistry.phase_behavior_source_documents(source_code),
    citation TEXT NOT NULL,
    reuse_terms TEXT NOT NULL,
    evidence_status VARCHAR(40) NOT NULL CHECK (evidence_status IN ('SOURCED_CORRELATION')),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_antoine_correlation UNIQUE (dataset_id, compound_code, initial_phase, final_phase, temperature_unit, pressure_unit, min_temperature_k, max_temperature_k)
);

CREATE TABLE IF NOT EXISTS chemistry.phase_boundary_points (
    boundary_id VARCHAR(96) PRIMARY KEY,
    dataset_id VARCHAR(64) NOT NULL REFERENCES chemistry.phase_transition_dataset_versions(dataset_id),
    compound_id UUID NOT NULL REFERENCES chemistry.compounds(id),
    compound_code VARCHAR(64) NOT NULL,
    boundary_type VARCHAR(32) NOT NULL CHECK (boundary_type IN ('TRIPLE_POINT', 'CRITICAL_POINT', 'NORMAL_MELTING_POINT', 'NORMAL_BOILING_POINT')),
    temperature_k NUMERIC(12,5) NOT NULL CHECK (temperature_k > 0),
    pressure_pa NUMERIC(18,6) NOT NULL CHECK (pressure_pa > 0),
    source_code VARCHAR(64) NOT NULL REFERENCES chemistry.phase_behavior_source_documents(source_code),
    citation TEXT NOT NULL,
    reuse_terms TEXT NOT NULL,
    evidence_status VARCHAR(40) NOT NULL CHECK (evidence_status IN ('SOURCED_REFERENCE_VALUE')),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_phase_boundary_point UNIQUE (dataset_id, compound_code, boundary_type)
);

CREATE INDEX IF NOT EXISTS ix_phase_transition_compound ON chemistry.phase_transition_records(compound_code, transition_type);
CREATE INDEX IF NOT EXISTS ix_antoine_compound ON chemistry.antoine_correlations(compound_code, initial_phase, final_phase);
CREATE INDEX IF NOT EXISTS ix_phase_boundary_compound ON chemistry.phase_boundary_points(compound_code, boundary_type);
