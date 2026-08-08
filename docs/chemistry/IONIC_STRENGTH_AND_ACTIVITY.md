# Ionic Strength and Activity Correction

Phase 7I adds ionic-strength calculation, Davies activity coefficients, and
self-consistent activity-corrected acid-base equilibrium for supported aqueous
non-titration systems.

## Standard State

Chemical activities use the dimensionless convention:

```text
ai = gamma_i * ci / c0
c0 = 1 mol/L
```

Activity-corrected pH and pOH are therefore:

```text
pH = -log10(aH3O+)
pOH = -log10(aOH-)
```

When activity correction is enabled, pH is not reported from hydronium
concentration alone.

## Ionic Strength

The ionic-strength calculation is:

```text
I = 0.5 * sum(ci * zi^2)
```

Concentrations are mol/L and charges are validated against acid-base species
records at the service boundary. Neutral species contribute zero.

## Davies Model

The Davies equation is:

```text
log10(gamma_i) =
  -A * zi^2 * (sqrt(I) / (1 + sqrt(I)) - 0.3I)
```

For water at 298.15 K, Phase 7I stores `A = 0.509` with provenance in the
chemistry migration catalogue. Neutral species use `gamma = 1`. At zero ionic
strength all activity coefficients are `1`.

Davies is supported only for:

- solvent: `COMP-H2O`;
- temperature: `298.15 K`;
- ionic strength: `0 <= I <= 0.5 mol/L`.

Requests outside that range fail explicitly. The calculator does not silently
extrapolate Davies.

## Iteration Strategy

Activity correction is self-consistent, not post-processing:

1. solve the ideal concentration equilibrium;
2. calculate ionic strength from hydronium, hydroxide, charged equilibrium
   species, and spectator ions;
3. calculate activity coefficients;
4. solve charge balance again using activity-form equilibrium constants;
5. repeat with deterministic damping until hydronium, ionic strength, and
   activity coefficients converge.

The result reports hydronium delta, ionic-strength delta, maximum coefficient
change, mass residual, charge residual, and solver status.

## Supported Equilibria

Supported non-titration systems:

- pure water;
- strong monoprotic acids and bases;
- weak monoprotic acids and bases;
- conjugate acid/base salts;
- carbonic-family equilibrium;
- sulfuric-family equilibrium.

Titration curves remain ideal-solution calculations in Phase 7I.

Carbonic-family calculations are closed aqueous systems. They do not exchange
CO2 with the atmosphere and do not model Henry-law gas transfer.

## Deferred Work

Phase 7I does not implement Pitzer, SIT, ion-size Debye-Huckel extensions,
activity-corrected titration curves, mixed solvents, precipitation or Ksp,
redox, complex formation, REST endpoints, persisted calculations, or graph
generation.
