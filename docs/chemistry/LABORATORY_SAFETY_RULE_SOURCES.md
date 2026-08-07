# Laboratory Safety Rule Sources

## Rule Provenance Requirements

Every production safety rule stored in `chemistry.laboratory_safety_rules` requires explicit scientific or standard provenance:
- **`rule_id`**: Stable rule identifier (e.g. `SAFE-FUME-HOOD-REQ`).
- **`rule_version`**: Integer version tracking rule modifications.
- **`source_type`**: `EXTERNAL_STANDARD`, `MANUFACTURER_SPECIFICATION`, `SAFETY_DATA_SHEET`, or `INTERNAL_GOVERNED_POLICY`.
- **`source_identifier`**: Sourced standard body or published manual (e.g., `AI-LAB-POLICY-001`).
- **`source_citation`**: Full text citation of standard or reference manual.
- **`source_version_date`**: Release/effective date of the reference standard.
- **`evidence_status`**: Governance status (e.g., `VERIFIED`).

## Active Governed Production Rules

### SAFE-FUME-HOOD-REQ
- **`sourceType`**: `INTERNAL_GOVERNED_POLICY`
- **`sourceIdentifier`**: `AI-LAB-POLICY-001`
- **`sourceCitation`**: `AI Laboratory Conservative Operating Policy (OSHA 29 CFR 1910.1450 contextual guidance)`
- **`exactClauseSection`**: `Section 4.1 - Fume Hood Controls`
- **`supportedClaim`**: `AI-LAB-POLICY-001 requires an operating fume hood for all STOICHIOMETRIC_REACTION, EQUILIBRIUM_REACTION and KINETIC_PROGRESS operations supported by the MVP.`
- **`sourceUrl`**: `https://www.osha.gov/laws-regs/regulations/standardnumber/1910/1910.1450`
- **Policy Statement**: This is a conservative internal AI Laboratory application operating policy. OSHA 29 CFR 1910.1450 provides contextual guidance for Chemical Hygiene Plans and engineering controls, but OSHA does NOT mandate a blanket requirement that all chemical reactions use a fume hood.

## Inactive Historical Rules

### SAFE-TEMP-LIMIT-GLASS
- **Status**: Inactive since `V51` (dataset `laboratory-safety-reference-v1.1.0`).
- **Reason**: ASTM E438-92 establishes material specifications for laboratory glasses but does not establish a universal 773.15 K (500 °C) operating limit for all glass apparatus.
- **Runtime Policy**: Runtime temperature limits are composed directly from explicit Phase 12 apparatus profiles (`ContainerProfile`, `EquipmentProfile`) via `ContainerService` / `EquipmentService`.

### SAFE-PRESSURE-LIMIT-CONTAINER
- **Status**: Inactive since `V51` (dataset `laboratory-safety-reference-v1.1.0`).
- **Reason**: ISO 11114-1 provides gas cylinder compatibility guidelines but does not establish a universal 200 kPa working pressure limit for all laboratory containers.
- **Runtime Policy**: Runtime pressure limits are composed directly from explicit Phase 12 container working pressure ratings via `ContainerService`.

