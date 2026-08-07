# Phase 13 Process Events And State

Phase 13 was implemented as one complete module. The Simulation Engine and runtime Laboratory Safety were not started.

## Phase 12 Preflight

Phase 12 remains:

```text
PASS - Phase 12 complete; production repository, minimal operational dataset, equipment capability, calibration, container compatibility, environment suitability, PostgreSQL and regression gates pass. Laboratory Process and Simulation State work may begin.
```

V1 through V43 migrations remain immutable. Phase 13 adds only V44 and V45.

## Domains

Process definition:

```text
LaboratoryProcessDefinition, LaboratoryProcessVersion, LaboratoryProcessStatus,
LaboratoryProcessStep, ProcessStepId, ProcessStepType, ProcessStepDependency,
ProcessMaterialRequirement, ProcessEquipmentRequirement, ProcessContainerRequirement,
ProcessEnvironmentRequirement, ProcessValidationResult, ProcessValidationError,
LaboratoryProcessException, LaboratoryProcessValidator, LaboratoryProcessRepository
```

Events:

```text
LaboratoryEvent, LaboratoryEventId, LaboratoryEventType, LaboratoryEventSequence,
LaboratoryEventPayload, LaboratoryEventSource, CorrelationId, CausationId,
IdempotencyKey, LaboratoryEventStore
```

Simulation state:

```text
SimulationSession, SimulationSessionId, SimulationSessionStatus, SimulationState,
SimulationStateVersion, SimulationClock, ProcessExecutionState,
ProcessStepExecution, ProcessStepExecutionStatus, VesselState, MaterialPortion,
EquipmentAllocation, EnvironmentState, SampleState, SimulationStateResidual,
SimulationStateErrorCode, SimulationStateException, SimulationStateReducer,
SimulationStateRepository
```

`SimulationSession` is represented operationally by `SimulationState` plus the persisted `simulation_sessions` projection.

## Persistence

Additive migrations:

```text
V44__create_laboratory_process_and_simulation_state.sql
V45__create_laboratory_event_store.sql
```

V44 creates process definitions, process steps, dependencies, requirements, ports, simulation sessions, and snapshots. V45 creates append-only simulation events with unique sequence and idempotency constraints.

## Services

Internal APIs:

```text
LaboratoryProcessService
SimulationSessionService
```

The session service performs transactional append by checking idempotency, locking the current session projection, checking expected version, validating Phase 12 suitability when relevant, applying the pure reducer, appending the event, updating the projection, and writing snapshots at a fixed frequency.

## Verification Coverage

Process tests cover valid linear and branching graphs, duplicate step rejection, missing dependency rejection, cycle detection, unreachable-step rejection, optional-step semantics, published immutability, new draft version creation, explicit requirements, explicit units, and non-negative duration enforcement.

Reducer tests cover valid lifecycle, contiguous sequences, state-version increments, terminal immutability, dependency-controlled availability, invalid step transitions, optional and mandatory skip behavior, exclusive equipment allocation/release, material dispense, transfer, sampling, overfill rejection, excess-transfer rejection, bookkeeping-only mixing, and deterministic replay.

PostgreSQL integration tests cover service injection, V45 migration head, actual JDBC process lookup, actual event append and current-state retrieval, idempotent retry, idempotency conflict, stale-version rejection, incompatible-container rollback, concurrent append one-winner behavior, full replay, snapshot replay, and active production JDBC event store use.

Architecture tests cover framework-independent Phase 13 domain packages and absence of Simulation Engine/runtime safety APIs.

## Limitations

The module is a process and state foundation. It does not predict reactions, rates, equilibrium, heat flow, phase changes, or hazards. It does not control devices, ingest sensors, schedule work, provide REST/WebSocket APIs, or implement scenario search.

## Release Decision

PASS - Phase 13 complete; process-definition, event-integrity, state-transition, material-conservation, concurrency, replay, PostgreSQL and regression gates pass. The Simulation Engine may begin.
