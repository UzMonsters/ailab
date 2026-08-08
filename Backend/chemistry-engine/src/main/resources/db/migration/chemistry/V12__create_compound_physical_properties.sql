-- V12: Create compound physical properties schema
CREATE TABLE chemistry.compound_physical_property_dataset_versions (
    id VARCHAR(100) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    publication_date VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE chemistry.compound_physical_property_profiles (
    id UUID PRIMARY KEY,
    compound_id UUID NOT NULL REFERENCES chemistry.compounds(id) ON DELETE CASCADE,
    dataset_version_id VARCHAR(100) NOT NULL REFERENCES chemistry.compound_physical_property_dataset_versions(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_compound_physical_property_profile UNIQUE (compound_id, dataset_version_id)
);

CREATE TABLE chemistry.compound_property_availability (
    id UUID PRIMARY KEY,
    profile_id UUID NOT NULL REFERENCES chemistry.compound_physical_property_profiles(id) ON DELETE CASCADE,
    property_type VARCHAR(100) NOT NULL,
    availability_status VARCHAR(100) NOT NULL,
    CONSTRAINT uk_profile_property_availability UNIQUE (profile_id, property_type)
);

CREATE INDEX idx_compound_property_avail_type ON chemistry.compound_property_availability(property_type, availability_status);
