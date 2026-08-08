-- Flyway Migration V25: Create Solubility Equilibrium Reference Schema

CREATE TABLE IF NOT EXISTS chemistry.solubility_dataset_versions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    version VARCHAR(64) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    publication_date DATE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS chemistry.solubility_source_documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    source_identifier VARCHAR(128) NOT NULL UNIQUE,
    citation TEXT NOT NULL,
    publisher VARCHAR(128) NOT NULL,
    reuse_limitations TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS chemistry.solubility_equilibria (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    equilibrium_code VARCHAR(96) NOT NULL UNIQUE,
    solid_compound_code VARCHAR(64) NOT NULL,
    solid_compound_catalog_version_id VARCHAR(64) NOT NULL DEFAULT 'compound-core-v1.0.0',
    ksp_value NUMERIC(60, 50) NOT NULL CHECK (ksp_value > 0),
    temperature_celsius NUMERIC(6, 2) NOT NULL,
    solvent_code VARCHAR(64) NOT NULL,
    solvent_compound_catalog_version_id VARCHAR(64) NOT NULL DEFAULT 'compound-core-v1.0.0',
    activity_convention TEXT NOT NULL,
    dataset_version VARCHAR(64) NOT NULL REFERENCES chemistry.solubility_dataset_versions(version),
    source_identifier VARCHAR(128) NOT NULL REFERENCES chemistry.solubility_source_documents(source_identifier),
    citation TEXT NOT NULL,
    reuse_limitations TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_solubility_equilibrium_context UNIQUE (equilibrium_code, temperature_celsius, solvent_code, dataset_version),
    CONSTRAINT fk_solubility_solid_compound FOREIGN KEY (solid_compound_code, solid_compound_catalog_version_id)
        REFERENCES chemistry.compounds(compound_code, compound_catalog_version_id),
    CONSTRAINT fk_solubility_solvent_compound FOREIGN KEY (solvent_code, solvent_compound_catalog_version_id)
        REFERENCES chemistry.compounds(compound_code, compound_catalog_version_id)
);

CREATE TABLE IF NOT EXISTS chemistry.solubility_dissolution_terms (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    equilibrium_id UUID NOT NULL REFERENCES chemistry.solubility_equilibria(id) ON DELETE CASCADE,
    term_order INT NOT NULL CHECK (term_order >= 1),
    species_code VARCHAR(64) NOT NULL REFERENCES chemistry.chemical_species(species_code),
    formula VARCHAR(128) NOT NULL,
    charge INT NOT NULL,
    coefficient INT NOT NULL CHECK (coefficient >= 1),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_solubility_term_order UNIQUE (equilibrium_id, term_order),
    CONSTRAINT uk_solubility_term_species UNIQUE (equilibrium_id, species_code)
);

CREATE TABLE IF NOT EXISTS chemistry.solubility_constants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    equilibrium_id UUID NOT NULL REFERENCES chemistry.solubility_equilibria(id) ON DELETE CASCADE,
    constant_type VARCHAR(16) NOT NULL CHECK (constant_type = 'KSP'),
    k_value NUMERIC(60, 50) NOT NULL CHECK (k_value > 0),
    temperature_celsius NUMERIC(6, 2) NOT NULL,
    solvent_code VARCHAR(64) NOT NULL,
    solvent_compound_catalog_version_id VARCHAR(64) NOT NULL DEFAULT 'compound-core-v1.0.0',
    source_identifier VARCHAR(128) NOT NULL REFERENCES chemistry.solubility_source_documents(source_identifier),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_solubility_constant_context UNIQUE (equilibrium_id, constant_type, temperature_celsius, solvent_code),
    CONSTRAINT fk_solubility_constant_solvent FOREIGN KEY (solvent_code, solvent_compound_catalog_version_id)
        REFERENCES chemistry.compounds(compound_code, compound_catalog_version_id)
);

CREATE INDEX IF NOT EXISTS idx_solubility_equilibria_conditions
    ON chemistry.solubility_equilibria(equilibrium_code, temperature_celsius, solvent_code);

CREATE INDEX IF NOT EXISTS idx_solubility_terms_species
    ON chemistry.solubility_dissolution_terms(species_code);

CREATE INDEX IF NOT EXISTS idx_solubility_constants_conditions
    ON chemistry.solubility_constants(temperature_celsius, solvent_code);
