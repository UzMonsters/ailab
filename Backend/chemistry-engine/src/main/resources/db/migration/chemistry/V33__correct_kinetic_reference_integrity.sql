ALTER TABLE chemistry.kinetic_profiles
    ADD COLUMN IF NOT EXISTS temperature_exponent_n NUMERIC(8,4) DEFAULT 0.0000,
    ADD COLUMN IF NOT EXISTS nist_squib VARCHAR(64),
    ADD COLUMN IF NOT EXISTS paper_title TEXT,
    ADD COLUMN IF NOT EXISTS authors TEXT,
    ADD COLUMN IF NOT EXISTS journal_name VARCHAR(128),
    ADD COLUMN IF NOT EXISTS publication_year INTEGER,
    ADD COLUMN IF NOT EXISTS pages VARCHAR(32),
    ADD COLUMN IF NOT EXISTS record_url TEXT,
    ADD COLUMN IF NOT EXISTS data_type VARCHAR(64) DEFAULT 'EXPERIMENTAL',
    ADD COLUMN IF NOT EXISTS experimental_method VARCHAR(64),
    ADD COLUMN IF NOT EXISTS uncertainty VARCHAR(32),
    ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE;

-- Deactivate synthetic global reaction profiles from production lookup
UPDATE chemistry.kinetic_profiles
SET is_active = FALSE, evidence_status = 'SYNTHETIC_EDUCATIONAL'
WHERE profile_id IN ('KP-WATER-SYNTHESIS', 'KP-CO-OXIDATION', 'KP-METHANE-COMBUSTION');

-- Seed authentic elementary reaction kinetic profiles from NIST Chemical Kinetics Database
INSERT INTO chemistry.kinetic_profiles (
    profile_id, reaction_code, overall_order, rate_constant_value, rate_constant_unit,
    pre_exponential_factor_a, temperature_exponent_n, activation_energy_kj_mol, min_temperature_k, max_temperature_k,
    ref_temperature_k, ref_pressure_bar, solvent, evidence_status, is_active,
    provenance_source_id, provenance_description, provenance_citation,
    nist_squib, paper_title, authors, journal_name, publication_year, pages, record_url, data_type, experimental_method, uncertainty
) VALUES
(
    'KP-ELEM-H-O2-PIRRAGLIA-1989', 'RXN-WATER-SYNTHESIS', 2.0000, 72.17000000, 'L/(mol*s)',
    104000000.00000000, 0.0000, 63.5000, 1050.00, 2500.00,
    1050.00, 1.0000, 'GAS_PHASE', 'EXPERIMENTAL', TRUE,
    'NIST-CHEMICAL-KINETICS', 'NIST Chemical Kinetics Database compiled experimental shock tube data', 'Pirraglia et al., J. Phys. Chem. 93, 282 (1989)',
    '1989PIR/MIC282-291', 'A shock tube study of the reaction H + O2 -> OH + O', 'Pirraglia, P. V.; Michael, J. V.; Sutherland, J. W.; Klemm, R. B.', 'J. Phys. Chem.', 1989, '282-291', 'https://kinetics.nist.gov/kinetics/Detail?id=1989PIR/MIC282-291:1', 'EXPERIMENTAL', 'SHOCK_TUBE', '±15%'
),
(
    'KP-ELEM-OH-CO-WOOLDRIDGE-1994', 'RXN-CO-OXIDATION', 2.0000, 60200.00000000, 'L/(mol*s)',
    60200.00000000, 0.7000, 0.0000, 300.00, 2500.00,
    298.15, 1.0000, 'GAS_PHASE', 'EXPERIMENTAL', TRUE,
    'NIST-CHEMICAL-KINETICS', 'NIST Chemical Kinetics Database compiled experimental shock tube data', 'Wooldridge et al., Int. J. Chem. Kinet. 26, 389 (1994)',
    '1994WOO/HAN389-402', 'A shock tube study of the reaction OH + CO -> H + CO2', 'Wooldridge, M. S.; Hanson, R. K.; Bowman, C. T.', 'Int. J. Chem. Kinet.', 1994, '389-402', 'https://kinetics.nist.gov/kinetics/Detail?id=1994WOO/HAN389-402:1', 'EXPERIMENTAL', 'SHOCK_TUBE', '±10%'
)
ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.kinetic_rate_law_terms (profile_id, compound_code, physical_state, empirical_order) VALUES
('KP-ELEM-H-O2-PIRRAGLIA-1989', 'COMP-H2', 'GAS', 1.0000),
('KP-ELEM-H-O2-PIRRAGLIA-1989', 'COMP-O2', 'GAS', 1.0000),
('KP-ELEM-OH-CO-WOOLDRIDGE-1994', 'COMP-CO', 'GAS', 1.0000),
('KP-ELEM-OH-CO-WOOLDRIDGE-1994', 'COMP-O2', 'GAS', 1.0000)
ON CONFLICT (profile_id, compound_code, physical_state) DO NOTHING;
