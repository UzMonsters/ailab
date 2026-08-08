# Phase 7C Implementation Report — Acid-Base Reference Foundation

## Summary
Phase 7C establishes the framework-independent domain model and reference data catalogue for acid-base chemical species, conjugate pairs, dissociation steps, and equilibrium constants.

## Key Elements
- **Flyway Migrations**: V19 (schema) and V20 (seed data).
- **Core Domain Types**: `ChemicalSpecies`, `ConjugatePair`, `DissociationStep`, `EquilibriumConstant`, `AcidBaseProvenance`.
- **Preflight Regression**: Volume non-additivity test verified (`final volume = mixture mass / density`).
- **Semantic Evolution**: Updated in Phase 7D.1 via additive migration `V21__correct_acid_base_reference_semantics.sql` to separate `acidBaseRole` from `dissociationBehavior` and add `SPEC-NAOH`.
