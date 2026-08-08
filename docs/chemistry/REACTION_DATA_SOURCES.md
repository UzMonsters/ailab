# Reaction Data Sources & Provenance

## Primary Sourced Reference Documents

### 1. CRC Handbook of Chemistry and Physics (104th Edition)
- **Source Identifier**: `CRC-HANDBOOK-104`
- **Title**: CRC Handbook of Chemistry and Physics, 104th Edition
- **Publisher**: CRC Press / Taylor & Francis Group
- **Edition / Version**: 104th Edition (2023)
- **Publication / Access Date**: 2023 / 2026-08-06
- **Fields Supplied**: `equation`, `primaryName`, `catalysts`, `conditions`, `directionality`
- **Reaction Coverage**: Standard inorganic, organic combustion, neutralization, hydration, and decomposition reactions
- **Conditions & Catalyst Coverage**: Thermal ignition requirements, catalysts (e.g. CuO for H2O2 decomposition), hydration states
- **Normalization Policy**: Verified via `DefaultFormulaParser` and `DefaultEquationBalancer` with minimal integer coefficients
- **Evidence Status**: `CURATED_AUTHORITATIVE`
- **Licensing & Reuse Note**: Public scientific domain metadata and reference facts checked in under `chemistry-engine/src/main/resources/chemistry-data/reaction-core-v1.json`

### 2. NIST Chemistry WebBook (SRD 69)
- **Source Identifier**: `NIST-WEBBOOK-2025`
- **Title**: NIST Chemistry WebBook, SRD 69
- **Publisher**: National Institute of Standards and Technology (NIST)
- **Edition / Version**: 2025 Release
- **Publication / Access Date**: 2025 / 2026-08-06
- **Fields Supplied**: `equation`, `directionality`, `speciesStates`
- **Reaction Coverage**: Hydrocarbon oxidation and gas phase combustion reactions (ethane, propane, butane, ethylene, acetylene, benzene)
- **Normalization Policy**: Stoichiometric verification with minimal whole-number ratio
- **Evidence Status**: `CURATED_AUTHORITATIVE`
- **Licensing & Reuse Note**: US Government open reference data
