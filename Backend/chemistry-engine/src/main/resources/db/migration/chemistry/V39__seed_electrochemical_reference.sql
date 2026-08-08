INSERT INTO chemistry.electrochemical_dataset_versions (
    dataset_id, version, immutable_snapshot, description
) VALUES (
    'electrochemical-reference-v1.0.0',
    '1.0.0',
    TRUE,
    'Foundational sourced aqueous standard reduction-potential dataset at 298.15 K.'
)
ON CONFLICT (dataset_id) DO NOTHING;

INSERT INTO chemistry.electrochemical_source_documents (
    source_code, title, citation, reuse_terms, source_url, accessed_on
) VALUES
('CRC-ELECTRODE-POTENTIALS', 'CRC standard reduction potentials', 'CRC Handbook of Chemistry and Physics standard reduction potentials, aqueous, 298.15 K.', 'Citation required; values are stored with stated standard-state convention.', NULL, '2026-08-06'),
('IUPAC-SHE-CONVENTION', 'Standard hydrogen electrode convention', 'IUPAC electrochemical convention defining the standard hydrogen electrode as 0 V.', 'Citation required; zero is a reference convention, not a measured nonzero value.', NULL, '2026-08-06')
ON CONFLICT (source_code) DO NOTHING;

INSERT INTO chemistry.electrochemical_reference_conditions (
    condition_id, temperature_k, solvent_code, standard_state_convention, pressure_bar
) VALUES (
    'EC-COND-AQ-298K-1BAR',
    298.15000,
    'COMP-H2O',
    'aqueous solutes at unit activity, gases at 1 bar, pure condensed phases at activity 1',
    1.000000
)
ON CONFLICT (condition_id) DO NOTHING;

INSERT INTO chemistry.standard_reduction_potentials (
    record_id, dataset_id, condition_id, equation, reduction_direction, electron_count,
    standard_potential_v, original_value, original_unit, source_code, source_record_id,
    citation, evidence_status
) VALUES
('SRP-H2-REFERENCE', 'electrochemical-reference-v1.0.0', 'EC-COND-AQ-298K-1BAR', '2H+ + 2e- <=> H2(g)', 'REDUCTION', 2.000000, 0.000000, '0.000', 'V', 'IUPAC-SHE-CONVENTION', 'SHE-CONVENTION-0V', 'IUPAC electrochemical convention defining standard hydrogen electrode E0 as 0 V.', 'REFERENCE_CONVENTION'),
('SRP-CU2-CU', 'electrochemical-reference-v1.0.0', 'EC-COND-AQ-298K-1BAR', 'Cu2+ + 2e- <=> Cu(s)', 'REDUCTION', 2.000000, 0.340000, '+0.340', 'V', 'CRC-ELECTRODE-POTENTIALS', 'CRC-AQ-SRP-CU2-CU-298K', 'CRC aqueous standard reduction-potential table, Cu2+/Cu, 298.15 K.', 'SOURCED_REFERENCE_VALUE'),
('SRP-ZN2-ZN', 'electrochemical-reference-v1.0.0', 'EC-COND-AQ-298K-1BAR', 'Zn2+ + 2e- <=> Zn(s)', 'REDUCTION', 2.000000, -0.763000, '-0.763', 'V', 'CRC-ELECTRODE-POTENTIALS', 'CRC-AQ-SRP-ZN2-ZN-298K', 'CRC aqueous standard reduction-potential table, Zn2+/Zn, 298.15 K.', 'SOURCED_REFERENCE_VALUE'),
('SRP-AG-PLUS-AG', 'electrochemical-reference-v1.0.0', 'EC-COND-AQ-298K-1BAR', 'Ag+ + e- <=> Ag(s)', 'REDUCTION', 1.000000, 0.799600, '+0.7996', 'V', 'CRC-ELECTRODE-POTENTIALS', 'CRC-AQ-SRP-AG-AG-298K', 'CRC aqueous standard reduction-potential table, Ag+/Ag, 298.15 K.', 'SOURCED_REFERENCE_VALUE'),
('SRP-FE3-FE2', 'electrochemical-reference-v1.0.0', 'EC-COND-AQ-298K-1BAR', 'Fe3+ + e- <=> Fe2+', 'REDUCTION', 1.000000, 0.771000, '+0.771', 'V', 'CRC-ELECTRODE-POTENTIALS', 'CRC-AQ-SRP-FE3-FE2-298K', 'CRC aqueous standard reduction-potential table, Fe3+/Fe2+, 298.15 K.', 'SOURCED_REFERENCE_VALUE'),
('SRP-CL2-CL', 'electrochemical-reference-v1.0.0', 'EC-COND-AQ-298K-1BAR', 'Cl2(g) + 2e- <=> 2Cl-', 'REDUCTION', 2.000000, 1.358000, '+1.358', 'V', 'CRC-ELECTRODE-POTENTIALS', 'CRC-AQ-SRP-CL2-CL-298K', 'CRC aqueous standard reduction-potential table, Cl2/Cl-, 298.15 K.', 'SOURCED_REFERENCE_VALUE')
ON CONFLICT (record_id) DO NOTHING;

