# Polyprotic Titration Calculator

The polyprotic titration calculator provides stateless aqueous titration points,
curves, and characteristic points for supported diprotic acid-base families.

## Scope

Supported systems:

- diprotic acid with strong monobasic base;
- fully deprotonated diprotic salt with strong monoprotic acid;
- intermediate amphiprotic salt with strong monoprotic acid;
- intermediate amphiprotic salt with strong monobasic base.

Initial supported families:

- carbonic: `H2CO3 / HCO3- / CO3^2-`;
- sulfuric: `H2SO4 / HSO4- / SO4^2-`, with first dissociation complete and
  `Ka2` from the acid-base reference catalogue.

No weak titrants, mixed families, triprotic systems, indicators, activity
coefficients, precipitation, redox, complexometric titration, or experimental
noise are included.

## Calculation Contract

All calculations use additive volume:

```text
Vtotal = Vanalyte + Vtitrant
```

For each titration point, the calculator:

1. computes analytical family moles from the analyte concentration and volume;
2. computes titrant moles from the titrant concentration and added volume;
3. computes total analytical family concentration from `Vtotal`;
4. computes fixed spectator-ion charge concentration from analyte counterions
   and titrant spectator ions;
5. solves hydronium concentration with the Phase 7G polyprotic distribution
   and charge-balance model;
6. reports pH, pOH, species fractions, species concentrations, constants,
   assumptions, residuals, solver status, and titration region.

The curve implementation is not Henderson-Hasselbalch-only. Half-equivalence
and equivalence pH values are outcomes of the continuous equilibrium solve, not
hard-coded values.

## Equivalence Points

For a diprotic acid with analytical moles `nA` and monobasic titrant
concentration `Ct`:

```text
Veq1 = nA / Ct
Veq2 = 2 * nA / Ct
```

Reverse protonation titrations expose the same two protonation equivalents in
the opposite pH direction.

## Regions

The calculator classifies points using an explicit decimal volume tolerance:

- `INITIAL`
- `BEFORE_FIRST_EQUIVALENCE`
- `FIRST_HALF_EQUIVALENCE`
- `FIRST_EQUIVALENCE`
- `BETWEEN_EQUIVALENCE_POINTS`
- `SECOND_HALF_EQUIVALENCE`
- `SECOND_EQUIVALENCE`
- `AFTER_SECOND_EQUIVALENCE`

## Numerical Contract

The hydronium solve uses one deterministic bounded bisection method inherited
through the polyprotic equilibrium calculator. The calculation accepts pH values
outside 0-14 when the equilibrium model and strong-reagent excess imply them.

Documented tolerances:

- mass residual: less than `1E-14 mol/L` in the regression suite;
- charge residual: less than `1E-12 mol/L` in the regression suite;
- equivalence boundary volume tolerance: request-controlled decimal liters,
  defaulting to `1E-8 L`.

`AcidBaseDecimalMath` remains the single acid-base logarithm and base-10 power
utility.
