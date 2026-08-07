-- Seed Reaction Catalog Versions
INSERT INTO chemistry.reaction_catalog_versions (version_code, description, publication_date)
VALUES ('reaction-core-v1.0.0', 'Core Educational Reaction Database Catalogue', '2026-08-06');

-- Seed Reaction Source Documents
INSERT INTO chemistry.reaction_source_documents (source_id, source_type, issuer, title, edition, publication_date, access_date, coverage, fields_supplied, language, source_reference, licensing_note)
VALUES
('CRC-HANDBOOK-104', 'TEXTBOOK', 'CRC Press', 'CRC Handbook of Chemistry and Physics, 104th Edition', '104th', '2023', '2026-08-06', 'Standard inorganic and organic chemical reactions', 'equation,reactionName,catalysts,conditions,directionality', 'en', 'CRC-104-RXN-CATALOG', 'Public scientific reference metadata'),
('NIST-WEBBOOK-2025', 'AUTHORITATIVE_DATABASE', 'National Institute of Standards and Technology (NIST)', 'NIST Chemistry WebBook, SRD 69', '2025', '2025', '2026-08-06', 'Thermochemical and kinetic reference reactions', 'equation,directionality,speciesStates', 'en', 'NIST-SRD-69', 'US Government open reference dataset');

-- Seed Reaction Type Definitions
INSERT INTO chemistry.reaction_type_definitions (type_code, name, description, display_order)
VALUES
('SYNTHESIS', 'Synthesis / Combination', 'Two or more substances combine to form a single product', 1),
('DECOMPOSITION', 'Decomposition', 'A single compound breaks down into two or more simpler substances', 2),
('COMBUSTION', 'Combustion', 'Reaction with oxygen gas releasing energy and oxides', 3),
('SINGLE_DISPLACEMENT', 'Single Displacement', 'One element replaces another in a compound', 4),
('DOUBLE_DISPLACEMENT', 'Double Displacement', 'Exchange of ions between two compounds', 5),
('ACID_BASE_NEUTRALIZATION', 'Acid-Base Neutralization', 'Acid reacts with a base producing water and a salt', 6),
('PRECIPITATION', 'Precipitation', 'Formation of an insoluble solid precipitate in solution', 7),
('GAS_EVOLUTION', 'Gas Evolution', 'Reaction producing one or more gas products', 8),
('REDOX', 'Redox', 'Oxidation-reduction electron transfer reaction', 9),
('OXIDATION', 'Oxidation', 'Gain of oxygen or loss of electrons', 10),
('REDUCTION', 'Reduction', 'Loss of oxygen or gain of electrons', 11),
('HYDRATION', 'Hydration', 'Addition of water molecules to a chemical entity', 12),
('DEHYDRATION', 'Dehydration', 'Removal of water molecules from a chemical entity', 13),
('HYDROLYSIS', 'Hydrolysis', 'Cleavage of chemical bonds by addition of water', 14),
('REVERSIBLE_REACTION', 'Reversible Reaction', 'Reaction capable of proceeding in both forward and reverse directions', 15),
('OTHER', 'Other / Unclassified', 'General or unclassified chemical reaction', 16);

