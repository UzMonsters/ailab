# Phase 3B.1 Release Evidence and Dataset Integrity Report

## Executive Summary

Phase 3B.1 finalizes the release gate for Phase 3B (Extended Element Properties) by providing complete, empirical verification evidence across schema structure, clean installation, upgrade paths, manifest/SQL/database alignment, constraint enforcement, profile selection, working-tree determinism, exact test counts, representative lookups, and data source licensing.

---

## 1. Schema Reconciled Tables (Exact: 9 Tables)

The `V5__create_extended_element_properties.sql` Flyway migration creates exactly **9 database tables** under the `chemistry` schema:

1. `chemistry.element_property_dataset_versions`
2. `chemistry.element_property_profiles`
3. `chemistry.element_valencies`
4. `chemistry.element_oxidation_states`
5. `chemistry.element_electronegativities`
6. `chemistry.element_radii`
7. `chemistry.element_density_data`
8. `chemistry.element_phase_transitions`
9. `chemistry.element_appearance`

---

## 2. Database Property Statistics & Coverage

- **Property Dataset Version**: `extended-properties-v1.0.0`
- **Total Property Profiles**: 118 (100% of Periodic Table elements 1–118)
- **Distinct Element Coverage**:
  - Valencies: 132 records across 118 distinct elements (126 common, 6 uncommon)
  - Oxidation States: 146 records across 118 distinct elements (134 common, 9 uncommon, 3 predicted)
  - Electronegativities: 90 records across 90 distinct elements (Pauling scale; noble gases and superheavy elements excluded where undefined)
  - Radii: 154 records across 118 distinct elements (including 14 ionic radius contexts)
  - Density: 118 records across 118 distinct elements (all > 0 kg/m³)
  - Phase Transitions: 133 records (116 melting, 16 boiling, 1 sublimation) across 118 distinct elements
  - Appearance: 118 records across 118 distinct elements
- **Evidence Status Counts**:
  - `EVALUATED`: 880 records
  - `PREDICTED`: 11 records (Og Z=118 and specific superheavy elements)
  - `MEASURED`: 0 (represented under `EVALUATED` in CRC reference standards)
  - `CONVENTIONAL`: 0
  - `ESTIMATED`: 0
  - `PROVISIONAL`: 0
  - `UNKNOWN`: 0
  - `NOT_APPLICABLE`: 0

---

## 3. Manifest, SQL Seed, and Database Equivalence

- Verified semantic equivalence across:
  - `periodic-table-extended-properties-v1.json`
  - `V6__seed_extended_element_properties.sql`
  - PostgreSQL `chemistry` schema tables
- Every profile UUID, element UUID, valency, oxidation state, electronegativity value/scale, radius kind/length/ionic charge, density value/conditions, phase transition temperature/behavior, appearance text, and provenance source identifier matches across all 3 layers.

---

## 4. PostgreSQL Clean-Install & Upgrade Path Verification

- **Clean Installation Test**: Migrated schema from scratch (`V1` to `V6`). Successfully created all tables, applied all constraints, and populated 118 elements and property profiles.
- **Upgrade Path Test**: Applied `V1`–`V4` (Phase 3A.1 state), verified 118 core elements intact, then executed `V5`–`V6`. Migrations executed idempotently with 0 errors.

---

## 5. Database Constraint Enforcement

- Real PostgreSQL database verified to reject:
  - Duplicate profile for same element (`uk_element_property_profile` constraint violation)
  - Non-positive radius (`element_radii_radius_pm_check` constraint violation)
  - Ionic radius without charge (`chk_ionic_charge_required` constraint violation)
  - Duplicate valency / oxidation state per profile

---

## 6. Repository Profile Selection Verification

- `@Profile("!(test | standalone-engine)")`: Injects `ElementPropertyRepositoryImpl` (uses JPA, fails fast if persistence unavailable).
- `@Profile({"test", "standalone-engine"})`: Injects `InMemoryElementPropertyRepository` (isolated for standalone tests).

---

## 7. Working-Tree Determinism

- Executing `$env:JAVA_HOME="C:\Users\User\.jdks\ms-21.0.11"; .\mvnw.cmd clean verify`:
  - Does NOT modify tracked SQL seed migration `V6__seed_extended_element_properties.sql`.
  - Does NOT modify tracked JSON manifest `periodic-table-extended-properties-v1.json`.
  - All generator output is constrained strictly to `target/generated-extended-properties/`.
  - `git status --short` after clean verify remains clean.

---

## 8. Exact Test Reporting by Module

- **`identity-module`**: Executed 35, Failures 0, Errors 0, Skipped 0
- **`chemistry-engine`**: Executed 69, Failures 0, Errors 0, Skipped 0
- **`app`**: Executed 6, Failures 0, Errors 0, Skipped 0
- **Total Executed**: **110 JVM tests**
- **Total Skipped**: **0 tests**

---

## 9. Representative Property Lookups

- **H (Z=1)**: Valency 1, OxState +1 (common) / -1 (uncommon), Electronegativity 2.20 Pauling, Empirical Atomic Radius 25 pm, Density 0.08988 kg/m³, Melting 13.99 K, Boiling 20.27 K, Appearance "Colorless, odorless, tasteless gas".
- **C (Z=6)**: Valency 4, OxState +4/-4, Electronegativity 2.55 Pauling, Atomic Radius 70 pm, Density 2267 kg/m³, Sublimation 3915 K, Appearance "Black solid / Colorless solid".
- **Fe (Z=26)**: Valencies 2/3/6, OxStates +2/+3/+6, Electronegativity 1.83 Pauling, Metallic Radius 126 pm, Ionic Radius 78 pm (+2) / 64.5 pm (+3), Density 7874 kg/m³, Melting 1811 K, Boiling 3134 K, Appearance "Lustrous silvery-gray metallic solid".
- **Og (Z=118)**: Valency 0 (PREDICTED), OxState 0/+2/+4 (PREDICTED), Calculated Atomic Radius 152 pm, Density 4900 kg/m³, Melting 325 K, Boiling 350 K, Appearance "Synthetic radioactive superheavy element".

---

## 10. Data Licensing & Reuse Limitations

Scientific attribution is recorded for data provenance and reference transparency. Source attribution does not in itself establish public domain status or unrestricted commercial redistribution rights. Full dataset reproduction remains limited by original publisher terms (CRC / Taylor & Francis); usage within this engine is restricted to internal educational, computational, and reference evaluation purposes.

---

## 11. Release Gate Decision

- **Verdict**: **PASS**
- **Justification**: All 16 mandatory release-gate criteria for Phase 3B.1 have been empirically verified and documented. The codebase is clean, fully tested, deterministic, and ready to advance.
