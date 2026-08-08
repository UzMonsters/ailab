# Persistent Periodic Table Core Catalogue

This document describes the design, schema, rules, and APIs of the persistent periodic-table catalogue core implemented in the `chemistry-engine` library.

---

## 1. Identifiers & Terminology

- **Surrogate Technical Identifier**:
  - `id` (UUID): Internal primary key generated deterministically from element symbol and atomic number.
- **Natural Identifiers**:
  - `atomicNumber` (Integer, 1..118): Unique natural numeric identifier according to atomic structure (proton count).
  - `symbol` (String): Unique natural symbol identifier according to IUPAC nomenclature (e.g., `H`, `He`, `Bi`, `Og`).
  - **Database Constraints**: Both natural identifiers are protected by strict `UNIQUE` constraints in the database schema.

---

## 2. Domain Model Architecture

The `domain.element` package implements a framework-independent domain model representing chemical elements.

### Core Domain Entities & Value Objects
- **`Element`**: The aggregate root representing a chemical element. Fully immutable and encapsulated.
- **`ElementId`**: A value object wrapping a UUID surrogate technical identifier.
- **`AtomicMass`**: Models standard atomic weight, isotopic mass, lower/upper bounds, and mass kind using exact scale `BigDecimal`.
- **`AtomicMassKind`** (enum):
  - `STANDARD_ATOMIC_WEIGHT`: IUPAC standard atomic weight.
  - `CONVENTIONAL_STANDARD_VALUE`: Conventional standard value for display.
  - `INTERVAL_STANDARD_ATOMIC_WEIGHT`: Standard atomic weight expressed as an interval [lowerBound, upperBound] per IUPAC CIAAW.
  - `RADIOACTIVE_ISOTOPE_MASS_NUMBER`: Mass number of the longest-lived isotope for elements without standard atomic weight.
  - `PREDICTED_OR_PROVISIONAL`: Theoretical calculations for superheavy elements.
- **`ElementBlock`** (enum): `S`, `P`, `D`, `F` orbital blocks.
- **`ElementCategory`** (enum): Categories such as `ALKALI_METAL`, `NOBLE_GAS`, `LANTHANIDE`, `ACTINIDE`, etc.
- **`ElementSeries`** (enum): Major series groups (`MAIN_GROUP`, `TRANSITION`, `LANTHANIDE`, `ACTINIDE`, `UNKNOWN`).
- **`StandardState`** (enum): State at standard temperature and pressure (0 °C, 100 kPa): `SOLID`, `LIQUID`, `GAS`, `UNKNOWN`.
- **`RadioactivityStatus`** (enum):
  - `HAS_STABLE_ISOTOPES`: Element has at least one stable ground-state isotope.
  - `PRIMORDIAL_RADIOACTIVE`: Element has no stable isotopes but occurs naturally on Earth (e.g., Bismuth Bi-209, Radium, Thorium, Uranium).
  - `SYNTHETIC_RADIOACTIVE`: Element has no stable isotopes and is produced synthetically (e.g., Technetium, Promethium, transuranic elements).
  - `UNKNOWN`: Unassigned or undetermined radioactivity status.
- **`ElectronConfigurationStatus`** (enum):
  - `EVALUATED`: Sourced directly from NIST Atomic Weights and Isotopic Compositions ground-state evaluation (Z=1..92).
  - `PREDICTED`: Theoretical prediction for actinide series (Z=93..103).
  - `PROVISIONAL`: Provisional theoretical configuration for superheavy elements (Z=104..118).
  - `UNKNOWN`: Configuration unknown or unsourced.

---

## 3. Shared Registry & Parser Alignment

- **`KnownElementRegistry`**: A framework-independent, static registry containing minimal metadata (atomic number, symbol, charge-ambiguity status) for all 118 elements.
- **Alignment Rules**:
  - The `FormulaParser` delegates its IUPAC symbol validation directly to `KnownElementRegistry`.
  - Suffix charge ambiguity rules (e.g. rejecting shorthand molecular charges like `O2+`) check `KnownElementRegistry.isAmbiguousChargeShorthand(symbol)` which designates all non-metal/metalloid elements as ambiguous.
  - Unit tests enforce that registry symbols align exactly with persisted database seed symbols.

---

## 4. Database Schema & Migration Strategy

The catalogue persists inside the `chemistry` schema of the PostgreSQL database under tables:
1. `chemistry.periodic_table_catalog_versions`
2. `chemistry.elements`

### Migrations
- `V1__create_chemistry_engine_metadata.sql`: Initial metadata schema.
- `V2__create_periodic_table_core.sql`: Base periodic table schema.
- `V3__seed_periodic_table_core.sql`: Initial seed data (v1.0.0).
- `V4__correct_periodic_table_scientific_semantics.sql`: Additive migration upgrading dataset to `v1.1.0`, adding `electron_configuration_status`, correcting radioactivity semantics (Bismuth as `PRIMORDIAL_RADIOACTIVE`), and updating check constraints safely.

### elements Table Schema
- `id` (UUID, Primary Key, Surrogate Identifier)
- `atomic_number` (INT, Unique, Natural Identifier, 1..118)
- `symbol` (VARCHAR(5), Unique, Natural Identifier)
- `name` (VARCHAR(100), Unique)
- `latin_name` (VARCHAR(100), Nullable)
- `atomic_mass_value` (NUMERIC(20, 10))
- `atomic_mass_kind` (VARCHAR(50))
- `atomic_mass_lower_bound` (NUMERIC(20, 10), Nullable)
- `atomic_mass_upper_bound` (NUMERIC(20, 10), Nullable)
- `period_number` (INT, 1..7)
- `group_number` (INT, Nullable, 1..18)
- `block` (VARCHAR(5), S/P/D/F)
- `electron_configuration` (VARCHAR(100))
- `electron_configuration_status` (VARCHAR(50), EVALUATED/PREDICTED/PROVISIONAL/UNKNOWN)
- `standard_state` (VARCHAR(20), SOLID/LIQUID/GAS/UNKNOWN)
- `radioactivity_status` (VARCHAR(50), HAS_STABLE_ISOTOPES/PRIMORDIAL_RADIOACTIVE/SYNTHETIC_RADIOACTIVE/UNKNOWN)
- `category` (VARCHAR(50))
- `series` (VARCHAR(50))
- `catalog_version_id` (VARCHAR(50), Foreign Key)
- `source_reference` (VARCHAR(500))

---

## 5. Repository & Application Wiring

- **Production Repository (`ElementRepositoryImpl`)**:
  - Annotated with `@Component` and `@Profile("!(test | standalone-engine)")`.
  - Requires `JpaElementRepository` constructor injection.
  - Does NOT silently fall back to registry or in-memory repositories. Missing persistence causes immediate fail-fast startup failure.
- **In-Memory Repository (`InMemoryElementRepository`)**:
  - Annotated with `@Component`, `@Primary`, and `@Profile({"test", "standalone-engine"})`.
  - Used explicitly for test environments or standalone engine execution without database dependencies.

---

## 6. Deferred Properties (Phase 3B)
The following fields and features are intentionally deferred to Phase 3B:
- Valency and oxidation state datasets.
- Electronegativity (Pauling scale).
- Atomic, covalent, and ionic radii datasets.
- Density, melting points, and boiling points.
- Element colors, observations, and physical states at different conditions.
