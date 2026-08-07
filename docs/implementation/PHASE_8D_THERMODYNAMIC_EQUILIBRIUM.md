# Phase 8D Thermodynamic Equilibrium

Phase 8D adds internal thermodynamic equilibrium constants, reaction quotients, and nonstandard reaction Gibbs energy without adding migrations, REST endpoints, persistence, or equilibrium-composition solving.

Domain additions:

- `StandardEquilibriumConstant`
- `LogEquilibriumConstant`
- `ReactionQuotient`
- `LogReactionQuotient`
- `ParticipantActivity`
- `ReactionActivityInput`
- `ActivityBasis`
- `EquilibriumConstantRequest`
- `EquilibriumConstantResult`
- `NonstandardGibbsRequest`
- `NonstandardGibbsResult`
- `ThermodynamicDirection`
- `PhaseStabilityStatus`
- `EquilibriumCalculationMethod`
- `EquilibriumCalculationStatus`
- `EquilibriumErrorCode`
- `EquilibriumException`
- `ThermodynamicDecimalMath`
- `ThermodynamicEquilibriumCalculator`

Service flow:

```text
ThermodynamicEquilibriumServiceImpl
  -> ReactionThermodynamicsService at 298.15 K
  -> TemperatureDependentThermodynamicsService away from 298.15 K
  -> IonicActivityService for Davies activity validation
  -> ThermodynamicEquilibriumCalculator
```

Numerical contract: `ThermodynamicDecimalMath` centralizes Java transcendental conversion for `ln`, `log10`, `exp`, and `10^x`. Inputs and outputs are `BigDecimal`; nonpositive logarithm inputs and non-finite results are rejected. Direct exponential output is limited to the finite Java double range used for deterministic conversion, while logarithmic values remain primary.

Regression gates: Phase 8B reaction Gibbs energy is preserved at `298.15 K`; Phase 8C is reused for supported non-reference temperatures; V1-V30 migrations remain immutable; no V31 migration is introduced.

Limitations: aqueous standard-state thermodynamic data are not fabricated. Aqueous reaction-quotient input can be validated, including Davies validity through `IonicActivityService`, but nonstandard Gibbs energy is returned only when the standard reaction thermodynamic value exists.
