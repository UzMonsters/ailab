-- Deactivate old incorrect profiles without mutating V31-V33.
UPDATE chemistry.kinetic_profiles
SET is_active = FALSE
WHERE profile_id IN ('KP-ELEM-H-O2-PIRRAGLIA-1989', 'KP-ELEM-OH-CO-WOOLDRIDGE-1994');

ALTER TABLE chemistry.kinetic_profiles
    ADD COLUMN IF NOT EXISTS original_a_value VARCHAR(64),
    ADD COLUMN IF NOT EXISTS original_a_unit VARCHAR(64),
    ADD COLUMN IF NOT EXISTS original_k_value VARCHAR(64),
    ADD COLUMN IF NOT EXISTS original_k_unit VARCHAR(64),
    ADD COLUMN IF NOT EXISTS conversion_factor VARCHAR(64),
    ADD COLUMN IF NOT EXISTS min_pressure_bar NUMERIC(8,4),
    ADD COLUMN IF NOT EXISTS max_pressure_bar NUMERIC(8,4),
    ADD COLUMN IF NOT EXISTS bath_gas VARCHAR(64);

ALTER TABLE chemistry.kinetic_profiles
    ALTER COLUMN rate_constant_value TYPE NUMERIC(24,8);

INSERT INTO chemistry.compounds (
    id, compound_code, primary_name, original_formula, normalized_formula, net_charge,
    hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound,
    molar_mass_kind, element_catalog_version, compound_catalog_version_id,
    source_identifier, source_title, composition_formula
) VALUES
(
    '33333333-3333-3333-3333-333333333101', 'COMP-RAD-H', 'Atomic hydrogen radical',
    'H', 'H', 0, NULL, 1.008000, NULL, NULL,
    'EXACT_FROM_FIXED_VALUES', 'v1.1.0', 'compound-core-v1.0.0',
    'NIST-CHEMICAL-KINETICS', 'NIST Chemical Kinetics Database radical participant', 'H'
),
(
    '33333333-3333-3333-3333-333333333102', 'COMP-RAD-OH', 'Hydroxyl radical',
    'OH', 'OH', 0, NULL, 17.007000, NULL, NULL,
    'EXACT_FROM_FIXED_VALUES', 'v1.1.0', 'compound-core-v1.0.0',
    'NIST-CHEMICAL-KINETICS', 'NIST Chemical Kinetics Database radical participant', 'HO'
),
(
    '33333333-3333-3333-3333-333333333103', 'COMP-RAD-O', 'Atomic oxygen radical',
    'O', 'O', 0, NULL, 15.999000, NULL, NULL,
    'EXACT_FROM_FIXED_VALUES', 'v1.1.0', 'compound-core-v1.0.0',
    'NIST-CHEMICAL-KINETICS', 'NIST Chemical Kinetics Database radical participant', 'O'
)
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;

WITH components(compound_code, element_id, atomic_number, symbol, atom_count, component_id) AS (
    VALUES
        ('COMP-RAD-H', '6207a804-03dc-3cc1-aa3b-5b7303315c4b'::uuid, 1, 'H', 1, '33333333-3333-3333-3333-333333333111'::uuid),
        ('COMP-RAD-OH', '6207a804-03dc-3cc1-aa3b-5b7303315c4b'::uuid, 1, 'H', 1, '33333333-3333-3333-3333-333333333112'::uuid),
        ('COMP-RAD-OH', '2f51c0fc-fbf7-31dd-b8b2-de15529ee4be'::uuid, 8, 'O', 1, '33333333-3333-3333-3333-333333333113'::uuid),
        ('COMP-RAD-O', '2f51c0fc-fbf7-31dd-b8b2-de15529ee4be'::uuid, 8, 'O', 1, '33333333-3333-3333-3333-333333333114'::uuid)
)
INSERT INTO chemistry.compound_components (
    id, compound_id, element_id, atomic_number, symbol, atom_count
)
SELECT c.component_id, compound.id, c.element_id, c.atomic_number, c.symbol, c.atom_count
FROM components c
JOIN chemistry.compounds compound
  ON compound.compound_code = c.compound_code
 AND compound.compound_catalog_version_id = 'compound-core-v1.0.0'
ON CONFLICT (compound_id, element_id) DO NOTHING;

