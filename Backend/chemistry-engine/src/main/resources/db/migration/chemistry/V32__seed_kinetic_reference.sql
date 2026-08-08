INSERT INTO chemistry.kinetic_profiles (
    profile_id, reaction_code, overall_order, rate_constant_value, rate_constant_unit,
    pre_exponential_factor_a, activation_energy_kj_mol, min_temperature_k, max_temperature_k,
    ref_temperature_k, ref_pressure_bar, solvent, evidence_status,
    provenance_source_id, provenance_description, provenance_citation
) VALUES
(
    'KP-WATER-SYNTHESIS', 'RXN-WATER-SYNTHESIS', 2.0000, 0.05000000, 'L/(mol*s)',
    500000.00000000, 40.0000, 250.00, 1500.00,
    298.15, 1.0000, 'GAS_PHASE', 'REVIEWED',
    'NIST-CHEMICAL-KINETICS', 'NIST Chemical Kinetics Database compiled kinetic reference data', 'NIST Standard Reference Database 17; Chase et al.'
),
(
    'KP-CO-OXIDATION', 'RXN-CO-OXIDATION', 2.0000, 0.10000000, 'L/(mol*s)',
    60000000.00000000, 50.0000, 250.00, 1500.00,
    298.15, 1.0000, 'GAS_PHASE', 'REVIEWED',
    'NIST-CHEMICAL-KINETICS', 'NIST Chemical Kinetics Database compiled kinetic reference data', 'NIST Standard Reference Database 17; Chase et al.'
),
(
    'KP-METHANE-COMBUSTION', 'RXN-METHANE-COMBUSTION', 1.7500, 0.02000000, '(mol/L)^(-0.75)/s',
    1300000000.00000000, 125.0000, 298.00, 2000.00,
    298.15, 1.0000, 'GAS_PHASE', 'REVIEWED',
    'NIST-CHEMICAL-KINETICS', 'NIST Chemical Kinetics Database compiled kinetic reference data', 'NIST Standard Reference Database 17; Chase et al.'
)
ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.kinetic_rate_law_terms (profile_id, compound_code, physical_state, empirical_order) VALUES
('KP-WATER-SYNTHESIS', 'COMP-H2', 'GAS', 1.0000),
('KP-WATER-SYNTHESIS', 'COMP-O2', 'GAS', 1.0000),
('KP-CO-OXIDATION', 'COMP-CO', 'GAS', 1.0000),
('KP-CO-OXIDATION', 'COMP-O2', 'GAS', 1.0000),
('KP-METHANE-COMBUSTION', 'COMP-CH4', 'GAS', 0.5000),
('KP-METHANE-COMBUSTION', 'COMP-O2', 'GAS', 1.2500)
ON CONFLICT (profile_id, compound_code, physical_state) DO NOTHING;
