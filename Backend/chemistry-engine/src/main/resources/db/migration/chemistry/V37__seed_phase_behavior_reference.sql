INSERT INTO chemistry.phase_transition_dataset_versions (
    dataset_id, version, immutable_snapshot, description
) VALUES (
    'phase-behavior-reference-v1.0.0',
    '1.0.0',
    TRUE,
    'Foundational sourced phase-transition and vapor-pressure reference dataset for water, ethanol and carbon dioxide.'
)
ON CONFLICT (dataset_id) DO NOTHING;

INSERT INTO chemistry.phase_behavior_source_documents (
    source_code, title, citation, reuse_terms, source_url, accessed_on
) VALUES
(
    'NIST-WEBBOOK-PHASE',
    'NIST Chemistry WebBook phase-change and boundary data',
    'NIST Chemistry WebBook, SRD 69, phase-change, triple-point and critical-point records.',
    'NIST SRD data; cite NIST Chemistry WebBook.',
    'https://webbook.nist.gov/chemistry/',
    '2026-08-06'
),
(
    'YAWS-ANTOINE',
    'Yaws Handbook Antoine coefficient subset',
    'Yaws, C. L. The Yaws Handbook of Vapor Pressure: Antoine Coefficients.',
    'Citation required; coefficients stored with declared unit convention.',
    NULL,
    '2026-08-06'
)
ON CONFLICT (source_code) DO NOTHING;

WITH c AS (
    SELECT compound_code, id
    FROM chemistry.compounds
    WHERE compound_code IN ('COMP-H2O', 'COMP-ETHANOL', 'COMP-CO2')
)
INSERT INTO chemistry.phase_transition_records (
    record_id, dataset_id, compound_id, compound_code, transition_type, initial_phase, final_phase,
    temperature_k, pressure_pa, original_value, original_unit, normalized_enthalpy_j_mol,
    uncertainty, source_code, citation, reuse_terms, evidence_status
)
SELECT v.record_id, 'phase-behavior-reference-v1.0.0', c.id, v.compound_code,
       v.transition_type, v.initial_phase, v.final_phase, v.temperature_k, v.pressure_pa,
       v.original_value, v.original_unit, v.normalized_enthalpy_j_mol, v.uncertainty,
       v.source_code, v.citation, v.reuse_terms, 'SOURCED_REFERENCE_VALUE'
FROM (
    VALUES
    ('PT-H2O-FUSION-1ATM', 'COMP-H2O', 'FUSION', 'SOLID', 'LIQUID', 273.15000, 101325.000000, '6.011', 'kJ/mol', 6011.000000, NULL, 'NIST-WEBBOOK-PHASE', 'NIST Chemistry WebBook water enthalpy of fusion near normal melting point.', 'NIST SRD data; cite source.'),
    ('PT-H2O-VAPORIZATION-1ATM', 'COMP-H2O', 'VAPORIZATION', 'LIQUID', 'GAS', 373.15000, 101325.000000, '40.65', 'kJ/mol', 40650.000000, NULL, 'NIST-WEBBOOK-PHASE', 'NIST Chemistry WebBook water enthalpy of vaporization at normal boiling point.', 'NIST SRD data; cite source.'),
    ('PT-ETHANOL-VAPORIZATION-1ATM', 'COMP-ETHANOL', 'VAPORIZATION', 'LIQUID', 'GAS', 351.44000, 101325.000000, '38.56', 'kJ/mol', 38560.000000, NULL, 'NIST-WEBBOOK-PHASE', 'NIST Chemistry WebBook ethanol enthalpy of vaporization at normal boiling point.', 'NIST SRD data; cite source.'),
    ('PT-CO2-SUBLIMATION-1ATM', 'COMP-CO2', 'SUBLIMATION', 'SOLID', 'GAS', 194.67000, 101325.000000, '25.2', 'kJ/mol', 25200.000000, NULL, 'NIST-WEBBOOK-PHASE', 'NIST Chemistry WebBook carbon dioxide sublimation reference near 1 atm.', 'NIST SRD data; cite source.')
) AS v(record_id, compound_code, transition_type, initial_phase, final_phase, temperature_k, pressure_pa, original_value, original_unit, normalized_enthalpy_j_mol, uncertainty, source_code, citation, reuse_terms)
JOIN c ON c.compound_code = v.compound_code
ON CONFLICT (record_id) DO NOTHING;

