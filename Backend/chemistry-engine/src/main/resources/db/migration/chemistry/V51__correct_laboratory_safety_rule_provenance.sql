ALTER TABLE chemistry.laboratory_safety_rules
    ADD COLUMN IF NOT EXISTS source_type TEXT NOT NULL DEFAULT 'INTERNAL_GOVERNED_POLICY',
    ADD COLUMN IF NOT EXISTS exact_clause_section TEXT NOT NULL DEFAULT '',
    ADD COLUMN IF NOT EXISTS supported_claim TEXT NOT NULL DEFAULT '',
    ADD COLUMN IF NOT EXISTS source_url TEXT NOT NULL DEFAULT '';

-- Deactivate generic un-sourced rules from V50
UPDATE chemistry.laboratory_safety_rules
SET active = false
WHERE rule_id IN ('SAFE-TEMP-LIMIT-GLASS', 'SAFE-PRESSURE-LIMIT-CONTAINER')
  AND rule_version = 1;

-- Correct SAFE-FUME-HOOD-REQ provenance as Option A INTERNAL_GOVERNED_POLICY
UPDATE chemistry.laboratory_safety_rules
SET source_type = 'INTERNAL_GOVERNED_POLICY',
    source_identifier = 'AI-LAB-POLICY-001',
    source_citation = 'AI Laboratory Conservative Operating Policy (OSHA 29 CFR 1910.1450 contextual guidance)',
    exact_clause_section = 'Section 4.1 - Fume Hood Controls',
    supported_claim = 'Conservative internal policy requiring fume hood operation for high-reactivity operations in AI Lab MVP',
    source_url = 'https://www.osha.gov/laws-regs/regulations/standardnumber/1910/1910.1450'
WHERE rule_id = 'SAFE-FUME-HOOD-REQ' AND rule_version = 1;
