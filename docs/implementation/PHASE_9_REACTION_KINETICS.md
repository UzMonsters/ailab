# Phase 9 Reaction Kinetics Implementation

Phase 9 implements a complete foundational Reaction Kinetics module in the modular monolith.

## Architecture & Design

```text
ReactionKineticsService (API)
  -> ReactionKineticsServiceImpl (Service Layer Implementation in com.ailab.chemistry.service)
  -> ReactionCatalogService (API)
  -> KineticProfileRepository (Domain Interface in com.ailab.chemistry.domain.kinetics)
  -> pure ReactionKineticsCalculator (Pure Domain Calculator in com.ailab.chemistry.domain.kinetics)
```

- Domain types are framework-independent in `com.ailab.chemistry.domain.kinetics`.
- `ReactionKineticsServiceImpl` is strictly classified as a service-layer implementation in `com.ailab.chemistry.service`.
- Persistence adapters use Spring `@Profile("!test & !standalone-engine")` JDBC for PostgreSQL Flyway database access, while in-memory repositories serve standalone/test profiles.

## Database Migrations

- `V31__create_kinetic_reference.sql`: Creates `chemistry.kinetic_profiles` and `chemistry.kinetic_rate_law_terms` tables.
- `V32__seed_kinetic_reference.sql`: Seeds initial kinetic reference profiles.
- `V33__correct_kinetic_reference_integrity.sql`: Deactivates synthetic global profiles and adds missing source metadata columns.
- `V34__deactivate_and_seed_elementary_kinetics.sql`: Deactivates old non-record-specific profiles, seeds radical compounds (`COMP-RAD-H`, `COMP-RAD-OH`, `COMP-RAD-O`) with compound components, seeds exact elementary reactions (`RXN-ELEM-H-O2-PROPAGATION`, `RXN-ELEM-CO-OH-PROPAGATION`), widens normalized reference-rate storage, records original source units, conversion factor, pressure range, and bath gas, and seeds exact NIST detail-record elementary kinetic profiles (`KP-ELEM-H-O2-PIRRAGLIA-1989-REC3`, `KP-ELEM-CO-OH-WOOLDRIDGE-1994-REC1`).

## Release Integrity

- `KP-ELEM-H-O2-PIRRAGLIA-1989` and `KP-ELEM-OH-CO-WOOLDRIDGE-1994` remain present for audit but inactive.
- Active replacements use `1989PIR/MIC282:3` and `1994WOO/HAN741-748:1`, including exact record suffixes.
- Active rate-law participants equal the reactants in their referenced elementary reactions.
- No active elementary kinetic profile is bound to `RXN-WATER-SYNTHESIS` or `RXN-CO-OXIDATION`.

## Verification & Test Results

- All unit and integration tests pass cleanly across all modules (`identity-module`, `chemistry-engine`, `app`).
- Flyway migrations V1 through V34 execute cleanly.