WITH c AS (
    SELECT compound_code, id
    FROM chemistry.compounds
    WHERE compound_code IN ('COMP-H2O', 'COMP-ETHANOL')
)
INSERT INTO chemistry.antoine_correlations (
    correlation_id, dataset_id, compound_id, compound_code, initial_phase, final_phase,
    coefficient_a, coefficient_b, coefficient_c, temperature_unit, pressure_unit,
    min_temperature_k, max_temperature_k, convention, source_code, citation, reuse_terms, evidence_status
)
SELECT v.correlation_id, 'phase-behavior-reference-v1.0.0', c.id, v.compound_code,
       'LIQUID', 'GAS', v.a, v.b, v.c_coef, 'degC', 'mmHg',
       v.min_temperature_k, v.max_temperature_k, 'log10(P_mmHg)=A-B/(C+T_degC)',
       'YAWS-ANTOINE', v.citation, 'Citation required; do not mix unit conventions.', 'SOURCED_CORRELATION'
FROM (
    VALUES
    ('VP-H2O-ANTOINE-1-100C', 'COMP-H2O', 8.07131000, 1730.63000000, 233.42600000, 274.15000, 373.15000, 'Water Antoine coefficients for 1 C to 100 C, P in mmHg and T in degC.'),
    ('VP-ETHANOL-ANTOINE--57-80C', 'COMP-ETHANOL', 8.20417000, 1642.89000000, 230.30000000, 216.15000, 353.15000, 'Ethanol Antoine coefficients for -57 C to 80 C, P in mmHg and T in degC.')
) AS v(correlation_id, compound_code, a, b, c_coef, min_temperature_k, max_temperature_k, citation)
JOIN c ON c.compound_code = v.compound_code
ON CONFLICT (correlation_id) DO NOTHING;

WITH c AS (
    SELECT compound_code, id
    FROM chemistry.compounds
    WHERE compound_code IN ('COMP-H2O', 'COMP-ETHANOL', 'COMP-CO2')
)
INSERT INTO chemistry.phase_boundary_points (
    boundary_id, dataset_id, compound_id, compound_code, boundary_type, temperature_k, pressure_pa,
    source_code, citation, reuse_terms, evidence_status
)
SELECT v.boundary_id, 'phase-behavior-reference-v1.0.0', c.id, v.compound_code,
       v.boundary_type, v.temperature_k, v.pressure_pa, 'NIST-WEBBOOK-PHASE',
       v.citation, 'NIST SRD data; cite source.', 'SOURCED_REFERENCE_VALUE'
FROM (
    VALUES
    ('PB-H2O-TRIPLE', 'COMP-H2O', 'TRIPLE_POINT', 273.16000, 611.657000, 'Water triple point.'),
    ('PB-H2O-CRITICAL', 'COMP-H2O', 'CRITICAL_POINT', 647.09600, 22064000.000000, 'Water critical point.'),
    ('PB-H2O-NBP', 'COMP-H2O', 'NORMAL_BOILING_POINT', 373.15000, 101325.000000, 'Water normal boiling point at 1 atm.'),
    ('PB-H2O-NMP', 'COMP-H2O', 'NORMAL_MELTING_POINT', 273.15000, 101325.000000, 'Water normal melting point at 1 atm.'),
    ('PB-ETHANOL-CRITICAL', 'COMP-ETHANOL', 'CRITICAL_POINT', 514.00000, 6140000.000000, 'Ethanol critical point.'),
    ('PB-ETHANOL-NBP', 'COMP-ETHANOL', 'NORMAL_BOILING_POINT', 351.44000, 101325.000000, 'Ethanol normal boiling point at 1 atm.'),
    ('PB-CO2-TRIPLE', 'COMP-CO2', 'TRIPLE_POINT', 216.59200, 518500.000000, 'Carbon dioxide triple point.'),
    ('PB-CO2-CRITICAL', 'COMP-CO2', 'CRITICAL_POINT', 304.12820, 7377300.000000, 'Carbon dioxide critical point.')
) AS v(boundary_id, compound_code, boundary_type, temperature_k, pressure_pa, citation)
JOIN c ON c.compound_code = v.compound_code
ON CONFLICT (boundary_id) DO NOTHING;
