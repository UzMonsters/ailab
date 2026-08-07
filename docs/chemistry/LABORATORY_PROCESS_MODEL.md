# Laboratory Process Model

Phase 13 adds framework-independent process-definition types under:

```text
com.ailab.chemistry.domain.laboratoryprocess
```

## Versioning

Process definitions are identified by `(processCode, processVersion)`. Versions start at 1. A published, terminal, or archived definition is immutable; changes require a new draft version rather than mutation in place.

## Steps

Steps define intent only:

```text
MEASURE, DISPENSE, TRANSFER, ADD, MIX, HEAT, COOL, HOLD, SAMPLE, OBSERVE, VENT
```

Step type names do not imply capability, safety, compatibility, or scientific result. A `HEAT` step is not executable merely because a hot plate exists.

## Dependency Semantics

The validator requires:

- unique step ids;
- at least one initial step;
- dependencies reference existing steps;
- no circular dependencies;
- every non-initial step is reachable from an initial step;
- explicit input and output ports;
- explicit material, equipment, container, or environment requirements;
- explicit material units;
- non-negative expected duration through the measurement `Duration` value object.

Optional steps are explicit. Mandatory steps remain mandatory and cannot be skipped by the state reducer.

## Phase 12 Requirements

Process definitions carry equipment profile ids, container profile ids, material identity, physical state, and volume requirements. Suitability is evaluated later by the Phase 13 service layer using Phase 12 services; the process model does not infer missing requirements from step names.
