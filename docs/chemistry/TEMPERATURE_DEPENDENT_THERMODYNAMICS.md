# Temperature-Dependent Thermodynamics

Phase 8C evaluates phase-specific heat-capacity correlations for species and applies those increments to Phase 8B standard reaction thermodynamics.

The supported production correlation is Shomate. The calculator uses `t = T / 1000`, returns `Cp` in `J/(mol*K)`, `H(T)-H(298.15 K)` in `kJ/mol`, and `S(T)` in `J/(mol*K)`. At exactly `298.15 K`, species enthalpy and entropy increments are pinned to zero against the stored Phase 8A reference values so reaction results delegate to Phase 8B without coefficient residue.

Validation rules are strict: target temperature must be greater than zero and inside the inclusive validity range; correlations must be phase-specific `GAS`, `LIQUID`, or `SOLID`; missing correlations are reported as incomplete coverage; no extrapolation, phase substitution, or automatic constant-Cp fallback is allowed.

Reaction corrections sum signed stoichiometric species increments:

```text
Delta H_r(T) = Delta H_r(298.15 K) + sum(nu_i * [H_i(T)-H_i(298.15 K)])
Delta S_r(T) = Delta S_r(298.15 K) + sum(nu_i * [S_i(T)-S_i(298.15 K)])
Delta G_r(T) = Delta H_r(T) - T * Delta S_r(T) / 1000
Delta Cp_r(T) = sum(nu_i * Cp_i(T))
```

Phase overrides are explicit. For example, water synthesis with `COMP-H2O` as `GAS` uses the gas-water correlation, while a liquid override uses the liquid-water correlation and produces a different reaction enthalpy.
