# Electrochemical Reference Data Sources

Dataset:

```text
electrochemical-reference-v1.0.0
```

The initial dataset is a small aqueous standard reduction-potential set at 298.15 K, 1 bar gas standard pressure and unit activity standard states.

## Coverage

Active records:

```text
2H+ + 2e- <=> H2(g)      E0 = 0.0000 V
Cu2+ + 2e- <=> Cu(s)     E0 = 0.3400 V
Zn2+ + 2e- <=> Zn(s)     E0 = -0.7630 V
Ag+ + e- <=> Ag(s)       E0 = 0.7996 V
Fe3+ + e- <=> Fe2+       E0 = 0.7710 V
Cl2(g) + 2e- <=> 2Cl-    E0 = 1.3580 V
```

Sources:

```text
CRC-ELECTRODE-POTENTIALS
IUPAC-SHE-CONVENTION
```

The hydrogen electrode has zero standard potential because the standard hydrogen electrode convention defines it that way. Missing half-reactions are not assigned fabricated values.

## Integrity Rules

Every active record stores exact participants, coefficients, phase, charge, electron count, standard potential, temperature, medium, standard-state convention and provenance. Electrons are algebraic balancing participants and are not stored as ordinary compounds.

The SQL seed and `electrochemical-reference-v1.json` manifest use the same record identifiers. Additive catalogue species are inserted only for missing electrochemical identities with explicit formula, charge and compound identity.
