# Ionic Activity Data Sources

## Davies Parameter Set

Phase 7I seeds one activity parameter set:

- model: `DAVIES`;
- solvent: `COMP-H2O`;
- temperature: `25.00 C` / `298.15 K`;
- Davies A parameter: `0.509`;
- supported ionic-strength range: `0` through `0.5 mol/L`;
- source: CRC Handbook of Chemistry and Physics, 104th Ed. (2023-2024);
- evidence: Davies equation aqueous parameterization at 298.15 K;
- reuse: CRC tabular data are copyrighted. This project stores only a minimal
  cited educational subset; do not redistribute it as a standalone data table.

The parameter set is stored by migrations `V23` and `V24` in
`chemistry.ionic_activity_parameter_sets`. It is intentionally separate from
equilibrium constants because activity-model parameters have their own model,
temperature, solvent, and validity range.

## Validity

Davies coefficients are not extrapolated above ionic strength `0.5 mol/L`.
Unsupported temperature or solvent requests fail with structured activity
errors rather than falling back to an approximate value.

## Carbonic Boundary

Carbonic acid/base activity-corrected calculations are closed aqueous
calculations. Atmospheric CO2 exchange is out of scope and must not be inferred
from the carbonic-family results.
