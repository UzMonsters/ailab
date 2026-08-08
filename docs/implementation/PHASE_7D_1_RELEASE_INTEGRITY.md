# Phase 7D.1 Implementation Report — Acid-Base Release Integrity Correction

## Executive Summary
Phase 7D.1 corrects Flyway migration immutability, species reference semantics, and source provenance:
1. **Restored V20 Migration**: Restored original Phase 7C `V20__seed_acid_base_reference_foundation.sql` file content, preserving Flyway schema history immutability.
2. **Additive Migration V21**: Added `V21__correct_acid_base_reference_semantics.sql` to apply schema updates, add `dissociation_behavior`, seed `SPEC-NAOH`, and update dataset provenance.
3. **Role vs. Behavior Separation**:
   - `acidBaseRole`: `ACID`, `BASE`, `AMPHIPROTIC`, `NEUTRAL`.
   - `dissociationBehavior`: `STRONG_ELECTROLYTE`, `WEAK_ELECTROLYTE`, `NON_ELECTROLYTE`, `UNKNOWN`.
4. **NaOH Correction**: Added `SPEC-NAOH` linked to `COMP-NAOH`, with `role = BASE`, `behavior = STRONG_ELECTROLYTE`, and no fake $K_b$.
5. **Provenance Correction**: Separated CRC Handbook reuse terms, IUPAC source metadata, and internal calculations.
6. **Release Gate**: **PASS** — Phase 7D.1 complete; Flyway immutability, species behavior, regression and release-evidence gates pass. Buffer calculations may begin.
