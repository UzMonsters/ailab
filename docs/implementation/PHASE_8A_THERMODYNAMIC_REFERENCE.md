# Phase 8A Thermodynamic Reference

Phase 8A implements the thermodynamic reference foundation without reaction thermodynamic calculations.

## Implementation

- Added molar energy and molar entropy measurement primitives with J/mol and kJ/mol or J/(mol*K) and kJ/(mol*K) conversions.
- Added framework-independent thermodynamic domain types for profiles, property records, conditions, dataset version, provenance, evidence status and repository access.
- Added an internal `ThermodynamicReferenceService`.
- Added an in-memory repository for test and standalone-engine profiles.
- Added a JDBC repository for PostgreSQL-backed runtime profiles.
- Added V27/V28 additive Flyway migrations after the Phase 7J V26 baseline.
- Added a checked-in manifest at `Backend/chemistry-engine/src/main/resources/chemistry-data/thermodynamic-reference-v1.json`.

## Verification Scope

Tests cover compound references, phase-sensitive records, negative and zero formation values, positive heat capacity validation, unit conversions, exact-condition lookup, missing-data behavior, duplicate-record rejection, provenance completeness, PostgreSQL migration and service injection, and architecture isolation.

## Exclusions

The phase intentionally excludes reaction enthalpy, reaction entropy, reaction Gibbs energy, Hess's law, spontaneity, equilibrium constants, temperature corrections, calorimetry, phase-transition calculations, kinetics and persistence of user calculations.