INSERT INTO chemistry.reaction_source_documents (
    source_id, source_type, issuer, title, edition, publication_date, access_date,
    coverage, fields_supplied, language, source_reference, licensing_note
) VALUES
(
    'NIST-CHEMICAL-KINETICS', 'AUTHORITATIVE_DATABASE',
    'National Institute of Standards and Technology (NIST)',
    'NIST Chemical Kinetics Database', NULL, '2026', '2026-08-06',
    'Elementary gas-phase chemical kinetics records',
    'recordId,reaction,rateLaw,temperatureRange,pressureRange,bathGas,arrheniusParameters,referenceRate,sourceUnits',
    'en', 'NIST-SRD-17', 'US Government open reference dataset'
)
ON CONFLICT (source_id) DO NOTHING;

INSERT INTO chemistry.reactions (
    id, reaction_code, primary_name, original_equation, normalized_equation,
    canonical_balanced_equation, reaction_signature, directionality,
    catalog_version_id, source_document_id, provenance_notes
) VALUES
(
    '33333333-3333-3333-3333-333333333127',
    'RXN-ELEM-H-O2-PROPAGATION',
    'Elementary Step H + O2 -> OH + O',
    'H + O2 -> OH + O',
    'H + O2 -> OH + O',
    'H + O2 -> OH + O',
    '1*COMP-RAD-H+1*COMP-O2->1*COMP-RAD-OH+1*COMP-RAD-O[IRREVERSIBLE]',
    'IRREVERSIBLE',
    'reaction-core-v1.0.0',
    'NIST-CHEMICAL-KINETICS',
    'Exact elementary NIST reaction identity for 1989PIR/MIC282:3'
),
(
    '33333333-3333-3333-3333-333333333128',
    'RXN-ELEM-CO-OH-PROPAGATION',
    'Elementary Step CO + OH -> CO2 + H',
    'CO + OH -> CO2 + H',
    'CO + OH -> CO2 + H',
    'CO + OH -> CO2 + H',
    '1*COMP-CO+1*COMP-RAD-OH->1*COMP-CO2+1*COMP-RAD-H[IRREVERSIBLE]',
    'IRREVERSIBLE',
    'reaction-core-v1.0.0',
    'NIST-CHEMICAL-KINETICS',
    'Exact elementary NIST reaction identity for 1994WOO/HAN741-748:1'
)
ON CONFLICT (reaction_code) DO NOTHING;

WITH elementary_terms(reaction_code, compound_code, formula, side, coefficient, species_state, term_order) AS (
    VALUES
        ('RXN-ELEM-H-O2-PROPAGATION', 'COMP-RAD-H', 'H', 'REACTANT', 1, 'GAS', 1),
        ('RXN-ELEM-H-O2-PROPAGATION', 'COMP-O2', 'O2', 'REACTANT', 1, 'GAS', 2),
        ('RXN-ELEM-H-O2-PROPAGATION', 'COMP-RAD-OH', 'OH', 'PRODUCT', 1, 'GAS', 3),
        ('RXN-ELEM-H-O2-PROPAGATION', 'COMP-RAD-O', 'O', 'PRODUCT', 1, 'GAS', 4),
        ('RXN-ELEM-CO-OH-PROPAGATION', 'COMP-CO', 'CO', 'REACTANT', 1, 'GAS', 1),
        ('RXN-ELEM-CO-OH-PROPAGATION', 'COMP-RAD-OH', 'OH', 'REACTANT', 1, 'GAS', 2),
        ('RXN-ELEM-CO-OH-PROPAGATION', 'COMP-CO2', 'CO2', 'PRODUCT', 1, 'GAS', 3),
        ('RXN-ELEM-CO-OH-PROPAGATION', 'COMP-RAD-H', 'H', 'PRODUCT', 1, 'GAS', 4)
)
INSERT INTO chemistry.reaction_terms (
    reaction_id, compound_id, compound_code, formula, side, coefficient, species_state, term_order
)
SELECT r.id, c.id, t.compound_code, t.formula, t.side, t.coefficient, t.species_state, t.term_order
FROM elementary_terms t
JOIN chemistry.reactions r ON r.reaction_code = t.reaction_code
JOIN chemistry.compounds c
  ON c.compound_code = t.compound_code
 AND c.compound_catalog_version_id = 'compound-core-v1.0.0'
