# Extended Element Properties Domain Specification

## Overview

The Extended Element Properties module extends the Periodic Table domain with valencies, oxidation states, electronegativities, radii, density data, phase transition data, element appearance, scientific evidence statuses, and field-level provenance across all 118 chemical elements.

## Reconciled Database Schema (9 Tables)

The `V5__create_extended_element_properties.sql` migration creates exactly **9 tables** under the `chemistry` schema:

1. `chemistry.element_property_dataset_versions` (Primary Key: `id`)
2. `chemistry.element_property_profiles` (Primary Key: `id`, FK to `chemistry.elements`)
3. `chemistry.element_valencies` (Primary Key: `id`, FK to `element_property_profiles`)
4. `chemistry.element_oxidation_states` (Primary Key: `id`, FK to `element_property_profiles`)
5. `chemistry.element_electronegativities` (Primary Key: `id`, FK to `element_property_profiles`)
6. `chemistry.element_radii` (Primary Key: `id`, FK to `element_property_profiles`)
7. `chemistry.element_density_data` (Primary Key: `id`, FK to `element_property_profiles`)
8. `chemistry.element_phase_transitions` (Primary Key: `id`, FK to `element_property_profiles`)
9. `chemistry.element_appearance` (Primary Key: `id`, FK to `element_property_profiles`)

## Core Domain Concepts

### 1. Measurement Extension Value Objects
- **Length** (`picometer`, `nanometer`, `meter` canonical). Immutable, non-negative `BigDecimal` value object.
- **Density** (`kilogramPerCubicMeter` canonical, `gramPerCubicCentimeter`, `gramPerLiter`). Immutable, strictly positive (> 0) `BigDecimal` value object.

### 2. Element Property Profile Aggregate
- `ElementPropertyProfile` is an aggregate root encapsulating property records for a given element atomic number and symbol under a specific `PropertyDatasetVersion`.
- Includes collections for:
  - `Valency`: combining capacities (non-negative integer, `isCommon` flag).
  - `OxidationState`: signed integers (-7 to +8), flagged as common, uncommon, or predicted.
  - `Electronegativity`: Pauling primary scale, explicit positive value.
  - `ElementRadius`: empirical atomic, calculated atomic, covalent single bond, van der Waals, metallic, and ionic radii. Ionic radius requires `IonicRadiusContext` with non-zero charge and coordination number/spin state.
  - `ElementPhysicalProperties`: encapsulates `DensityDatum` (strictly positive density, reference conditions) and `PhaseTransitionDatum` (melting, boiling, sublimation with behavior flags).
  - `ElementAppearance`: normalized color name and descriptive text.

### 3. Provenance & Evidence Status
- Every property record retains a `ScientificEvidenceStatus` (`EVALUATED`, `MEASURED`, `CONVENTIONAL`, `ESTIMATED`, `PREDICTED`, `PROVISIONAL`, `UNKNOWN`, `NOT_APPLICABLE`).
- Every property record retains field-level `PropertyProvenance` tracking `sourceIdentifier`, `sourceTitle`, `publisher`, `edition`, `accessDate`, `methodology`, and `license`.

## Architecture & Zero Implicit Fallbacks
- The domain layer is pure Java without Spring or ORM annotations.
- Production repository adapter `ElementPropertyRepositoryImpl` relies on JPA and fails fast if persistence is unavailable.
- In-memory adapter `InMemoryElementPropertyRepository` is isolated behind `@Profile({"test", "standalone-engine"})`.
