# Phase Behavior Data Sources

Dataset:

```text
phase-behavior-reference-v1.0.0
```

The checked-in manifest is:

```text
Backend/chemistry-engine/src/main/resources/chemistry-data/phase-behavior-reference-v1.json
```

## Source Policy

Records require source citation, reuse terms, exact source units, original values, normalized values, phase metadata and condition metadata. Missing data remains unavailable.

Antoine coefficient sets are not mixed across unit conventions. The stored convention is:

```text
log10(P_mmHg)=A-B/(C+T_degC)
```

## Coverage

Water:

```text
fusion at 273.15 K and 1 atm
vaporization at 373.15 K and 1 atm
triple point
critical point
normal melting point
normal boiling point
Antoine liquid-vapor correlation from 274.15 K through 373.15 K
```

Ethanol:

```text
vaporization at normal boiling point
critical point
normal boiling point
Antoine liquid-vapor correlation from 216.15 K through 353.15 K
```

Carbon dioxide:

```text
sublimation at 1 atm
triple point
critical point
```

## Exclusions

The dataset intentionally excludes fabricated transition records, pressure-dependent melting curves, polymorph selection, full phase diagrams, VLE, flash calculations, nucleation, supercooling and heat-transfer rates.