-- Seed Reactions
INSERT INTO chemistry.reactions (id, reaction_code, primary_name, original_equation, normalized_equation, canonical_balanced_equation, reaction_signature, directionality, catalog_version_id, source_document_id, provenance_notes)
VALUES
('11111111-1111-1111-1111-111111111101', 'RXN-WATER-SYNTHESIS', 'Synthesis of Water', '2H2 + O2 -> 2H2O', '2H2 + O2 -> 2H2O', '2H2 + O2 -> 2H2O', '2*COMP-H2+1*COMP-O2->2*COMP-H2O[IRREVERSIBLE]', 'IRREVERSIBLE', 'reaction-core-v1.0.0', 'CRC-HANDBOOK-104', 'Standard thermochemical reference reaction'),
('11111111-1111-1111-1111-111111111102', 'RXN-H2O2-DECOMP', 'Decomposition of Hydrogen Peroxide', '2H2O2 -> 2H2O + O2', '2H2O2 -> 2H2O + O2', '2H2O2 -> 2H2O + O2', '2*COMP-H2O2->2*COMP-H2O+1*COMP-O2[IRREVERSIBLE]', 'IRREVERSIBLE', 'reaction-core-v1.0.0', 'CRC-HANDBOOK-104', 'Catalyzed decomposition reaction'),
('11111111-1111-1111-1111-111111111103', 'RXN-METHANE-COMBUSTION', 'Combustion of Methane', 'CH4 + 2O2 -> CO2 + 2H2O', 'CH4 + 2O2 -> CO2 + 2H2O', 'CH4 + 2O2 -> CO2 + 2H2O', '1*COMP-CH4+2*COMP-O2->1*COMP-CO2+2*COMP-H2O[IRREVERSIBLE]', 'IRREVERSIBLE', 'reaction-core-v1.0.0', 'CRC-HANDBOOK-104', 'Complete hydrocarbon combustion'),
('11111111-1111-1111-1111-111111111104', 'RXN-NEUT-HCL-NAOH', 'Hydrochloric Acid Neutralization by Sodium Hydroxide', 'HCl + NaOH -> NaCl + H2O', 'HCl + NaOH -> NaCl + H2O', 'HCl + NaOH -> NaCl + H2O', '1*COMP-HCL+1*COMP-NAOH->1*COMP-H2O+1*COMP-NACL[IRREVERSIBLE]', 'IRREVERSIBLE', 'reaction-core-v1.0.0', 'CRC-HANDBOOK-104', 'Standard acid-base titration benchmark'),
('11111111-1111-1111-1111-111111111105', 'RXN-NAHCO3-DECOMP', 'Thermal Decomposition of Sodium Bicarbonate', '2NaHCO3 -> Na2CO3 + CO2 + H2O', '2NaHCO3 -> Na2CO3 + CO2 + H2O', '2NaHCO3 -> Na2CO3 + CO2 + H2O', '2*COMP-NAHCO3->1*COMP-CO2+1*COMP-H2O+1*COMP-NA2CO3[IRREVERSIBLE]', 'IRREVERSIBLE', 'reaction-core-v1.0.0', 'CRC-HANDBOOK-104', 'Thermal decomposition benchmark'),
('11111111-1111-1111-1111-111111111106', 'RXN-CA-OH-2-CO2', 'Reaction of Calcium Hydroxide with Carbon Dioxide', 'Ca(OH)2 + CO2 -> CaCO3 + H2O', 'Ca(OH)2 + CO2 -> CaCO3 + H2O', 'Ca(OH)2 + CO2 -> CaCO3 + H2O', '1*COMP-CA-OH-2+1*COMP-CO2->1*COMP-CACO3+1*COMP-H2O[IRREVERSIBLE]', 'IRREVERSIBLE', 'reaction-core-v1.0.0', 'CRC-HANDBOOK-104', 'Limewater test benchmark'),
('11111111-1111-1111-1111-111111111107', 'RXN-ETHANOL-COMBUSTION', 'Combustion of Ethanol', 'C2H5OH + 3O2 -> 2CO2 + 3H2O', 'C2H5OH + 3O2 -> 2CO2 + 3H2O', 'C2H5OH + 3O2 -> 2CO2 + 3H2O', '1*COMP-ETHANOL+3*COMP-O2->2*COMP-CO2+3*COMP-H2O[IRREVERSIBLE]', 'IRREVERSIBLE', 'reaction-core-v1.0.0', 'CRC-HANDBOOK-104', 'Isomer specific reaction for ethanol'),
('11111111-1111-1111-1111-111111111108', 'RXN-DIMETHYL-ETHER-COMBUSTION', 'Combustion of Dimethyl Ether', 'CH3OCH3 + 3O2 -> 2CO2 + 3H2O', 'CH3OCH3 + 3O2 -> 2CO2 + 3H2O', 'CH3OCH3 + 3O2 -> 2CO2 + 3H2O', '1*COMP-DIMETHYL-ETHER+3*COMP-O2->2*COMP-CO2+3*COMP-H2O[IRREVERSIBLE]', 'IRREVERSIBLE', 'reaction-core-v1.0.0', 'CRC-HANDBOOK-104', 'Isomer specific reaction for dimethyl ether'),
('11111111-1111-1111-1111-111111111109', 'RXN-CUSO4-HYDRATION', 'Hydration of Copper(II) Sulfate', 'CuSO4 + 5H2O -> CuSO4·5H2O', 'CuSO4 + 5H2O -> CuSO4·5H2O', 'CuSO4 + 5H2O -> CuSO4·5H2O', '1*COMP-CUSO4+5*COMP-H2O->1*COMP-CUSO4-5H2O[REVERSIBLE]', 'REVERSIBLE', 'reaction-core-v1.0.0', 'CRC-HANDBOOK-104', 'Hydrate formation benchmark'),
('11111111-1111-1111-1111-111111111110', 'RXN-CUSO4-DEHYDRATION', 'Dehydration of Copper(II) Sulfate Pentahydrate', 'CuSO4·5H2O -> CuSO4 + 5H2O', 'CuSO4·5H2O -> CuSO4 + 5H2O', 'CuSO4·5H2O -> CuSO4 + 5H2O', '1*COMP-CUSO4-5H2O->1*COMP-CUSO4+5*COMP-H2O[REVERSIBLE]', 'REVERSIBLE', 'reaction-core-v1.0.0', 'CRC-HANDBOOK-104', 'Thermal dehydration benchmark'),
('11111111-1111-1111-1111-111111111111', 'RXN-HABER-PROCESS', 'Synthesis of Ammonia (Haber-Bosch Process)', 'N2 + 3H2 -> 2NH3', 'N2 + 3H2 -> 2NH3', 'N2 + 3H2 -> 2NH3', '3*COMP-H2+1*COMP-N2->2*COMP-NH3[REVERSIBLE]', 'REVERSIBLE', 'reaction-core-v1.0.0', 'CRC-HANDBOOK-104', 'Haber-Bosch reversible industrial reaction'),
('11111111-1111-1111-1111-111111111112', 'RXN-ETHANE-COMBUSTION', 'Combustion of Ethane', '2C2H6 + 7O2 -> 4CO2 + 6H2O', '2C2H6 + 7O2 -> 4CO2 + 6H2O', '2C2H6 + 7O2 -> 4CO2 + 6H2O', '2*COMP-C2H6+7*COMP-O2->4*COMP-CO2+6*COMP-H2O[IRREVERSIBLE]', 'IRREVERSIBLE', 'reaction-core-v1.0.0', 'NIST-WEBBOOK-2025', 'Hydrocarbon oxidation'),
('11111111-1111-1111-1111-111111111113', 'RXN-PROPANE-COMBUSTION', 'Combustion of Propane', 'C3H8 + 5O2 -> 3CO2 + 4H2O', 'C3H8 + 5O2 -> 3CO2 + 4H2O', 'C3H8 + 5O2 -> 3CO2 + 4H2O', '1*COMP-C3H8+5*COMP-O2->3*COMP-CO2+4*COMP-H2O[IRREVERSIBLE]', 'IRREVERSIBLE', 'reaction-core-v1.0.0', 'NIST-WEBBOOK-2025', 'Alcane combustion'),
('11111111-1111-1111-1111-111111111114', 'RXN-BUTANE-COMBUSTION', 'Combustion of Butane', '2C4H10 + 13O2 -> 8CO2 + 10H2O', '2C4H10 + 13O2 -> 8CO2 + 10H2O', '2C4H10 + 13O2 -> 8CO2 + 10H2O', '2*COMP-C4H10+13*COMP-O2->8*COMP-CO2+10*COMP-H2O[IRREVERSIBLE]', 'IRREVERSIBLE', 'reaction-core-v1.0.0', 'NIST-WEBBOOK-2025', 'Alcane combustion'),
('11111111-1111-1111-1111-111111111115', 'RXN-ETHYLENE-COMBUSTION', 'Combustion of Ethylene', 'C2H4 + 3O2 -> 2CO2 + 2H2O', 'C2H4 + 3O2 -> 2CO2 + 2H2O', 'C2H4 + 3O2 -> 2CO2 + 2H2O', '1*COMP-C2H4+3*COMP-O2->2*COMP-CO2+2*COMP-H2O[IRREVERSIBLE]', 'IRREVERSIBLE', 'reaction-core-v1.0.0', 'NIST-WEBBOOK-2025', 'Alkene oxidation'),
('11111111-1111-1111-1111-111111111116', 'RXN-ACETYLENE-COMBUSTION', 'Combustion of Acetylene', '2C2H2 + 5O2 -> 4CO2 + 2H2O', '2C2H2 + 5O2 -> 4CO2 + 2H2O', '2C2H2 + 5O2 -> 4CO2 + 2H2O', '2*COMP-C2H2+5*COMP-O2->4*COMP-CO2+2*COMP-H2O[IRREVERSIBLE]', 'IRREVERSIBLE', 'reaction-core-v1.0.0', 'NIST-WEBBOOK-2025', 'Oxy-acetylene torch reaction'),
('11111111-1111-1111-1111-111111111117', 'RXN-BENZENE-COMBUSTION', 'Combustion of Benzene', '2C6H6 + 15O2 -> 12CO2 + 6H2O', '2C6H6 + 15O2 -> 12CO2 + 6H2O', '2C6H6 + 15O2 -> 12CO2 + 6H2O', '2*COMP-C6H6+15*COMP-O2->12*COMP-CO2+6*COMP-H2O[IRREVERSIBLE]', 'IRREVERSIBLE', 'reaction-core-v1.0.0', 'NIST-WEBBOOK-2025', 'Aromatic hydrocarbon combustion'),
('11111111-1111-1111-1111-111111111118', 'RXN-H2SO4-NAOH-NEUT', 'Neutralization of Sulfuric Acid with Sodium Hydroxide', 'H2SO4 + 2NaOH -> Na2SO4 + 2H2O', 'H2SO4 + 2NaOH -> Na2SO4 + 2H2O', 'H2SO4 + 2NaOH -> Na2SO4 + 2H2O', '1*COMP-H2SO4+2*COMP-NAOH->2*COMP-H2O+1*COMP-NA2SO4[IRREVERSIBLE]', 'IRREVERSIBLE', 'reaction-core-v1.0.0', 'CRC-HANDBOOK-104', 'Acid-base neutralization'),
('11111111-1111-1111-1111-111111111119', 'RXN-H2SO4-MGOH2-NEUT', 'Neutralization of Sulfuric Acid with Magnesium Hydroxide', 'H2SO4 + Mg(OH)2 -> MgSO4 + 2H2O', 'H2SO4 + Mg(OH)2 -> MgSO4 + 2H2O', 'H2SO4 + Mg(OH)2 -> MgSO4 + 2H2O', '1*COMP-H2SO4+1*COMP-MG-OH-2->2*COMP-H2O+1*COMP-MGSO4[IRREVERSIBLE]', 'IRREVERSIBLE', 'reaction-core-v1.0.0', 'CRC-HANDBOOK-104', 'Insoluble base neutralization'),
('11111111-1111-1111-1111-111111111120', 'RXN-CACO3-HCL', 'Reaction of Calcium Carbonate with Hydrochloric Acid', 'CaCO3 + 2HCl -> CaCl2 + CO2 + H2O', 'CaCO3 + 2HCl -> CaCl2 + CO2 + H2O', 'CaCO3 + 2HCl -> CaCl2 + CO2 + H2O', '1*COMP-CACO3+2*COMP-HCL->1*COMP-CACL2+1*COMP-CO2+1*COMP-H2O[IRREVERSIBLE]', 'IRREVERSIBLE', 'reaction-core-v1.0.0', 'CRC-HANDBOOK-104', 'Effervescence gas evolution reaction'),
('11111111-1111-1111-1111-111111111121', 'RXN-MGOH2-HCL', 'Reaction of Magnesium Hydroxide with Hydrochloric Acid', 'Mg(OH)2 + 2HCl -> MgCl2 + 2H2O', 'Mg(OH)2 + 2HCl -> MgCl2 + 2H2O', 'Mg(OH)2 + 2HCl -> MgCl2 + 2H2O', '2*COMP-HCL+1*COMP-MG-OH-2->2*COMP-H2O+1*COMP-MGCL2[IRREVERSIBLE]', 'IRREVERSIBLE', 'reaction-core-v1.0.0', 'CRC-HANDBOOK-104', 'Antacid stomach reaction model'),
('11111111-1111-1111-1111-111111111122', 'RXN-GLUCOSE-COMBUSTION', 'Combustion of Glucose', 'C6H12O6 + 6O2 -> 6CO2 + 6H2O', 'C6H12O6 + 6O2 -> 6CO2 + 6H2O', 'C6H12O6 + 6O2 -> 6CO2 + 6H2O', '1*COMP-GLUCOSE+6*COMP-O2->6*COMP-CO2+6*COMP-H2O[IRREVERSIBLE]', 'IRREVERSIBLE', 'reaction-core-v1.0.0', 'CRC-HANDBOOK-104', 'Carbohydrate oxidation'),
('11111111-1111-1111-1111-111111111123', 'RXN-SUCROSE-COMBUSTION', 'Combustion of Sucrose', 'C12H22O11 + 12O2 -> 12CO2 + 11H2O', 'C12H22O11 + 12O2 -> 12CO2 + 11H2O', 'C12H22O11 + 12O2 -> 12CO2 + 11H2O', '1*COMP-SUCROSE+12*COMP-O2->12*COMP-CO2+11*COMP-H2O[IRREVERSIBLE]', 'IRREVERSIBLE', 'reaction-core-v1.0.0', 'CRC-HANDBOOK-104', 'Disaccharide oxidation'),
('11111111-1111-1111-1111-111111111124', 'RXN-CO2-H2O-SYNTHESIS', 'Formation of Carbonic Acid', 'CO2 + H2O -> H2CO3', 'CO2 + H2O -> H2CO3', 'CO2 + H2O -> H2CO3', '1*COMP-CO2+1*COMP-H2O->1*COMP-H2CO3[REVERSIBLE]', 'REVERSIBLE', 'reaction-core-v1.0.0', 'CRC-HANDBOOK-104', 'Carbonated water equilibrium'),
('11111111-1111-1111-1111-111111111125', 'RXN-H2CO3-DECOMP', 'Decomposition of Carbonic Acid', 'H2CO3 -> CO2 + H2O', 'H2CO3 -> CO2 + H2O', 'H2CO3 -> CO2 + H2O', '1*COMP-H2CO3->1*COMP-CO2+1*COMP-H2O[REVERSIBLE]', 'REVERSIBLE', 'reaction-core-v1.0.0', 'CRC-HANDBOOK-104', 'Effervescence decomposition'),
('11111111-1111-1111-1111-111111111126', 'RXN-CO-OXIDATION', 'Oxidation of Carbon Monoxide', '2CO + O2 -> 2CO2', '2CO + O2 -> 2CO2', '2CO + O2 -> 2CO2', '2*COMP-CO+1*COMP-O2->2*COMP-CO2[IRREVERSIBLE]', 'IRREVERSIBLE', 'reaction-core-v1.0.0', 'CRC-HANDBOOK-104', 'Carbon monoxide oxidation');

