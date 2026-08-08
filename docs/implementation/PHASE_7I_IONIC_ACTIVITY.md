# Phase 7I Ionic Activity

Phase 7I adds ionic-strength calculation, Davies activity coefficients,
dimensionless chemical activities, activity-based pH/pOH, and self-consistent
activity-corrected equilibrium for supported aqueous non-titration acid-base
systems.

## Domain Types

- `IonicSpeciesConcentration`
- `IonicStrength`
- `ActivityCoefficient`
- `ChemicalActivity`
- `ActivityModel`
- `ActivityParameterSet`
- `ActivityCorrectionRequest`
- `ActivityCorrectionResult`
- `ActivityCorrectedEquilibriumResult`
- `ActivityIterationResult`
- `ActivitySolverStatus`
- `ActivityErrorCode`
- `ActivityException`
- `IonicActivityCalculator`

The pure calculator is framework-independent. JPA is limited to parameter-set
lookup in the infrastructure adapter.

## Dataset

New migrations:

- `V23__create_ionic_activity_reference.sql`
- `V24__seed_ionic_activity_reference.sql`

The seeded Davies parameter set is water at 298.15 K with `A = 0.509` and a
maximum ionic strength of `0.5 mol/L`.

Migrations V1-V22 remain unchanged.

## Service

`IonicActivityService` exposes:

```java
IonicStrength calculateIonicStrength(List<IonicSpeciesConcentration> species);

ActivityCorrectionResult calculateActivities(
    List<IonicSpeciesConcentration> species,
    Temperature temperature,
    String solventCode,
    ActivityModel model
);

ActivityCorrectedEquilibriumResult calculateEquilibrium(
    ActivityCorrectionRequest request
);
```

The service resolves acid-base reference constants and species charges, loads
the activity parameter set, and delegates numeric work to
`IonicActivityCalculator`.

## Verification Coverage

Tests verify:

- `0.100 M NaCl` ionic strength is `0.100 M`;
- `0.100 M Na2CO3` pre-hydrolysis ionic strength is near `0.300 M`;
- neutral species and zero ionic strength give `gamma = 1`;
- divalent ions receive stronger Davies correction than monovalent ions;
- values above `0.5 M` fail explicitly;
- missing parameter sets, unsupported solvent, and unsupported temperature fail
  explicitly;
- ideal mode reproduces existing ideal equilibrium pH;
- Davies weak-acid, weak-base, conjugate salt, carbonic, carbonate, bicarbonate,
  and sulfuric cases converge;
- mass and charge residuals stay below documented tolerances;
- repeated execution is deterministic;
- PostgreSQL/Flyway reaches V24 while V22 remains applied.

## Exclusions

No precipitation equilibrium, activity-corrected titration curves, Pitzer, SIT,
ion-size Debye-Huckel correction, mixed solvents, redox, complex formation,
REST, persistence of calculations, or graph generation is included.
