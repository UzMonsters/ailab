-- V7: Create Compound Core Schema
-- Dataset Version: compound-core-v1.0.0
-- Tables for compounds, aliases, elemental components, and external identifiers

CREATE TABLE IF NOT EXISTS chemistry.compound_catalog_versions (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    publication_date VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS chemistry.compounds (
    id UUID PRIMARY KEY,
    compound_code VARCHAR(50) NOT NULL,
    primary_name VARCHAR(250) NOT NULL,
    original_formula VARCHAR(200) NOT NULL,
    normalized_formula VARCHAR(200) NOT NULL,
    net_charge INT NOT NULL DEFAULT 0,
    hydrate_info VARCHAR(100),
    molar_mass_value NUMERIC(15, 6) NOT NULL CHECK (molar_mass_value > 0),
    molar_mass_lower_bound NUMERIC(15, 6),
    molar_mass_upper_bound NUMERIC(15, 6),
    molar_mass_kind VARCHAR(50) NOT NULL,
    element_catalog_version VARCHAR(50) NOT NULL,
    compound_catalog_version_id VARCHAR(50) NOT NULL REFERENCES chemistry.compound_catalog_versions(id),
    source_identifier VARCHAR(100) NOT NULL,
    source_title VARCHAR(500) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_compound_code UNIQUE (compound_code, compound_catalog_version_id),
    CONSTRAINT chk_molar_mass_bounds CHECK (
        (molar_mass_lower_bound IS NULL AND molar_mass_upper_bound IS NULL) OR
        (molar_mass_lower_bound IS NOT NULL AND molar_mass_upper_bound IS NOT NULL AND molar_mass_lower_bound <= molar_mass_upper_bound)
    )
);

CREATE TABLE IF NOT EXISTS chemistry.compound_aliases (
    id UUID PRIMARY KEY,
    compound_id UUID NOT NULL REFERENCES chemistry.compounds(id) ON DELETE CASCADE,
    name VARCHAR(250) NOT NULL,
    role VARCHAR(50) NOT NULL,
    CONSTRAINT uk_compound_alias UNIQUE (compound_id, name)
);

CREATE TABLE IF NOT EXISTS chemistry.compound_components (
    id UUID PRIMARY KEY,
    compound_id UUID NOT NULL REFERENCES chemistry.compounds(id) ON DELETE CASCADE,
    element_id UUID NOT NULL REFERENCES chemistry.elements(id),
    atomic_number INT NOT NULL CHECK (atomic_number BETWEEN 1 AND 118),
    symbol VARCHAR(5) NOT NULL,
    atom_count NUMERIC(20, 0) NOT NULL CHECK (atom_count > 0),
    CONSTRAINT uk_compound_component UNIQUE (compound_id, element_id)
);

CREATE TABLE IF NOT EXISTS chemistry.compound_external_identifiers (
    id UUID PRIMARY KEY,
    compound_id UUID NOT NULL REFERENCES chemistry.compounds(id) ON DELETE CASCADE,
    scheme VARCHAR(50) NOT NULL,
    identifier_value VARCHAR(250) NOT NULL,
    CONSTRAINT uk_compound_ext_id UNIQUE (compound_id, scheme)
);

CREATE INDEX IF NOT EXISTS idx_compounds_formula ON chemistry.compounds(normalized_formula);
CREATE INDEX IF NOT EXISTS idx_compounds_primary_name ON chemistry.compounds(primary_name);
CREATE INDEX IF NOT EXISTS idx_compounds_code ON chemistry.compounds(compound_code);
CREATE INDEX IF NOT EXISTS idx_compound_aliases_name ON chemistry.compound_aliases(name);