-- Seed Reaction Aliases
INSERT INTO chemistry.reaction_aliases (reaction_id, alias_name, alias_type) VALUES
('11111111-1111-1111-1111-111111111101', 'Hydrogen Combustion', 'COMMON'),
('11111111-1111-1111-1111-111111111102', 'Hydrogen Peroxide Disproportionation', 'SCIENTIFIC'),
('11111111-1111-1111-1111-111111111103', 'Natural Gas Combustion', 'COMMON'),
('11111111-1111-1111-1111-111111111104', 'Strong Acid Strong Base Neutralization', 'SCIENTIFIC'),
('11111111-1111-1111-1111-111111111105', 'Baking Soda Thermal Breakdown', 'COMMON'),
('11111111-1111-1111-1111-111111111106', 'Limewater Test for Carbon Dioxide', 'COMMON'),
('11111111-1111-1111-1111-111111111107', 'Ethanol Oxidation', 'COMMON'),
('11111111-1111-1111-1111-111111111108', 'DME Combustion', 'COMMON'),
('11111111-1111-1111-1111-111111111109', 'Anhydrous Copper Sulfate Hydration', 'COMMON'),
('11111111-1111-1111-1111-111111111110', 'Blue Vitriol Dehydration', 'COMMON'),
('11111111-1111-1111-1111-111111111111', 'Ammonia Synthesis', 'COMMON'),
('11111111-1111-1111-1111-111111111112', 'Ethane Oxidation', 'COMMON'),
('11111111-1111-1111-1111-111111111113', 'Propane Oxidation', 'COMMON'),
('11111111-1111-1111-1111-111111111114', 'Butane Oxidation', 'COMMON'),
('11111111-1111-1111-1111-111111111115', 'Ethene Combustion', 'SCIENTIFIC'),
('11111111-1111-1111-1111-111111111116', 'Ethyne Combustion', 'SCIENTIFIC'),
('11111111-1111-1111-1111-111111111117', 'Benzene Oxidation', 'COMMON'),
('11111111-1111-1111-1111-111111111118', 'Sulfuric Acid Neutralization', 'COMMON'),
('11111111-1111-1111-1111-111111111119', 'Milk of Magnesia Acid Reaction', 'COMMON'),
('11111111-1111-1111-1111-111111111120', 'Limestone Acid Dissolution', 'COMMON'),
('11111111-1111-1111-1111-111111111121', 'Antacid Neutralization', 'COMMON'),
('11111111-1111-1111-1111-111111111122', 'Glucose Oxidation', 'COMMON'),
('11111111-1111-1111-1111-111111111123', 'Table Sugar Oxidation', 'COMMON'),
('11111111-1111-1111-1111-111111111124', 'Carbon Dioxide Hydration', 'COMMON'),
('11111111-1111-1111-1111-111111111125', 'Carbonic Acid Breakdown', 'COMMON'),
('11111111-1111-1111-1111-111111111126', 'Carbon Monoxide Combustion', 'COMMON');

