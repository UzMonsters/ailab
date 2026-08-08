# Solubility Data Sources

## Dataset

Dataset version: `solubility-ksp-v1.0.0`

The initial Phase 7J subset includes only existing neutral solids from the
compound catalogue:

| Equilibrium | Solid | Shape | Ksp at 25 C in water |
| --- | --- | --- | --- |
| `KSP-CACO3-CALCITE` | `COMP-CACO3` | AB | `4.20e-9` |
| `KSP-MG-OH-2` | `COMP-MG-OH-2` | AB2 | `5.61e-12` |
| `KSP-AL-OH-3` | `COMP-AL-OH-3` | AB3 | `3.00e-34` |

The subset intentionally does not add new neutral compounds. Candidate salts
such as AgCl, BaSO4, CaF2, PbI2, and Ag2CO3 were not seeded because those
solids are not present in the current compound catalogue.

## Source

Source identifier: `CRC-HANDBOOK-104`

Citation: CRC Handbook of Chemistry and Physics, 104th Edition, Section 8,
solubility products at 25 C.

Reuse limitation: CRC tabular data are copyrighted. This project stores a
minimal cited educational subset and does not provide permission to
redistribute the values as a standalone data table.

## Species

The solubility seed adds only the aqueous ions required by the seeded
equilibria:

- `SPEC-CA-2PLUS`
- `SPEC-MG-2PLUS`
- `SPEC-AL-3PLUS`

Existing acid-base ions are reused for hydroxide and carbonate. Dissolution
terms store explicit formula, charge, and positive integer coefficient; no
polyatomic ion is inferred from a solid formula.
