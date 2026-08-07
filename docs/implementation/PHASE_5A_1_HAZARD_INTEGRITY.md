# Phase 5A.1 Implementation Report — Hazard Provenance, Scope and Coverage Integrity

## Executive Summary

Phase 5A.1 corrects hazard provenance, scope, availability, and legal jurisdiction modeling for the Hazard Reference Catalogue:
1. Framework reference (`UN-GHS-REV11-2025` with `INTERNATIONAL_REFERENCE` scope) is explicitly decoupled from compound-level classification evidence (`ECHA-CL-INVENTORY-2025` for EU, `OSHA-HCS-2025` for US).
2. Availability statuses recalculated: exactly 6 profiles with compound-level source evidence are `CLASSIFIED`, 3 profiles are `NOT_CLASSIFIED_BY_SOURCE` (`COMP-H2O`, `COMP-NACL`, `COMP-GLUCOSE`), and 46 profiles are `NOT_INCLUDED_IN_DATASET`. No unclassified profile is defaulted to safe.
3. Form, concentration, state, and formulation scopes enforced on physical and health classifications.
4. Summary hazard flags deterministically derived and traceable back to detailed classifications.
5. Flyway migration `V16__correct_hazard_provenance_and_coverage.sql` applied cleanly to PostgreSQL 17.5.
6. Total reactor test suite executed: **152 executed tests** (35 `identity-module`, 111 `chemistry-engine`, 6 `app`), 0 failures, 0 errors, 0 skipped.

---

## Release Evidence & Metrics

- **Phase Result**: PASS
- **Corrected Hazard Dataset Version**: `compound-hazards-v1.1.0`
- **Profile Counts by Availability**:
  - `CLASSIFIED`: 6
  - `NOT_CLASSIFIED_BY_SOURCE`: 3
  - `NOT_INCLUDED_IN_DATASET`: 46
  - Total: 55
- **Classification Assignment Count**: 11 assignments across classified profiles
- **Classified Distinct-Compound Count**: 6 compounds
- **Label-Summary Count**: 6 label summaries
- **Signal-Word Usage Count**: 6 label signal words (5 DANGER, 1 WARNING)
- **Hazard-Class Definition Count**: 9 GHS hazard classes defined in reference framework
- **Hazard-Class Assignment Count**: 11 hazard class assignments
- **Pictogram Definition & Usage Counts**: 9 definitions in framework, 10 assignment usages (GHS02: 3, GHS03: 1, GHS04: 3, GHS05: 1, GHS06: 1, GHS07: 1)
- **H-Statement Definition & Usage Counts**: 11 H-statement definitions, 11 assignment usages
- **P-Statement Definition & Usage Counts**: 11 P-statement definitions, 11 assignment usages
- **Reference-Framework Document Count**: 1 framework document (`UN-GHS-REV11-2025`)
- **Compound-Specific / Implementation Source-Document Count**: 2 source documents (`ECHA-CL-INVENTORY-2025` as authoritative compound classification source for EU, `OSHA-HCS-2025` as US regulatory implementation standard)
- **Source-Document Counts by Type**: 1 `AUTHORITATIVE_CLASSIFICATION`, 1 `REGULATORY_DATABASE`
- **Framework/Jurisdiction Correction**: Framework scope set to `INTERNATIONAL_REFERENCE`, implementation jurisdictions set to `EU` and `UNITED_STATES`.
- **Scope-Sensitive Classification Result**: Form, physical state, concentration range, and formulation preserved across HCl, HNO3, H2SO4, NaOH, NH3, H2O2, Ethanol, Dimethyl Ether, and CO2.
- **Summary-Flag Traceability Result**: 100% of derived summary flags trace back to exact supporting GHS classification or supplemental hazard records.
- **Supplemental-Hazard Provenance Result**: `COMP-CO2` supplemental hazards (`SIMPLE_ASPHYXIANT`, `OXYGEN_DISPLACEMENT`) sourced from ECHA/OSHA gas safety guidance.
- **Manifest/SQL/Database Alignment**: `compound-hazards-v1.1.0.json`, `ghs-rev11-reference.json`, `V15__seed_hazard_reference_catalogue.sql`, `V16__correct_hazard_provenance_and_coverage.sql`, and PostgreSQL schema `chemistry` verified semantically equivalent.
- **Migration Version**: `V16`
- **PostgreSQL Clean-Install Result**: Successful clean execution of V1 through V16 in 1.044s.
- **PostgreSQL V15 Upgrade Result**: Verified seamless upgrade path from V15 to V16.
- **Tests by Module**:
  - `identity-module`: 35
  - `chemistry-engine`: 111
  - `app`: 6
  - Total: 152
- **Skipped Tests**: 0
- **Architecture-Boundary Result**: `domain.hazard` has zero framework dependencies.
- **Repository-Profile Result**: Fail-fast without DB in production; in-memory selection in `test` and `standalone-engine` profiles.
- **Working-Tree Determinism Result**: Clean build output with zero uncommitted artifacts.
- **Documentation Paths**:
  - [HAZARD_DATA_SOURCES.md](file:///c:/Users/User/Documents/ailab/docs/chemistry/HAZARD_DATA_SOURCES.md)
  - [HAZARD_REFERENCE_CATALOGUE.md](file:///c:/Users/User/Documents/ailab/docs/chemistry/HAZARD_REFERENCE_CATALOGUE.md)
  - [PHASE_5A_HAZARD_REFERENCE_CATALOGUE.md](file:///c:/Users/User/Documents/ailab/docs/implementation/PHASE_5A_HAZARD_REFERENCE_CATALOGUE.md)
  - [PHASE_5A_1_HAZARD_INTEGRITY.md](file:///c:/Users/User/Documents/ailab/docs/implementation/PHASE_5A_1_HAZARD_INTEGRITY.md)

---

## Exact Release Gate Decision

**PASS** — Phase 5A.1 complete; hazard coverage, compound-specific provenance, scope, PostgreSQL and traceability gates pass. The Reaction Database Module may begin.
