# Compound Physical Properties Architecture & Reference Models

## Overview
The Compound Physical Properties Module (`com.ailab.chemistry.domain.physicalproperty`) provides condition-aware reference physical property data for chemical compounds.

## Key Design Principles
1. **Reference Data vs. Simulation**: Stores evaluated reference data. Does not interpolate vapor pressures, simulate phase transitions, or calculate solution pH at runtime.
2. **Explicit Reference Conditions**: Every property datum is bound to `PropertyReferenceConditions` (temperature, pressure, matter state, solvent reference, concentration, wavelength).
3. **Explicit Property Availability**: Every profile explicitly tracks availability for all 18 property types using `PropertyAvailability` (`AVAILABLE`, `UNKNOWN`, `NOT_APPLICABLE`, `NOT_INCLUDED_IN_DATASET`, `CONDITION_REQUIRED`, `REACTS_OR_DECOMPOSES`).
4. **Condition-Aware pH**: pH is stored only as an aqueous solution observation with explicit solvent (`COMP-H2O`) and concentration context. No pure dry compound receives an intrinsic pH.
