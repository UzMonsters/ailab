# Runtime Laboratory Safety

## Overview

The `com.ailab.chemistry.domain.laboratorysafety` package provides a deterministic, governed safety validation layer wrapping Phase 14 simulation commands. Safety evaluation constrains execution by acting as pre-execution and post-calculation gates around scientific operation execution.

## Safety Evaluation Flow

```text
SimulationCommand
  │
  ▼
Process & State Validation
  │
  ▼
Phase 12 Apparatus & Environment Suitability
  │
  ▼
PRE-EXECUTION SAFETY GATE  ──(Blocked / Insufficient Data)──► Abort (No State Mutation)
  │
  ▼
Explicit Scientific Calculator Execution
  │
  ▼
Proposed State Delta & Conservation Validation
  │
  ▼
POST-CALCULATION SAFETY GATE  ──(Blocked)──────────────────► Abort (No State Mutation)
  │
  ▼
Atomic Transaction Commit
 (Typed Event + Calculation Audit + Safety Audit + State Projection)
```

## Evaluated Safety Stages

1. **`PRE_EXECUTION`**: Evaluates operating conditions, fume hood status, environment, equipment limits, and container compatibility *before* invoking scientific solvers.
2. **`POST_CALCULATION`**: Evaluates proposed simulation state deltas (e.g. calculated vessel temperatures, gas pressures, headspace, material combinations) *before* committing state changes.

## Decision Statuses

- `ALLOWED`: Execution proceeds to next stage.
- `ALLOWED_WITH_WARNINGS`: Execution proceeds, but safety warnings are recorded.
- `BLOCKED`: Command execution is blocked; transaction rolls back cleanly without state mutation.
- `INSUFFICIENT_DATA`: Missing required input fields for applicable safety rules block execution.

## Disclaimer

> [!IMPORTANT]
> Runtime safety is a deterministic educational/application policy engine. It does not constitute regulatory certification or replace laboratory-specific risk assessment, SDS review, institutional procedures, or qualified safety oversight.

