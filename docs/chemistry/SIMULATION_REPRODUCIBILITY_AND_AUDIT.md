# Simulation Reproducibility and Audit

## Determinism & Hashes

The Simulation Engine guarantees complete reproducibility through deterministic input and output hashing (SHA-256):
- `input_hash`: Computed over operational target, specified model, parameters, dataset versions, and initial state projection.
- `result_hash`: Computed over the state delta, calculation trace, and conservation ledger.

## Audit Projection

Every scientific operation atomically writes an immutable audit record to `chemistry.simulation_calculation_audits`:
- `event_id` (Primary Key, referencing `simulation_events`)
- `session_id`
- `command_id`
- `operation_type`
- `model_identifier` & `model_version`
- `dataset_versions` (JSONB)
- `input_hash` & `result_hash`
- `calculation_trace` (JSONB)
- `conservation_ledger` (JSONB)
- `created_at`

## Read-Only Recalculation Audit

`SimulationEngineService.audit(sessionId, eventId)` provides a read-only recalculation audit capability:
- Fetches historical event & audit record.
- Re-executes the scientific operation against current reference datasets.
- Compares new calculation hashes and residuals against historical audit data.
- **Immutability Guarantee**: Recalculation audit never modifies committed event stream or historical simulation projections.
