ALTER TABLE chemistry.equipment_reference_profiles
    ADD COLUMN IF NOT EXISTS performance_qualified BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS model_or_standard_class TEXT,
    ADD COLUMN IF NOT EXISTS evidence_status VARCHAR(48) NOT NULL DEFAULT 'TYPE_TAXONOMY'
        CHECK (evidence_status IN ('SOURCED_REFERENCE_VALUE', 'TYPE_TAXONOMY', 'INTERNAL_GOVERNED_POLICY'));

ALTER TABLE chemistry.equipment_capabilities
    ADD COLUMN IF NOT EXISTS original_unit VARCHAR(32),
    ADD COLUMN IF NOT EXISTS normalized_unit VARCHAR(32),
    ADD COLUMN IF NOT EXISTS environmental_restrictions TEXT,
    ADD COLUMN IF NOT EXISTS source_record_id VARCHAR(160),
    ADD COLUMN IF NOT EXISTS evidence_status VARCHAR(48) NOT NULL DEFAULT 'TYPE_TAXONOMY'
        CHECK (evidence_status IN ('SOURCED_REFERENCE_VALUE', 'TYPE_TAXONOMY', 'INTERNAL_GOVERNED_POLICY'));

ALTER TABLE chemistry.container_reference_profiles
    ADD COLUMN IF NOT EXISTS performance_qualified BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS model_or_standard_class TEXT,
    ADD COLUMN IF NOT EXISTS evidence_status VARCHAR(48) NOT NULL DEFAULT 'TYPE_TAXONOMY'
        CHECK (evidence_status IN ('SOURCED_REFERENCE_VALUE', 'TYPE_TAXONOMY'));

ALTER TABLE chemistry.container_compatibility_records
    ADD COLUMN IF NOT EXISTS source_record_id VARCHAR(160),
    ADD COLUMN IF NOT EXISTS source_defined_boundaries BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE chemistry.laboratory_source_documents
   SET evidence_status = 'TYPE_TAXONOMY',
       title = 'AI Laboratory internal calibration policy fixture',
       citation = 'AI Laboratory internal governed calibration policy for suitability-validation fixtures; caller supplies actual calibration records.',
       edition = '2026-08-06',
       table_or_section = 'calibration requirement policy, not manufacturer evidence',
       page_or_record_identifier = 'LAB-POLICY-CAL-2026'
 WHERE source_code = 'LAB-POLICY-CAL-2026';

UPDATE chemistry.container_compatibility_records
   SET is_active = FALSE
 WHERE dataset_id = 'laboratory-container-reference-v1.0.0'
   AND (source_record_id IS NULL OR contact_duration_limit IS NULL OR min_temperature_c IS NULL OR max_temperature_c IS NULL);

ALTER TABLE chemistry.container_compatibility_records
    ADD CONSTRAINT ck_active_compatibility_complete_source
    CHECK (
        is_active = FALSE OR (
            source_record_id IS NOT NULL
            AND contact_duration_limit IS NOT NULL
            AND min_temperature_c IS NOT NULL
            AND max_temperature_c IS NOT NULL
            AND source_defined_boundaries = TRUE
        )
    );

ALTER TABLE chemistry.equipment_reference_profiles
    ADD CONSTRAINT ck_performance_profile_identity
    CHECK (
        performance_qualified = FALSE OR (
            model_or_standard_class IS NOT NULL
            AND evidence_status = 'SOURCED_REFERENCE_VALUE'
        )
    );

ALTER TABLE chemistry.equipment_capabilities
    ADD CONSTRAINT ck_performance_capability_source_fields
    CHECK (
        evidence_status <> 'SOURCED_REFERENCE_VALUE' OR (
            source_record_id IS NOT NULL
            AND original_unit IS NOT NULL
            AND normalized_unit IS NOT NULL
        )
    );
