-- Flyway Migration V24: Seed Ionic Activity Reference Data

INSERT INTO chemistry.acid_base_dataset_versions (version, source_identifier, citation, license, evidence_status)
VALUES (
    '1.1.0',
    'CRC-HANDBOOK-DAVIES-A-WATER-298K',
    'CRC Handbook of Chemistry and Physics, 104th Ed. (2023-2024); Davies equation parameterization for aqueous solutions at 298.15 K',
    'Public Academic / IUPAC Standard Data',
    'PEER_REVIEWED_EXPERIMENTAL'
)
ON CONFLICT (version) DO NOTHING;

INSERT INTO chemistry.ionic_activity_parameter_sets (
    model,
    solvent_code,
    temperature_celsius,
    davies_a,
    min_ionic_strength,
    max_ionic_strength,
    source_document,
    evidence,
    license
)
VALUES (
    'DAVIES',
    'COMP-H2O',
    25.00,
    0.50900000,
    0.00000000,
    0.50000000,
    'CRC Handbook of Chemistry and Physics, 104th Ed. (2023-2024)',
    'Davies equation A parameter for water at 298.15 K; validity limited to ionic strength <= 0.5 mol/L',
    'Public Academic / IUPAC Standard Data'
)
ON CONFLICT (model, solvent_code, temperature_celsius) DO NOTHING;
