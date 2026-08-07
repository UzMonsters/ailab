# Phase 7J Solubility Equilibrium

Phase 7J adds framework-independent solubility domain types, a stateless
calculator, service wiring, additive PostgreSQL reference migrations, and
regression coverage for Ksp, saturation, molar solubility, common-ion behavior,
single-solid precipitation, and Davies activity correction.

## Implementation

- Domain package: `com.ailab.chemistry.domain.solubility`
- API: `SolubilityEquilibriumService`
- Service: `SolubilityEquilibriumServiceImpl`
- Repositories: `SolubilityReferenceRepository`, in-memory adapter, and JPA
  adapter
- Migrations: `V25__create_solubility_equilibrium_reference.sql` and
  `V26__seed_solubility_equilibrium_reference.sql`

The service flow is:

```text
SolubilityEquilibriumService
-> SolubilityReferenceRepository
-> IonicActivityService / ActivityParameterSetRepository
-> CompoundCatalogService
-> SolubilityEquilibriumCalculator
```

`IonicActivityService` is invoked at the service boundary for activity-model
support and species-charge validation. The pure calculator remains independent
of Spring and persistence.

## Numerical Policy

The solver uses deterministic bisection with `MathContext.DECIMAL128`.
Activity powers for Davies reuse `AcidBaseDecimalMath.tenPower`; no second
Java transcendental implementation is added for acid-base activity powers.
Ksp comparison uses an explicit decimal tolerance.

For very small Ksp values, residual stopping uses a target-relative tolerance
with a tiny absolute floor so small hydroxide products are not accepted at
zero-like precision.

## PostgreSQL

V25 creates version, source, equilibrium, dissolution-term, and constant
tables. Compound references use the existing compound composite key
`(compound_code, compound_catalog_version_id)`. V26 seeds three existing
catalogue solids and adds only required aqueous metal ion species.

## Verification Coverage

Tests cover:

- Qsp below, at, and above Ksp
- zero ion concentration
- 1:1, 1:2, and 1:3 pure-water solubility
- common-ion reduction
- precipitation after mixing and limiting-ion bounds
- precipitate mass from molar mass
- ideal versus Davies deterministic difference
- Davies range violation
- invalid charge-balanced records and missing Ksp
- unsupported temperature and invalid species
- residuals, convergence, deterministic repeat execution
- PostgreSQL V26 migration and semantic charge balance

## Limitations

The phase intentionally excludes simultaneous precipitates, selective
precipitation, acid-base-coupled solubility, complexation, gas exchange,
kinetics, temperature interpolation, precipitation titration, REST, graphing,
and persisted calculation history.
