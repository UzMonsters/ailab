ALTER TABLE chemistry.elements
    ADD COLUMN IF NOT EXISTS electron_configuration_status VARCHAR(50);

UPDATE chemistry.elements
    SET electron_configuration_status = 'EVALUATED'
    WHERE atomic_number BETWEEN 1 AND 92;

UPDATE chemistry.elements
    SET electron_configuration_status = 'PREDICTED'
    WHERE atomic_number BETWEEN 93 AND 103;

UPDATE chemistry.elements
    SET electron_configuration_status = 'PROVISIONAL'
    WHERE atomic_number BETWEEN 104 AND 118;

ALTER TABLE chemistry.elements
    ALTER COLUMN electron_configuration_status SET NOT NULL;

ALTER TABLE chemistry.elements
    DROP CONSTRAINT IF EXISTS elements_radioactivity_status_check;

UPDATE chemistry.elements
    SET radioactivity_status = 'HAS_STABLE_ISOTOPES'
    WHERE radioactivity_status = 'STABLE_OR_HAS_STABLE_ISOTOPES'
      AND atomic_number != 83;

UPDATE chemistry.elements
    SET radioactivity_status = 'PRIMORDIAL_RADIOACTIVE'
    WHERE atomic_number = 83;

UPDATE chemistry.elements
    SET radioactivity_status = 'PRIMORDIAL_RADIOACTIVE'
    WHERE radioactivity_status = 'RADIOACTIVE'
      AND atomic_number IN (84, 85, 86, 87, 88, 89, 90, 91, 92);

UPDATE chemistry.elements
    SET radioactivity_status = 'SYNTHETIC_RADIOACTIVE'
    WHERE radioactivity_status = 'RADIOACTIVE'
      AND atomic_number IN (43, 61);

UPDATE chemistry.elements
    SET radioactivity_status = 'SYNTHETIC_RADIOACTIVE'
    WHERE radioactivity_status = 'RADIOACTIVE'
      AND atomic_number BETWEEN 93 AND 118;

ALTER TABLE chemistry.elements
    ADD CONSTRAINT elements_radioactivity_status_check
    CHECK (radioactivity_status IN ('HAS_STABLE_ISOTOPES', 'PRIMORDIAL_RADIOACTIVE', 'SYNTHETIC_RADIOACTIVE', 'UNKNOWN'));

ALTER TABLE chemistry.elements
    ADD CONSTRAINT elements_electron_configuration_status_check
    CHECK (electron_configuration_status IN ('EVALUATED', 'PREDICTED', 'PROVISIONAL', 'UNKNOWN'));

INSERT INTO chemistry.periodic_table_catalog_versions (id, version, data_sources, reference_conditions)
SELECT 'v1.1.0', '1.1.0',
       'CIAAW Standard Atomic Weights 2021; NIST Atomic Weights and Isotopic Compositions; IUPAC Periodic Table 2024',
       'Standard Temperature and Pressure (STP): 273.15 K, 100 kPa. Radioactivity classification per IUPAC/NUBASE evaluation.'
WHERE NOT EXISTS (
    SELECT 1 FROM chemistry.periodic_table_catalog_versions WHERE id = 'v1.1.0'
);

UPDATE chemistry.elements
    SET catalog_version_id = 'v1.1.0';
