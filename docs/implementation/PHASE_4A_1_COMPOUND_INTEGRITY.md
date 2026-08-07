# Phase 4A.1 Implementation Report — Compound Catalogue Integrity & Element-Mass Source Correction

## Executive Summary

Phase 4A.1 fixes all structural and architectural issues identified in Phase 4A:
1. Removed duplicate `KnownAtomicMassTable` from the Compound domain.
2. Injected `ElementMassProvider` into `MolarMassCalculator` so atomic masses originate solely from the authoritative Periodic Table catalogue.
3. Separated formula representations into `originalFormula`, `normalizedFormula`, and `compositionFormula` (Hill notation), preserving display/hydrate semantics while providing exact Hill-notation isomer searching.
4. Added Flyway migration `V9__separate_compound_formula_fields.sql` to persist `composition_formula` and index both formula fields.
5. All 98 executed unit, integration, architecture, and migration tests pass cleanly.

---

## 1. Single-Source-of-Truth Architecture

### Removed Duplicate Data Table
- `KnownAtomicMassTable` has been deleted from `domain.compound`.
- ArchUnit test `compoundDomainShouldNotContainDuplicateAtomicMassTable` actively prevents reintroduction of any hardcoded 118-element mass table in the Compound domain.

### Production Provider (`CatalogElementMassProvider`)
- Implements `ElementMassProvider` port in the infrastructure layer.
- Fetches `Element` directly from `ElementRepository` (backed by the Phase 3A Periodic Table catalogue).
- Maps to `ElementMassData` preserving dataset version (`v1.1.0`), provenance string, atomic mass kind, representative value, and interval bounds (`lowerBound`, `upperBound`).
- Fails fast with `CompoundException(ELEMENT_MASS_NOT_FOUND)` if an element is missing. Never substitutes zero or default fallbacks.

### Test Provider (`TestElementMassProvider`)
- Located strictly under `src/test/java`.
- Derived from `GenerateElementDataTest.ELEMENT_DATA` fixture for isolated domain unit testing.
- Supports dynamic mass manipulation (`setCustomMass`) to prove `MolarMassCalculatorImpl` dynamically uses the provider and has zero hardcoded static mass fallbacks.

---

## 2. Formula Representation Model

Every compound now holds three distinct formula representations:

| Property | Description | Ethanol (`COMP-ETHANOL`) | Dimethyl Ether (`COMP-DIMETHYL-ETHER`) | Copper(II) Sulfate Pentahydrate (`COMP-CUSO4-5H2O`) |
|---|---|---|---|---|
| `originalFormula` | Display representation | `C2H5OH` | `CH3OCH3` | `CuSO4.5H2O` |
| `normalizedFormula` | Parser-normalized format | `C2H5OH` | `CH3OCH3` | `CuSO4·5H2O` |
| `compositionFormula` | Hill-notation composition key | `C2H6O` | `C2H6O` | `CuH10O9S` |

### Search Behavior
- `findByNormalizedFormula("C2H5OH")`: Exact lookup returning Ethanol (1 result).
- `findByCompositionFormula("C2H6O")`: Isomer search returning both Ethanol and Dimethyl ether (2 results).
- `findByNormalizedFormula("CuSO4·5H2O")`: Hydrate search preserving hydrate notation.
- `findByCompositionFormula("CuH10O9S")`: Composition search for pentahydrate.

---

## 3. Database Migration V9 (`V9__separate_compound_formula_fields.sql`)

