# Thermodynamic Reference Foundation

Phase 8A adds a versioned thermodynamic reference catalogue for existing compound catalogue records. It stores condition-aware reference properties only:

- standard enthalpy of formation;
- standard Gibbs energy of formation;
- standard molar entropy;
- molar heat capacity;
- reference temperature and pressure;
- physical state;
- standard-state convention;
- provenance and evidence status.

The catalogue does not calculate reaction enthalpy, entropy, Gibbs energy, Hess cycles, equilibrium constants, temperature corrections, calorimetry or kinetics. Those operations belong to later thermodynamic phases.

## Scientific Contract

Every property record belongs to one compound profile, one dataset version, one physical state, one temperature, one pressure and one source. Gas and liquid water are separate records. Missing data remains absent; it is never represented as zero. Formation enthalpy and Gibbs values may be negative, zero or positive. Zero formation values are used for elemental reference states only when the source convention defines that reference state. Heat capacity records must be positive.

Molar formation energies use `MolarEnergy`, not total `Energy`. Entropy uses `MolarEntropy`. Heat capacity reuses `MolarHeatCapacity`. Temperature and pressure reuse the existing measurement primitives.

## Storage

V27 creates normalized tables for dataset versions, source documents, reference conditions, compound profiles and property records. V28 seeds `thermodynamic-reference-v1.0.0` at 298.15 K and 1 bar.

The internal service supports:

- profile lookup by compound code;
- property filtering by compound code and property type;
- exact lookup by compound code, type, state, temperature and pressure.

No REST or gRPC endpoint is added.
