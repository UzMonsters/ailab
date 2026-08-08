-- Flyway Migration V19: Create Acid-Base Reference Foundation Schema

CREATE TABLE IF NOT EXISTS chemistry.acid_base_dataset_versions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    version VARCHAR(32) NOT NULL UNIQUE,
    source_identifier VARCHAR(255) NOT NULL,
    citation TEXT NOT NULL,
    license VARCHAR(255) NOT NULL,
    evidence_status VARCHAR(64) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS chemistry.chemical_species (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    species_code VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(128) NOT NULL,
    formula VARCHAR(128) NOT NULL,
    kind VARCHAR(32) NOT NULL,
    charge INT NOT NULL,
    primary_role VARCHAR(32) NOT NULL,
    associated_compound_code VARCHAR(64),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS chemistry.conjugate_pairs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    pair_code VARCHAR(64) NOT NULL UNIQUE,
    acid_species_code VARCHAR(64) NOT NULL REFERENCES chemistry.chemical_species(species_code) ON DELETE CASCADE,
    base_species_code VARCHAR(64) NOT NULL REFERENCES chemistry.chemical_species(species_code) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_distinct_conjugate_species CHECK (acid_species_code <> base_species_code)
);

CREATE TABLE IF NOT EXISTS chemistry.dissociation_steps (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    acid_species_code VARCHAR(64) NOT NULL REFERENCES chemistry.chemical_species(species_code) ON DELETE CASCADE,
    deprotonated_species_code VARCHAR(64) NOT NULL REFERENCES chemistry.chemical_species(species_code) ON DELETE CASCADE,
    step_number INT NOT NULL CHECK (step_number >= 1),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_acid_step UNIQUE (acid_species_code, step_number)
);

CREATE TABLE IF NOT EXISTS chemistry.equilibrium_constants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    species_code VARCHAR(64) NOT NULL REFERENCES chemistry.chemical_species(species_code) ON DELETE CASCADE,
    type VARCHAR(16) NOT NULL CHECK (type IN ('KA', 'KB', 'KW')),
    step_number INT NOT NULL DEFAULT 1 CHECK (step_number >= 1),
    k_value NUMERIC(38, 16) CHECK (k_value > 0 OR k_value IS NULL),
    p_value NUMERIC(10, 4) CHECK (p_value IS NULL OR k_value IS NOT NULL),
    temperature_celsius NUMERIC(6, 2) NOT NULL DEFAULT 25.0,
    solvent_code VARCHAR(64) NOT NULL DEFAULT 'COMP-H2O',
    is_strong_electrolyte BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_species_constant_type_step UNIQUE (species_code, type, step_number, temperature_celsius, solvent_code)
);

CREATE INDEX IF NOT EXISTS idx_chemical_species_kind ON chemistry.chemical_species(kind);
CREATE INDEX IF NOT EXISTS idx_chemical_species_role ON chemistry.chemical_species(primary_role);
CREATE INDEX IF NOT EXISTS idx_chemical_species_associated_compound ON chemistry.chemical_species(associated_compound_code);
CREATE INDEX IF NOT EXISTS idx_conjugate_pairs_acid ON chemistry.conjugate_pairs(acid_species_code);
CREATE INDEX IF NOT EXISTS idx_conjugate_pairs_base ON chemistry.conjugate_pairs(base_species_code);
CREATE INDEX IF NOT EXISTS idx_equilibrium_constants_species ON chemistry.equilibrium_constants(species_code);
