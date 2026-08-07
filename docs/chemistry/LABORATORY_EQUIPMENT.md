# Laboratory Equipment

Phase 12 adds framework-independent equipment suitability validation under:

```text
com.ailab.chemistry.domain.equipment
```

An equipment profile is not suitable because of its display name or type. Suitability requires an explicit `EquipmentCapability` matching the requested capability type and quantity.

## Repository-Backed Evaluation

The pure calculator still accepts an `EquipmentSuitabilityRequest` containing an already selected `EquipmentReferenceProfile`. Production service evaluation can also accept an `EquipmentProfileSuitabilityRequest`; the service loads the active profile from `EquipmentReferenceRepository`, maps the PostgreSQL row set into domain records, and then delegates to the same pure calculator.

The local/prod/migration-test profile wires `JdbcEquipmentReferenceRepositoryAdapter`. Test and standalone domains can continue to use explicit in-memory repositories or direct profile fixtures.

If a selected profile id is absent or inactive, service evaluation returns `PROFILE_UNAVAILABLE` instead of falling back to taxonomy records or name inference.

## Capability Semantics

Each capability declares:

- capability type, such as `MEASURE`, `CONTROL`, `HEAT`, or `STIR`
- measured or controlled quantity, such as `MASS`, `TEMPERATURE`, `PH`, or `VOLUME`
- explicit operating range and unit
- resolution when relevant
- accuracy or uncertainty when available
- capacity when relevant
- calibration requirement
- environmental restrictions and provenance

Operating ranges are unit-aware in the domain API. The calculator does not convert unrelated units or infer capabilities from names.

## Accuracy, Resolution, And Uncertainty

Resolution is the smallest displayed or reportable increment. Accuracy is an absolute error bound. Uncertainty is an uncertainty statement. Phase 12 keeps them separate: a fine resolution does not prove accuracy, and missing accuracy or uncertainty remains unavailable.

## Calibration

Calibration evaluation uses the caller-supplied `evaluationTimestamp`. The pure calculator does not read the system clock.

Statuses:

- `VALID`
- `DUE_SOON`
- `EXPIRED`
- `NOT_REQUIRED`
- `MISSING`

Missing required calibration and expired calibration block suitability. Due-soon calibration produces a warning.

V1.1 seeds calibration requirements as internal governed policy only. It does not seed calibration completion records; callers must provide current calibration evidence when a capability requires it.

## Exclusions

Phase 12 does not add inventory, booking, maintenance, automatic calibration, live sensors, IoT, manufacturer control protocols, wear simulation, experiment execution, event processing, simulation state, or REST endpoints.