-- Seed Reaction Terms
INSERT INTO chemistry.reaction_terms (reaction_id, compound_id, compound_code, formula, side, coefficient, species_state, term_order) VALUES
-- RXN 1
('11111111-1111-1111-1111-111111111101', '650b152a-3a54-334b-9006-627007c122b0', 'COMP-H2', 'H2', 'REACTANT', 2, 'GAS', 1),
('11111111-1111-1111-1111-111111111101', '6c11ca8c-0546-3a30-8558-92fb83efacb6', 'COMP-O2', 'O2', 'REACTANT', 1, 'GAS', 2),
('11111111-1111-1111-1111-111111111101', 'f38e4f83-fe95-3243-887e-f448d74ef717', 'COMP-H2O', 'H2O', 'PRODUCT', 2, 'GAS', 3),

-- RXN 2
('11111111-1111-1111-1111-111111111102', '25aa9b6b-0e15-3c35-905d-9c056f332a8a', 'COMP-H2O2', 'H2O2', 'REACTANT', 2, 'AQUEOUS', 1),
('11111111-1111-1111-1111-111111111102', 'f38e4f83-fe95-3243-887e-f448d74ef717', 'COMP-H2O', 'H2O', 'PRODUCT', 2, 'LIQUID', 2),
('11111111-1111-1111-1111-111111111102', '6c11ca8c-0546-3a30-8558-92fb83efacb6', 'COMP-O2', 'O2', 'PRODUCT', 1, 'GAS', 3),

-- RXN 3
('11111111-1111-1111-1111-111111111103', '095d8580-8beb-3e71-800d-add10b6590ae', 'COMP-CH4', 'CH4', 'REACTANT', 1, 'GAS', 1),
('11111111-1111-1111-1111-111111111103', '6c11ca8c-0546-3a30-8558-92fb83efacb6', 'COMP-O2', 'O2', 'REACTANT', 2, 'GAS', 2),
('11111111-1111-1111-1111-111111111103', 'baa7a54f-9112-3a55-8cfe-8b400d31a0b6', 'COMP-CO2', 'CO2', 'PRODUCT', 1, 'GAS', 3),
('11111111-1111-1111-1111-111111111103', 'f38e4f83-fe95-3243-887e-f448d74ef717', 'COMP-H2O', 'H2O', 'PRODUCT', 2, 'GAS', 4),

-- RXN 4
('11111111-1111-1111-1111-111111111104', '3c7d0fda-9a6e-3b79-aebb-1a41aecc6c09', 'COMP-HCL', 'HCl', 'REACTANT', 1, 'AQUEOUS', 1),
('11111111-1111-1111-1111-111111111104', 'c6258e3d-0693-3248-94bc-8d455560be75', 'COMP-NAOH', 'NaOH', 'REACTANT', 1, 'AQUEOUS', 2),
('11111111-1111-1111-1111-111111111104', '95b7e3d3-ce4a-3239-b362-8a2bd4252950', 'COMP-NACL', 'NaCl', 'PRODUCT', 1, 'AQUEOUS', 3),
('11111111-1111-1111-1111-111111111104', 'f38e4f83-fe95-3243-887e-f448d74ef717', 'COMP-H2O', 'H2O', 'PRODUCT', 1, 'LIQUID', 4),

-- RXN 5
('11111111-1111-1111-1111-111111111105', '41ad250b-399a-344c-94b2-13a3a63bc28d', 'COMP-NAHCO3', 'NaHCO3', 'REACTANT', 2, 'SOLID', 1),
('11111111-1111-1111-1111-111111111105', '5c5ee053-0520-3976-85d9-78b89adff2e9', 'COMP-NA2CO3', 'Na2CO3', 'PRODUCT', 1, 'SOLID', 2),
('11111111-1111-1111-1111-111111111105', 'baa7a54f-9112-3a55-8cfe-8b400d31a0b6', 'COMP-CO2', 'CO2', 'PRODUCT', 1, 'GAS', 3),
('11111111-1111-1111-1111-111111111105', 'f38e4f83-fe95-3243-887e-f448d74ef717', 'COMP-H2O', 'H2O', 'PRODUCT', 1, 'GAS', 4),

-- RXN 6
('11111111-1111-1111-1111-111111111106', 'b0e1b520-700c-3136-a849-6fb348890d68', 'COMP-CA-OH-2', 'Ca(OH)2', 'REACTANT', 1, 'AQUEOUS', 1),
('11111111-1111-1111-1111-111111111106', 'baa7a54f-9112-3a55-8cfe-8b400d31a0b6', 'COMP-CO2', 'CO2', 'REACTANT', 1, 'GAS', 2),
('11111111-1111-1111-1111-111111111106', 'afc3cd4b-bab8-379c-91fe-d13594d7bbde', 'COMP-CACO3', 'CaCO3', 'PRODUCT', 1, 'SOLID', 3),
('11111111-1111-1111-1111-111111111106', 'f38e4f83-fe95-3243-887e-f448d74ef717', 'COMP-H2O', 'H2O', 'PRODUCT', 1, 'LIQUID', 4),

-- RXN 7
('11111111-1111-1111-1111-111111111107', '10b0b074-d84b-3e34-8392-20f74663472d', 'COMP-ETHANOL', 'C2H5OH', 'REACTANT', 1, 'LIQUID', 1),
('11111111-1111-1111-1111-111111111107', '6c11ca8c-0546-3a30-8558-92fb83efacb6', 'COMP-O2', 'O2', 'REACTANT', 3, 'GAS', 2),
('11111111-1111-1111-1111-111111111107', 'baa7a54f-9112-3a55-8cfe-8b400d31a0b6', 'COMP-CO2', 'CO2', 'PRODUCT', 2, 'GAS', 3),
('11111111-1111-1111-1111-111111111107', 'f38e4f83-fe95-3243-887e-f448d74ef717', 'COMP-H2O', 'H2O', 'PRODUCT', 3, 'GAS', 4),

