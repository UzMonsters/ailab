# Acid-Base Reference Foundation Architecture

## Overview
The Acid-Base Reference Foundation provides versioned, provenance-backed chemical species, conjugate acid-base pairs, dissociation steps, and equilibrium constants ($K_a, K_b, K_w$) for aqueous systems.

## Data Model & Separation of Concerns

### 1. Acid-Base Role vs. Dissociation Behavior
Each `ChemicalSpecies` explicitly separates role from strength:
- **`acidBaseRole`**: `ACID`, `BASE`, `AMPHIPROTIC`, `NEUTRAL`.
- **`dissociationBehavior`**: `STRONG_ELECTROLYTE`, `WEAK_ELECTROLYTE`, `NON_ELECTROLYTE`, `AUTOIONIZING_SOLVENT`, `NOT_APPLICABLE`, `UNKNOWN`.

Example:
- `SPEC-NAOH`: `role = BASE`, `dissociationBehavior = STRONG_ELECTROLYTE`, linked to `COMP-NAOH`.
- `SPEC-HCL`: `role = ACID`, `dissociationBehavior = STRONG_ELECTROLYTE`, linked to `COMP-HCL`.
- `SPEC-CH3COOH`: `role = ACID`, `dissociationBehavior = WEAK_ELECTROLYTE`, linked to `COMP-CH3COOH`.
- `SPEC-H3O-PLUS`, `SPEC-OH-MINUS`, `SPEC-NA-PLUS`, `SPEC-CL-MINUS`, and other already dissociated ions: `dissociationBehavior = NOT_APPLICABLE`.
- `SPEC-H2O`: `role = AMPHIPROTIC`, `dissociationBehavior = AUTOIONIZING_SOLVENT`.

### 2. Strong Electrolytes
Strong electrolytes do NOT store dummy $K = \text{null}$ or infinite equilibrium constant records in `equilibrium_constants`. Complete dissociation is modeled explicitly in calculation algorithms using exact stoichiometry.

### 3. Flyway Migrations & Semantics
- `V19__create_acid_base_reference_foundation.sql`: Initial schema.
- `V20__seed_acid_base_reference_foundation.sql`: Initial seed dataset.
- `V21__correct_acid_base_reference_semantics.sql`: Additive migration adding `dissociation_behavior`, seeding `SPEC-NAOH`, updating strong electrolytes, and refining dataset provenance.
- `V22__correct_acid_base_dissociation_semantics.sql`: Additive migration correcting already ionic species to `NOT_APPLICABLE` and water to `AUTOIONIZING_SOLVENT`.
