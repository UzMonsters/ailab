-- V4: Correct periodic table scientific semantics
-- Dataset upgraded from v1.0.0 to v1.1.0
-- Changes:
--   1. Add electron_configuration_status column
--   2. Correct radioactivity_status check constraint (new enum values)
--   3. Correct Bismuth (Bi, Z=83) radioactivity from STABLE_OR_HAS_STABLE_ISOTOPES to PRIMORDIAL_RADIOACTIVE
--   4. Update catalog version to v1.1.0
--   5. Update all radioactivity_status values from old model to new model

-- Step 1: Add new electron_configuration_status column (nullable initially)
ALTER TABLE chemistry.elements
    ADD COLUMN IF NOT EXISTS electron_configuration_status VARCHAR(50);

-- Step 2: Populate electron_configuration_status based on atomic_number:
--   Z = 1..92  → EVALUATED (NIST coverage)
--   Z = 93..103 → PREDICTED (actinide theoretical)
--   Z = 104..118 → PROVISIONAL (superheavy)
UPDATE chemistry.elements
    SET electron_configuration_status = 'EVALUATED'
    WHERE atomic_number BETWEEN 1 AND 92;

UPDATE chemistry.elements
    SET electron_configuration_status = 'PREDICTED'
    WHERE atomic_number BETWEEN 93 AND 103;

UPDATE chemistry.elements
    SET electron_configuration_status = 'PROVISIONAL'
    WHERE atomic_number BETWEEN 104 AND 118;

-- Verify no nulls remain before adding NOT NULL constraint
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM chemistry.elements WHERE electron_configuration_status IS NULL) THEN
        RAISE EXCEPTION 'Unexpected NULL in electron_configuration_status after population';
    END IF;
END $$;

ALTER TABLE chemistry.elements
    ALTER COLUMN electron_configuration_status SET NOT NULL;

-- Step 3: Drop old radioactivity_status check constraint before updating values
ALTER TABLE chemistry.elements
    DROP CONSTRAINT IF EXISTS elements_radioactivity_status_check;

-- Step 4: Update radioactivity_status values from old model to new model
-- Old value → New value mapping:
--   STABLE_OR_HAS_STABLE_ISOTOPES → HAS_STABLE_ISOTOPES (elements with true stable isotopes)
--   RADIOACTIVE                   → stays RADIOACTIVE temporarily, then differentiated below
--   UNKNOWN                       → UNKNOWN

-- First, update elements that were STABLE_OR_HAS_STABLE_ISOTOPES
-- Note: These are safe because we verify Bismuth (Z=83) gets corrected separately
UPDATE chemistry.elements
    SET radioactivity_status = 'HAS_STABLE_ISOTOPES'
    WHERE radioactivity_status = 'STABLE_OR_HAS_STABLE_ISOTOPES'
      AND atomic_number != 83; -- Bismuth handled separately

-- Correct Bismuth (Bi, Z=83) - confirmed radioactive since 2003 (Danevich et al.)
-- Bi-209 has t1/2 ≈ 2.01×10^19 years, still primordial in nature
UPDATE chemistry.elements
    SET radioactivity_status = 'PRIMORDIAL_RADIOACTIVE'
    WHERE atomic_number = 83;

-- Verify Bismuth is no longer classified as having stable isotopes
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM chemistry.elements WHERE atomic_number = 83 AND radioactivity_status = 'HAS_STABLE_ISOTOPES') THEN
        RAISE EXCEPTION 'Bismuth (Z=83) must not be classified as HAS_STABLE_ISOTOPES';
    END IF;
END $$;

-- Step 5: Classify old RADIOACTIVE values into PRIMORDIAL_RADIOACTIVE vs SYNTHETIC_RADIOACTIVE
-- PRIMORDIAL_RADIOACTIVE: occurs naturally, Z = 43(Tc-exception), 61, 84..88
-- Actually by convention:
--   Z = 84 (Po), 85 (At), 86 (Rn), 87 (Fr), 88 (Ra), 89 (Ac) → PRIMORDIAL_RADIOACTIVE
--   Z = 43 (Tc), 61 (Pm), 93-118 → SYNTHETIC_RADIOACTIVE
UPDATE chemistry.elements
    SET radioactivity_status = 'PRIMORDIAL_RADIOACTIVE'
    WHERE radioactivity_status = 'RADIOACTIVE'
      AND atomic_number IN (84, 85, 86, 87, 88, 89, 90, 91, 92);
-- Th(90), Pa(91), U(92) were already standard atomic weight → HAS_STABLE_ISOTOPES? No, they're radioactive.
-- Actually Th, Pa, U have standard atomic weights because they occur naturally, but have NO stable isotopes.
-- They are PRIMORDIAL_RADIOACTIVE.

UPDATE chemistry.elements
    SET radioactivity_status = 'SYNTHETIC_RADIOACTIVE'
    WHERE radioactivity_status = 'RADIOACTIVE'
      AND atomic_number IN (43, 61);

UPDATE chemistry.elements
    SET radioactivity_status = 'SYNTHETIC_RADIOACTIVE'
    WHERE radioactivity_status = 'RADIOACTIVE'
      AND atomic_number BETWEEN 93 AND 118;

-- Verify no old enum values remain
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM chemistry.elements
        WHERE radioactivity_status IN ('STABLE_OR_HAS_STABLE_ISOTOPES', 'RADIOACTIVE')
    ) THEN
        RAISE EXCEPTION 'Old radioactivity_status values remain after migration';
    END IF;
END $$;

-- Step 6: Add new check constraints
ALTER TABLE chemistry.elements
    ADD CONSTRAINT elements_radioactivity_status_check
    CHECK (radioactivity_status IN ('HAS_STABLE_ISOTOPES', 'PRIMORDIAL_RADIOACTIVE', 'SYNTHETIC_RADIOACTIVE', 'UNKNOWN'));

-- Add constraint for electron_configuration_status
ALTER TABLE chemistry.elements
    ADD CONSTRAINT elements_electron_configuration_status_check
    CHECK (electron_configuration_status IN ('EVALUATED', 'PREDICTED', 'PROVISIONAL', 'UNKNOWN'));

-- Step 7: Insert the new catalog version entry
INSERT INTO chemistry.periodic_table_catalog_versions (id, version, data_sources, reference_conditions)
    VALUES (
        'v1.1.0',
        '1.1.0',
        'CIAAW Standard Atomic Weights 2021; NIST Atomic Weights and Isotopic Compositions; IUPAC Periodic Table 2024',
        'Standard Temperature and Pressure (STP): 273.15 K, 100 kPa. Radioactivity classification per IUPAC/NUBASE evaluation.'
    )
    ON CONFLICT (id) DO NOTHING;

-- Step 8: Update all existing elements to point to new catalog version
UPDATE chemistry.elements
    SET catalog_version_id = 'v1.1.0';