```sql
-- V9: Separate normalized_formula from composition_formula (Hill notation)
ALTER TABLE chemistry.compounds ADD COLUMN composition_formula VARCHAR(255);

-- Backfill composition_formula (Hill notation)
UPDATE chemistry.compounds SET composition_formula = 'C2H6O' WHERE compound_code IN ('COMP-ETHANOL', 'COMP-DIMETHYL-ETHER');
UPDATE chemistry.compounds SET composition_formula = 'CuH10O9S' WHERE compound_code = 'COMP-CUSO4-5H2O';
UPDATE chemistry.compounds SET composition_formula = normalized_formula WHERE composition_formula IS NULL;

-- Restore normalized_formula to preserve input representation
UPDATE chemistry.compounds SET normalized_formula = 'C2H5OH' WHERE compound_code = 'COMP-ETHANOL';
UPDATE chemistry.compounds SET normalized_formula = 'CH3OCH3' WHERE compound_code = 'COMP-DIMETHYL-ETHER';
UPDATE chemistry.compounds SET normalized_formula = 'CuSO4·5H2O' WHERE compound_code = 'COMP-CUSO4-5H2O';

ALTER TABLE chemistry.compounds ALTER COLUMN composition_formula SET NOT NULL;

CREATE INDEX idx_compounds_composition_formula ON chemistry.compounds(composition_formula);
CREATE INDEX idx_compounds_normalized_formula ON chemistry.compounds(normalized_formula);
```

---

## 4. Required Molar Mass Regressions

Calculated via `CatalogElementMassProvider` using the authoritative Periodic Table catalogue:

| Code | Compound Name | Composition | Representative Molar Mass (g/mol) | Mass Kind | Dataset Version |
|---|---|---|---|---|---|
| `COMP-H2O` | Water | H₂O | 18.015 [18.01059, 18.01599] | `INTERVAL` | v1.1.0 |
| `COMP-CO2` | Carbon dioxide | CO₂ | 44.009 [44.00441, 44.01114] | `INTERVAL` | v1.1.0 |
| `COMP-NACL` | Sodium chloride | NaCl | 58.44 [58.436, 58.447] | `INTERVAL` | v1.1.0 |
| `COMP-H2SO4` | Sulfuric acid | H₂SO₄ | 98.072 [98.054, 98.131] | `INTERVAL` | v1.1.0 |
| `COMP-CACO3` | Calcium carbonate | CaCO₃ | 100.086 [100.062, 100.111] | `INTERVAL` | v1.1.0 |
| `COMP-GLUCOSE` | Glucose | C₆H₁₂O₆ | 180.156 [180.115, 180.187] | `INTERVAL` | v1.1.0 |
| `COMP-CUSO4` | Copper(II) sulfate | CuSO₄ | 159.602 [159.585, 159.670] | `INTERVAL` | v1.1.0 |
| `COMP-CUSO4-5H2O` | Copper(II) sulfate pentahydrate | CuSO₄·5H₂O | 249.677 [249.638, 249.750] | `INTERVAL` | v1.1.0 |
| `COMP-KMNO4` | Potassium permanganate | KMnO₄ | 158.032 [158.012, 158.036] | `INTERVAL` | v1.1.0 |

---

## 5. Verification Metrics

### Test Breakdown by Module

| Module | Executed | Failures | Errors | Skipped | Status |
|---|---|---|---|---|---|
| `identity-module` | **12** | **0** | **0** | **0** | SUCCESS |
| `chemistry-engine` | **80** | **0** | **0** | **0** | SUCCESS |
| `app` (Integration & Migration) | **6** | **0** | **0** | **0** | SUCCESS |
| **Total** | **98** | **0** | **0** | **0** | **SUCCESS** |

### Flyway Migration History (Clean Install & Upgrade)
- Total Applied Migrations: **9** (V1 through V9)
- V1: `create chemistry engine metadata`
- V2: `create periodic table core`
- V3: `seed periodic table core`
- V4: `correct periodic table scientific semantics`
- V5: `create extended element properties`
- V6: `seed extended element properties`
- V7: `create compound core schema`
- V8: `seed compound core catalogue`
- V9: `separate compound formula fields`
- Final Schema Version: `v9`

---

## 6. Release Gate Verdict

**PASS** — Phase 4A.1 is complete. Compound identity, formula semantics, authoritative molar-mass sourcing from the Periodic Table catalogue, PostgreSQL schema V9, and full data-integrity test suites pass. Chemical Classification may begin.