-- RXN 8
('11111111-1111-1111-1111-111111111108', 'd1461121-ac57-31a4-bb71-8ae345d27f33', 'COMP-DIMETHYL-ETHER', 'CH3OCH3', 'REACTANT', 1, 'GAS', 1),
('11111111-1111-1111-1111-111111111108', '6c11ca8c-0546-3a30-8558-92fb83efacb6', 'COMP-O2', 'O2', 'REACTANT', 3, 'GAS', 2),
('11111111-1111-1111-1111-111111111108', 'baa7a54f-9112-3a55-8cfe-8b400d31a0b6', 'COMP-CO2', 'CO2', 'PRODUCT', 2, 'GAS', 3),
('11111111-1111-1111-1111-111111111108', 'f38e4f83-fe95-3243-887e-f448d74ef717', 'COMP-H2O', 'H2O', 'PRODUCT', 3, 'GAS', 4),

-- RXN 9
('11111111-1111-1111-1111-111111111109', '6a5ccf10-c302-3c07-a19f-f345106ca4a4', 'COMP-CUSO4', 'CuSO4', 'REACTANT', 1, 'SOLID', 1),
('11111111-1111-1111-1111-111111111109', 'f38e4f83-fe95-3243-887e-f448d74ef717', 'COMP-H2O', 'H2O', 'REACTANT', 5, 'LIQUID', 2),
('11111111-1111-1111-1111-111111111109', '60927ca1-04ea-3bc8-a7dd-fc0131ff9f0b', 'COMP-CUSO4-5H2O', 'CuSO4·5H2O', 'PRODUCT', 1, 'SOLID', 3),

-- RXN 10
('11111111-1111-1111-1111-111111111110', '60927ca1-04ea-3bc8-a7dd-fc0131ff9f0b', 'COMP-CUSO4-5H2O', 'CuSO4·5H2O', 'REACTANT', 1, 'SOLID', 1),
('11111111-1111-1111-1111-111111111110', '6a5ccf10-c302-3c07-a19f-f345106ca4a4', 'COMP-CUSO4', 'CuSO4', 'PRODUCT', 1, 'SOLID', 2),
('11111111-1111-1111-1111-111111111110', 'f38e4f83-fe95-3243-887e-f448d74ef717', 'COMP-H2O', 'H2O', 'PRODUCT', 5, 'GAS', 3),

-- RXN 11
('11111111-1111-1111-1111-111111111111', '9ee0626a-3425-3597-a497-5cbf32c8570f', 'COMP-N2', 'N2', 'REACTANT', 1, 'GAS', 1),
('11111111-1111-1111-1111-111111111111', '650b152a-3a54-334b-9006-627007c122b0', 'COMP-H2', 'H2', 'REACTANT', 3, 'GAS', 2),
('11111111-1111-1111-1111-111111111111', '665185a5-e410-38fa-8e02-d4a2be56e2c7', 'COMP-NH3', 'NH3', 'PRODUCT', 2, 'GAS', 3),

-- RXN 12
('11111111-1111-1111-1111-111111111112', '41536b4f-237a-38ae-9cf9-b0099a36b773', 'COMP-C2H6', 'C2H6', 'REACTANT', 2, 'GAS', 1),
('11111111-1111-1111-1111-111111111112', '6c11ca8c-0546-3a30-8558-92fb83efacb6', 'COMP-O2', 'O2', 'REACTANT', 7, 'GAS', 2),
('11111111-1111-1111-1111-111111111112', 'baa7a54f-9112-3a55-8cfe-8b400d31a0b6', 'COMP-CO2', 'CO2', 'PRODUCT', 4, 'GAS', 3),
('11111111-1111-1111-1111-111111111112', 'f38e4f83-fe95-3243-887e-f448d74ef717', 'COMP-H2O', 'H2O', 'PRODUCT', 6, 'GAS', 4),

-- RXN 13
('11111111-1111-1111-1111-111111111113', 'dcaa9932-0ee6-37a4-be71-fb00655d1a14', 'COMP-C3H8', 'C3H8', 'REACTANT', 1, 'GAS', 1),
('11111111-1111-1111-1111-111111111113', '6c11ca8c-0546-3a30-8558-92fb83efacb6', 'COMP-O2', 'O2', 'REACTANT', 5, 'GAS', 2),
('11111111-1111-1111-1111-111111111113', 'baa7a54f-9112-3a55-8cfe-8b400d31a0b6', 'COMP-CO2', 'CO2', 'PRODUCT', 3, 'GAS', 3),
('11111111-1111-1111-1111-111111111113', 'f38e4f83-fe95-3243-887e-f448d74ef717', 'COMP-H2O', 'H2O', 'PRODUCT', 4, 'GAS', 4),

-- RXN 14
('11111111-1111-1111-1111-111111111114', '7589a123-6728-3310-b5f0-87d0d514cac5', 'COMP-C4H10', 'C4H10', 'REACTANT', 2, 'GAS', 1),
('11111111-1111-1111-1111-111111111114', '6c11ca8c-0546-3a30-8558-92fb83efacb6', 'COMP-O2', 'O2', 'REACTANT', 13, 'GAS', 2),
('11111111-1111-1111-1111-111111111114', 'baa7a54f-9112-3a55-8cfe-8b400d31a0b6', 'COMP-CO2', 'CO2', 'PRODUCT', 8, 'GAS', 3),
('11111111-1111-1111-1111-111111111114', 'f38e4f83-fe95-3243-887e-f448d74ef717', 'COMP-H2O', 'H2O', 'PRODUCT', 10, 'GAS', 4),

-- RXN 15
('11111111-1111-1111-1111-111111111115', '6b7fc3b3-25c0-3512-bd61-65bf96e79f67', 'COMP-C2H4', 'C2H4', 'REACTANT', 1, 'GAS', 1),
('11111111-1111-1111-1111-111111111115', '6c11ca8c-0546-3a30-8558-92fb83efacb6', 'COMP-O2', 'O2', 'REACTANT', 3, 'GAS', 2),
('11111111-1111-1111-1111-111111111115', 'baa7a54f-9112-3a55-8cfe-8b400d31a0b6', 'COMP-CO2', 'CO2', 'PRODUCT', 2, 'GAS', 3),
('11111111-1111-1111-1111-111111111115', 'f38e4f83-fe95-3243-887e-f448d74ef717', 'COMP-H2O', 'H2O', 'PRODUCT', 2, 'GAS', 4),

-- RXN 16
('11111111-1111-1111-1111-111111111116', '4d0d1768-0572-30f2-a1fb-6bc37b29090f', 'COMP-C2H2', 'C2H2', 'REACTANT', 2, 'GAS', 1),
('11111111-1111-1111-1111-111111111116', '6c11ca8c-0546-3a30-8558-92fb83efacb6', 'COMP-O2', 'O2', 'REACTANT', 5, 'GAS', 2),
('11111111-1111-1111-1111-111111111116', 'baa7a54f-9112-3a55-8cfe-8b400d31a0b6', 'COMP-CO2', 'CO2', 'PRODUCT', 4, 'GAS', 3),
('11111111-1111-1111-1111-111111111116', 'f38e4f83-fe95-3243-887e-f448d74ef717', 'COMP-H2O', 'H2O', 'PRODUCT', 2, 'GAS', 4),

