-- Flyway Migration V22: Correct Acid-Base Dissociation Semantics

UPDATE chemistry.chemical_species
SET dissociation_behavior = 'AUTOIONIZING_SOLVENT'
WHERE species_code = 'SPEC-H2O';

UPDATE chemistry.chemical_species
SET dissociation_behavior = 'NOT_APPLICABLE'
WHERE species_code IN (
  'SPEC-H3O-PLUS',
  'SPEC-OH-MINUS',
  'SPEC-H-PLUS',
  'SPEC-NA-PLUS',
  'SPEC-CL-MINUS',
  'SPEC-NH4-PLUS',
  'SPEC-HSO4-MINUS',
  'SPEC-SO4-2MINUS',
  'SPEC-CH3COO-MINUS',
  'SPEC-HCO3-MINUS',
  'SPEC-CO3-2MINUS'
);
