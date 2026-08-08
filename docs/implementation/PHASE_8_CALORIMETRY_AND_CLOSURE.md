# Phase 8 Calorimetry and Thermodynamics Closure

Phase 8 completes calorimetry and thermodynamics by adding framework-independent calorimetry domain types, pure calculators, and internal service integration.

## Domain additions

- `HeatCapacity`
- `ThermalSample`
- `Calorimeter`
- `SensibleHeatRequest`
- `SensibleHeatResult`
- `ThermalMixingRequest`
- `ThermalMixingResult`
- `ReactionCalorimetryRequest`
- `ReactionCalorimetryResult`
- `AdiabaticTemperatureRequest`
- `AdiabaticTemperatureResult`
- `ThermalEnergyBalance`
- `CalorimetryMethod`
- `CalorimetryStatus`
- `CalorimetryErrorCode`
- `CalorimetryException`
- `CalorimetryCalculator`

## Service flow

```text
CalorimetryService
  -> ReactionThermodynamicsService
  -> TemperatureDependentThermodynamicsService
  -> pure CalorimetryCalculator
```

## Numerical & Scientific Contracts

1. **Preflight Extent Verification**: Reaction extent $\xi$ measures reaction advancement relative to requested initial composition $n_{i,0}$, while final equilibrium composition $n_i(\xi) = n_{i,0} + \nu_i \xi$ is state-invariant.
2. **Thermal Energy Balance**: Isolated system energy balance $\sum q_k + q_{\text{cal}} = 0$ is verified with absolute residuals below $10^{-3}\text{ J}$. In thermal mixing, the calorimeter initial temperature $T_{\text{cal},i}$ (e.g. $20\text{ }^\circ\text{C} = 293.15\text{ K}$) is explicit.
3. **Service Layer Classification**: `CalorimetryServiceImpl` is classified strictly as a service-layer implementation (`com.ailab.chemistry.service`), delegating to pure framework-independent domain types and `CalorimetryCalculator`.
4. **Correlation Safeguards**: Shomate temperature correlations are integrated strictly inside validity ranges ($298.15\text{ K} - 6000\text{ K}$). No extrapolation, latent heat, or automatic phase transitions are permitted.
5. **Regression Gates**: V1-V30 Flyway migrations remain unchanged; exactly 398 unit and integration tests pass cleanly with 0 skipped tests.

## Phase 8 Release Decision
`PASS — Phase 8 complete; reference thermodynamics, Hess’s law, temperature correction, equilibrium, composition, calorimetry, regression and integration gates pass. Reaction Kinetics may begin.`