-- RXN 17
('11111111-1111-1111-1111-111111111117', 'fb70900a-3666-3cff-ad6d-4d827638a1b7', 'COMP-C6H6', 'C6H6', 'REACTANT', 2, 'LIQUID', 1),
('11111111-1111-1111-1111-111111111117', '6c11ca8c-0546-3a30-8558-92fb83efacb6', 'COMP-O2', 'O2', 'REACTANT', 15, 'GAS', 2),
('11111111-1111-1111-1111-111111111117', 'baa7a54f-9112-3a55-8cfe-8b400d31a0b6', 'COMP-CO2', 'CO2', 'PRODUCT', 12, 'GAS', 3),
('11111111-1111-1111-1111-111111111117', 'f38e4f83-fe95-3243-887e-f448d74ef717', 'COMP-H2O', 'H2O', 'PRODUCT', 6, 'GAS', 4),

-- RXN 18
('11111111-1111-1111-1111-111111111118', '025e3c8a-cba6-39cd-bf57-58218839b82e', 'COMP-H2SO4', 'H2SO4', 'REACTANT', 1, 'AQUEOUS', 1),
('11111111-1111-1111-1111-111111111118', 'c6258e3d-0693-3248-94bc-8d455560be75', 'COMP-NAOH', 'NaOH', 'REACTANT', 2, 'AQUEOUS', 2),
('11111111-1111-1111-1111-111111111118', 'b77d9820-d830-3461-902b-bbe170a40038', 'COMP-NA2SO4', 'Na2SO4', 'PRODUCT', 1, 'AQUEOUS', 3),
('11111111-1111-1111-1111-111111111118', 'f38e4f83-fe95-3243-887e-f448d74ef717', 'COMP-H2O', 'H2O', 'PRODUCT', 2, 'LIQUID', 4),

-- RXN 19
('11111111-1111-1111-1111-111111111119', '025e3c8a-cba6-39cd-bf57-58218839b82e', 'COMP-H2SO4', 'H2SO4', 'REACTANT', 1, 'AQUEOUS', 1),
('11111111-1111-1111-1111-111111111119', '8c611aaa-209c-3fe5-942e-6b2777fc75c7', 'COMP-MG-OH-2', 'Mg(OH)2', 'REACTANT', 1, 'SOLID', 2),
('11111111-1111-1111-1111-111111111119', 'f8a18806-7192-35ec-af10-9bd0afabcd91', 'COMP-MGSO4', 'MgSO4', 'PRODUCT', 1, 'AQUEOUS', 3),
('11111111-1111-1111-1111-111111111119', 'f38e4f83-fe95-3243-887e-f448d74ef717', 'COMP-H2O', 'H2O', 'PRODUCT', 2, 'LIQUID', 4),

-- RXN 20
('11111111-1111-1111-1111-111111111120', 'afc3cd4b-bab8-379c-91fe-d13594d7bbde', 'COMP-CACO3', 'CaCO3', 'REACTANT', 1, 'SOLID', 1),
('11111111-1111-1111-1111-111111111120', '3c7d0fda-9a6e-3b79-aebb-1a41aecc6c09', 'COMP-HCL', 'HCl', 'REACTANT', 2, 'AQUEOUS', 2),
('11111111-1111-1111-1111-111111111120', '1c7585e5-613d-3411-a3e5-cf08960aae4a', 'COMP-CACL2', 'CaCl2', 'PRODUCT', 1, 'AQUEOUS', 3),
('11111111-1111-1111-1111-111111111120', 'baa7a54f-9112-3a55-8cfe-8b400d31a0b6', 'COMP-CO2', 'CO2', 'PRODUCT', 1, 'GAS', 4),
('11111111-1111-1111-1111-111111111120', 'f38e4f83-fe95-3243-887e-f448d74ef717', 'COMP-H2O', 'H2O', 'PRODUCT', 1, 'LIQUID', 5),

-- RXN 21
('11111111-1111-1111-1111-111111111121', '8c611aaa-209c-3fe5-942e-6b2777fc75c7', 'COMP-MG-OH-2', 'Mg(OH)2', 'REACTANT', 1, 'SOLID', 1),
('11111111-1111-1111-1111-111111111121', '3c7d0fda-9a6e-3b79-aebb-1a41aecc6c09', 'COMP-HCL', 'HCl', 'REACTANT', 2, 'AQUEOUS', 2),
('11111111-1111-1111-1111-111111111121', 'ca1f3c39-3ecd-3bc7-9758-ddf0513e504d', 'COMP-MGCL2', 'MgCl2', 'PRODUCT', 1, 'AQUEOUS', 3),
('11111111-1111-1111-1111-111111111121', 'f38e4f83-fe95-3243-887e-f448d74ef717', 'COMP-H2O', 'H2O', 'PRODUCT', 2, 'LIQUID', 4),

-- RXN 22
('11111111-1111-1111-1111-111111111122', 'e3b456f2-9873-3a9a-aba2-e1606ffd5f5b', 'COMP-GLUCOSE', 'C6H12O6', 'REACTANT', 1, 'SOLID', 1),
('11111111-1111-1111-1111-111111111122', '6c11ca8c-0546-3a30-8558-92fb83efacb6', 'COMP-O2', 'O2', 'REACTANT', 6, 'GAS', 2),
('11111111-1111-1111-1111-111111111122', 'baa7a54f-9112-3a55-8cfe-8b400d31a0b6', 'COMP-CO2', 'CO2', 'PRODUCT', 6, 'GAS', 3),
('11111111-1111-1111-1111-111111111122', 'f38e4f83-fe95-3243-887e-f448d74ef717', 'COMP-H2O', 'H2O', 'PRODUCT', 6, 'LIQUID', 4),

-- RXN 23
('11111111-1111-1111-1111-111111111123', 'af05f4c0-9722-3faf-8ad8-8ecbebc9ff19', 'COMP-SUCROSE', 'C12H22O11', 'REACTANT', 1, 'SOLID', 1),
('11111111-1111-1111-1111-111111111123', '6c11ca8c-0546-3a30-8558-92fb83efacb6', 'COMP-O2', 'O2', 'REACTANT', 12, 'GAS', 2),
('11111111-1111-1111-1111-111111111123', 'baa7a54f-9112-3a55-8cfe-8b400d31a0b6', 'COMP-CO2', 'CO2', 'PRODUCT', 12, 'GAS', 3),
('11111111-1111-1111-1111-111111111123', 'f38e4f83-fe95-3243-887e-f448d74ef717', 'COMP-H2O', 'H2O', 'PRODUCT', 11, 'LIQUID', 4),

-- RXN 24
('11111111-1111-1111-1111-111111111124', 'baa7a54f-9112-3a55-8cfe-8b400d31a0b6', 'COMP-CO2', 'CO2', 'REACTANT', 1, 'GAS', 1),
('11111111-1111-1111-1111-111111111124', 'f38e4f83-fe95-3243-887e-f448d74ef717', 'COMP-H2O', 'H2O', 'REACTANT', 1, 'LIQUID', 2),
('11111111-1111-1111-1111-111111111124', 'a1a20e49-bce3-39ac-98f8-162131991c48', 'COMP-H2CO3', 'H2CO3', 'PRODUCT', 1, 'AQUEOUS', 3),

