CREATE TABLE IF NOT EXISTS chemistry.electrochemical_dataset_versions (
    dataset_id VARCHAR(80) PRIMARY KEY,
    version VARCHAR(32) NOT NULL,
    immutable_snapshot BOOLEAN NOT NULL DEFAULT TRUE,
    description TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS chemistry.electrochemical_source_documents (
    source_code VARCHAR(80) PRIMARY KEY,
    title TEXT NOT NULL,
    citation TEXT NOT NULL,
    reuse_terms TEXT NOT NULL,
    source_url TEXT,
    accessed_on DATE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS chemistry.electrochemical_reference_conditions (
    condition_id VARCHAR(96) PRIMARY KEY,
    temperature_k NUMERIC(12,5) NOT NULL CHECK (temperature_k > 0),
    solvent_code VARCHAR(64) NOT NULL,
    standard_state_convention TEXT NOT NULL,
    pressure_bar NUMERIC(12,6) NOT NULL CHECK (pressure_bar > 0),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS chemistry.standard_reduction_potentials (
    record_id VARCHAR(96) PRIMARY KEY,
    dataset_id VARCHAR(80) NOT NULL REFERENCES chemistry.electrochemical_dataset_versions(dataset_id),
    condition_id VARCHAR(96) NOT NULL REFERENCES chemistry.electrochemical_reference_conditions(condition_id),
    equation TEXT NOT NULL,
    reduction_direction VARCHAR(24) NOT NULL CHECK (reduction_direction = 'REDUCTION'),
    electron_count NUMERIC(12,6) NOT NULL CHECK (electron_count > 0),
    standard_potential_v NUMERIC(14,6) NOT NULL,
    original_value VARCHAR(64) NOT NULL,
    original_unit VARCHAR(32) NOT NULL CHECK (original_unit IN ('V', 'mV')),
    source_code VARCHAR(80) NOT NULL REFERENCES chemistry.electrochemical_source_documents(source_code),
    source_record_id VARCHAR(120) NOT NULL,
    citation TEXT NOT NULL,
    evidence_status VARCHAR(40) NOT NULL CHECK (evidence_status IN ('SOURCED_REFERENCE_VALUE', 'REFERENCE_CONVENTION')),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_electrochemical_half_reaction UNIQUE (dataset_id, equation, condition_id)
);

CREATE TABLE IF NOT EXISTS chemistry.half_reaction_participants (
    participant_id VARCHAR(120) PRIMARY KEY,
    record_id VARCHAR(96) NOT NULL REFERENCES chemistry.standard_reduction_potentials(record_id) ON DELETE CASCADE,
    species_code VARCHAR(64) NOT NULL,
    compound_id UUID REFERENCES chemistry.compounds(id),
    display_formula VARCHAR(64) NOT NULL,
    element_counts VARCHAR(200) NOT NULL,
    coefficient NUMERIC(12,6) NOT NULL CHECK (coefficient > 0),
    phase VARCHAR(16) NOT NULL CHECK (phase IN ('SOLID', 'LIQUID', 'GAS', 'AQUEOUS')),
    charge INTEGER NOT NULL,
    side VARCHAR(16) NOT NULL CHECK (side IN ('REACTANT', 'PRODUCT')),
    display_order INTEGER NOT NULL CHECK (display_order > 0),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_half_reaction_participant UNIQUE (record_id, species_code, phase, side)
);

CREATE INDEX IF NOT EXISTS ix_standard_reduction_potentials_active ON chemistry.standard_reduction_potentials(record_id, is_active);
CREATE INDEX IF NOT EXISTS ix_half_reaction_participants_record ON chemistry.half_reaction_participants(record_id);

INSERT INTO chemistry.compounds (
    id, compound_code, primary_name, original_formula, normalized_formula, composition_formula, net_charge, hydrate_info,
    molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind,
    element_catalog_version, compound_catalog_version_id, source_identifier, source_title
)
VALUES
('ba100001-0000-3000-9000-000000000001', 'COMP-H-PLUS', 'Hydrogen ion', 'H+', 'H+', 'H', 1, NULL, 1.008, 1.00784, 1.00811, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'Additive electrochemistry catalogue species; formula and charge explicit.'),
('ba100001-0000-3000-9000-000000000002', 'COMP-CU', 'Copper', 'Cu', 'Cu', 'Cu', 0, NULL, 63.546, 63.546, 63.546, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'Additive electrochemistry catalogue species; formula and charge explicit.'),
('ba100001-0000-3000-9000-000000000003', 'COMP-CU2-PLUS', 'Copper(II) ion', 'Cu2+', 'Cu2+', 'Cu', 2, NULL, 63.546, 63.546, 63.546, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'Additive electrochemistry catalogue species; formula and charge explicit.'),
('ba100001-0000-3000-9000-000000000004', 'COMP-ZN', 'Zinc', 'Zn', 'Zn', 'Zn', 0, NULL, 65.38, 65.38, 65.38, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'Additive electrochemistry catalogue species; formula and charge explicit.'),
('ba100001-0000-3000-9000-000000000005', 'COMP-ZN2-PLUS', 'Zinc ion', 'Zn2+', 'Zn2+', 'Zn', 2, NULL, 65.38, 65.38, 65.38, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'Additive electrochemistry catalogue species; formula and charge explicit.'),
('ba100001-0000-3000-9000-000000000006', 'COMP-AG', 'Silver', 'Ag', 'Ag', 'Ag', 0, NULL, 107.8682, 107.8682, 107.8682, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'Additive electrochemistry catalogue species; formula and charge explicit.'),
('ba100001-0000-3000-9000-000000000007', 'COMP-AG-PLUS', 'Silver ion', 'Ag+', 'Ag+', 'Ag', 1, NULL, 107.8682, 107.8682, 107.8682, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'Additive electrochemistry catalogue species; formula and charge explicit.'),
('ba100001-0000-3000-9000-000000000008', 'COMP-FE3-PLUS', 'Iron(III) ion', 'Fe3+', 'Fe3+', 'Fe', 3, NULL, 55.845, 55.845, 55.845, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'Additive electrochemistry catalogue species; formula and charge explicit.'),
('ba100001-0000-3000-9000-000000000009', 'COMP-FE2-PLUS', 'Iron(II) ion', 'Fe2+', 'Fe2+', 'Fe', 2, NULL, 55.845, 55.845, 55.845, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'Additive electrochemistry catalogue species; formula and charge explicit.'),
('ba100001-0000-3000-9000-000000000010', 'COMP-CL-MINUS', 'Chloride ion', 'Cl-', 'Cl-', 'Cl', -1, NULL, 35.45, 35.446, 35.457, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'Additive electrochemistry catalogue species; formula and charge explicit.')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;

INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
SELECT v.component_id::uuid, c.id, e.id, v.atomic_number, v.symbol, v.atom_count
FROM (
    VALUES
    ('ba200001-0000-3000-9000-000000000001', 'COMP-H-PLUS', 1, 'H', 1),
    ('ba200001-0000-3000-9000-000000000002', 'COMP-CU', 29, 'Cu', 1),
    ('ba200001-0000-3000-9000-000000000003', 'COMP-CU2-PLUS', 29, 'Cu', 1),
    ('ba200001-0000-3000-9000-000000000004', 'COMP-ZN', 30, 'Zn', 1),
    ('ba200001-0000-3000-9000-000000000005', 'COMP-ZN2-PLUS', 30, 'Zn', 1),
    ('ba200001-0000-3000-9000-000000000006', 'COMP-AG', 47, 'Ag', 1),
    ('ba200001-0000-3000-9000-000000000007', 'COMP-AG-PLUS', 47, 'Ag', 1),
    ('ba200001-0000-3000-9000-000000000008', 'COMP-FE3-PLUS', 26, 'Fe', 1),
    ('ba200001-0000-3000-9000-000000000009', 'COMP-FE2-PLUS', 26, 'Fe', 1),
    ('ba200001-0000-3000-9000-000000000010', 'COMP-CL-MINUS', 17, 'Cl', 1)
) AS v(component_id, compound_code, atomic_number, symbol, atom_count)
JOIN chemistry.compounds c ON c.compound_code = v.compound_code
JOIN chemistry.elements e ON e.atomic_number = v.atomic_number
ON CONFLICT DO NOTHING;
