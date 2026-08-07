# Simulation Engine

## Overview

The `com.ailab.chemistry.domain.simulationengine` package defines the deterministic, pure Simulation Engine for executing scientific operations against Phase 13 simulation sessions.

The engine requires explicit scientific operation and model selection. It never infers or automatically discovers reactions, equilibrium states, phase changes, kinetics, or electrochemical reactions.

## Supported Operations

1. `STOICHIOMETRIC_REACTION`: Balanced reaction extent calculation via `StoichiometryService` and `ReactionCatalogService`.
2. `EQUILIBRIUM_REACTION`: Equilibrium composition derived via `EquilibriumCompositionService`.
3. `KINETIC_PROGRESS`: Rate and time-series progress endpoints via `ReactionKineticsService`.
4. `THERMAL_OPERATION`: Sensible heating/cooling and enthalpy balances via `CalorimetryService` and `TemperatureDependentThermodynamicsService`.
5. `GAS_STATE_CHANGE`: Ideal gas law state updates (`PV = nRT`) via `GasLawService`.
6. `PHASE_TRANSITION`: Latent heat and phase changes via `PhaseBehaviorService`.
7. `ELECTROLYSIS`: Faraday-law mass and ion changes via `ElectrochemistryService`.
8. `BOOKKEEPING_MIX`: Vessel material mixing without chemistry execution.

## Explicit Model Selection

Scientific operations must explicitly specify the reaction code, calculation method, dataset version, kinetic profile, or thermodynamic model. Inferred chemical reactions or automatic phase changes are explicitly rejected.

## State Delta & Invariants

Every operation converts scientific output into an immutable `SimulationStateDelta` containing state changes for:
- Materials (`MaterialStateDelta`)
- Vessels (`VesselStateDelta`)
- Thermal conditions (`ThermalStateDelta`)
- Pressure (`PressureStateDelta`)
- Phases (`PhaseStateDelta`)
- Equipment (`EquipmentStateDelta`)

Physical invariants enforced prior to event recording:
- Non-negative material amounts
- Vessel capacity limits
- Element, charge, and mass conservation (where applicable)
- Temperature strictly above 0 K
- Equipment and container operating envelopes
