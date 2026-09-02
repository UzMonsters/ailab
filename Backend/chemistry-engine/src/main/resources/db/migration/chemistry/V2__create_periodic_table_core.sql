CREATE TABLE chemistry.periodic_table_catalog_versions (
    id VARCHAR(50) PRIMARY KEY,
    version VARCHAR(50) NOT NULL UNIQUE,
    generated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    data_sources TEXT NOT NULL,
    reference_conditions TEXT NOT NULL
);

CREATE TABLE chemistry.elements (
    id UUID PRIMARY KEY,
    atomic_number INT NOT NULL UNIQUE CHECK (atomic_number >= 1 AND atomic_number <= 118),
    symbol VARCHAR(5) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL UNIQUE,
    latin_name VARCHAR(100),
    atomic_mass_value NUMERIC(20, 10) NOT NULL CHECK (atomic_mass_value > 0),
    atomic_mass_kind VARCHAR(50) NOT NULL,
    atomic_mass_lower_bound NUMERIC(20, 10) CHECK (atomic_mass_lower_bound > 0),
    atomic_mass_upper_bound NUMERIC(20, 10) CHECK (atomic_mass_upper_bound > 0),
    period_number INT NOT NULL CHECK (period_number >= 1 AND period_number <= 7),
    group_number INT CHECK (group_number >= 1 AND group_number <= 18),
    block VARCHAR(5) NOT NULL CHECK (block IN ('S', 'P', 'D', 'F')),
    electron_configuration VARCHAR(100) NOT NULL,
    standard_state VARCHAR(20) NOT NULL CHECK (standard_state IN ('SOLID', 'LIQUID', 'GAS', 'UNKNOWN')),
    radioactivity_status VARCHAR(50) NOT NULL,
    category VARCHAR(50) NOT NULL CHECK (category IN ('ALKALI_METAL', 'ALKALINE_EARTH_METAL', 'TRANSITION_METAL', 'POST_TRANSITION_METAL', 'METALLOID', 'REACTIVE_NONMETAL', 'HALOGEN', 'NOBLE_GAS', 'LANTHANIDE', 'ACTINIDE', 'UNKNOWN')),
    series VARCHAR(50) NOT NULL CHECK (series IN ('MAIN_GROUP', 'TRANSITION', 'LANTHANIDE', 'ACTINIDE', 'UNKNOWN')),
    catalog_version_id VARCHAR(50) NOT NULL,
    source_reference VARCHAR(500),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (catalog_version_id) REFERENCES chemistry.periodic_table_catalog_versions(id),
    CONSTRAINT elements_radioactivity_status_check CHECK (radioactivity_status IN ('STABLE_OR_HAS_STABLE_ISOTOPES', 'RADIOACTIVE', 'UNKNOWN')),
    CONSTRAINT chk_mass_bounds CHECK (
        (atomic_mass_lower_bound IS NULL AND atomic_mass_upper_bound IS NULL) OR 
        (atomic_mass_lower_bound IS NOT NULL AND atomic_mass_upper_bound IS NOT NULL AND atomic_mass_lower_bound <= atomic_mass_upper_bound)
    )
);

CREATE INDEX idx_elements_symbol ON chemistry.elements(symbol);
CREATE INDEX idx_elements_atomic_number ON chemistry.elements(atomic_number);
CREATE INDEX idx_elements_name ON chemistry.elements(name);
