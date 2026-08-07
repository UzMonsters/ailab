# Phase 7F Implementation Report - Monoprotic Acid-Base Titration Calculator

## Summary
Phase 7F adds a framework-independent monoprotic titration calculator and an injectable internal `TitrationCalculationService`. It supports strong acid / strong base, strong base / strong acid, weak acid / strong base, and weak base / strong acid aqueous systems using additive final volumes and exact-temperature reference constants.

## Domain Types
New acid-base domain types include `TitrationCalculator`, `TitrationRequest`, `TitrationPointRequest`, `TitrationPointResult`, `TitrationCurveResult`, `EquivalencePoint`, `TitrationResidual`, `TitrationSystemType`, `TitrationRegion`, `TitrationCalculationMethod`, `TitrationAssumption`, `TitrationSolverStatus`, `TitrationErrorCode`, and `TitrationException`.

## Calculation Semantics
Strong systems use stoichiometric neutralization and water autoionization. Weak-acid and weak-base systems use continuous charge-balance equations through the full curve, so near-equivalence points are solved by the same equilibrium model rather than switching to disconnected formulas.

Equivalence volume is derived from analyte moles and titrant concentration. Every post-addition concentration uses total volume (`Vanalyte + Vtitrant`).

## Service Flow
`TitrationCalculationServiceImpl` validates water solvent support, rejects amphiprotic and polyprotic systems, resolves catalogue species roles and dissociation behavior, retrieves required `Ka`, `Kb`, and `Kw` through `AcidBaseReferenceService`, and delegates arithmetic to `TitrationCalculator`.

`AcidBaseEquilibriumService` remains injectable in the service layer for consistency with the Phase 7D/7E acid-base service composition, while the titration domain calculator has no framework dependencies.

## Verification Coverage
Tests cover:

- strong acid / strong base initial, pre-equivalence, equivalence, and post-equivalence points;
- reverse strong base / strong acid monotonic pH decrease;
- weak acid / strong base initial, half-equivalence, equivalence, post-equivalence, continuity near equivalence, residuals, and convergence status;
- weak base / strong acid initial, half-equivalence pOH, equivalence, post-equivalence, monotonic pH decrease, and convergence status;
- deterministic curve ordering and characteristic points;
- duplicate volume rejection and unsupported weak acid / weak base rejection;
- service-level catalogue resolution and point/curve delegation;
- unsupported solvent, unsupported temperature, invalid quantities, and polyprotic rejection;
- Spring integration with PostgreSQL/Flyway V22 and service injection.

## Deferred Work
Phase 7F intentionally does not implement weak acid / weak base titrations, polyprotic titrations, indicator transitions, precipitation titrations, redox titrations, or activity-coefficient corrections.

## Migration Integrity
Phase 7F adds no Flyway migration. PostgreSQL remains at chemistry schema version V22, and V1-V22 checksums are preserved.
