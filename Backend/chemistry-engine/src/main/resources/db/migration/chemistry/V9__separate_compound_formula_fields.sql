-- V9: Separate normalized_formula from composition_formula (Hill notation)
ALTER TABLE chemistry.compounds ADD COLUMN composition_formula VARCHAR(255);

-- Backfill composition_formula based on elemental composition (Hill notation)
-- Ethanol & Dimethyl ether -> C2H6O
UPDATE chemistry.compounds SET composition_formula = 'C2H6O' WHERE compound_code IN ('COMP-ETHANOL', 'COMP-DIMETHYL-ETHER');

-- Copper sulfate pentahydrate -> CuH10O9S
UPDATE chemistry.compounds SET composition_formula = 'CuH10O9S' WHERE compound_code = 'COMP-CUSO4-5H2O';

-- For all other compounds, backfill composition_formula with normalized_formula for basic compounds
UPDATE chemistry.compounds SET composition_formula = normalized_formula WHERE composition_formula IS NULL;

-- Restore normalized_formula for Ethanol & Dimethyl ether and CuSO4·5H2O
UPDATE chemistry.compounds SET normalized_formula = 'C2H5OH' WHERE compound_code = 'COMP-ETHANOL';
UPDATE chemistry.compounds SET normalized_formula = 'CH3OCH3' WHERE compound_code = 'COMP-DIMETHYL-ETHER';
UPDATE chemistry.compounds SET normalized_formula = 'CuSO4·5H2O' WHERE compound_code = 'COMP-CUSO4-5H2O';

-- Set composition_formula NOT NULL
ALTER TABLE chemistry.compounds ALTER COLUMN composition_formula SET NOT NULL;

-- Create indexes for efficient formula searching
CREATE INDEX idx_compounds_composition_formula ON chemistry.compounds(composition_formula);
CREATE INDEX idx_compounds_normalized_formula ON chemistry.compounds(normalized_formula);
