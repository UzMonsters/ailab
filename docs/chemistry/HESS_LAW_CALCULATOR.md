# Hess Law Calculator

Phase 8B supports Hess-law combinations of existing calculable reactions.

## Vector Algebra

Each reaction is represented as an exact compound-and-state vector:

```text
compoundCode|state -> exact rational coefficient
```

Reactants are negative and products are positive. Multipliers are exact rational numbers. Reaction-vector equality and cancellation are performed with exact rational arithmetic, not decimal approximation.

## Supported Operations

The calculator supports:

- Reversing reactions with negative rational multipliers
- Scaling reactions with rational multipliers
- Adding component reaction vectors
- Cancelling exact intermediates
- Rejecting target equations that do not exactly match the resulting vector
- Summing `delta_r H deg`, `delta_r G deg`, `delta_r S deg` and `delta_r Cp deg` from component reaction results

## State Integrity

Cancellation is state-aware. A liquid-water term cannot cancel a gas-water term. If a combination attempts opposing cancellation across formula-identical compounds with different states, the request is rejected as a state-incompatible Hess cancellation.

## Derivation Output

The result includes:

- The requested target vector
- The resulting combined vector
- The component reactions and exact multipliers
- Intermediate cancellations
- Combined reaction properties
- A deterministic explanation of the method

## Limitations

Hess-law calculations reuse standard reaction properties already calculated from complete Phase 8A reference records. The calculator does not derive missing thermodynamic reference records, perform temperature correction, calculate equilibrium constants or handle phase-transition thermodynamics.
