# Electrochemistry

Phase 11 adds a framework-independent electrochemistry domain with a Spring service wrapper.

## Conventions

Stored table records are standard reduction potentials. During cell construction, the cathode remains in the stored reduction direction and the anode record is reversed only for the overall cell reaction.

Electrode potentials are intensive:

```text
Ecell = Ecathode - Eanode
```

Scaling a half-reaction changes electron count and Gibbs energy, but not the electrode potential or cell potential.

## Cell Calculations

The calculator balances electron counts exactly, combines the reversed anode with the cathode, cancels identical compound-phase species and verifies zero atom and charge residuals. Status is classified as galvanic, nonspontaneous or equilibrium within tolerance.

Thermodynamic coupling uses:

```text
DeltaG = -nFEcell
lnK = nFEcell / RT
```

`lnK` and `log10K` are reported so extreme equilibrium constants do not require direct exponentiation.

## Nernst Calculations

Nonstandard potentials use the natural-log form:

```text
E = E0 - (RT / nF) ln Q
```

Activities must be explicit. Concentrations are not silently treated as activities. Pure solids and pure liquids are excluded from `Q` only when an explicit pure-phase activity record is supplied. Gas activities are normalized as `p / 1 bar`; aqueous ideal activities as `c / 1 mol/L`; Davies activities use declared concentration, charge and ionic strength with validity enforcement.

## Cell Notation

Cell notation is deterministic descriptive output, not an input parser:

```text
Zn(s) | Zn2+(aq) || Cu2+(aq) | Cu(s)
```

The anode is left, the cathode is right, phase boundaries use `|`, and the salt bridge uses `||`.

## Electrolysis

Faraday-law calculations use:

```text
Q = I * t
ne = Qeffective / F
nsubstance = ne * stoichiometric coefficient / electron count
m = nsubstance * molar mass
```

Current, duration and charge are non-negative. Current efficiency satisfies `0 < efficiency <= 1`. Molar mass is supplied directly to the pure calculator or by `CompoundCatalogService` in the service layer.

## Limitations

The module does not implement Butler-Volmer kinetics, Tafel equations, overpotential, current density, transport, corrosion, electrode geometry, batteries, fuel-cell engineering, voltammetry, impedance, laboratory equipment or the Simulation Engine.
