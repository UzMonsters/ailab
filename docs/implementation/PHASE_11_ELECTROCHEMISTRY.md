# Phase 11 Electrochemistry

Phase 11 was implemented as one complete module after Phase 10. Laboratory Equipment, Environment and the Simulation Engine were not started.

## Phase 10 Preflight

The exact Phase 10 release decision remains:

```text
PASS — Phase 10 complete; gas-state, mixture, compressibility, phase-transition, vapour-pressure, heating-path, reference-data, PostgreSQL and regression gates pass. Electrochemistry may begin.
```

Gas Laws documentation clarifies that constant amount is common to all supported transformation models.

## Domain And Service

Pure domain package:

```text
com.ailab.chemistry.domain.electrochemistry
```

Service API and implementation:

```text
ElectrochemistryService
ElectrochemistryServiceImpl
```

The implementation remains outside the domain package. The domain depends on measurement primitives and does not depend on Spring, Jakarta Persistence, JDBC, Flyway or HTTP.

## Migrations

```text
V38__create_electrochemical_reference.sql
V39__seed_electrochemical_reference.sql
```

V38 creates electrochemical dataset, source, condition, standard-potential and participant tables and additively inserts missing catalogue species. V39 seeds `electrochemical-reference-v1.0.0`.

## Verification Coverage

Tests cover half-reaction atom and charge balance, participant charge and phase identity, Daniell cell roles, standard potential, two-electron transfer, reversal, scaling without potential multiplication, standard hydrogen electrode convention, Nernst activity handling, pure-solid exclusion, Davies validity failure, Faraday-law electrolysis, current efficiency, service injection, manifest/SQL/PostgreSQL equivalence and architecture exclusions.

## Limitations

No potentials are fabricated for missing records. No arbitrary half-reaction balancing, kinetics, corrosion, transport, equipment, REST endpoint or calculation persistence was added.
