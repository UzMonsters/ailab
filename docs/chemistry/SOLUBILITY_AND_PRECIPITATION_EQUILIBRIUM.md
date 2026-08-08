# Solubility and Precipitation Equilibrium

Phase 7J adds Ksp-based solubility and single-solid precipitation calculations
for a small versioned reference subset.

## Standard State

Solubility products use dimensionless aqueous activities:

```text
ai = gamma_i * ci / c0
c0 = 1 mol/L
Ksp = product(ai ^ coefficient_i)
Qsp = product(ai ^ coefficient_i)
```

In ideal mode, all activity coefficients are 1. In Davies mode, coefficients
come from the same Davies convention introduced in Phase 7I and are solved
self-consistently with the solubility or precipitation extent.

## Saturation

Given dissolved ion concentrations, the calculator validates the requested
ions against the dissolution record, calculates Qsp, and compares Qsp/Ksp with
an explicit decimal tolerance.

Statuses:

- `UNSATURATED`: Qsp is below Ksp outside tolerance.
- `SATURATED`: Qsp and Ksp match within tolerance.
- `SUPERSATURATED`: Qsp is above Ksp outside tolerance.
- `OUTSIDE_ACTIVITY_MODEL_RANGE`: Davies validity is exceeded.

Zero concentration for any required ion gives Qsp = 0 and an unsaturated
result.

## Molar Solubility

Pure-water and common-ion solubility use a bounded deterministic bisection
solver over dissolution extent. The solver applies each dissolution coefficient
from the reference record, so it is not hard-coded to 1:1 salts.

For a solid:

```text
solid <=> nu1 Ion1 + nu2 Ion2 + ...
```

the concentration of each product ion is:

```text
ci(final) = ci(initial) + nui * s
```

Common ions are therefore preserved as initial concentration terms and reduce
the reported molar solubility when they push Qsp closer to Ksp.

## Precipitation After Mixing

Single-solid precipitation combines explicit ion moles in an additive final
volume. If the initial Qsp is not supersaturated, precipitated moles are zero.
If Qsp is supersaturated, the calculator solves the precipitation extent:

```text
ci(final) = ci(initial) - nui * x
```

The extent is bounded by available ion amounts, so a limiting ion cannot be
consumed past zero. When the catalogue molar mass is available, precipitate
mass is reported from:

```text
mass = precipitated moles * molar mass
```

## Activity Correction

Supported modes:

- `IDEAL`
- `DAVIES`

Davies mode is limited to water at 25 C and ionic strength from 0 through 0.5
mol/L. The calculation includes all charged dissolution ions and spectator
ions in ionic strength. Requests outside the range fail explicitly.

## Residuals

Results report the absolute Ksp residual and ion-balance residual. Bounded
solver paths also report iteration count and solver status.

## Exclusions

Phase 7J does not model simultaneous precipitates, selective precipitation,
carbonate or phosphate acid-base coupling, complex ions, gas exchange,
kinetics, supersaturation delay, temperature interpolation, Pitzer/SIT,
precipitation titration, REST endpoints, persisted calculations, graphing, or
colligative properties.
