# Laboratory Environment

Phase 12 adds framework-independent environment suitability validation under:

```text
com.ailab.chemistry.domain.labenvironment
```

An environment snapshot may include ambient temperature, ambient pressure, relative humidity, ventilation mode, fume-hood state, atmosphere declaration, and observation timestamp.

## Relative Humidity

`RelativeHumidity` accepts values from 0 percent through 100 percent inclusive. Values below 0 percent or above 100 percent are rejected.

## Ventilation

Supported ventilation modes:

- `GENERAL_VENTILATION`
- `LOCAL_EXHAUST`
- `FUME_HOOD`
- `ISOLATED_ENCLOSURE`
- `NONE`

Fume-hood states:

- `AVAILABLE`
- `OPERATING`
- `UNAVAILABLE`
- `NOT_REQUIRED`

A fume hood marked `AVAILABLE` is not equivalent to `OPERATING`.

## Suitability Versus Safety

The environment calculator reports whether explicit ambient and ventilation requirements are satisfied. It does not claim that an operation is legally safe, universally safe, or compliant with all regulations.

## Exclusions

Phase 12 does not add runtime laboratory safety decisions, live monitoring, IoT ingestion, event processing, simulation state, or the Simulation Engine.
