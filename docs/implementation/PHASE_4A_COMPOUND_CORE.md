# Phase 4A Implementation Report — Compound Core Catalogue and Molar-Mass Engine

## Implementation Summary

Phase 4A implements the Compound Module core foundation: immutable compound identity, chemical formula linkage, elemental composition representation, names and aliases, optional external identifiers, net charge, exact molar-mass calculation with standard atomic weight interval propagation, persistent versioned compound catalogue, deterministic seed dataset (55 core educational compounds including isomers and hydrates), and internal lookup service API.

---

## Architecture

### Domain Layer (`domain.compound`) — Zero Framework Dependencies

| Class | Purpose |
|---|---|
| `CompoundId`, `CompoundCode` | Immutable value-object identifiers |
| `CompoundAlias`, `CompoundAliasRole` | Names with roles (IUPAC, common, historical) |
| `CompoundFormula` | `originalFormula`, `normalizedFormula`, and `compositionFormula` (Hill notation) |
| `CompoundComposition`, `CompoundElementCount` | `BigInteger` atom counts ordered by atomic number |
| `CompoundCharge` | Net ionic charge (separate from mass calculation) |
| `MolarMass`, `MolarMassKind`, `MolarMassCalculationBasis` | Interval-propagated molar mass with `MathContext.DECIMAL128` |
| `MolarMassCalculator`, `MolarMassCalculatorImpl` | Interval propagation engine |
| `ElementMassProvider`, `ElementMassData` | Port interface for atomic mass resolution from authoritative source |
| `Compound` | Aggregate root; enforces immutability and alias deduplication |
| `CompoundRepository` | Port interface for compound storage |
| `KnownCompoundRegistry` | Static seed specs for 55 compounds with Hill-notation builder |
| `CompoundErrorCode`, `CompoundException` | Structured error model |

### Authoritative Scientific Data Source
- Production Mass Source: Periodic Table Catalogue `v1.1.0` (`chemistry.periodic_table_catalog_versions`).
- Production Adapter: `CatalogElementMassProvider` in infrastructure layer (`infrastructure.persistence.compound`). Reads from `ElementRepository` backed by `v1.1.0` Periodic Table dataset.
- Test Fixture Adapter: `TestElementMassProvider` (under `src/test/java`). Used exclusively in unit tests.
- ArchUnit Guard: `compoundDomainShouldNotContainDuplicateAtomicMassTable` prevents duplicate atomic-mass tables in `domain.compound`.

---

## Key Design Decisions

### 1. Formula Is NOT Compound Identity

`compound_code` is the unique identity. Formula lookup returns a **list**. Both ethanol and dimethyl ether share Hill-notation `compositionFormula = "C2H6O"` and coexist as distinct compounds:

| Code | Primary Name | originalFormula | normalizedFormula | compositionFormula | Molar Mass |
|---|---|---|---|---|---|
| `COMP-ETHANOL` | Ethanol | `C2H5OH` | `C2H5OH` | `C2H6O` | 46.069 g/mol |
| `COMP-DIMETHYL-ETHER` | Dimethyl ether | `CH3OCH3` | `CH3OCH3` | `C2H6O` | 46.069 g/mol |

### 2. Distinct Formula Representations

- `originalFormula`: Raw user/catalog display string (e.g., `C2H5OH`, `CuSO4.5H2O`).
- `normalizedFormula`: Parser-normalized format preserving structural/hydrate grouping (e.g., `C2H5OH`, `CuSO4·5H2O`).
- `compositionFormula`: Hill-notation formula derived strictly from elemental composition (e.g., `C2H6O`, `CuH10O9S`).

### 3. Interval Molar Mass Propagation

When any constituent element has an interval standard atomic weight, the compound molar mass propagates lower and upper bounds independently:
- Water H₂O: representative `18.015 g/mol`, lower `18.01059`, upper `18.01599`, kind `INTERVAL`
- CuSO₄·5H₂O: representative `249.677 g/mol`, lower `249.638`, upper `249.750`, kind `INTERVAL`

---

## Database Schema (V7, V8, V9)

- `V7__create_compound_core_schema.sql`: Core tables under `chemistry` schema.
- `V8__seed_compound_core_catalogue.sql`: 55 compounds seeded.
- `V9__separate_compound_formula_fields.sql`: Added `composition_formula` column, backfilled Hill-notation keys, indexed `normalized_formula` and `composition_formula`.

---

## Release Gate Verdict

**PASS** — Phase 4A/4A.1 complete. Final pre-classification schema reaches V9.
