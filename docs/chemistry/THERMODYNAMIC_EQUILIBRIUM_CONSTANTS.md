# Thermodynamic Equilibrium Constants

Phase 8D calculates dimensionless standard thermodynamic equilibrium constants from standard reaction Gibbs energy:

```text
Delta_r G standard(T) = -R*T*ln(K standard)
ln(K standard) = -Delta_r G standard(T)/(R*T)
log10(K standard) = ln(K standard)/ln(10)
```

`lnK` and `log10K` are primary outputs because `K` can be extremely large or small. Direct `K` is returned only when it is finite and safely representable by the governed thermodynamic math utility.

Standard-state interpretation is explicit:

- ideal gas: activity is `p_i / p standard`;
- pure liquid: activity is `1` only for an explicitly present liquid participant;
- pure solid: activity is `1` only for an explicitly present solid participant;
- aqueous ideal/Davies: supported only as explicit activity inputs, and only when standard thermodynamic reaction data are available.

Correlation validity and phase stability are separate. Correlation validity only says the heat-capacity correlation supports the requested temperature. Phase stability is reported as either `PHASE_STABILITY_NOT_EVALUATED` or `PRESCRIBED_PHASE_ASSUMPTION`. Liquid water at `400 K` and `1 bar` is therefore a prescribed-phase calculation, not an automatic phase-stability claim.

At `298.15 K`, Phase 8D uses Phase 8B standard reaction thermodynamics. At other supported temperatures, it uses Phase 8C temperature-corrected standard reaction Gibbs energy. It does not extrapolate, change phases, solve equilibrium composition, or convert between thermodynamic `K standard` and `Kc`/`Kp` tables.