WITH c AS (
    SELECT compound_code, id FROM chemistry.compounds
)
INSERT INTO chemistry.half_reaction_participants (
    participant_id, record_id, species_code, compound_id, display_formula, element_counts,
    coefficient, phase, charge, side, display_order
)
SELECT v.participant_id, v.record_id, v.species_code, c.id, v.display_formula, v.element_counts,
       v.coefficient, v.phase, v.charge, v.side, v.display_order
FROM (
    VALUES
    ('HRP-H2-HPLUS', 'SRP-H2-REFERENCE', 'COMP-H-PLUS', 'H+', 'H:1', 2.000000, 'AQUEOUS', 1, 'REACTANT', 1),
    ('HRP-H2-H2', 'SRP-H2-REFERENCE', 'COMP-H2', 'H2', 'H:2', 1.000000, 'GAS', 0, 'PRODUCT', 2),
    ('HRP-CU-CU2', 'SRP-CU2-CU', 'COMP-CU2-PLUS', 'Cu2+', 'Cu:1', 1.000000, 'AQUEOUS', 2, 'REACTANT', 1),
    ('HRP-CU-CU', 'SRP-CU2-CU', 'COMP-CU', 'Cu', 'Cu:1', 1.000000, 'SOLID', 0, 'PRODUCT', 2),
    ('HRP-ZN-ZN2', 'SRP-ZN2-ZN', 'COMP-ZN2-PLUS', 'Zn2+', 'Zn:1', 1.000000, 'AQUEOUS', 2, 'REACTANT', 1),
    ('HRP-ZN-ZN', 'SRP-ZN2-ZN', 'COMP-ZN', 'Zn', 'Zn:1', 1.000000, 'SOLID', 0, 'PRODUCT', 2),
    ('HRP-AG-AGPLUS', 'SRP-AG-PLUS-AG', 'COMP-AG-PLUS', 'Ag+', 'Ag:1', 1.000000, 'AQUEOUS', 1, 'REACTANT', 1),
    ('HRP-AG-AG', 'SRP-AG-PLUS-AG', 'COMP-AG', 'Ag', 'Ag:1', 1.000000, 'SOLID', 0, 'PRODUCT', 2),
    ('HRP-FE-FE3', 'SRP-FE3-FE2', 'COMP-FE3-PLUS', 'Fe3+', 'Fe:1', 1.000000, 'AQUEOUS', 3, 'REACTANT', 1),
    ('HRP-FE-FE2', 'SRP-FE3-FE2', 'COMP-FE2-PLUS', 'Fe2+', 'Fe:1', 1.000000, 'AQUEOUS', 2, 'PRODUCT', 2),
    ('HRP-CL-CL2', 'SRP-CL2-CL', 'COMP-CL2', 'Cl2', 'Cl:2', 1.000000, 'GAS', 0, 'REACTANT', 1),
    ('HRP-CL-CLMINUS', 'SRP-CL2-CL', 'COMP-CL-MINUS', 'Cl-', 'Cl:1', 2.000000, 'AQUEOUS', -1, 'PRODUCT', 2)
) AS v(participant_id, record_id, species_code, display_formula, element_counts, coefficient, phase, charge, side, display_order)
JOIN c ON c.compound_code = v.species_code
ON CONFLICT (participant_id) DO NOTHING;
