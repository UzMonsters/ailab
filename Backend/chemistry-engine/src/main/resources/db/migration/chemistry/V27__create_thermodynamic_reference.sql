-- Flyway Migration V27: Create Thermodynamic Reference Foundation Schema

CREATE TABLE IF NOT EXISTS chemistry.thermodynamic_dataset_versions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    version VARCHAR(64) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    publication_date DATE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS chemistry.thermodynamic_source_documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    source_identifier VARCHAR(128) NOT NULL UNIQUE,
    citation TEXT NOT NULL,
    publisher VARCHAR(128) NOT NULL,
    reuse_limitations TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS chemistry.thermodynamic_reference_conditions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    temperature_kelvin NUMERIC(12, 4) NOT NULL CHECK (temperature_kelvin >= 0),
    pressure_pascal NUMERIC(16, 4) NOT NULL CHECK (pressure_pascal >= 0),
    physical_state VARCHAR(32) NOT NULL CHECK (physical_state IN ('SOLID', 'LIQUID', 'GAS', 'SUPERCRITICAL', 'MIXED')),
    standard_state_convention VARCHAR(64) NOT NULL CHECK (
        standard_state_convention IN ('PURE_SUBSTANCE_STANDARD_STATE', 'IDEAL_GAS_STANDARD_STATE', 'SOLID_REFERENCE_STATE')
    ),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_thermo_reference_conditions UNIQUE (
        temperature_kelvin,
        pressure_pascal,
        physical_state,
        standard_state_convention
    )
);

CREATE TABLE IF NOT EXISTS chemistry.thermodynamic_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    compound_code VARCHAR(64) NOT NULL,
    compound_catalog_version_id VARCHAR(64) NOT NULL DEFAULT 'compound-core-v1.0.0',
    dataset_version VARCHAR(64) NOT NULL REFERENCES chemistry.thermodynamic_dataset_versions(version),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_thermo_profile_compound_dataset UNIQUE (compound_code, dataset_version),
    CONSTRAINT fk_thermo_profile_compound FOREIGN KEY (compound_code, compound_catalog_version_id)
        REFERENCES chemistry.compounds(compound_code, compound_catalog_version_id)
);

CREATE TABLE IF NOT EXISTS chemistry.thermodynamic_property_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    profile_id UUID NOT NULL REFERENCES chemistry.thermodynamic_profiles(id) ON DELETE CASCADE,
    property_type VARCHAR(64) NOT NULL CHECK (
        property_type IN (
            'STANDARD_ENTHALPY_OF_FORMATION',
            'STANDARD_GIBBS_ENERGY_OF_FORMATION',
            'STANDARD_MOLAR_ENTROPY',
            'MOLAR_HEAT_CAPACITY'
        )
    ),
    numeric_value NUMERIC(20, 6) NOT NULL,
    unit_symbol VARCHAR(32) NOT NULL CHECK (unit_symbol IN ('kJ/mol', 'J/mol', 'J/(mol*K)', 'kJ/(mol*K)', 'J/(mol*K)-Cp')),
    reference_condition_id UUID NOT NULL REFERENCES chemistry.thermodynamic_reference_conditions(id),
    evidence_status VARCHAR(64) NOT NULL CHECK (evidence_status IN ('EVALUATED', 'MEASURED', 'REFERENCE_STATE_DEFINED')),
    source_identifier VARCHAR(128) NOT NULL REFERENCES chemistry.thermodynamic_source_documents(source_identifier),
    citation TEXT NOT NULL,
    reuse_limitations TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_thermo_heat_capacity_positive CHECK (
        property_type <> 'MOLAR_HEAT_CAPACITY' OR numeric_value > 0
    ),
    CONSTRAINT uk_thermo_property_exact_context UNIQUE (
        profile_id,
        property_type,
        reference_condition_id,
        source_identifier
    )
);

CREATE INDEX IF NOT EXISTS idx_thermo_profiles_compound
    ON chemistry.thermodynamic_profiles(compound_code);

CREATE INDEX IF NOT EXISTS idx_thermo_property_type
    ON chemistry.thermodynamic_property_records(property_type);

CREATE INDEX IF NOT EXISTS idx_thermo_property_conditions
    ON chemistry.thermodynamic_property_records(reference_condition_id);
