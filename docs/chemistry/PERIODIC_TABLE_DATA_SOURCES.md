# Periodic Table Core Data Sources & Scientific Provenance

This document describes the data sources, field-level provenance, normalization rules, and scientific semantics for the periodic-table reference dataset (Version `v1.1.0`).

---

## 1. Authority & Field-Level Provenance

The checked-in periodic-table dataset manifest (`periodic-table-core-v1.1.0.json`) specifies exact field-level provenance mappings:

1. **IUPAC CIAAW (Commission on Isotopic Abundances and Atomic Weights)**:
   - **Dataset Release**: Standard Atomic Weights / Atomic Weight Intervals (2021/2024).
   - **Covered Fields**: `atomicMassValue`, `atomicMassKind`, `atomicMassLowerBound`, `atomicMassUpperBound`.
   - **Atomic Weight Intervals**: 14 standard elements (H, Li, B, C, N, O, Mg, Si, S, Cl, Ar, Br, Tl, Pb) have standard atomic weights defined as intervals. Both lower and upper bounds are stored explicitly. Conventional values are retained as `representativeValue` for display without erasing intervals.
2. **NIST (National Institute of Standards and Technology)**:
   - **Dataset Release**: Atomic Weights and Isotopic Compositions Database (Ground-State Electron Configurations Table).
   - **Coverage Range**: Neutral elements Z=1..92 (Hydrogen through Uranium).
   - **Covered Fields**: `electronConfiguration`, `electronConfigurationStatus`.
   - **Provenance Status**:
     - `Z = 1..92`: `EVALUATED` (NIST evaluated data).
     - `Z = 93..103`: `PREDICTED` (Actinide theoretical predictions).
     - `Z = 104..118`: `PROVISIONAL` (Superheavy element provisional predictions).
3. **IUPAC Periodic Table of the Elements**:
   - **Dataset Release**: Official IUPAC Periodic Table 2024.
   - **Covered Fields**: `symbol`, `name`, `latinName`, `period`, `group`, `block`, `category`, `series`, `standardState`.
4. **NUBASE2020 / IUPAC Evaluation**:
   - **Dataset Release**: NUBASE2020 Evaluation of Nuclear Properties.
   - **Covered Fields**: `radioactivityStatus`.

---

## 2. Scientific Semantics & Classification Rules

### Radioactivity Semantics
- **`HAS_STABLE_ISOTOPES`**: Element has at least one genuinely stable nuclide (e.g. H, He, C, Fe, Au).
- **`PRIMORDIAL_RADIOACTIVE`**: Element has no stable isotopes, but occurs naturally on Earth due to extremely long half-life or decay chains (e.g., Bismuth Bi-209 ($t_{1/2} \approx 2.01 \times 10^{19}$ y), Radon, Radium, Thorium, Uranium).
- **`SYNTHETIC_RADIOACTIVE`**: Element has no stable isotopes and does not occur naturally on Earth in primordial quantities (e.g., Technetium, Promethium, and all transuranic elements beyond Plutonium).
- **`UNKNOWN`**: Radioactivity status undetermined.

### Bismuth Scientific Correction
Bi-209 was confirmed in 2003 (Danevich et al.) to undergo alpha decay with a half-life of $\sim 2.01 \times 10^{19}$ years. Bismuth therefore has NO stable isotopes and is correctly classified as `PRIMORDIAL_RADIOACTIVE` rather than `HAS_STABLE_ISOTOPES`.

---

## 3. Reference Conditions

Standard Temperature and Pressure (STP) is used as the reference condition for elements' standard state:
- **Temperature**: 0 °C (273.15 K)
- **Pressure**: 100 kPa (1 bar)
- **State Classification**: SOLID, LIQUID, GAS, or UNKNOWN.

---

## 4. Data Normalization Rules

- **English Names**: Capitalized standard English names (e.g., `Hydrogen`, `Helium`).
- **Latin Names**: Populated only where historically established and distinct from the English name (10 elements: Na, K, Fe, Cu, Ag, Sb, W, Au, Hg, Pb).
- **Electron Configurations**: Ground-state shorthand configurations using noble-gas cores.
- **Mass Values**: No bracket notation stored inside database numeric columns. Mass numbers for unstable elements are stored as exact integers with kind `RADIOACTIVE_ISOTOPE_MASS_NUMBER`.
