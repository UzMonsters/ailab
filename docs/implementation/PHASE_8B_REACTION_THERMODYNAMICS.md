# Phase 8B Reaction Thermodynamics

## Result

Phase 8B adds an internal standard reaction thermodynamics service and pure domain calculators for formation-sum reaction properties and Hess-law combinations.

## Internal API

`ReactionThermodynamicsService` exposes:

- `calculate(reactionCode, conditions, stateOverrides)`
- `calculateHessLaw(request)`
- `evaluateCatalogueCoverage(conditions)`

The service flow is:

```text
ReactionThermodynamicsService
-> ReactionCatalogService
-> ThermodynamicReferenceService
-> ReactionThermodynamicsCalculator
```

There is no REST endpoint, gRPC endpoint or calculation persistence.

## Domain Model

Key domain types include:

- `ReactionThermodynamicsRequest`
- `ReactionThermodynamicsResult`
- `ReactionThermodynamicTerm`
- `ReactionThermodynamicProperty`
- `ReactionThermodynamicCoverage`
- `ReactionThermodynamicStatus`
- `ThermodynamicSign`
- `HessReactionTerm`
- `HessLawRequest`
- `HessLawResult`
- `ThermodynamicCalculationMethod`
- `ReactionThermodynamicsCalculator`
- `HessLawCalculator`

The thermodynamics domain remains framework independent.

## Coverage

The Phase 8A thermodynamic reference catalogue is intentionally limited. Phase 8B evaluates all 26 reaction-catalogue entries and reports incomplete coverage for missing compounds, unsupported states, missing property types and missing phase-specific records. It does not fabricate thermodynamic data.

At 298.15 K and 1 bar, the fully calculable catalogue reactions are the reactions whose exact compound/state terms are covered by `thermodynamic-reference-v1.0.0`.

## Verification Focus

Tests cover:

- Water formation and phase-sensitive gas/liquid water results
- Methane combustion term signs and explicit product states
- Carbon monoxide oxidation aggregation
- Reaction reversal and scaling
- Hess-law exact rational vector combination
- Exact intermediate cancellation
- Target mismatch rejection
- State-incompatible Hess cancellation rejection
- Missing compound/property/state/condition behavior
- Deterministic repeated execution
- Absence of temperature-correction and equilibrium-constant APIs
- PostgreSQL V28 startup and service injection

## Exclusions

Phase 8B intentionally excludes temperature correction, Kirchhoff's law, temperature-dependent heat capacity, equilibrium constants, nonstandard Gibbs energy, reaction quotient handling, calorimetry, phase-transition thermodynamics, kinetics, external endpoints and calculation persistence.
