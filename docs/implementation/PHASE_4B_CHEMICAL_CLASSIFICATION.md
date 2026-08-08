# Phase 4B Implementation Report — Chemical Classification Taxonomy & Compound Profiles

## Executive Summary

Phase 4B implements the versioned Chemical Classification module for compounds:
1. Versioned, acyclic taxonomy (`chemical-classification-v1.0.0`) with 41 classification definitions across 6 explicit dimensions.
2. Immutable classification profiles for all 55 educational compounds in the catalogue.
3. Multi-label classification supporting orthogonal dimensions and parent-child hierarchy prerequisites.
4. Clear separation between curated scientific classification (`CURATED_REFERENCE`) and safe formula/composition rules (`SAFE_RULE_DERIVED`).
5. Isomer differentiation between Ethanol (`COMP-ETHANOL` -> `ALCOHOL`) and Dimethyl Ether (`COMP-DIMETHYL-ETHER` -> `ETHER`).
6. PostgreSQL migrations `V10` (schema) and `V11` (seed) applied successfully.
7. Total reactor test suite executed: 134 tests (35 `identity-module`, 93 `chemistry-engine`, 6 `app`), 0 failures, 0 errors, 0 skipped.

---

## 1. Taxonomy & Classification Dimensions

| Dimension | Cardinality | Purpose & Output Examples |
|---|---|---|
| `SUBSTANCE_DOMAIN` | Exactly 1 per profile | `ELEMENTAL_SUBSTANCE`, `INORGANIC_COMPOUND`, `ORGANIC_COMPOUND` |
| `COMPOSITION_PATTERN` | Multi-valued | `MONOATOMIC_OR_ELEMENTAL`, `BINARY_COMPOSITION`, `TERNARY_COMPOSITION`, `QUATERNARY_OR_HIGHER_COMPOSITION`, `HYDRATE`, `NEUTRAL_SPECIES`, `CHARGED_SPECIES` |
| `INORGANIC_FUNCTIONAL_CLASS` | Multi-valued | `OXIDE`, `PEROXIDE`, `HYDRIDE`, `HYDROXIDE`, `ACID`, `BASE`, `SALT`, `OTHER_INORGANIC` |
| `ACID_SUBTYPE` | Single-valued (0 or 1) | `BINARY_ACID`, `OXYACID`, `OTHER_ACID` (Requires `ACID` or `CARBOXYLIC_ACID`) |
| `SALT_SUBTYPE` | Multi-valued | `NORMAL_SALT`, `ACID_SALT`, `BASIC_SALT`, `DOUBLE_SALT`, `HYDRATED_SALT`, `OTHER_SALT` (Requires `SALT`) |
| `ORGANIC_FUNCTIONAL_CLASS` | Multi-valued | `HYDROCARBON`, `ALCOHOL`, `ETHER`, `ALDEHYDE`, `KETONE`, `CARBOXYLIC_ACID`, `ESTER`, `AMINE`, `AMIDE`, `CARBOHYDRATE`, `OTHER_ORGANIC` |

---

## 2. Derivation Basis vs. Curated Reference

- **`SAFE_RULE_DERIVED`**: Derived deterministically by `ClassificationDerivationEngine` using 4 bounded rules:
  - `RULE-ELEMENTAL-COMPOSITION` (`ELEMENTAL_SUBSTANCE`, `MONOATOMIC_OR_ELEMENTAL`)
  - `RULE-DISTINCT-ELEMENT-COUNT` (`BINARY_COMPOSITION`, `TERNARY_COMPOSITION`, `QUATERNARY_OR_HIGHER_COMPOSITION`)
  - `RULE-HYDRATE-PRESENCE` (`HYDRATE`)
  - `RULE-NET-CHARGE` (`NEUTRAL_SPECIES`, `CHARGED_SPECIES`)
- **`CURATED_REFERENCE`**: Functional classifications curated from CRC Handbook of Chemistry and Physics (104th Edition). Recorded with `ClassificationProvenance`.

---

## 3. Representative Compound Classification Profiles

- **`H2`**: `ELEMENTAL_SUBSTANCE`, `MONOATOMIC_OR_ELEMENTAL`, `NEUTRAL_SPECIES`
- **`O2`**: `ELEMENTAL_SUBSTANCE`, `MONOATOMIC_OR_ELEMENTAL`, `NEUTRAL_SPECIES`
- **`H2O`**: `INORGANIC_COMPOUND`, `BINARY_COMPOSITION`, `OXIDE`, `NEUTRAL_SPECIES`
- **`H2O2`**: `INORGANIC_COMPOUND`, `BINARY_COMPOSITION`, `OXIDE`, `PEROXIDE`, `NEUTRAL_SPECIES`
- **`HCl`**: `INORGANIC_COMPOUND`, `BINARY_COMPOSITION`, `ACID`, `BINARY_ACID`, `NEUTRAL_SPECIES`
- **`H2SO4`**: `INORGANIC_COMPOUND`, `TERNARY_COMPOSITION`, `ACID`, `OXYACID`, `NEUTRAL_SPECIES`
- **`NaOH`**: `INORGANIC_COMPOUND`, `TERNARY_COMPOSITION`, `HYDROXIDE`, `BASE`, `NEUTRAL_SPECIES`
- **`NaCl`**: `INORGANIC_COMPOUND`, `BINARY_COMPOSITION`, `SALT`, `NORMAL_SALT`, `NEUTRAL_SPECIES`
- **`NaHCO3`**: `INORGANIC_COMPOUND`, `QUATERNARY_OR_HIGHER_COMPOSITION`, `SALT`, `ACID_SALT`, `NEUTRAL_SPECIES`
- **`CuSO4·5H2O`**: `INORGANIC_COMPOUND`, `QUATERNARY_OR_HIGHER_COMPOSITION`, `SALT`, `HYDRATED_SALT`, `HYDRATE`, `NEUTRAL_SPECIES`
- **`CH4`**: `ORGANIC_COMPOUND`, `BINARY_COMPOSITION`, `HYDROCARBON`, `NEUTRAL_SPECIES`
- **`C2H5OH`**: `ORGANIC_COMPOUND`, `TERNARY_COMPOSITION`, `ALCOHOL`, `NEUTRAL_SPECIES`
- **`CH3OCH3`**: `ORGANIC_COMPOUND`, `TERNARY_COMPOSITION`, `ETHER`, `NEUTRAL_SPECIES`
- **`C6H12O6`**: `ORGANIC_COMPOUND`, `TERNARY_COMPOSITION`, `CARBOHYDRATE`, `NEUTRAL_SPECIES`

---

## 4. Release Gate Verdict

**PASS** — Phase 4B complete; taxonomy, compound profiles, PostgreSQL migrations V10 & V11, and classification-integrity gates pass. Compound Physical Properties may begin.
