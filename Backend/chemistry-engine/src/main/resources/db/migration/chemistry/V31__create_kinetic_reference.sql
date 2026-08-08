CREATE TABLE IF NOT EXISTS chemistry.kinetic_profiles (
    profile_id VARCHAR(64) PRIMARY KEY,
    reaction_code VARCHAR(64) NOT NULL,
    overall_order NUMERIC(8,4) NOT NULL,
    rate_constant_value NUMERIC(16,8) NOT NULL,
    rate_constant_unit VARCHAR(64) NOT NULL,
    pre_exponential_factor_a NUMERIC(24,8),
    activation_energy_kj_mol NUMERIC(12,4),
    min_temperature_k NUMERIC(8,2),
    max_temperature_k NUMERIC(8,2),
    ref_temperature_k NUMERIC(8,2) DEFAULT 298.15,
    ref_pressure_bar NUMERIC(8,4) DEFAULT 1.0000,
    solvent VARCHAR(64),
    catalyst VARCHAR(64),
    ph NUMERIC(5,2),
    ionic_strength NUMERIC(8,4),
    evidence_status VARCHAR(32) NOT NULL,
    provenance_source_id VARCHAR(64) NOT NULL,
    provenance_description TEXT,
    provenance_citation TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS chemistry.kinetic_rate_law_terms (
    id BIGSERIAL PRIMARY KEY,
    profile_id VARCHAR(64) NOT NULL REFERENCES chemistry.kinetic_profiles(profile_id) ON DELETE CASCADE,
    compound_code VARCHAR(64) NOT NULL,
    physical_state VARCHAR(16) NOT NULL,
    empirical_order NUMERIC(8,4) NOT NULL,
    CONSTRAINT uk_profile_compound UNIQUE (profile_id, compound_code, physical_state)
);