ON CONFLICT (reaction_id, side, compound_id) DO NOTHING;

INSERT INTO chemistry.kinetic_profiles (
    profile_id, reaction_code, overall_order, rate_constant_value, rate_constant_unit,
    pre_exponential_factor_a, temperature_exponent_n, activation_energy_kj_mol, min_temperature_k, max_temperature_k,
    ref_temperature_k, ref_pressure_bar, min_pressure_bar, max_pressure_bar, bath_gas, solvent, evidence_status, is_active,
    provenance_source_id, provenance_description, provenance_citation,
    nist_squib, paper_title, authors, journal_name, publication_year, pages, record_url, data_type, experimental_method, uncertainty,
    original_a_value, original_a_unit, original_k_value, original_k_unit, conversion_factor
) VALUES
(
    'KP-ELEM-H-O2-PIRRAGLIA-1989-REC3', 'RXN-ELEM-H-O2-PROPAGATION', 2.0000, 73470117.27200000, 'L/(mol*s)',
    168017727204.00000000, 0.0000, 67.5140, 962.00, 1700.00,
    1050.00, 0.0413, 0.0133, 0.0413, 'Ar', 'GAS_PHASE (Ar bath)', 'EXPERIMENTAL', TRUE,
    'NIST-CHEMICAL-KINETICS', 'NIST Chemical Kinetics Database record 1989PIR/MIC282:3', 'Pirraglia et al., J. Phys. Chem. 93, 282 (1989)',
    '1989PIR/MIC282:3', 'A shock tube study of the reaction H + O2 -> OH + O', 'Pirraglia, P. V.; Michael, J. V.; Sutherland, J. W.; Klemm, R. B.', 'J. Phys. Chem.', 1989, '282-291', 'https://kinetics.nist.gov/kinetics/Detail?id=1989PIR/MIC282:3', 'EXPERIMENTAL', 'SHOCK_TUBE', '+/-15%',
    '2.79E-10', 'cm3 molecule-1 s-1', '1.22E-13', 'cm3 molecule-1 s-1', '6.02214076E20'
),
(
    'KP-ELEM-CO-OH-WOOLDRIDGE-1994-REC1', 'RXN-ELEM-CO-OH-PROPAGATION', 2.0000, 190356900.00000000, 'L/(mol*s)',
    2119793547.52000000, 0.0000, 21.8670, 1090.00, 2370.00,
    1090.00, 0.8300, 0.1900, 0.8300, 'Ar', 'GAS_PHASE (Ar bath)', 'EXPERIMENTAL', TRUE,
    'NIST-CHEMICAL-KINETICS', 'NIST Chemical Kinetics Database record 1994WOO/HAN741-748:1', 'Wooldridge et al., Int. J. Chem. Kinet. 26, 741 (1994)',
    '1994WOO/HAN741-748:1', 'A shock tube study of the reaction CO + OH -> CO2 + H', 'Wooldridge, M. S.; Hanson, R. K.; Bowman, C. T.', 'Int. J. Chem. Kinet.', 1994, '741-748', 'https://kinetics.nist.gov/kinetics/Detail?id=1994WOO/HAN741-748:1', 'DIRECT_ABSOLUTE_EXPERIMENTAL_VALUE', 'SHOCK_TUBE', '+/-10%',
    '3.52E-12', 'cm3 molecule-1 s-1', '3.16E-13', 'cm3 molecule-1 s-1', '6.02214076E20'
)
ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.kinetic_rate_law_terms (profile_id, compound_code, physical_state, empirical_order) VALUES
('KP-ELEM-H-O2-PIRRAGLIA-1989-REC3', 'COMP-RAD-H', 'GAS', 1.0000),
('KP-ELEM-H-O2-PIRRAGLIA-1989-REC3', 'COMP-O2', 'GAS', 1.0000),
('KP-ELEM-CO-OH-WOOLDRIDGE-1994-REC1', 'COMP-CO', 'GAS', 1.0000),
('KP-ELEM-CO-OH-WOOLDRIDGE-1994-REC1', 'COMP-RAD-OH', 'GAS', 1.0000)
ON CONFLICT (profile_id, compound_code, physical_state) DO NOTHING;
