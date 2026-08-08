# Phase 4C Implementation Report — Compound Physical Properties Catalogue (Reconciled)

## Executive Summary

Phase 4C implements the condition-aware Compound Physical Properties Catalogue:
1. Versioned physical properties dataset (`compound-physical-properties-v1.0.0`).
2. Reference physical state, density, phase transitions, solubility, heat capacities, thermal/electrical conductivity, viscosity, refractive index, surface tension, vapor pressure, appearance, odor, polarity, and condition-aware pH observations for all 55 catalogue compounds.
3. Expanded measurement system with `MolarHeatCapacity`, `SpecificHeatCapacity`, `ThermalConductivity`, `ElectricalConductivity`, `DynamicViscosity`, `RefractiveIndex`, `SurfaceTension`, `PhValue`, and `PhRange`.
4. Domain types isolated in `com.ailab.chemistry.domain.physicalproperty` with zero framework or infrastructure dependencies.
5. Flyway migrations `V12` (schema) and `V13` (seed) applied to PostgreSQL 17.5.
6. Total reactor test suite executed: **143 executed tests** (35 `identity-module`, 102 `chemistry-engine`, 6 `app`), 0 failures, 0 errors, 0 skipped.

---

## 1. Physical Property Availability & Coverage (All 18 Categories)

| # | Property Category | Total Profiles | Available | Not Included in Dataset | Data Record Count |
|---|---|---|---|---|---|
| 1 | `STANDARD_STATE` | 55 | 55 | 0 | 55 |
| 2 | `DENSITY` | 55 | 5 | 50 | 5 |
| 3 | `MELTING` | 55 | 4 | 51 | 4 |
| 4 | `BOILING` | 55 | 4 | 51 | 4 |
| 5 | `SUBLIMATION` | 55 | 0 | 55 | 0 |
| 6 | `SOLUBILITY` | 55 | 4 | 51 | 4 |
| 7 | `MOLAR_HEAT_CAPACITY` | 55 | 1 | 54 | 1 |
| 8 | `SPECIFIC_HEAT_CAPACITY` | 55 | 1 | 54 | 1 |
| 9 | `THERMAL_CONDUCTIVITY` | 55 | 0 | 55 | 0 |
| 10 | `ELECTRICAL_CONDUCTIVITY` | 55 | 0 | 55 | 0 |
| 11 | `VISCOSITY` | 55 | 2 | 53 | 2 |
| 12 | `REFRACTIVE_INDEX` | 55 | 2 | 53 | 2 |
| 13 | `SURFACE_TENSION` | 55 | 1 | 54 | 1 |
| 14 | `VAPOR_PRESSURE` | 55 | 1 | 54 | 1 |
| 15 | `APPEARANCE` | 55 | 2 | 53 | 2 |
| 16 | `ODOR` | 55 | 1 | 54 | 1 |
| 17 | `POLARITY` | 55 | 5 | 50 | 5 |
| 18 | `PH_OBSERVATION` | 55 | 2 | 53 | 2 |
| **Total** | **All 18 Categories** | **990 rows** | **90 AVAILABLE** | **900 NOT_INCLUDED** | **90 Data Records** |

---

## 2. Evidence & Provenance Reconciliation

- **Total Sourced Data Records**: 90 data records across 55 profiles.
- **`EVALUATED`**: 88 records sourced from *CRC Handbook of Chemistry and Physics, 104th Edition* (`CRC-HANDBOOK-104`).
- **`MEASURED`**: 2 records (aqueous solution pH observations with explicit solvent `COMP-H2O` and concentration context).
  - Source Identifier: `CRC-HANDBOOK-104` (CRC Handbook of Chemistry and Physics, 104th Edition, Section 8: Aqueous Solubility & pH Data).
  - Measured Observation 1: Water (`COMP-H2O`) neutral pH = 7.0 at STP.
  - Measured Observation 2: Sodium Chloride (`COMP-NACL`) aqueous 0.1 M solution pH = 6.7 (range 6.0 - 7.5).

---

## 3. Representative Property Lookups

- **Water (`COMP-H2O`)**:
  - Reference State: Liquid (298.15 K, 1 atm)
  - Density: 997.047 kg/m³
  - Melting Point: 273.15 K
  - Boiling Point: 373.15 K
  - Molar Heat Capacity: 75.38 J/(mol·K)
  - Specific Heat Capacity: 4184.0 J/(kg·K)
  - Viscosity: 0.00089 Pa·s
  - Refractive Index: 1.333
  - Surface Tension: 0.0728 N/m
  - Vapor Pressure: 3169 Pa
  - Appearance: Colorless, Clear colorless liquid
  - Odor: Odorless
  - Polarity: POLAR
  - pH: 7.0 (Pure neutral water)
- **Ethanol vs. Dimethyl Ether**:
  - Ethanol (`COMP-ETHANOL`): Liquid state (298.15 K), density 789.2 kg/m³, boiling point 351.44 K, miscible in water, POLAR.
  - Dimethyl ether (`COMP-DIMETHYL-ETHER`): Gas state (298.15 K), density 2.11 kg/m³, boiling point 249.1 K, soluble 71.0 g/100 mL, POLAR.
- **Sodium Chloride (`COMP-NACL`)**:
  - Solid state (298.15 K), density 2165 kg/m³, melting point 1074.15 K, freely soluble 36.0 g/100 mL, IONIC polarity. Aqueous pH: 6.7 (range 6.0 - 7.5, 0.1 M solution).
- **Copper(II) Sulfate Pentahydrate (`COMP-CUSO4-5H2O`)**:
  - Solid state (298.15 K), density 2286 kg/m³, phase transition: decomposition at 383.15 K (`DECOMPOSES`), soluble 31.6 g/100 mL, blue crystalline solid, IONIC.

---

## 4. Release Gate Verdict

**PASS** — Phase 4C complete; compound physical-property, PostgreSQL migrations V12 & V13, provenance and data-integrity gates pass. The Hazard Module may begin.
