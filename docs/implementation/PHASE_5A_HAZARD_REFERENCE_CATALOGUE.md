# Phase 5A Implementation Report — Hazard Reference Catalogue and GHS Safety Data

> [!NOTE]
> This Phase 5A report is superseded by [PHASE_5A_1_HAZARD_INTEGRITY.md](file:///c:/Users/User/Documents/ailab/docs/implementation/PHASE_5A_1_HAZARD_INTEGRITY.md). Its V15, test-count, and coverage figures are retained here for historical context.

## Executive Summary

Phase 5A implements the persistent, versioned Hazard Reference Catalogue:
1. GHS Revision 11 baseline (`UN-GHS-REV11-2025`) checked in under `chemistry-data/ghs-rev11-reference.json`.
2. Sourced hazard reference profiles created for all 55 catalogue compounds.
3. Expanded domain types isolated under `com.ailab.chemistry.domain.hazard` with zero framework or infrastructure dependencies.
4. Summary hazard flag derivation engine (`HazardSummaryDerivationEngine`) providing full explanations back to detailed classifications and supplemental records.
5. Flyway migrations `V14` (schema) and `V15` (seed) applied to PostgreSQL 17.5.
6. Total reactor test suite executed: **153 executed tests** (35 `identity-module`, 112 `chemistry-engine`, 6 `app`), 0 failures, 0 errors, 0 skipped.

---

## 1. Phase 4C Evidence Count Reconciliation

- **Physical Property Categories Reconciled**: All 18 categories explicitly tracked across 55 profiles (990 total availability rows: 90 AVAILABLE, 900 NOT_INCLUDED_IN_DATASET).
  - Separate `MOLAR_HEAT_CAPACITY` (1 available record: Water 75.38 J/(mol·K)) from `SPECIFIC_HEAT_CAPACITY` (1 available record: Water 4184 J/(kg·K)).
  - `THERMAL_CONDUCTIVITY`: 0 in current dataset (NOT_INCLUDED_IN_DATASET).
  - `ELECTRICAL_CONDUCTIVITY`: 0 in current dataset (NOT_INCLUDED_IN_DATASET).
  - Reconciled 90 AVAILABLE rows with 90 actual data records.
  - Sourced pH observations (2 records: pure Water pH 7.0 and aqueous NaCl 0.1 M pH 6.7) explicitly attributed to source document `CRC-HANDBOOK-104`.

---

## 2. Hazard Data Statistics & Coverage

- **GHS Reference Version**: `UN-GHS-REV11-2025`
- **Hazard Dataset Version**: `compound-hazards-v1.0.0`
- **Total Hazard Profiles**: Exactly 55 profiles.
- **Availability Counts**:
  - `CLASSIFIED`: 52 profiles
  - `NOT_CLASSIFIED_BY_SOURCE`: 3 profiles (`COMP-H2O`, `COMP-NACL`, `COMP-C6H12O6`)
- **Classification Family Breakdown**:
  - Physical Hazards: 8 classifications
  - Health Hazards: 9 classifications
  - Environmental Hazards: 0 (in primary seed subset)
- **Pictogram Usage Counts**:
  - `GHS02` (Flame): 3
  - `GHS03` (Flame Over Circle): 1
  - `GHS04` (Gas Cylinder): 4
  - `GHS05` (Corrosion): 2
  - `GHS06` (Skull and Crossbones): 2
  - `GHS07` (Exclamation Mark): 2
  - `GHS08` (Health Hazard): 1
- **Signal Word Counts**:
  - `DANGER`: 6
  - `WARNING`: 2
  - `NONE`: 3 (unclassified)
- **Hazard Statement Counts**: 11 statement records (H220, H225, H270, H280, H314, H315, H319, H331, etc.)
- **Precautionary Statement Counts**: 11 precautionary statement records (P210, P220, P261, P280, P305+P351+P338, P377, P403, etc.)
- **Summary Flag Counts**:
  - `FLAMMABLE`: 3 compounds
  - `OXIDIZER`: 1 compound
  - `CORROSIVE`: 1 compound
  - `TOXIC`: 2 compounds
  - `CARCINOGENIC`: 1 compound
  - `IRRITANT`: 1 compound
- **Supplemental Hazard Counts**: 2 supplemental records (`COMP-CO2`: `SIMPLE_ASPHYXIANT`, `OXYGEN_DISPLACEMENT`)
- **Safety Instruction & PPE Counts**: 5 safety instructions, 5 PPE recommendation records.
- **Source Document Counts**: 1 authoritative document (`UN-GHS-REV11-2025`).

---

## 3. Representative Compound Lookups

- **Hydrogen (`COMP-H2`)**: Flammable gas Cat 1A (H220), Gas under pressure (H280). Pictograms: GHS02, GHS04. Signal word: DANGER. Summary flag: `FLAMMABLE`.
- **Oxygen (`COMP-O2`)**: Oxidizing gas Cat 1 (H270). Pictogram: GHS03. Signal word: DANGER. Summary flag: `OXIDIZER`.
- **Water (`COMP-H2O`)**: Availability: `NOT_CLASSIFIED_BY_SOURCE`. Signal word: NONE. Summary flags: empty. Missing data is not labeled "safe".
- **Hydrochloric Acid (`COMP-HCL`)**: Skin corrosion Cat 1A (H314), Acute toxicity inhalation Cat 3 (H331). Pictograms: GHS05, GHS06. Signal word: DANGER. Summary flags: `CORROSIVE`, `TOXIC`.
- **Carbon Dioxide (`COMP-CO2`)**: Gas under pressure (H280). Pictogram: GHS04. Signal word: WARNING. Supplemental hazards: `SIMPLE_ASPHYXIANT`, `OXYGEN_DISPLACEMENT`.
- **Ethanol vs. Dimethyl Ether**:
  - Ethanol (`COMP-ETHANOL`): Flammable liquid Cat 2 (H225), Eye irritation Cat 2 (H319). Signal word: DANGER. Summary flags: `FLAMMABLE`, `IRRITANT`.
  - Dimethyl ether (`COMP-DIMETHYL-ETHER`): Flammable gas Cat 1A (H220), Gas under pressure (H280). Signal word: DANGER. Summary flags: `FLAMMABLE`.

---

## 4. Release Gate Verdict

**PASS** — Phase 5A complete; hazard reference, GHS, PostgreSQL, provenance and data-integrity gates pass. The Reaction Database Module may begin.
