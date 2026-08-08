# Polyprotic Acid-Base Equilibrium And Speciation

## Supported Families
Phase 7G supports ideal aqueous, stateless equilibrium and speciation for:

- carbonic acid family: `H2CO3 / HCO3- / CO3^2-`;
- sulfuric acid family: `H2SO4 / HSO4- / SO4^2-`, with first dissociation treated as complete and the second governed by catalogue `Ka2`.

The solvent must be water (`COMP-H2O`), temperature is mandatory, total analytical concentration must be positive, and all constants must be present at the exact requested temperature and solvent.

## Distribution Fractions
For a normal n-protic acid with hydronium concentration `h` and contiguous constants `K1...Kn`, species fractions are calculated from:

```text
alpha_i = (K1 * ... * Ki) * h^(n-i) / D
D = sum((K1 * ... * Ki) * h^(n-i)), i = 0..n
```

For a diprotic acid:

```text
D = h^2 + K1*h + K1*K2
alpha0 = h^2 / D
alpha1 = K1*h / D
alpha2 = K1*K2 / D
```

Each fraction is constrained to `[0, 1]`, fractions are tested to sum to 1, and species concentrations are `Ct * alpha_i`.

## Charge Balance
Equilibrium pH is solved from charge balance, not from distribution fractions alone:

```text
f(h) = h - Kw/h + fixedSpectatorCharge + sum(speciesCharge_i * Ct * alpha_i)
```

Salt requests must provide spectator-ion stoichiometry. For example:

- bicarbonate as sodium bicarbonate uses one `Na+` per `HCO3-`;
- carbonate as sodium carbonate uses two `Na+` per `CO3^2-`.

The calculator does not silently infer counterions.

## Amphiprotic Systems
`INTERMEDIATE_AMPHIPROTIC_SALT` represents an amphiprotic intermediate such as bicarbonate. Its pH emerges from the same mass-balance, charge-balance, `Ka`, and `Kw` system used for the rest of the family.

## Sulfuric Acid
Sulfuric acid is modeled with complete first dissociation:

```text
H2SO4 -> H+ + HSO4-
HSO4- <-> H+ + SO4^2-
```

No fake `Ka1` is introduced. The distribution has zero `H2SO4` fraction for the supported ideal model and uses the catalogue `Ka2` for `HSO4- / SO4^2-`.

## Solver And Residuals
The hydronium root is solved by deterministic bounded bisection using `MathContext.DECIMAL128`. The bracket starts at `1E-30` and expands upward until charge balance changes sign. The solver runs up to 240 iterations with absolute function tolerance `1E-28`.

Results report:

- pH and pOH rounded to four decimals;
- hydronium and hydroxide concentration;
- distribution fractions and concentrations ordered by protonation state;
- constants used;
- assumptions;
- mass-balance and charge-balance residuals;
- solver status.

## Numerical Utility
All acid-base logarithm and power operations use `AcidBaseDecimalMath`, the single internal decimal-to-Java-transcendental utility shared by buffer, titration, and polyprotic calculators.

## Deferred Work
Phase 7G does not implement polyprotic titration, activity coefficients, ionic-strength correction, mixed acid/base mixtures, indicators, precipitation, redox, complexometric calculations, REST/gRPC, persistence, or UI graphing.
