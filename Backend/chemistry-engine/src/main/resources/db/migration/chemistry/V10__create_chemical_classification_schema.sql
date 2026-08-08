-- V10: Create chemical classification schema
CREATE TABLE chemistry.classification_taxonomy_versions (
    id VARCHAR(100) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    publication_date VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE chemistry.classification_definitions (
    id UUID PRIMARY KEY,
    taxonomy_version_id VARCHAR(100) NOT NULL REFERENCES chemistry.classification_taxonomy_versions(id),
    dimension VARCHAR(100) NOT NULL,
    code VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    sort_order INT NOT NULL,
    parent_code VARCHAR(100),
    CONSTRAINT uk_classification_code_version UNIQUE (code, taxonomy_version_id)
);

CREATE TABLE chemistry.compound_classification_profiles (
    id UUID PRIMARY KEY,
    compound_id UUID NOT NULL REFERENCES chemistry.compounds(id) ON DELETE CASCADE,
    taxonomy_version_id VARCHAR(100) NOT NULL REFERENCES chemistry.classification_taxonomy_versions(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_compound_profile_version UNIQUE (compound_id, taxonomy_version_id)
);

CREATE TABLE chemistry.compound_classification_assignments (
    id UUID PRIMARY KEY,
    profile_id UUID NOT NULL REFERENCES chemistry.compound_classification_profiles(id) ON DELETE CASCADE,
    dimension VARCHAR(100) NOT NULL,
    code VARCHAR(100) NOT NULL,
    basis VARCHAR(100) NOT NULL,
    evidence_status VARCHAR(100) NOT NULL,
    rule_code VARCHAR(100),
    source_identifier VARCHAR(255),
    source_title TEXT,
    explanatory_note TEXT,
    CONSTRAINT uk_profile_assignment_code UNIQUE (profile_id, code)
);

CREATE INDEX idx_classification_definitions_dimension ON chemistry.classification_definitions(dimension);
CREATE INDEX idx_classification_definitions_code ON chemistry.classification_definitions(code);
CREATE INDEX idx_classification_assignments_code ON chemistry.compound_classification_assignments(code);
CREATE INDEX idx_classification_assignments_dimension ON chemistry.compound_classification_assignments(dimension);
