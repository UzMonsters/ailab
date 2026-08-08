# Phase 7G Implementation Report - Polyprotic Equilibrium And Speciation

## Summary
Phase 7G adds a framework-independent polyprotic equilibrium calculator and an injectable `PolyproticEquilibriumService`. It supports carbonic-family acid/salt speciation and sulfuric-acid first-stage complete dissociation with second-stage equilibrium. No migration is added; PostgreSQL/Flyway remains at V22.

## Shared Decimal Math
`BufferDecimalMath` was generalized to `AcidBaseDecimalMath`. Buffer, titration, and polyprotic calculators now share the same internal `log10` and `10^x` utility, preserving one decimal-to-Java-transcendental implementation.

## Domain Types
New domain/API types include `PolyproticAcidFamily`, `PolyproticSpecies`, `PolyproticDissociationConstant`, `PolyproticEquilibriumRequest`, `PolyproticEquilibriumResult`, `SpeciesDistribution`, `DistributionFraction`, `PolyproticInitialForm`, `PolyproticCalculationMethod`, `PolyproticAssumption`, `PolyproticResidual`, `PolyproticSolverStatus`, `PolyproticErrorCode`, `PolyproticException`, `PolyproticEquilibriumCalculator`, and `PolyproticEquilibriumService`.

## Calculation Semantics
The calculator computes standard distribution fractions for contiguous polyprotic families, then solves hydronium by mass balance, charge balance, `Kw`, formal concentration, initial form, and explicit spectator-ion charge. No `x << C` approximation is used.

Sulfuric acid is modeled with complete first dissociation and catalogue `Ka2`; no fabricated `Ka1` is stored or reported.

## Service Flow
`PolyproticEquilibriumServiceImpl` resolves supported families through `AcidBaseReferenceService`, retrieves exact-temperature `Ka` values and `Kw`, resolves spectator-ion charge when supplied, and delegates arithmetic to the pure domain calculator.

## Verification Coverage
Tests cover:

- carbonic distribution fractions at fixed pH;
- carbonic acid, bicarbonate amphiprotic salt, and carbonate salt equilibria;
- fraction sum, concentration sum, dominant species, residuals, and convergence;
- sulfuric first-dissociation-complete treatment and absence of fake `Ka1`;
- acidic, intermediate, and basic dominant species;
- missing Ka, noncontiguous steps, mixed reference conditions, invalid spectator stoichiometry, missing spectator ion, unsupported solvent, unsupported temperature, and solver failure;
- deterministic service resolution from active in-memory reference data;
- absence of polyprotic titration and activity-correction APIs;
- PostgreSQL/Flyway V22 integration and service injection.

## Limitations
Phase 7G intentionally defers polyprotic titration, activity coefficients, ionic-strength correction, mixed acid/base mixtures, indicators, precipitation, redox, complexometric calculations, external APIs, persistence, and UI graphing.
