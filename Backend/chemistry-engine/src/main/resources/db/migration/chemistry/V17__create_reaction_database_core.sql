CREATE TABLE chemistry.reaction_catalog_versions (
    version_code VARCHAR(50) PRIMARY KEY,
    description TEXT NOT NULL,
    publication_date DATE NOT NULL
);

CREATE TABLE chemistry.reaction_source_documents (
    source_id VARCHAR(50) PRIMARY KEY,
    source_type VARCHAR(50) NOT NULL,
    issuer VARCHAR(100),
    title VARCHAR(255) NOT NULL,
    edition VARCHAR(50),
    publication_date VARCHAR(50),
    access_date VARCHAR(50),
    coverage TEXT,
    fields_supplied TEXT,
    language VARCHAR(20),
    source_reference VARCHAR(255),
    licensing_note TEXT
);

CREATE TABLE chemistry.reaction_type_definitions (
    type_code VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    display_order INT NOT NULL
);

CREATE TABLE chemistry.reactions (
    id UUID PRIMARY KEY,
    reaction_code VARCHAR(100) NOT NULL UNIQUE,
    primary_name VARCHAR(255) NOT NULL,
    original_equation TEXT NOT NULL,
    normalized_equation TEXT NOT NULL,
    canonical_balanced_equation TEXT NOT NULL,
    reaction_signature VARCHAR(512) NOT NULL,
    directionality VARCHAR(50) NOT NULL CHECK (directionality IN ('IRREVERSIBLE', 'REVERSIBLE', 'EQUILIBRIUM_REPRESENTATION', 'UNKNOWN')),
    catalog_version_id VARCHAR(50) NOT NULL REFERENCES chemistry.reaction_catalog_versions(version_code),
    source_document_id VARCHAR(50) NOT NULL REFERENCES chemistry.reaction_source_documents(source_id),
    provenance_notes TEXT
);

CREATE TABLE chemistry.reaction_aliases (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reaction_id UUID NOT NULL REFERENCES chemistry.reactions(id) ON DELETE CASCADE,
    alias_name VARCHAR(255) NOT NULL,
    alias_type VARCHAR(50)
);

CREATE TABLE chemistry.reaction_terms (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reaction_id UUID NOT NULL REFERENCES chemistry.reactions(id) ON DELETE CASCADE,
    compound_id UUID NOT NULL REFERENCES chemistry.compounds(id),
    compound_code VARCHAR(50) NOT NULL,
    formula VARCHAR(100) NOT NULL,
    side VARCHAR(20) NOT NULL CHECK (side IN ('REACTANT', 'PRODUCT')),
    coefficient NUMERIC NOT NULL CHECK (coefficient > 0),
    species_state VARCHAR(20) CHECK (species_state IN ('SOLID', 'LIQUID', 'GAS', 'AQUEOUS', 'DISSOLVED', 'MOLTEN', 'UNKNOWN')),
    term_order INT NOT NULL,
    CONSTRAINT uk_reaction_term_side_compound UNIQUE (reaction_id, side, compound_id)
);

CREATE TABLE chemistry.reaction_condition_sets (
    id UUID PRIMARY KEY,
    reaction_id UUID NOT NULL REFERENCES chemistry.reactions(id) ON DELETE CASCADE,
    temperature_value VARCHAR(100),
    pressure_value VARCHAR(100),
    medium VARCHAR(100),
    atmosphere VARCHAR(50),
    energy_input VARCHAR(50),
    concentration_notes TEXT,
    description TEXT,
    evidence_status VARCHAR(50),
    source_document_id VARCHAR(50) REFERENCES chemistry.reaction_source_documents(source_id)
);

CREATE TABLE chemistry.reaction_catalysts (
    id UUID PRIMARY KEY,
    reaction_id UUID NOT NULL REFERENCES chemistry.reactions(id) ON DELETE CASCADE,
    reference_type VARCHAR(20) NOT NULL CHECK (reference_type IN ('COMPOUND', 'ELEMENT')),
    reference_code VARCHAR(50) NOT NULL,
    compound_id UUID REFERENCES chemistry.compounds(id),
    element_atomic_number INT REFERENCES chemistry.elements(atomic_number),
    catalyst_role VARCHAR(50) NOT NULL,
    physical_form VARCHAR(100),
    loading_description TEXT,
    evidence_status VARCHAR(50),
    source_document_id VARCHAR(50) REFERENCES chemistry.reaction_source_documents(source_id)
);

CREATE TABLE chemistry.reaction_type_assignments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reaction_id UUID NOT NULL REFERENCES chemistry.reactions(id) ON DELETE CASCADE,
    type_code VARCHAR(50) NOT NULL REFERENCES chemistry.reaction_type_definitions(type_code),
    derivation_basis VARCHAR(50) NOT NULL CHECK (derivation_basis IN ('CURATED_REFERENCE', 'SAFE_RULE_DERIVED', 'MANUAL_REVIEW', 'PROVISIONAL')),
    explanation TEXT,
    CONSTRAINT uk_reaction_type_assignment UNIQUE (reaction_id, type_code)
);

CREATE INDEX idx_reaction_terms_reactant ON chemistry.reaction_terms(compound_code) WHERE side = 'REACTANT';
CREATE INDEX idx_reaction_terms_product ON chemistry.reaction_terms(compound_code) WHERE side = 'PRODUCT';
CREATE INDEX idx_reaction_type_assignments_code ON chemistry.reaction_type_assignments(type_code);
CREATE INDEX idx_reactions_signature ON chemistry.reactions(reaction_signature);
