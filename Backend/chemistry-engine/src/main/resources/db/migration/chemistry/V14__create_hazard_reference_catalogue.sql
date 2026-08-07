-- V14: Create hazard reference catalogue schema
CREATE TABLE chemistry.hazard_dataset_versions (
    id VARCHAR(100) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    publication_date VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE chemistry.hazard_source_documents (
    id VARCHAR(100) PRIMARY KEY,
    document_type VARCHAR(100) NOT NULL,
    issuer_or_supplier VARCHAR(255) NOT NULL,
    document_title VARCHAR(255) NOT NULL,
    classification_system VARCHAR(100) NOT NULL,
    revision_or_edition VARCHAR(100),
    jurisdiction VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE chemistry.hazard_profiles (
    id UUID PRIMARY KEY,
    compound_id UUID NOT NULL REFERENCES chemistry.compounds(id) ON DELETE CASCADE,
    dataset_version_id VARCHAR(100) NOT NULL REFERENCES chemistry.hazard_dataset_versions(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_hazard_profile UNIQUE (compound_id, dataset_version_id)
);

CREATE TABLE chemistry.hazard_availability (
    id UUID PRIMARY KEY,
    profile_id UUID NOT NULL REFERENCES chemistry.hazard_profiles(id) ON DELETE CASCADE,
    classification_system VARCHAR(100) NOT NULL,
    availability_status VARCHAR(100) NOT NULL,
    CONSTRAINT uk_hazard_availability UNIQUE (profile_id, classification_system)
);

CREATE INDEX idx_hazard_availability_status ON chemistry.hazard_availability(classification_system, availability_status);
