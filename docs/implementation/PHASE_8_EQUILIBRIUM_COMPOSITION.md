# Phase 8 Equilibrium Composition Solver

Phase 8 completes equilibrium thermodynamics by implementing stateless equilibrium-composition calculations for one balanced reaction at a time.

Domain additions:

- `EquilibriumCompositionRequest`
- `InitialParticipantAmount`
- `EquilibriumParticipantState`
- `EquilibriumExtent`
- `ExtentBounds`
- `EquilibriumCompositionResult`
- `EquilibriumCompositionMethod`
- `EquilibriumCompositionStatus`
- `EquilibriumCompositionResidual`
- `EquilibriumCompositionErrorCode`
- `EquilibriumCompositionException`
- `EquilibriumCompositionCalculator`
- `EquilibriumCompositionService`

Service flow:

```text
EquilibriumCompositionService
  -> ReactionCatalogService
  -> ThermodynamicEquilibriumService
  -> IonicActivityService when required
  -> pure EquilibriumCompositionCalculator
```

Numerical contract:
Bounded deterministic root solver (Safeguarded Bisection / Secant) operating on logarithmic residuals $\ln Q(\xi) - \ln K$. Physical extent bounds $[\xi_{\min}, \xi_{\max}]$ guarantee non-negative final amounts for all participants. Mass balance residuals and equilibrium residuals are reported explicitly.

Regression gates:
Phase 8B reaction Gibbs energy is preserved at `298.15 K`; Phase 8C is reused for supported non-reference temperatures; Phase 8D equilibrium constants and reaction quotients are preserved; V1-V30 Flyway migrations remain unchanged; no V31 migration is introduced.
