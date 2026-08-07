# Scientific Operation Events

## Typed Event Types

Each successful scientific operation produces exactly one atomic event in `chemistry.simulation_events`:

1. `STOICHIOMETRIC_REACTION_APPLIED`
2. `EQUILIBRIUM_REACTION_APPLIED`
3. `KINETIC_PROGRESS_APPLIED`
4. `THERMAL_OPERATION_APPLIED`
5. `GAS_STATE_CHANGED`
6. `PHASE_TRANSITION_APPLIED`
7. `ELECTROLYSIS_APPLIED`
8. `BOOKKEEPING_MIX_APPLIED`

## Event Payload Structure

`ScientificOperationAppliedPayload` retains complete traceability:
- Operation Type
- Command ID
- Process Code / Version / Step ID
- Scientific Model Selection & Calculation Method
- Reaction / Profile Identifier
- Dataset Versions
- Input values, units, and assumptions
- Solver status & iteration counts
- State Delta & Conservation Ledger
- Deterministic Input & Result Hashes
- Event & Engine Schema Versions

## Event Replay Semantics

During normal replay, the engine applies the recorded `SimulationStateDelta` directly to update simulation state. It does NOT rerun scientific calculators during standard event stream replay. This ensures absolute immutability of historical simulation projections even across dataset or calculator upgrades.
