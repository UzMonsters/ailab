# Phase Behavior And Transitions

Phase 10 adds sourced pure-substance phase-transition and vapor-pressure calculations. It does not add a full phase diagram.

## Transition Heat

Stored transition enthalpy is positive in the forward direction. Reverse signs are calculated.

```text
q_transition = n * DeltaH_transition
```

Absorbing heat:

```text
FUSION
VAPORIZATION
SUBLIMATION
```

Releasing heat:

```text
FREEZING
CONDENSATION
DEPOSITION
```

Reverse transitions have equal magnitude and opposite sign. Values at different pressures or polymorphs remain distinct.

## Saturation Pressure

Antoine correlations are stored with exact unit convention:

```text
log10(P) = A - B / (C + T)
```

The source equation is evaluated in its declared pressure and temperature units first. Only the final pressure is converted to the internal pressure unit.

Validity boundaries are inclusive. Outside the source temperature range, the calculator returns a structured out-of-range failure through `PhaseBehaviorException`.

## Triple And Critical Boundaries

Where sourced boundary data exists:

```text
triple-point temperature and pressure
critical temperature and pressure
normal melting or boiling point with reference pressure
```

Above the critical point, an ordinary liquid-vapor transition is rejected. Below the triple-point pressure, liquid paths are not fabricated. These checks are limited to substances with sourced boundary data.

## Heating Paths

Heating and cooling paths are ordered segments:

```text
sensible heating/cooling
isothermal phase transition
sensible heating/cooling
```

Segments must be continuous. Sensible segments require explicit constant molar heat capacity in this foundational implementation. Phase-change segments require sourced latent heat. Total heat equals the sum of segment heats, and reverse cooling reconciles to the opposite heat.

No automatic multi-transition route is created without complete data. Missing heat capacity, missing latent heat, phase mismatch and unsupported pressure are rejected.
