# Phase 12 Laboratory Apparatus And Environment

Phase 12 was implemented as one complete module. Event Processing, Simulation State, Simulation Engine, and runtime Laboratory Safety were not started.

## Phase 11 Preflight

The exact Phase 11 closure is preserved in scope: electrochemistry remains a pure domain module with V38/V39 migrations untouched. The Phase 11 release decision remains:

```text
PASS — Phase 11 complete; half-reaction, cell-potential, Nernst, thermodynamic-coupling, electrolysis, reference-data, PostgreSQL and regression gates pass. Laboratory Equipment and Environment work may begin.
```

Electrochemical source records retain stable source record identifiers. Faraday constant verification now covers explicit value `96485.3321233100184`, unit `C mol^-1`, source `2019 SI exact e*N_A`, and version `CODATA-2018-EXACT`.

## Domain Types

Equipment domain:

```text
EquipmentType, EquipmentReferenceProfile, EquipmentCapability, OperatingRange,
CapacityLimit, MeasurementResolution, AccuracySpecification, MeasurementUncertainty,
UncertaintySpecification, CalibrationRequirement, CalibrationRecord, CalibrationStatus,
EquipmentCondition, EquipmentRequirement, EquipmentSuitabilityRequest,
EquipmentProfileSuitabilityRequest,
EquipmentSuitabilityResult, EquipmentSuitabilityStatus, EquipmentViolation,
EquipmentWarning, EquipmentErrorCode, EquipmentException, EquipmentSuitabilityCalculator,
EquipmentReferenceRepository
```

Container domain:

```text
ContainerType, ContainerProfile, ContainerMaterial, ContainerClosureType,
ContainerGeometry, NominalCapacity, MaximumWorkingVolume, FillFraction, Headspace,
ContainerTemperatureLimit, ContainerPressureLimit, ContainerRequirement,
ContainerCompatibilityRecord, CompatibilityStatus, CompatibilityCondition,
ContainerSuitabilityRequest, ContainerProfileSuitabilityRequest,
ContainerSuitabilityResult, ContainerSuitabilityStatus,
ContainerViolation, ContainerErrorCode, ContainerException,
ContainerSuitabilityCalculator, ContainerReferenceRepository
```

Environment domain:

```text
LaboratoryEnvironmentSnapshot, RelativeHumidity, VentilationMode, FumeHoodState,
EnvironmentalRequirement, EnvironmentSuitabilityRequest, EnvironmentSuitabilityResult,
EnvironmentSuitabilityStatus, EnvironmentViolation, EnvironmentWarning,
EnvironmentErrorCode, EnvironmentException, EnvironmentSuitabilityCalculator
```

Combined operation domain:

```text
LaboratoryOperationRequirement, LaboratoryOperationSuitabilityRequest,
LaboratoryOperationSuitabilityResult, LaboratoryOperationStatus,
LaboratoryOperationViolation, LaboratoryOperationWarning,
LaboratoryOperationSuitabilityCalculator
```

## Measurements

Reused existing measurement types:

```text
Temperature, Pressure, Volume, Mass, AmountOfSubstance, ElectricPotential,
Duration, SpecificHeatCapacity, MolarHeatCapacity
```

Added primitives:

```text
RelativeHumidity, MeasurementResolution, MeasurementUncertainty
```

No duplicate temperature, pressure, volume, or mass representation was added.

## Migrations

Additive migrations:

```text
V40__create_laboratory_apparatus_reference.sql
V41__seed_laboratory_apparatus_reference.sql
V42__wire_and_extend_laboratory_reference_integrity.sql
V43__seed_minimal_operational_apparatus_profiles.sql
```

V1 through V41 were not modified. V42 adds integrity columns and checks for performance-qualified records and active compatibility boundaries. V43 registers `laboratory-equipment-reference-v1.1.0` and `laboratory-container-reference-v1.1.0`, then seeds the corrected minimal operational profiles.

## Verification Coverage

Equipment tests cover capacity acceptance/rejection, range validation, pH range validation, missing capability rejection, resolution/accuracy separation, missing accuracy/uncertainty, valid/expired/missing/due-soon calibration, deterministic timestamp evaluation, and no name-inferred capability.

Container tests cover working-limit acceptance, overfill rejection, fill fraction, headspace, sealed headspace requirements, open pressure rejection, pressure-rated closed-container acceptance, pressure rejection, temperature rejection, compatible, limited-compatible, incompatible, and unknown compatibility behavior.

Environment tests cover acceptable ambient conditions, humidity boundaries, invalid humidity, fume hood unavailable, fume hood available but not operating, operating fume hood acceptance, ventilation mismatch, missing values, and deterministic explicit snapshot evaluation.

Combined suitability tests cover aggregation of selected profiles, assumptions, provenance, and blockers from equipment, container, and environment results.

Integration tests cover all four service injections, V43 migration head, manifest/SQL semantic equivalence for the V1.1 active snapshot, local PostgreSQL migration from V41 to V43, active JDBC repository selection, inactive fixture filtering, equipment profile service evaluation, container profile service evaluation, and absence of production UNKNOWN compatibility rows.

Architecture tests cover framework-independent Phase 12 domain packages and excluded simulation/runtime API names.

## Documentation

Created:

```text
docs/chemistry/LABORATORY_EQUIPMENT.md
docs/chemistry/LABORATORY_CONTAINERS.md
docs/chemistry/LABORATORY_ENVIRONMENT.md
docs/chemistry/LABORATORY_APPARATUS_DATA_SOURCES.md
docs/implementation/PHASE_12_LABORATORY_APPARATUS_AND_ENVIRONMENT.md
```

## Limitations

The checked-in operational reference data remains intentionally narrow: one analytical balance, one pH meter, one heating/stirring plate, one Class A volumetric flask, one HDPE bottle, and three bounded compatibility records. V1.0 taxonomy records remain immutable but are not treated as performance-qualified operational data. The module validates suitability only and does not execute experiments, simulate state changes, control devices, manage inventory, or act as runtime safety infrastructure.

## Release Decision

PASS - Phase 12 complete; production repository, minimal operational dataset, equipment capability, calibration, container compatibility, environment suitability, PostgreSQL and regression gates pass. Laboratory Process and Simulation State work may begin.
