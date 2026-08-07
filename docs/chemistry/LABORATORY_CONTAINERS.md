# Laboratory Containers

Phase 12 adds framework-independent container suitability validation under:

```text
com.ailab.chemistry.domain.container
```

Container suitability validates capacity, headspace, closure semantics, temperature and pressure limits, and explicit material-chemical compatibility records.

## Repository-Backed Evaluation

The pure calculator still accepts a `ContainerSuitabilityRequest` containing an already selected `ContainerProfile`. Production service evaluation can also accept a `ContainerProfileSuitabilityRequest`; the service loads the active profile from `ContainerReferenceRepository`, maps the PostgreSQL row set into domain records, and delegates to the same pure calculator.

The local/prod/migration-test profile wires `JdbcContainerReferenceRepositoryAdapter`. Test and standalone domains can continue to use explicit in-memory repositories or direct profile fixtures.

If a selected profile id is absent or inactive, service evaluation returns `PROFILE_UNAVAILABLE` instead of falling back to generic material or container-type inference.

## Capacity And Headspace

The calculator validates:

```text
0 <= actualVolume <= maximumWorkingVolume <= nominalCapacity
```

Fill fraction is:

```text
actualVolume / nominalCapacity
```

Headspace is:

```text
nominalCapacity - actualVolume
```

Sealed operations may declare required headspace. Insufficient headspace blocks suitability.

## Pressure And Temperature

Open containers are never treated as pressure-rated. Closed containers are also not pressure-rated unless an explicit pressure limit exists.

Temperature limits are mandatory when an operating temperature is evaluated. Pressure limits are mandatory when pressure containment is evaluated.

## Material Compatibility

Compatibility statuses are:

- `COMPATIBLE`
- `COMPATIBLE_WITH_LIMITS`
- `INCOMPATIBLE`
- `UNKNOWN`

`UNKNOWN` is always blocking. Compatibility is matched by explicit compound or chemical family, physical state, container material, and closure material when relevant. Concentration, temperature, contact-duration and physical-state limits remain part of the record when the source defines them.

Active production compatibility rows must include a source record id, source-defined contact-duration boundary, temperature bounds, and `source_defined_boundaries = TRUE`. V1.0 compatibility rows that lacked the corrected boundary evidence are kept historically but deactivated before V1.1 rows are seeded.

## Exclusions

The dataset does not provide pressure-vessel certification, heat-transfer rates, broad chemical-resistance tables, or inferred compatibility from generic material names.
