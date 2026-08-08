# Phase 8C Temperature-Dependent Thermodynamics

Phase 8C adds framework-independent domain types and a pure `TemperatureDependentThermodynamicsCalculator` for Shomate species calculations. Spring appears only in service and infrastructure adapters.

Domain additions include `HeatCapacityCorrelation`, `HeatCapacityCorrelationType`, `PolynomialCoefficientSet`, `TemperatureValidityRange`, `TemperatureDependentPropertyResult`, `SpeciesTemperatureCorrection`, `ReactionTemperatureCorrectionRequest`, `ReactionTemperatureCorrectionResult`, `TemperatureCorrectionCoverage`, `TemperatureCorrectionMethod`, `TemperatureCorrectionStatus`, `TemperatureCorrectionErrorCode`, `TemperatureCorrectionException`, `TemperatureCorrelationRepository`, and `TemperatureDependentThermodynamicsCalculator`.

Service flow:

```text
TemperatureDependentThermodynamicsServiceImpl
  -> ReactionCatalogService
  -> ReactionThermodynamicsService / ThermodynamicReferenceService
  -> TemperatureCorrelationRepository
  -> TemperatureDependentThermodynamicsCalculator
```

V29 creates the temperature-correlation tables. V30 seeds `thermodynamic-temperature-functions-v1.0.0`. V1 through V28 are unchanged.

Release limitations: no equilibrium constants, Van't Hoff calculation, nonstandard Gibbs energy, electrochemical correction, automatic phase changes, extrapolation, or automatic constant-Cp fallback were added.
