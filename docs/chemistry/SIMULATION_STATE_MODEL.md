# Simulation State Model

Phase 13 adds framework-independent state types under:

```text
com.ailab.chemistry.domain.simulationstate
```

## State Contents

`SimulationState` tracks:

- session id, status, clock, and version;
- process-step execution state;
- vessel contents;
- material portions;
- equipment allocations;
- environment state placeholder.

Session statuses:

```text
CREATED, READY, RUNNING, PAUSED, COMPLETED, CANCELLED, FAILED
```

Step statuses:

```text
PENDING, AVAILABLE, RUNNING, COMPLETED, SKIPPED, FAILED
```

## Reducer Rules

The pure reducer enforces exact event sequence, one state-version increment per accepted event, terminal-state immutability, dependency-gated step starts, running-only step completion/failure, optional-only skipping, exclusive equipment allocation, resource release, and material conservation.

Material portions include compound code, quantity, unit, physical state, and source event. Dispense, transfer, and sample events preserve quantity and compound identity. Vessel contents cannot become negative and mL bookkeeping cannot exceed working volume.

Mixing is bookkeeping only. It does not trigger reaction, equilibrium, heat, phase, or hazard calculations.

## Snapshot And Replay

The service can rebuild state from the full event stream or from the latest snapshot plus later events. Full replay and snapshot replay must produce equal states. Snapshot checksums are stored as integrity metadata, but events remain authoritative and snapshots may be deleted and regenerated.

## Boundary

Phase 13 manages state integrity only. It does not implement the Simulation Engine, automatic chemistry prediction, runtime safety rules, equipment control, sensor ingestion, scheduling, REST, WebSocket, or UI projections.
