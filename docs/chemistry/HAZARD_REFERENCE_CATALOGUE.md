# Hazard Reference Catalogue Architecture & Data Model

## Overview
The Hazard Reference Catalogue (`com.ailab.chemistry.domain.hazard`) provides versioned, provenance-tracked, scope-aware GHS reference safety classifications for chemical compounds.

## Core Architectural Principles
1. **Reference Data vs. Runtime Validation**: Stores evaluated GHS reference classifications and safety instructions. Does not perform runtime experiment blocking, chemical compatibility calculations, or explosion/leak detection (deferred to Laboratory Safety Module).
2. **Explicit Jurisdiction & Revision Scope**: Every classification record preserves `HazardClassificationSystem` (`UN_GHS`), revision (`UN-GHS-REV11-2025`), `frameworkScope = INTERNATIONAL_REFERENCE`, `implementationJurisdiction` (`EU` / `UNITED_STATES` / `SUPPLIER_SPECIFIC` / `UNSPECIFIED`), and `HazardSourceDocument`.
3. **Condition & Scope Scoping**: Classifications are bound to `HazardScope` preserving physical state, form, formulation, concentration range, temperature/pressure bounds. Missing classification data receives explicit `NOT_CLASSIFIED_BY_SOURCE` or `DATA_NOT_AVAILABLE` status and is never defaulted to "safe".
4. **Summary Hazard Flags**: Educational summary flags (`TOXIC`, `CORROSIVE`, `FLAMMABLE`, `EXPLOSIVE`, `OXIDIZER`, `RADIOACTIVE`, `CARCINOGENIC`, `IRRITANT`, `ENVIRONMENTAL_HAZARD`) are deterministically derived by `HazardSummaryDerivationEngine` and can be fully explained back to detailed GHS records.
