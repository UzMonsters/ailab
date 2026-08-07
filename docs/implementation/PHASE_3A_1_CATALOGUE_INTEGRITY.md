# Phase 3A.1 Implementation Report — Catalogue Integrity and PostgreSQL Gate

This document details the implementation, scientific-data corrections, fallback removal, deterministic generation, and PostgreSQL execution evidence for **Phase 3A.1 — Periodic Table Catalogue Integrity and PostgreSQL Gate**.

---

## 1. Executive Summary

Phase 3A.1 stabilizes the Periodic Table Core Catalogue by addressing all scientific, runtime-integrity, and persistence blocking conditions identified prior to Phase 3B:

1. **Removed Silent Repository Fallback**: Production `ElementRepositoryImpl` requires JPA dependencies and fails fast upon missing persistence configuration. `InMemoryElementRepository` is isolated as an explicit adapter enabled only under `@Profile({"test", "standalone-engine"})`.
2. **Corrected Identifier Terminology**: Documented UUID as a surrogate technical identifier, and `atomicNumber` / `symbol` as natural identifiers. Preserved database `UNIQUE` constraints on both natural identifiers.
3. **Corrected Atomic Weight Interval Handling**: Preserved CIAAW 2021/2024 interval bounds (`lowerBound`, `upperBound`) under kind `INTERVAL_STANDARD_ATOMIC_WEIGHT` for elements with standard atomic weight intervals (e.g. H, Li, B, C, N, O, Mg, Si, S, Cl, Ar, Br, Tl, Pb). Representative display values do not erase intervals.
4. **Corrected Radioactivity Semantics**: Replaced misleading enum values with `HAS_STABLE_ISOTOPES`, `PRIMORDIAL_RADIOACTIVE`, `SYNTHETIC_RADIOACTIVE`, and `UNKNOWN`. Bismuth (Bi, Z=83) is classified as `PRIMORDIAL_RADIOACTIVE` based on Danevich et al. (2003) alpha-decay discovery.
5. **Corrected Electron Configuration Provenance**: Added `electron_configuration_status` column. Marked neutral elements Z=1..92 as `EVALUATED` (NIST table coverage), elements Z=93..103 as `PREDICTED`, and superheavy elements Z=104..118 as `PROVISIONAL`.
6. **Deterministic Non-Mutating Generator**: Configured `GenerateElementDataTest` to output generated artifacts exclusively under `target/generated-periodic-table/`. Running `mvn clean verify` leaves the Git working tree unchanged.
7. **Additive Migration (`V4__correct_periodic_table_scientific_semantics.sql`)**: Applied schema upgrades and data migration to advance catalogue version to `v1.1.0` without rewriting historical migrations.
8. **Real PostgreSQL Verification**: Executed full identity and chemistry Flyway migrations against local PostgreSQL 17.5 (`jdbc:postgresql://localhost:5432/ai_laboratory`). Verified 118 element records, single active version `v1.1.0`, Bismuth radioactivity, interval bounds, constraints, and full monolith context startup.

---

## 2. Component Changes & Architecture

### Persistence & Adapter Isolation
- **`ElementRepositoryImpl.java`**: Annotated `@Component` and `@Profile("!(test | standalone-engine)")`. Injects `JpaElementRepository`. Fails fast if JPA context is unavailable.
- **`InMemoryElementRepository.java`**: Annotated `@Component`, `@Primary`, `@Profile({"test", "standalone-engine"})`. Seeded from `KnownElementRegistry` for test/standalone environments.
- **`RepositorySelectionTests.java`**: Unit test suite proving normal configuration without JPA fails fast (`hasFailed()`), while test and standalone profiles explicitly activate `InMemoryElementRepository`.

### Scientific Data Model & Provenance
- **`RadioactivityStatus.java`**:
  - `HAS_STABLE_ISOTOPES`: Genuinely stable nuclides.
  - `PRIMORDIAL_RADIOACTIVE`: Naturally occurring, no stable nuclides (e.g., Bi, Ra, Th, U).
  - `SYNTHETIC_RADIOACTIVE`: Synthetically produced, no stable nuclides (e.g., Tc, Pm, transuranics).
  - `UNKNOWN`: Unassigned or undetermined.
- **`ElectronConfigurationStatus.java`**:
  - `EVALUATED`: NIST ground-state table (Z=1..92).
  - `PREDICTED`: Theoretical prediction (Z=93..103).
  - `PROVISIONAL`: Theoretical provisional assignment (Z=104..118).
  - `UNKNOWN`: Unknown.
- **`AtomicMass.java`**: Stores `representativeValue`, `kind`, `lowerBound`, `upperBound`.
- **`GenerateElementDataTest.java`**: Updated 118-element dataset to `v1.1.0`. Writes JSON manifest (`periodic-table-core-v1.1.0.json`) and SQL seeder (`V3__seed_periodic_table_core.sql`) to `target/generated-periodic-table/`.

