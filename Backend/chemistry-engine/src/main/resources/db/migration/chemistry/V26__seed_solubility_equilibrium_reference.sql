-- Flyway Migration V26: Seed Solubility Equilibrium Reference Data
-- Dataset Version: solubility-ksp-v1.0.0

INSERT INTO chemistry.solubility_dataset_versions (version, description, publication_date)
VALUES (
    'solubility-ksp-v1.0.0',
    'Minimal educational solubility-product subset for existing neutral compound catalogue solids at 25 C in water',
    '2026-08-06'
)
ON CONFLICT (version) DO NOTHING;

INSERT INTO chemistry.solubility_source_documents (source_identifier, citation, publisher, reuse_limitations)
VALUES (
    'CRC-HANDBOOK-104',
    'CRC Handbook of Chemistry and Physics, 104th Edition, Section 8, solubility products at 25 C',
    'CRC Press / Taylor & Francis',
    'CRC tabular data are copyrighted. This project stores a minimal cited educational subset; do not redistribute as a standalone data table.'
)
ON CONFLICT (source_identifier) DO NOTHING;

INSERT INTO chemistry.chemical_species (species_code, name, formula, kind, charge, primary_role, associated_compound_code)
VALUES
    ('SPEC-CA-2PLUS', 'Calcium ion', 'Ca^2+', 'CATION', 2, 'NEUTRAL', NULL),
    ('SPEC-MG-2PLUS', 'Magnesium ion', 'Mg^2+', 'CATION', 2, 'NEUTRAL', NULL),
    ('SPEC-AL-3PLUS', 'Aluminium ion', 'Al^3+', 'CATION', 3, 'NEUTRAL', NULL)
ON CONFLICT (species_code) DO NOTHING;

WITH inserted AS (
    INSERT INTO chemistry.solubility_equilibria (
        equilibrium_code,
        solid_compound_code,
        ksp_value,
        temperature_celsius,
        solvent_code,
        activity_convention,
        dataset_version,
        source_identifier,
        citation,
        reuse_limitations
    )
    VALUES
        ('KSP-CACO3-CALCITE', 'COMP-CACO3', 4.20e-9, 25.00, 'COMP-H2O',
         'Dimensionless activities ai = gamma_i * ci / c0, c0 = 1 mol/L',
         'solubility-ksp-v1.0.0', 'CRC-HANDBOOK-104',
         'CRC Handbook of Chemistry and Physics, 104th Edition, Section 8, solubility products at 25 C',
         'CRC tabular data are copyrighted. This project stores a minimal cited educational subset; do not redistribute as a standalone data table.'),
        ('KSP-MG-OH-2', 'COMP-MG-OH-2', 5.61e-12, 25.00, 'COMP-H2O',
         'Dimensionless activities ai = gamma_i * ci / c0, c0 = 1 mol/L',
         'solubility-ksp-v1.0.0', 'CRC-HANDBOOK-104',
         'CRC Handbook of Chemistry and Physics, 104th Edition, Section 8, solubility products at 25 C',
         'CRC tabular data are copyrighted. This project stores a minimal cited educational subset; do not redistribute as a standalone data table.'),
        ('KSP-AL-OH-3', 'COMP-AL-OH-3', 3.00e-34, 25.00, 'COMP-H2O',
         'Dimensionless activities ai = gamma_i * ci / c0, c0 = 1 mol/L',
         'solubility-ksp-v1.0.0', 'CRC-HANDBOOK-104',
         'CRC Handbook of Chemistry and Physics, 104th Edition, Section 8, solubility products at 25 C',
         'CRC tabular data are copyrighted. This project stores a minimal cited educational subset; do not redistribute as a standalone data table.')
    ON CONFLICT (equilibrium_code, temperature_celsius, solvent_code, dataset_version) DO UPDATE SET
        ksp_value = EXCLUDED.ksp_value
    RETURNING id, equilibrium_code, ksp_value, temperature_celsius, solvent_code, source_identifier
)
INSERT INTO chemistry.solubility_constants (equilibrium_id, constant_type, k_value, temperature_celsius, solvent_code, source_identifier)
SELECT id, 'KSP', ksp_value, temperature_celsius, solvent_code, source_identifier
FROM inserted
ON CONFLICT (equilibrium_id, constant_type, temperature_celsius, solvent_code) DO NOTHING;

INSERT INTO chemistry.solubility_dissolution_terms (equilibrium_id, term_order, species_code, formula, charge, coefficient)
SELECT e.id, t.term_order, t.species_code, t.formula, t.charge, t.coefficient
FROM chemistry.solubility_equilibria e
JOIN (VALUES
    ('KSP-CACO3-CALCITE', 1, 'SPEC-CA-2PLUS', 'Ca^2+', 2, 1),
    ('KSP-CACO3-CALCITE', 2, 'SPEC-CO3-2MINUS', 'CO3^2-', -2, 1),
    ('KSP-MG-OH-2', 1, 'SPEC-MG-2PLUS', 'Mg^2+', 2, 1),
    ('KSP-MG-OH-2', 2, 'SPEC-OH-MINUS', 'OH-', -1, 2),
    ('KSP-AL-OH-3', 1, 'SPEC-AL-3PLUS', 'Al^3+', 3, 1),
    ('KSP-AL-OH-3', 2, 'SPEC-OH-MINUS', 'OH-', -1, 3)
) AS t(equilibrium_code, term_order, species_code, formula, charge, coefficient)
    ON e.equilibrium_code = t.equilibrium_code
ON CONFLICT (equilibrium_id, species_code) DO NOTHING;
