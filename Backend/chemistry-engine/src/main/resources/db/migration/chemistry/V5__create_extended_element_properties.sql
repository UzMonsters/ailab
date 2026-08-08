-- V5: Create extended element properties schema
-- Dataset Version: extended-properties-v1.0.0
-- Tables for valencies, oxidation states, electronegativities, radii, density, phase transitions, appearance

CREATE TABLE IF NOT EXISTS chemistry.element_property_dataset_versions (
    id VARCHAR(50) PRIMARY KEY,
    description VARCHAR(500) NOT NULL,
    publication_date VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS chemistry.element_property_profiles (
    id UUID PRIMARY KEY,
    element_id UUID NOT NULL REFERENCES chemistry.elements(id) ON DELETE CASCADE,
    atomic_number INT NOT NULL CHECK (atomic_number BETWEEN 1 AND 118),
    symbol VARCHAR(5) NOT NULL,
    dataset_version_id VARCHAR(50) NOT NULL REFERENCES chemistry.element_property_dataset_versions(id),
    CONSTRAINT uk_element_property_profile UNIQUE (element_id, dataset_version_id),
    CONSTRAINT uk_element_property_atomic_number UNIQUE (atomic_number, dataset_version_id)
);

CREATE TABLE IF NOT EXISTS chemistry.element_valencies (
    id UUID PRIMARY KEY,
    profile_id UUID NOT NULL REFERENCES chemistry.element_property_profiles(id) ON DELETE CASCADE,
    valency INT NOT NULL CHECK (valency >= 0),
    is_common BOOLEAN NOT NULL DEFAULT TRUE,
    evidence_status VARCHAR(50) NOT NULL,
    source_identifier VARCHAR(100) NOT NULL,
    source_title VARCHAR(500) NOT NULL,
    CONSTRAINT uk_element_valency UNIQUE (profile_id, valency)
);

CREATE TABLE IF NOT EXISTS chemistry.element_oxidation_states (
    id UUID PRIMARY KEY,
    profile_id UUID NOT NULL REFERENCES chemistry.element_property_profiles(id) ON DELETE CASCADE,
    state INT NOT NULL,
    is_common BOOLEAN NOT NULL DEFAULT FALSE,
    is_uncommon BOOLEAN NOT NULL DEFAULT FALSE,
    is_predicted BOOLEAN NOT NULL DEFAULT FALSE,
    evidence_status VARCHAR(50) NOT NULL,
    source_identifier VARCHAR(100) NOT NULL,
    source_title VARCHAR(500) NOT NULL,
    CONSTRAINT uk_element_oxidation_state UNIQUE (profile_id, state)
);

CREATE TABLE IF NOT EXISTS chemistry.element_electronegativities (
    id UUID PRIMARY KEY,
    profile_id UUID NOT NULL REFERENCES chemistry.element_property_profiles(id) ON DELETE CASCADE,
    value NUMERIC(10, 4) NOT NULL CHECK (value > 0),
    scale VARCHAR(50) NOT NULL,
    is_predicted BOOLEAN NOT NULL DEFAULT FALSE,
    evidence_status VARCHAR(50) NOT NULL,
    source_identifier VARCHAR(100) NOT NULL,
    source_title VARCHAR(500) NOT NULL,
    CONSTRAINT uk_element_electronegativity UNIQUE (profile_id, scale)
);

CREATE TABLE IF NOT EXISTS chemistry.element_radii (
    id UUID PRIMARY KEY,
    profile_id UUID NOT NULL REFERENCES chemistry.element_property_profiles(id) ON DELETE CASCADE,
    kind VARCHAR(50) NOT NULL,
    radius_pm NUMERIC(10, 4) NOT NULL CHECK (radius_pm > 0),
    ionic_charge INT,
    coordination_number INT,
    spin_state VARCHAR(50),
    evidence_status VARCHAR(50) NOT NULL,
    source_identifier VARCHAR(100) NOT NULL,
    source_title VARCHAR(500) NOT NULL,
    CONSTRAINT chk_ionic_charge_required CHECK (kind <> 'IONIC' OR (ionic_charge IS NOT NULL AND ionic_charge <> 0))
);

CREATE TABLE IF NOT EXISTS chemistry.element_density_data (
    id UUID PRIMARY KEY,
    profile_id UUID NOT NULL REFERENCES chemistry.element_property_profiles(id) ON DELETE CASCADE,
    density_kg_m3 NUMERIC(15, 6) NOT NULL CHECK (density_kg_m3 > 0),
    ref_temp_k NUMERIC(10, 4) CHECK (ref_temp_k >= 0),
    ref_pressure_kpa NUMERIC(12, 4) CHECK (ref_pressure_kpa >= 0),
    ref_state VARCHAR(20),
    evidence_status VARCHAR(50) NOT NULL,
    source_identifier VARCHAR(100) NOT NULL,
    source_title VARCHAR(500) NOT NULL
);

CREATE TABLE IF NOT EXISTS chemistry.element_phase_transitions (
    id UUID PRIMARY KEY,
    profile_id UUID NOT NULL REFERENCES chemistry.element_property_profiles(id) ON DELETE CASCADE,
    kind VARCHAR(50) NOT NULL,
    temp_k NUMERIC(10, 4) CHECK (temp_k >= 0),
    ref_pressure_kpa NUMERIC(12, 4) CHECK (ref_pressure_kpa >= 0),
    behavior VARCHAR(50) NOT NULL,
    evidence_status VARCHAR(50) NOT NULL,
    source_identifier VARCHAR(100) NOT NULL,
    source_title VARCHAR(500) NOT NULL
);

CREATE TABLE IF NOT EXISTS chemistry.element_appearance (
    id UUID PRIMARY KEY,
    profile_id UUID NOT NULL REFERENCES chemistry.element_property_profiles(id) ON DELETE CASCADE,
    normalized_color_name VARCHAR(100),
    appearance_description VARCHAR(1000),
    evidence_status VARCHAR(50) NOT NULL,
    source_identifier VARCHAR(100) NOT NULL,
    source_title VARCHAR(500) NOT NULL,
    CONSTRAINT uk_element_appearance UNIQUE (profile_id)
);

CREATE INDEX IF NOT EXISTS idx_element_property_profiles_atomic ON chemistry.element_property_profiles(atomic_number);
CREATE INDEX IF NOT EXISTS idx_element_property_profiles_symbol ON chemistry.element_property_profiles(symbol);
CREATE INDEX IF NOT EXISTS idx_element_radii_profile_kind ON chemistry.element_radii(profile_id, kind);
