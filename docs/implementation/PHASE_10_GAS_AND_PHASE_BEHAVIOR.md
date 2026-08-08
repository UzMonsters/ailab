# Phase 10 Gas And Phase Behavior

Phase 10 was implemented as one module after Phase 9. Electrochemistry and the Simulation Engine were not started.

## Phase 9 Preflight

The exact Phase 9 release decision remains:

```text
PASS — Phase 9 release integrity complete; active profiles reference exact elementary reactions, use correct participants, preserve source units, normalize units correctly and satisfy source-specific Arrhenius invariants. Gas Laws and Phase Transition work may begin.
```

The corrected kinetic dataset is registered additively as:

```text
kinetic-reference-v1.1.0
```

V1 through V34 are not edited.

## Domain

Gas domain package:

```text
com.ailab.chemistry.domain.gas
```

Phase behavior domain package:

```text
com.ailab.chemistry.domain.phasebehavior
```

Service implementations:

```text
GasLawServiceImpl
PhaseBehaviorServiceImpl
```

These are service-layer implementations. Pure calculators do not depend on Spring, JDBC, Flyway or HTTP.

## Migrations

```text
V35__register_corrected_kinetic_dataset_version.sql
V36__create_phase_behavior_reference.sql
V37__seed_phase_behavior_reference.sql
```

Tables include dataset versions, source documents, transition records, Antoine correlations and phase boundary points with positive-value checks, phase checks, uniqueness constraints, foreign keys and indexes.

## Verification

The tests cover:

```text
ideal gas and explicit-Z state solving
overdetermined residual validation
mixtures and partial pressures
gas density and molar mass
explicit transformation constraints
transition heat signs
Antoine saturation pressure and boiling-point solving
validity-boundary handling
triple and critical boundary rejection
heating and reverse cooling paths
manifest/SQL/PostgreSQL equivalence
service injection
architecture isolation
excluded API absence
```

PostgreSQL verification used the local PostgreSQL fallback when Docker/Testcontainers was unavailable.