-- RXN 25
('11111111-1111-1111-1111-111111111125', 'a1a20e49-bce3-39ac-98f8-162131991c48', 'COMP-H2CO3', 'H2CO3', 'REACTANT', 1, 'AQUEOUS', 1),
('11111111-1111-1111-1111-111111111125', 'baa7a54f-9112-3a55-8cfe-8b400d31a0b6', 'COMP-CO2', 'CO2', 'PRODUCT', 1, 'GAS', 2),
('11111111-1111-1111-1111-111111111125', 'f38e4f83-fe95-3243-887e-f448d74ef717', 'COMP-H2O', 'H2O', 'PRODUCT', 1, 'LIQUID', 3),

-- RXN 26
('11111111-1111-1111-1111-111111111126', '44d58472-c681-3f7e-b989-9b8730603a2b', 'COMP-CO', 'CO', 'REACTANT', 2, 'GAS', 1),
('11111111-1111-1111-1111-111111111126', '6c11ca8c-0546-3a30-8558-92fb83efacb6', 'COMP-O2', 'O2', 'REACTANT', 1, 'GAS', 2),
('11111111-1111-1111-1111-111111111126', 'baa7a54f-9112-3a55-8cfe-8b400d31a0b6', 'COMP-CO2', 'CO2', 'PRODUCT', 2, 'GAS', 3);

-- Seed Reaction Condition Sets
INSERT INTO chemistry.reaction_condition_sets (id, reaction_id, energy_input, atmosphere, description, evidence_status, source_document_id)
VALUES
('21111111-1111-1111-1111-111111111101', '11111111-1111-1111-1111-111111111101', 'HEAT', 'OXYGEN', 'Triggered by spark or thermal ignition source', 'CURATED_AUTHORITATIVE', 'CRC-HANDBOOK-104'),
('21111111-1111-1111-1111-111111111105', '11111111-1111-1111-1111-111111111105', 'HEAT', 'UNSPECIFIED', 'Requires heating above 80 degrees C', 'CURATED_AUTHORITATIVE', 'CRC-HANDBOOK-104'),
('21111111-1111-1111-1111-111111111110', '11111111-1111-1111-1111-111111111110', 'HEAT', 'UNSPECIFIED', 'Thermal dehydration above 110 degrees C', 'CURATED_AUTHORITATIVE', 'CRC-HANDBOOK-104');

-- Seed Reaction Catalysts
INSERT INTO chemistry.reaction_catalysts (id, reaction_id, reference_type, reference_code, compound_id, catalyst_role, physical_form, loading_description, evidence_status, source_document_id)
VALUES
('31111111-1111-1111-1111-111111111102', '11111111-1111-1111-1111-111111111102', 'COMPOUND', 'COMP-CUO', 'd3e7fd9b-1962-3e0a-ab37-bc677cfc4b69', 'CATALYST', 'Solid powder', 'Heterogeneous catalytic decomposition', 'CURATED_AUTHORITATIVE', 'CRC-HANDBOOK-104');

-- Seed Reaction Type Assignments
INSERT INTO chemistry.reaction_type_assignments (reaction_id, type_code, derivation_basis, explanation) VALUES
-- RXN 1
('11111111-1111-1111-1111-111111111101', 'SYNTHESIS', 'CURATED_REFERENCE', 'Combination of elemental hydrogen and oxygen to form water'),
('11111111-1111-1111-1111-111111111101', 'COMBUSTION', 'CURATED_REFERENCE', 'Exothermic oxidation of hydrogen in oxygen atmosphere'),
('11111111-1111-1111-1111-111111111101', 'REDOX', 'CURATED_REFERENCE', 'Oxidation state change of H (0 to +1) and O (0 to -2)'),

-- RXN 2
('11111111-1111-1111-1111-111111111102', 'DECOMPOSITION', 'CURATED_REFERENCE', 'Single reactant breaks down into water and oxygen gas'),
('11111111-1111-1111-1111-111111111102', 'REDOX', 'CURATED_REFERENCE', 'Oxygen disproportionation from -1 to -2 and 0'),
('11111111-1111-1111-1111-111111111102', 'GAS_EVOLUTION', 'SAFE_RULE_DERIVED', 'RULE-EXPLICIT-GAS-PRODUCT: Gas state product formed from non-gaseous reactants'),

-- RXN 3
('11111111-1111-1111-1111-111111111103', 'COMBUSTION', 'CURATED_REFERENCE', 'Hydrocarbon oxidation with oxygen producing carbon dioxide and water'),
('11111111-1111-1111-1111-111111111103', 'REDOX', 'CURATED_REFERENCE', 'Complete carbon oxidation from -4 to +4'),

-- RXN 4
('11111111-1111-1111-1111-111111111104', 'ACID_BASE_NEUTRALIZATION', 'CURATED_REFERENCE', 'Reaction between aqueous hydrochloric acid and sodium hydroxide base'),
('11111111-1111-1111-1111-111111111104', 'DOUBLE_DISPLACEMENT', 'CURATED_REFERENCE', 'Metathesis exchange of hydrogen and sodium ions'),

-- RXN 5
('11111111-1111-1111-1111-111111111105', 'DECOMPOSITION', 'CURATED_REFERENCE', 'Solid bicarbonate decomposes under heat into sodium carbonate, carbon dioxide, and water'),
('11111111-1111-1111-1111-111111111105', 'GAS_EVOLUTION', 'SAFE_RULE_DERIVED', 'RULE-EXPLICIT-GAS-PRODUCT: Gas state product formed from non-gaseous reactants'),

-- RXN 6
('11111111-1111-1111-1111-111111111106', 'ACID_BASE_NEUTRALIZATION', 'CURATED_REFERENCE', 'Basic calcium hydroxide reacts with acidic carbon dioxide gas'),
('11111111-1111-1111-1111-111111111106', 'PRECIPITATION', 'SAFE_RULE_DERIVED', 'RULE-EXPLICIT-PRECIPITATE-STATE: Solid calcium carbonate precipitate formed from aqueous solution'),

-- RXN 7
('11111111-1111-1111-1111-111111111107', 'COMBUSTION', 'CURATED_REFERENCE', 'Combustion of ethanol alcohol yielding carbon dioxide and water'),
('11111111-1111-1111-1111-111111111107', 'REDOX', 'CURATED_REFERENCE', 'Oxidation of ethanol organic substrate'),

-- RXN 8
('11111111-1111-1111-1111-111111111108', 'COMBUSTION', 'CURATED_REFERENCE', 'Combustion of dimethyl ether isomer yielding carbon dioxide and water'),
('11111111-1111-1111-1111-111111111108', 'REDOX', 'CURATED_REFERENCE', 'Oxidation of ether substrate'),

