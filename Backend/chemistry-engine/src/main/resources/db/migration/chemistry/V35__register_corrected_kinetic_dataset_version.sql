CREATE TABLE IF NOT EXISTS chemistry.kinetic_dataset_versions (
    dataset_id VARCHAR(64) PRIMARY KEY,
    version VARCHAR(32) NOT NULL,
    supersedes_dataset_id VARCHAR(64),
    immutable_snapshot BOOLEAN NOT NULL DEFAULT TRUE,
    description TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO chemistry.kinetic_dataset_versions (
    dataset_id, version, supersedes_dataset_id, immutable_snapshot, description
) VALUES (
    'kinetic-reference-v1.1.0',
    '1.1.0',
    'kinetic-reference-v1.0.0',
    TRUE,
    'Corrected Phase 9 kinetic dataset: exact elementary H + O2 and CO + OH records, preserved source units, normalized units and inactive superseded profiles.'
)
ON CONFLICT (dataset_id) DO NOTHING;
