# Compound Module Core Architecture & Data Sourcing

## Overview
The Compound Module (`com.ailab.chemistry.domain.compound`) provides immutable compound identity, elemental composition representation, formula parsing and searching, and exact molar-mass calculation with standard atomic-weight interval propagation.

## Atomic Mass Sourcing
- **Sole Production Source**: Periodic Table Catalogue (Phase 3A / 3A.1).
- **Interface Port**: `ElementMassProvider` (`domain.compound`).
- **Production Infrastructure Adapter**: `CatalogElementMassProvider` (`infrastructure.persistence.compound`). Reads from `ElementRepository` backed by `v1.1.0` Periodic Table dataset.
- **Test Fixture Provider**: `TestElementMassProvider` (under `src/test/java`). Used exclusively in unit tests and standalone fixtures.
- **Architectural Guard**: ArchUnit rule `compoundDomainShouldNotContainDuplicateAtomicMassTable` prevents duplicate atomic-mass tables in `domain.compound`.

## Formula Representations
1. `originalFormula`: User/catalog input representation (e.g. `C2H5OH`, `CuSO4.5H2O`).
2. `normalizedFormula`: Parser-normalized format preserving group structures and hydrates (e.g. `C2H5OH`, `CuSO4·5H2O`). Indexed in PostgreSQL.
3. `compositionFormula`: Hill-notation formula derived strictly from elemental composition (e.g. `C2H6O` for isomers, `CuH10O9S` for hydrate). Indexed in PostgreSQL for isomer search.

## Database Schema
- Schema: `chemistry`
- Version: `V9` (`V9__separate_compound_formula_fields.sql`)
- Tables: `compounds`, `compound_aliases`, `compound_components`, `compound_external_identifiers`, `compound_catalog_versions`
