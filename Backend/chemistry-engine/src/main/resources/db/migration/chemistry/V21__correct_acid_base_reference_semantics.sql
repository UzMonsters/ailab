-- Flyway Migration V21: Correct Acid-Base Reference Semantics & Separate Role from Behavior

-- 1. Add dissociation_behavior column to chemical_species
ALTER TABLE chemistry.chemical_species
ADD COLUMN IF NOT EXISTS dissociation_behavior VARCHAR(32) NOT NULL DEFAULT 'WEAK_ELECTROLYTE';

CREATE INDEX IF NOT EXISTS idx_chemical_species_behavior ON chemistry.chemical_species(dissociation_behavior);

-- 2. Update existing strong electrolytes with primary role and dissociation behavior
UPDATE chemistry.chemical_species
SET primary_role = 'ACID', dissociation_behavior = 'STRONG_ELECTROLYTE'
WHERE species_code IN ('SPEC-HCL', 'SPEC-HNO3', 'SPEC-H2SO4');

UPDATE chemistry.chemical_species
SET dissociation_behavior = 'STRONG_ELECTROLYTE'
WHERE species_code IN ('SPEC-H3O-PLUS', 'SPEC-OH-MINUS', 'SPEC-NA-PLUS', 'SPEC-CL-MINUS');

-- 3. Add Sodium Hydroxide (SPEC-NAOH)
INSERT INTO chemistry.chemical_species (species_code, name, formula, kind, charge, primary_role, dissociation_behavior, associated_compound_code)
VALUES ('SPEC-NAOH', 'Sodium Hydroxide', 'NaOH', 'NEUTRAL_COMPOUND', 0, 'BASE', 'STRONG_ELECTROLYTE', 'COMP-NAOH')
ON CONFLICT (species_code) DO UPDATE SET
  primary_role = 'BASE',
  dissociation_behavior = 'STRONG_ELECTROLYTE',
  associated_compound_code = 'COMP-NAOH';

-- 4. Delete legacy null k_value strong-electrolyte entries from equilibrium_constants
DELETE FROM chemistry.equilibrium_constants WHERE k_value IS NULL;

-- 5. Update dataset provenance metadata with explicit CRC / IUPAC separate licenses
UPDATE chemistry.acid_base_dataset_versions
SET citation = 'CRC Handbook of Chemistry and Physics, 104th Ed. (2023-2024), Haynes, W.M., Ed., CRC Press; IUPAC Critical Evaluation of Equilibrium Constants in Aqueous System.',
    license = 'CRC Handbook Citation / IUPAC Recommended Standards (Internal Calculation for Derived pK Values)'
WHERE version = '1.0.0';
