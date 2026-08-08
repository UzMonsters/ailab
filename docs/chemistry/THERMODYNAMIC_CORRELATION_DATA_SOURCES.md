# Thermodynamic Correlation Data Sources

Dataset: `thermodynamic-temperature-functions-v1.0.0`.

Seeded source: `NIST-WEBBOOK-SHOMATE`, cited as NIST Chemistry WebBook Shomate heat capacity correlations; Chase 1998.

Primary source entry point: https://webbook.nist.gov/chemistry/

Seeded coverage is intentionally limited to sourced records needed for Phase 8C verification: `H2(g)`, `O2(g)`, `H2O(g)`, `H2O(l)`, `CO2(g)`, `CO(g)`, and `CH4(g)`. Unsupported catalogue species remain missing by design, and the service reports incomplete coverage rather than fabricating values.

Stored fields include dataset version, compound code, phase, correlation type, all eight Shomate coefficients, validity range, units, scaling convention, source identifier, citation, and reuse limitations.

The manifest lives at `Backend/chemistry-engine/src/main/resources/chemistry-data/thermodynamic-temperature-functions-v1.json`; PostgreSQL storage is created by V29 and seeded by V30.
