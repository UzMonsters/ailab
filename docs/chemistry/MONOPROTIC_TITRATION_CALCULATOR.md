# Monoprotic Acid-Base Titration Calculator

## Supported Systems
Phase 7F supports aqueous monoprotic acid-base titration calculations for:

- strong acid analyte with strong base titrant;
- strong base analyte with strong acid titrant;
- weak acid analyte with strong base titrant;
- weak base analyte with strong acid titrant.

The solvent must be water (`COMP-H2O`), temperature is mandatory, and reference constants must be available for that exact temperature and solvent. Weak acid / weak base titrations, polyprotic and amphiprotic systems, indicators, precipitation titrations, redox titrations, and activity-coefficient corrections are intentionally deferred.

## Volume And Equivalence
All point calculations use additive volumes:

```text
Vtotal = Vanalyte + Vtitrant
```

Equivalence volume is determined from analyte moles and titrant concentration:

```text
Ve = nanalyte / Ctitrant
```

Curve generation sorts requested titrant volumes deterministically and rejects duplicate titrant volumes. Negative volumes are rejected by the measurement value objects before a titration calculation can be constructed.

## Strong Acid And Strong Base Systems
Strong acid / strong base and strong base / strong acid systems use stoichiometric neutralization followed by water autoionization correction for the excess strong reagent.

At equivalence:

```text
[H+] = sqrt(Kw)
```

Before and after equivalence:

```text
Cexcess = |nacid - nbase| / Vtotal
[H+] or [OH-] = (Cexcess + sqrt(Cexcess^2 + 4Kw)) / 2
```

For strong-base excess, hydronium is derived from `Kw / [OH-]`. The calculator does not silently hold concentration constant as titrant volume changes.

## Weak Acid With Strong Base
Weak acid titrations use one continuous charge-balance equation over the whole curve, including initial, buffer, equivalence, and post-equivalence points.

```text
F = initial weak-acid moles / Vtotal
S = strong cation moles / Vtotal
[A-] = F * Ka / ([H+] + Ka)
f([H+]) = [H+] + S - Kw/[H+] - [A-]
```

The hydronium root is solved by bounded bisection. The returned residual records mass-balance and charge-balance error for the solved point.

## Weak Base With Strong Acid
Weak base titrations are solved continuously through the conjugate-acid equilibrium:

```text
F = initial weak-base moles / Vtotal
C = strong anion moles / Vtotal
Ka(conjugate acid) = Kw / Kb
[BH+] = F * [H+] / ([H+] + Ka)
f([H+]) = [H+] + [BH+] - Kw/[H+] - C
```

The same bounded bisection strategy and residual reporting are used.

## Numerical Contract
Titration pH and pOH conversion uses the existing internal `AcidBaseDecimalMath` utility. That utility remains the only acid-base-domain path from `BigDecimal` to Java transcendental math for `log10` and `10^x`.

Solver arithmetic uses `MathContext.DECIMAL128`. Display pH and pOH are rounded to four decimals with half-up rounding. The weak-system hydronium bracket is bounded from `1E-30` to at least `1 mol/L`, expanded by formal concentrations, and iterated up to 240 bisection steps with an absolute function tolerance of `1E-28`.

## Service Contract
`TitrationCalculationService` resolves catalogue species through `AcidBaseReferenceService`, retrieves temperature-specific `Ka`, `Kb`, and `Kw`, then delegates pure calculation to the framework-independent `TitrationCalculator`.

The public service exposes only:

- system resolution;
- single-point calculation;
- point-request calculation;
- requested-volume curve calculation;
- characteristic point calculation.

It does not expose deferred indicator, precipitation, redox, or activity-correction behavior.