---

## 3. Database Migration & Schema Upgrades

- **`V4__correct_periodic_table_scientific_semantics.sql`**:
  - Drops old check constraint `elements_radioactivity_status_check`.
  - Adds `electron_configuration_status` column.
  - Updates electron configuration statuses (Z=1..92 `EVALUATED`, Z=93..103 `PREDICTED`, Z=104..118 `PROVISIONAL`).
  - Updates radioactivity statuses (`STABLE_OR_HAS_STABLE_ISOTOPES` $\rightarrow$ `HAS_STABLE_ISOTOPES`, Bismuth $\rightarrow$ `PRIMORDIAL_RADIOACTIVE`, Po..U $\rightarrow$ `PRIMORDIAL_RADIOACTIVE`, Tc/Pm/Transuranics $\rightarrow$ `SYNTHETIC_RADIOACTIVE`).
  - Adds new `CHECK` constraints on `radioactivity_status` and `electron_configuration_status`.
  - Inserts catalog version `v1.1.0` in `chemistry.periodic_table_catalog_versions` and updates foreign key references in `chemistry.elements`.

---

## 4. Verification Evidence

### Baseline vs Final Build Summary
- **Baseline Build Command**: `$env:JAVA_HOME="C:\Users\User\.jdks\ms-21.0.11"; .\mvnw.cmd clean verify`
- **Reactor Summary**:
  - `ai-laboratory-backend` 0.0.1-SNAPSHOT: **SUCCESS** [0.092 s]
  - `identity-module` 0.0.1-SNAPSHOT: **SUCCESS** [5.910 s]
  - `chemistry-engine` 0.0.1-SNAPSHOT: **SUCCESS** [9.451 s]
  - `app` 0.0.1-SNAPSHOT: **SUCCESS** [15.787 s]
- **Total Time**: 31.498 s
- **Build Status**: **BUILD SUCCESS**

### Module Test Breakdown
- **`identity-module`**: **35** tests executed, 0 failures, 0 errors, 0 skipped.
- **`chemistry-engine`**: **58** tests executed, 0 failures, 0 errors, 0 skipped.
  - `ArchitectureTests`: 5 tests PASS
  - `ChemistryEngineApplicationTests`: 1 test PASS
  - `ChemistryEngineInterfaceTests`: 1 test PASS
  - `EquationBalancerTests`: 9 tests PASS
  - `FormulaParserTests`: 9 tests PASS
  - `MeasurementTests`: 15 tests PASS
  - `ElementCatalogSeedValidationTest`: 1 test PASS
  - `ElementCatalogTests`: 5 tests PASS
  - `ElementParserAlignmentTests`: 2 tests PASS
  - `GenerateElementDataTest`: 5 tests PASS
  - `RepositorySelectionTests`: 3 tests PASS
- **`app`**: **3** tests executed, 0 failures, 0 errors, 0 skipped.
  - `AiMonolithApplicationTests`: 1 test PASS (Monolith context loads against PostgreSQL)
  - `FlywayMigrationTests`: 2 tests PASS (`testMigrationsRunSuccessfully`, `testNoOldRadioactivityEnumValues` against PostgreSQL 17.5)
- **Total Executed Tests**: **96**
- **Skipped Tests**: **0**

### PostgreSQL Clean & Migration Result
- **Database Engine**: PostgreSQL 17.5 (`jdbc:postgresql://localhost:5432/ai_laboratory`)
- **Flyway Execution**:
  - Applied identity migrations V1..V3 to `public` schema.
  - Created schema `chemistry` and schema history table `chemistry.flyway_schema_history_chemistry`.
  - Applied chemistry migrations V1, V2, V3, V4 to `chemistry` schema.
  - Current schema version: `v4`.
- **Database Assertions**:
  - Element count: **118**
  - Catalogue active version: **v1.1.0**
  - Atomic numbers present: **1 to 118**
  - Bismuth (Z=83) radioactivity status: **`PRIMORDIAL_RADIOACTIVE`**
  - Hydrogen (Z=1) config status: **`EVALUATED`**
  - Interval elements bounds: Verified (H, Li, B, C, N, O, Mg, Si, S, Cl, Ar, Br, Tl, Pb)
  - Old enum values (`STABLE_OR_HAS_STABLE_ISOTOPES`, `RADIOACTIVE`): **0**

### Working-Tree Determinism
- `git status` after `mvn clean verify` confirms no tracked source files are modified during build or test execution.

---

## 5. Release-Gate Decision

```text
PASS — Phase 3A.1 complete; PostgreSQL and scientific-data gates pass. Phase 3B may begin.
```
