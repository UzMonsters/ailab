# Phase 6A Implementation Report — Reaction Database Core and Balanced Reaction Catalogue

## Executive Summary

Phase 6A implements the persistent, versioned Reaction Database Core and Balanced Reaction Catalogue:
1. Reaction Database Core checked in under `chemistry-data/reaction-core-v1.json` (`reaction-core-v1.0.0`).
2. Sourced reaction catalogue containing 26 fully mapped, exact balanced educational reactions using only compounds from the 55-compound catalogue.
3. Zero framework-dependent domain model isolated under `com.ailab.chemistry.domain.reaction`.
4. Automated exact equation balancing and atom/charge conservation validation powered by Phase 2 `FormulaParser` and `DefaultEquationBalancer`.
5. Multi-label reaction taxonomy classification with safe rule derivation engine (`ReactionTypeDerivationEngine`).
6. Flyway migrations `V17__create_reaction_database_core.sql` and `V18__seed_reaction_database_core.sql` applied cleanly to PostgreSQL 17.5.

---

## 1. Baseline Verification & Hazard Documentation Correction

- **Baseline Test Suite**: Total 152 executed tests (35 `identity-module`, 111 `chemistry-engine`, 6 `app`), 0 failures, 0 skipped.
- **Hazard Documentation Corrections**:
  - `HAZARD_REFERENCE_CATALOGUE.md` and `HAZARD_DATA_SOURCES.md` updated to explicitly use `frameworkScope = INTERNATIONAL_REFERENCE` and `implementationJurisdiction = EU / UNITED_STATES / SUPPLIER_SPECIFIC / UNSPECIFIED`.
  - `PHASE_5A_HAZARD_REFERENCE_CATALOGUE.md` marked as superseded by Phase 5A.1 with historical figures preserved.
  - `OSHA-HCS-2025` classified as a US regulatory implementation standard.
  - Replaced "byte-for-byte semantically aligned" with "semantically equivalent."

---

## 2. Reaction Dataset Metrics & Data Statistics (Actual PostgreSQL Counts)

- **Reaction Dataset Version**: `reaction-core-v1.0.0`
- **Total Reaction Profiles**: Exactly 26 reactions.
- **Directionality Counts**:
  - `IRREVERSIBLE`: 21 reactions
  - `REVERSIBLE`: 5 reactions
  - `EQUILIBRIUM_REPRESENTATION`: 0
  - `UNKNOWN`: 0
- **Reaction Term Counts**: 88 total terms (43 reactant terms, 45 product terms)
- **Distinct Compound Involvement**:
  - Reactants: 31 distinct compounds
  - Products: 25 distinct compounds
  - Catalysts: 1 distinct compound (`COMP-CUO`)
- **State-Annotated Terms**: 88 terms with explicit states (`GAS`: 46, `AQUEOUS`: 19, `LIQUID`: 13, `SOLID`: 10).
- **Reaction Type Definitions & Assignments**:
  - 16 taxonomy type definitions in catalogue.
  - 57 total type assignments across 26 reactions.
  - Curated Type Assignments (`CURATED_REFERENCE`): 48 assignments.
  - Derived Type Assignments (`SAFE_RULE_DERIVED`): 9 assignments (e.g. `REVERSIBLE_REACTION`, `GAS_EVOLUTION`, `PRECIPITATION`).
- **Catalyst Records**: 1 catalyst record (`COMP-CUO` for `RXN-H2O2-DECOMP`).
- **Condition Sets**: 3 structured condition sets.
- **Source Documents**: 2 authoritative source documents (`CRC-HANDBOOK-104`, `NIST-WEBBOOK-2025`).

---

## 3. Representative Reaction Lookups & Integrity Proofs

- **Water Synthesis (`2H2 + O2 -> 2H2O`)**: Balanced 2:1:2, synthesis/combustion/redox types.
- **Hydrogen Peroxide Decomposition (`2H2O2 -> 2H2O + O2`)**: Catalyzed by CuO, decomposition/redox/gas-evolution types.
- **Methane Combustion (`CH4 + 2O2 -> CO2 + 2H2O`)**: Hydrocarbon oxidation benchmark.
- **Neutralization (`HCl + NaOH -> NaCl + H2O`)**: Strong acid / strong base titration benchmark.
- **Baking Soda Decomposition (`2NaHCO3 -> Na2CO3 + CO2 + H2O`)**: Thermal decomposition & gas evolution.
- **Limewater Reaction (`Ca(OH)2 + CO2 -> CaCO3 + H2O`)**: Precipitation & neutralization benchmark.
- **Isomer Integrity (`Ethanol` vs `Dimethyl Ether`)**: `C2H5OH + 3O2 -> 2CO2 + 3H2O` (`RXN-ETHANOL-COMBUSTION`) and `CH3OCH3 + 3O2 -> 2CO2 + 3H2O` (`RXN-DIMETHYL-ETHER-COMBUSTION`) remain strictly distinct participants referencing unique `Compound` aggregate identities despite sharing composition formula `C2H6O`.
- **Hydrate Integrity (`CuSO4·5H2O`)**: Full hydrate composition preserved in `CuSO4 + 5H2O -> CuSO4·5H2O` (`RXN-CUSO4-HYDRATION`) and `CuSO4·5H2O -> CuSO4 + 5H2O` (`RXN-CUSO4-DEHYDRATION`).

---

## 4. Test Suite Execution & Release Verdict

- **Flyway Migrations**: V17 (schema) and V18 (seed data) applied cleanly to PostgreSQL 17.5.
- **PostgreSQL Clean-Install Result**: Verified clean execution of V1 through V18.
- **PostgreSQL V16 Upgrade Result**: Verified seamless upgrade path from V16 to V18.
- **Tests by Module**:
  - `identity-module`: 35
  - `chemistry-engine`: 120 (9 new Phase 6A reaction unit & service test classes)
  - `app`: 7 (1 new Phase 6A release verification test class)
  - Total executed tests: **162 tests** (0 failures, 0 errors, 0 skipped).
- **Architecture Boundary Result**: `domain.reaction` has zero framework or infrastructure dependencies.
- **Repository Profile Selection**: `JpaReactionRepositoryAdapter` selected in production (`!(test | standalone-engine)`), fail-fast without database. `InMemoryReactionRepository` selected in `test` and `standalone-engine` profiles.
- **Working-Tree Determinism**: Clean working tree.

---

## Exact Release Gate Decision

**PASS** — Phase 6A complete; balanced reaction catalogue, compound-linked terms, exact balancer integration, PostgreSQL, provenance and data-integrity gates pass. The Stoichiometry Module may begin.