-- RXN 9
('11111111-1111-1111-1111-111111111109', 'HYDRATION', 'CURATED_REFERENCE', 'Addition of 5 water molecules to anhydrous copper sulfate'),
('11111111-1111-1111-1111-111111111109', 'SYNTHESIS', 'CURATED_REFERENCE', 'Combination of salt and water to form crystalline hydrate'),
('11111111-1111-1111-1111-111111111109', 'REVERSIBLE_REACTION', 'SAFE_RULE_DERIVED', 'RULE-DIRECTIONALITY: Reaction marked as reversible in reference catalogue'),

-- RXN 10
('11111111-1111-1111-1111-111111111110', 'DEHYDRATION', 'CURATED_REFERENCE', 'Removal of 5 water molecules from copper sulfate pentahydrate'),
('11111111-1111-1111-1111-111111111110', 'DECOMPOSITION', 'CURATED_REFERENCE', 'Hydrate breakdown into anhydrous salt and water vapor'),
('11111111-1111-1111-1111-111111111110', 'REVERSIBLE_REACTION', 'SAFE_RULE_DERIVED', 'RULE-DIRECTIONALITY: Reaction marked as reversible in reference catalogue'),

-- RXN 11
('11111111-1111-1111-1111-111111111111', 'SYNTHESIS', 'CURATED_REFERENCE', 'Combination of nitrogen and hydrogen gases to produce ammonia'),
('11111111-1111-1111-1111-111111111111', 'REVERSIBLE_REACTION', 'SAFE_RULE_DERIVED', 'RULE-DIRECTIONALITY: Reaction marked as reversible in reference catalogue'),

-- RXN 12
('11111111-1111-1111-1111-111111111112', 'COMBUSTION', 'CURATED_REFERENCE', 'Complete combustion of ethane gas'),
('11111111-1111-1111-1111-111111111112', 'REDOX', 'CURATED_REFERENCE', 'Ethane carbon oxidation'),

-- RXN 13
('11111111-1111-1111-1111-111111111113', 'COMBUSTION', 'CURATED_REFERENCE', 'Complete combustion of propane gas'),
('11111111-1111-1111-1111-111111111113', 'REDOX', 'CURATED_REFERENCE', 'Propane oxidation'),

-- RXN 14
('11111111-1111-1111-1111-111111111114', 'COMBUSTION', 'CURATED_REFERENCE', 'Complete combustion of butane gas'),
('11111111-1111-1111-1111-111111111114', 'REDOX', 'CURATED_REFERENCE', 'Butane oxidation'),

-- RXN 15
('11111111-1111-1111-1111-111111111115', 'COMBUSTION', 'CURATED_REFERENCE', 'Combustion of unsaturated alkene hydrocarbon'),
('11111111-1111-1111-1111-111111111115', 'REDOX', 'CURATED_REFERENCE', 'Ethylene oxidation'),

-- RXN 16
('11111111-1111-1111-1111-111111111116', 'COMBUSTION', 'CURATED_REFERENCE', 'Combustion of alkyne gas'),
('11111111-1111-1111-1111-111111111116', 'REDOX', 'CURATED_REFERENCE', 'Acetylene oxidation'),

-- RXN 17
('11111111-1111-1111-1111-111111111117', 'COMBUSTION', 'CURATED_REFERENCE', 'Combustion of aromatic benzene'),
('11111111-1111-1111-1111-111111111117', 'REDOX', 'CURATED_REFERENCE', 'Benzene oxidation'),

-- RXN 18
('11111111-1111-1111-1111-111111111118', 'ACID_BASE_NEUTRALIZATION', 'CURATED_REFERENCE', 'Diprotic acid neutralization with strong base'),
('11111111-1111-1111-1111-111111111118', 'DOUBLE_DISPLACEMENT', 'CURATED_REFERENCE', 'Metathesis of sulfate and hydroxide ions'),

-- RXN 19
('11111111-1111-1111-1111-111111111119', 'ACID_BASE_NEUTRALIZATION', 'CURATED_REFERENCE', 'Acid reaction with insoluble metal hydroxide base'),
('11111111-1111-1111-1111-111111111119', 'DOUBLE_DISPLACEMENT', 'CURATED_REFERENCE', 'Salt formation metathesis'),

-- RXN 20
('11111111-1111-1111-1111-111111111120', 'DOUBLE_DISPLACEMENT', 'CURATED_REFERENCE', 'Metathesis producing carbonic acid intermediate'),
('11111111-1111-1111-1111-111111111120', 'GAS_EVOLUTION', 'SAFE_RULE_DERIVED', 'RULE-EXPLICIT-GAS-PRODUCT: Gas state carbon dioxide produced from solid/aqueous reactants'),

-- RXN 21
('11111111-1111-1111-1111-111111111121', 'ACID_BASE_NEUTRALIZATION', 'CURATED_REFERENCE', 'Stomach acid neutralization by magnesia base'),
('11111111-1111-1111-1111-111111111121', 'DOUBLE_DISPLACEMENT', 'CURATED_REFERENCE', 'Magnesium chloride salt formation metathesis'),

-- RXN 22
('11111111-1111-1111-1111-111111111122', 'COMBUSTION', 'CURATED_REFERENCE', 'Complete combustion of monosaccharide sugar'),
('11111111-1111-1111-1111-111111111122', 'REDOX', 'CURATED_REFERENCE', 'Cellular respiration chemical stoichiometry model'),

-- RXN 23
('11111111-1111-1111-1111-111111111123', 'COMBUSTION', 'CURATED_REFERENCE', 'Combustion of disaccharide sucrose'),
('11111111-1111-1111-1111-111111111123', 'REDOX', 'CURATED_REFERENCE', 'Sucrose carbon oxidation'),

-- RXN 24
('11111111-1111-1111-1111-111111111124', 'SYNTHESIS', 'CURATED_REFERENCE', 'Acidic oxide reaction with water'),
('11111111-1111-1111-1111-111111111124', 'HYDRATION', 'CURATED_REFERENCE', 'Gas dissolution and hydration'),
('11111111-1111-1111-1111-111111111124', 'REVERSIBLE_REACTION', 'SAFE_RULE_DERIVED', 'RULE-DIRECTIONALITY: Reaction marked as reversible in reference catalogue'),

-- RXN 25
('11111111-1111-1111-1111-111111111125', 'DECOMPOSITION', 'CURATED_REFERENCE', 'Unstable acid breakdown into gas and water'),
('11111111-1111-1111-1111-111111111125', 'GAS_EVOLUTION', 'SAFE_RULE_DERIVED', 'RULE-EXPLICIT-GAS-PRODUCT: Gas state carbon dioxide produced'),
('11111111-1111-1111-1111-111111111125', 'REVERSIBLE_REACTION', 'SAFE_RULE_DERIVED', 'RULE-DIRECTIONALITY: Reaction marked as reversible in reference catalogue'),

-- RXN 26
('11111111-1111-1111-1111-111111111126', 'SYNTHESIS', 'CURATED_REFERENCE', 'Combination of carbon monoxide and oxygen gas'),
('11111111-1111-1111-1111-111111111126', 'COMBUSTION', 'CURATED_REFERENCE', 'Exothermic gas oxidation'),
('11111111-1111-1111-1111-111111111126', 'REDOX', 'CURATED_REFERENCE', 'Carbon oxidation state increase from +2 to +4');
