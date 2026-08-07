-- Flyway Migration V20: Seed Acid-Base Reference Foundation Data

INSERT INTO chemistry.acid_base_dataset_versions (version, source_identifier, citation, license, evidence_status)
VALUES ('1.0.0', 'IUPAC/CRC-HANDBOOK', 'CRC Handbook of Chemistry and Physics, 104th Ed. (2023-2024)', 'Public Academic / IUPAC Standard Data', 'PEER_REVIEWED_EXPERIMENTAL')
ON CONFLICT (version) DO NOTHING;

INSERT INTO chemistry.chemical_species (species_code, name, formula, kind, charge, primary_role, associated_compound_code)
VALUES
  ('SPEC-H2O', 'Water', 'H2O', 'SOLVENT', 0, 'AMPHIPROTIC', 'COMP-H2O'),
  ('SPEC-H3O-PLUS', 'Hydronium', 'H3O+', 'CATION', 1, 'ACID', NULL),
  ('SPEC-OH-MINUS', 'Hydroxide', 'OH-', 'ANION', -1, 'BASE', NULL),
  ('SPEC-H-PLUS', 'Hydrogen Ion', 'H+', 'CATION', 1, 'ACID', NULL),
  ('SPEC-NA-PLUS', 'Sodium Ion', 'Na+', 'CATION', 1, 'NEUTRAL_SPECIES', NULL),
  ('SPEC-CL-MINUS', 'Chloride Ion', 'Cl-', 'ANION', -1, 'NEUTRAL_SPECIES', NULL),
  ('SPEC-NH4-PLUS', 'Ammonium', 'NH4+', 'CATION', 1, 'ACID', NULL),
  ('SPEC-NH3', 'Ammonia', 'NH3', 'NEUTRAL_COMPOUND', 0, 'BASE', 'COMP-NH3'),
  ('SPEC-HCL', 'Hydrochloric Acid', 'HCl', 'NEUTRAL_COMPOUND', 0, 'STRONG_ELECTROLYTE', 'COMP-HCL'),
  ('SPEC-HNO3', 'Nitric Acid', 'HNO3', 'NEUTRAL_COMPOUND', 0, 'STRONG_ELECTROLYTE', 'COMP-HNO3'),
  ('SPEC-H2SO4', 'Sulfuric Acid', 'H2SO4', 'NEUTRAL_COMPOUND', 0, 'STRONG_ELECTROLYTE', 'COMP-H2SO4'),
  ('SPEC-HSO4-MINUS', 'Hydrogen Sulfate', 'HSO4-', 'ANION', -1, 'AMPHIPROTIC', NULL),
  ('SPEC-SO4-2MINUS', 'Sulfate', 'SO4^2-', 'ANION', -2, 'BASE', NULL),
  ('SPEC-CH3COOH', 'Acetic Acid', 'CH3COOH', 'NEUTRAL_COMPOUND', 0, 'ACID', 'COMP-CH3COOH'),
  ('SPEC-CH3COO-MINUS', 'Acetate', 'CH3COO-', 'ANION', -1, 'BASE', NULL),
  ('SPEC-H2CO3', 'Carbonic Acid', 'H2CO3', 'NEUTRAL_COMPOUND', 0, 'ACID', 'COMP-H2CO3'),
  ('SPEC-HCO3-MINUS', 'Bicarbonate', 'HCO3-', 'ANION', -1, 'AMPHIPROTIC', NULL),
  ('SPEC-CO3-2MINUS', 'Carbonate', 'CO3^2-', 'ANION', -2, 'BASE', NULL)
ON CONFLICT (species_code) DO NOTHING;

INSERT INTO chemistry.conjugate_pairs (pair_code, acid_species_code, base_species_code)
VALUES
  ('PAIR-H2O-OH', 'SPEC-H2O', 'SPEC-OH-MINUS'),
  ('PAIR-H3O-H2O', 'SPEC-H3O-PLUS', 'SPEC-H2O'),
  ('PAIR-NH4-NH3', 'SPEC-NH4-PLUS', 'SPEC-NH3'),
  ('PAIR-CH3COOH-CH3COO', 'SPEC-CH3COOH', 'SPEC-CH3COO-MINUS'),
  ('PAIR-H2CO3-HCO3', 'SPEC-H2CO3', 'SPEC-HCO3-MINUS'),
  ('PAIR-HCO3-CO3', 'SPEC-HCO3-MINUS', 'SPEC-CO3-2MINUS'),
  ('PAIR-H2SO4-HSO4', 'SPEC-H2SO4', 'SPEC-HSO4-MINUS'),
  ('PAIR-HSO4-SO4', 'SPEC-HSO4-MINUS', 'SPEC-SO4-2MINUS')
ON CONFLICT (pair_code) DO NOTHING;

INSERT INTO chemistry.dissociation_steps (acid_species_code, deprotonated_species_code, step_number)
VALUES
  ('SPEC-H2O', 'SPEC-OH-MINUS', 1),
  ('SPEC-CH3COOH', 'SPEC-CH3COO-MINUS', 1),
  ('SPEC-H2CO3', 'SPEC-HCO3-MINUS', 1),
  ('SPEC-HCO3-MINUS', 'SPEC-CO3-2MINUS', 2),
  ('SPEC-H2SO4', 'SPEC-HSO4-MINUS', 1),
  ('SPEC-HSO4-MINUS', 'SPEC-SO4-2MINUS', 2),
  ('SPEC-NH4-PLUS', 'SPEC-NH3', 1)
ON CONFLICT (acid_species_code, step_number) DO NOTHING;

INSERT INTO chemistry.equilibrium_constants (species_code, type, step_number, k_value, p_value, temperature_celsius, solvent_code, is_strong_electrolyte)
VALUES
  ('SPEC-H2O', 'KW', 1, 0.00000000000001, 14.0000, 25.0, 'COMP-H2O', FALSE),
  ('SPEC-CH3COOH', 'KA', 1, 0.0000175, 4.7567, 25.0, 'COMP-H2O', FALSE),
  ('SPEC-CH3COO-MINUS', 'KB', 1, 0.000000000571, 9.2433, 25.0, 'COMP-H2O', FALSE),
  ('SPEC-H2CO3', 'KA', 1, 0.000000445, 6.3516, 25.0, 'COMP-H2O', FALSE),
  ('SPEC-HCO3-MINUS', 'KA', 2, 0.0000000000469, 10.3288, 25.0, 'COMP-H2O', FALSE),
  ('SPEC-HCO3-MINUS', 'KB', 1, 0.0000000225, 7.6484, 25.0, 'COMP-H2O', FALSE),
  ('SPEC-CO3-2MINUS', 'KB', 1, 0.00000213, 3.6712, 25.0, 'COMP-H2O', FALSE),
  ('SPEC-NH4-PLUS', 'KA', 1, 0.000000000569, 9.2449, 25.0, 'COMP-H2O', FALSE),
  ('SPEC-NH3', 'KB', 1, 0.0000176, 4.7545, 25.0, 'COMP-H2O', FALSE),
  ('SPEC-HSO4-MINUS', 'KA', 2, 0.0102, 1.9914, 25.0, 'COMP-H2O', FALSE),
  ('SPEC-HCL', 'KA', 1, NULL, NULL, 25.0, 'COMP-H2O', TRUE),
  ('SPEC-HNO3', 'KA', 1, NULL, NULL, 25.0, 'COMP-H2O', TRUE),
  ('SPEC-H2SO4', 'KA', 1, NULL, NULL, 25.0, 'COMP-H2O', TRUE)
ON CONFLICT (species_code, type, step_number, temperature_celsius, solvent_code) DO NOTHING;
