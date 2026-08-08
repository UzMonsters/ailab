-- Flyway Migration V29: Create Temperature-Dependent Thermodynamic Correlation Schema

CREATE TABLE IF NOT EXISTS chemistry.thermodynamic_temperature_dataset_versions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    version VARCHAR(64) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    publication_date DATE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS chemistry.thermodynamic_temperature_source_documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    source_identifier VARCHAR(128) NOT NULL UNIQUE,
    citation TEXT NOT NULL,
    publisher VARCHAR(128) NOT NULL,
    reuse_limitations TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS chemistry.thermodynamic_temperature_correlations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    dataset_version VARCHAR(64) NOT NULL REFERENCES chemistry.thermodynamic_temperature_dataset_versions(version),
    compound_code VARCHAR(64) NOT NULL,
    compound_catalog_version_id VARCHAR(64) NOT NULL DEFAULT 'compound-core-v1.0.0',
    physical_state VARCHAR(32) NOT NULL CHECK (physical_state IN ('SOLID', 'LIQUID', 'GAS')),
    correlation_type VARCHAR(64) NOT NULL CHECK (correlation_type IN ('SHOMATE', 'CONSTANT_CP_APPROXIMATION')),
    coefficient_a NUMERIC(24, 9) NOT NULL,
    coefficient_b NUMERIC(24, 9) NOT NULL,
    coefficient_c NUMERIC(24, 9) NOT NULL,
    coefficient_d NUMERIC(24, 9) NOT NULL,
    coefficient_e NUMERIC(24, 9) NOT NULL,
    coefficient_f NUMERIC(24, 9) NOT NULL,
    coefficient_g NUMERIC(24, 9) NOT NULL,
    coefficient_h NUMERIC(24, 9) NOT NULL,
    temperature_min_kelvin NUMERIC(12, 4) NOT NULL,
    temperature_max_kelvin NUMERIC(12, 4) NOT NULL,
    heat_capacity_unit VARCHAR(32) NOT NULL CHECK (heat_capacity_unit = 'J/(mol*K)'),
    enthalpy_unit VARCHAR(32) NOT NULL CHECK (enthalpy_unit = 'kJ/mol'),
    entropy_unit VARCHAR(32) NOT NULL CHECK (entropy_unit = 'J/(mol*K)'),
    scaling_convention TEXT NOT NULL,
    source_identifier VARCHAR(128) NOT NULL REFERENCES chemistry.thermodynamic_temperature_source_documents(source_identifier),
    citation TEXT NOT NULL,
    reuse_limitations TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_thermo_temp_correlation_compound FOREIGN KEY (compound_code, compound_catalog_version_id)
        REFERENCES chemistry.compounds(compound_code, compound_catalog_version_id),
    CONSTRAINT chk_thermo_temp_range_positive CHECK (
        temperature_min_kelvin > 0
        AND temperature_max_kelvin > 0
        AND temperature_min_kelvin <= temperature_max_kelvin
    ),
    CONSTRAINT uk_thermo_temp_correlation_identity UNIQUE (
        dataset_version,
        compound_code,
        physical_state,
        correlation_type,
        temperature_min_kelvin,
        temperature_max_kelvin,
        source_identifier
    )
);

CREATE INDEX IF NOT EXISTS idx_thermo_temp_correlations_compound_state
    ON chemistry.thermodynamic_temperature_correlations(compound_code, physical_state);

CREATE INDEX IF NOT EXISTS idx_thermo_temp_correlations_range
    ON chemistry.thermodynamic_temperature_correlations(temperature_min_kelvin, temperature_max_kelvin);
