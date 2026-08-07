# Nonstandard Reaction Gibbs Energy

Phase 8D calculates nonstandard reaction Gibbs energy from an explicit dimensionless reaction quotient:

```text
Q = product(a_i ^ nu_i)
Delta_r G(T) = Delta_r G standard(T) + R*T*ln(Q)
```

Stoichiometric coefficients are signed: products are positive and reactants are negative. Missing participant activities are rejected; no concentration, pressure, `Kc`, or `Kp` value is silently interpreted as a thermodynamic activity.

Supported activity bases:

- `IDEAL_GAS_PARTIAL_PRESSURE`: `a_i = p_i / p standard`;
- `PURE_SOLID`: `a_i = 1` for explicitly present pure solids;
- `PURE_LIQUID`: `a_i = 1` for explicitly present pure liquids;
- `AQUEOUS_IDEAL`: `a_i = c_i / c standard`, only with explicit activity input and standard thermodynamic coverage;
- `AQUEOUS_DAVIES`: validates through `IonicActivityService`, only with explicit activity input and standard thermodynamic coverage;
- `EXPLICIT_DIMENSIONLESS_ACTIVITY`: caller supplies a positive finite dimensionless activity.

Direction classification is thermodynamic only:

- `FORWARD_THERMODYNAMIC_DRIVING_FORCE` when `Delta_r G < 0`;
- `REVERSE_THERMODYNAMIC_DRIVING_FORCE` when `Delta_r G > 0`;
- `EQUILIBRIUM_WITHIN_TOLERANCE` near zero.

This is not a kinetic prediction and does not calculate conversion, extent, final composition, simultaneous equilibria, fugacity, electrochemical potentials, or real-gas corrections.
