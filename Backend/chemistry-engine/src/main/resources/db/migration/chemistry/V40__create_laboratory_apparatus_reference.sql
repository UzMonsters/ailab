CREATE TABLE IF NOT EXISTS chemistry.laboratory_dataset_versions (
    dataset_id VARCHAR(96) PRIMARY KEY,
    version VARCHAR(32) NOT NULL,
    immutable_snapshot BOOLEAN NOT NULL DEFAULT TRUE,
    description TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS chemistry.laboratory_source_documents (
    source_code VARCHAR(96) PRIMARY KEY,
    title TEXT NOT NULL,
    citation TEXT NOT NULL,
    edition TEXT NOT NULL,
    table_or_section TEXT NOT NULL,
    page_or_record_identifier TEXT NOT NULL,
    reuse_terms TEXT NOT NULL,
    source_url TEXT,
    accessed_on DATE NOT NULL,
    evidence_status VARCHAR(48) NOT NULL CHECK (evidence_status IN ('SOURCED_REFERENCE_VALUE', 'TYPE_TAXONOMY', 'REFERENCE_CONVENTION')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS chemistry.equipment_reference_profiles (
    profile_id VARCHAR(96) PRIMARY KEY,
    dataset_id VARCHAR(96) NOT NULL REFERENCES chemistry.laboratory_dataset_versions(dataset_id),
    equipment_type VARCHAR(48) NOT NULL,
    display_name TEXT NOT NULL,
    manufacturer TEXT,
    model_identifier TEXT,
    provenance_source_code VARCHAR(96) NOT NULL REFERENCES chemistry.laboratory_source_documents(source_code),
    provenance_note TEXT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_equipment_active_identity UNIQUE (dataset_id, profile_id, is_active)
);

CREATE TABLE IF NOT EXISTS chemistry.equipment_capabilities (
    capability_id VARCHAR(120) PRIMARY KEY,
    profile_id VARCHAR(96) NOT NULL REFERENCES chemistry.equipment_reference_profiles(profile_id) ON DELETE CASCADE,
    capability_type VARCHAR(48) NOT NULL,
    quantity VARCHAR(48) NOT NULL,
    unit VARCHAR(32) NOT NULL CHECK (unit IN ('g', 'kg', 'mg', 'degC', 'K', 'mL', 'L', 'pH', 'rpm')),
    minimum_value NUMERIC(20,8) NOT NULL,
    maximum_value NUMERIC(20,8) NOT NULL,
    resolution_value NUMERIC(20,8) CHECK (resolution_value >= 0),
    accuracy_value NUMERIC(20,8) CHECK (accuracy_value >= 0),
    uncertainty_value NUMERIC(20,8) CHECK (uncertainty_value >= 0),
    capacity_value NUMERIC(20,8) CHECK (capacity_value > 0),
    provenance_source_code VARCHAR(96) NOT NULL REFERENCES chemistry.laboratory_source_documents(source_code),
    provenance_note TEXT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT ck_equipment_operating_range CHECK (minimum_value <= maximum_value),
    CONSTRAINT uk_equipment_capability UNIQUE (profile_id, capability_type, quantity, unit)
);

CREATE TABLE IF NOT EXISTS chemistry.equipment_calibration_requirements (
    requirement_id VARCHAR(120) PRIMARY KEY,
    capability_id VARCHAR(120) NOT NULL REFERENCES chemistry.equipment_capabilities(capability_id) ON DELETE CASCADE,
    calibration_required BOOLEAN NOT NULL,
    interval_seconds NUMERIC(20,0) CHECK (interval_seconds > 0),
    due_soon_seconds NUMERIC(20,0) CHECK (due_soon_seconds >= 0),
    provenance_source_code VARCHAR(96) NOT NULL REFERENCES chemistry.laboratory_source_documents(source_code),
    provenance_note TEXT NOT NULL,
    CONSTRAINT ck_calibration_interval_when_required CHECK (
        (calibration_required = FALSE AND interval_seconds IS NULL)
        OR (calibration_required = TRUE AND interval_seconds IS NOT NULL)
    )
);

CREATE TABLE IF NOT EXISTS chemistry.container_reference_profiles (
    profile_id VARCHAR(96) PRIMARY KEY,
    dataset_id VARCHAR(96) NOT NULL REFERENCES chemistry.laboratory_dataset_versions(dataset_id),
    container_type VARCHAR(48) NOT NULL,
    material VARCHAR(48) NOT NULL,
    closure_type VARCHAR(48) NOT NULL,
    closure_material VARCHAR(48),
    geometry_description TEXT NOT NULL,
    nominal_capacity_ml NUMERIC(20,8) NOT NULL CHECK (nominal_capacity_ml > 0),
    maximum_working_volume_ml NUMERIC(20,8) NOT NULL CHECK (maximum_working_volume_ml > 0),
    min_temperature_c NUMERIC(20,8),
    max_temperature_c NUMERIC(20,8),
    max_pressure_bar NUMERIC(20,8) CHECK (max_pressure_bar > 0),
    provenance_source_code VARCHAR(96) NOT NULL REFERENCES chemistry.laboratory_source_documents(source_code),
    provenance_note TEXT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_container_working_le_nominal CHECK (maximum_working_volume_ml <= nominal_capacity_ml),
    CONSTRAINT ck_container_temperature_range CHECK (min_temperature_c IS NULL OR max_temperature_c IS NULL OR min_temperature_c <= max_temperature_c),
    CONSTRAINT uk_container_active_identity UNIQUE (dataset_id, profile_id, is_active)
);

CREATE TABLE IF NOT EXISTS chemistry.container_compatibility_records (
    compatibility_id VARCHAR(120) PRIMARY KEY,
    dataset_id VARCHAR(96) NOT NULL REFERENCES chemistry.laboratory_dataset_versions(dataset_id),
    compound_or_family VARCHAR(96) NOT NULL,
    physical_state VARCHAR(32) NOT NULL,
    container_material VARCHAR(48) NOT NULL,
    closure_material VARCHAR(48),
    compatibility_status VARCHAR(32) NOT NULL CHECK (compatibility_status IN ('COMPATIBLE', 'COMPATIBLE_WITH_LIMITS', 'INCOMPATIBLE', 'UNKNOWN')),
    concentration_condition TEXT,
    min_temperature_c NUMERIC(20,8),
    max_temperature_c NUMERIC(20,8),
    contact_duration_limit TEXT,
    source_code VARCHAR(96) NOT NULL REFERENCES chemistry.laboratory_source_documents(source_code),
    evidence_status VARCHAR(48) NOT NULL CHECK (evidence_status IN ('SOURCED_REFERENCE_VALUE', 'TYPE_TAXONOMY')),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_compatibility_temperature_range CHECK (min_temperature_c IS NULL OR max_temperature_c IS NULL OR min_temperature_c <= max_temperature_c)
);

CREATE INDEX IF NOT EXISTS ix_equipment_profiles_active ON chemistry.equipment_reference_profiles(profile_id, is_active);
CREATE INDEX IF NOT EXISTS ix_equipment_capabilities_profile ON chemistry.equipment_capabilities(profile_id);
CREATE INDEX IF NOT EXISTS ix_container_profiles_active ON chemistry.container_reference_profiles(profile_id, is_active);
CREATE INDEX IF NOT EXISTS ix_container_compatibility_lookup ON chemistry.container_compatibility_records(compound_or_family, physical_state, container_material, is_active);
CREATE UNIQUE INDEX IF NOT EXISTS uk_container_compatibility_active
    ON chemistry.container_compatibility_records(dataset_id, compound_or_family, physical_state, container_material, COALESCE(closure_material, ''), is_active);
