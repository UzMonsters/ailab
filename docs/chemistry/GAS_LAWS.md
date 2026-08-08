# Gas Laws

Phase 10 adds a framework-independent gas-law domain and a Spring service wrapper.

## Models

Supported models:

```text
IDEAL_GAS
EXPLICIT_COMPRESSIBILITY_FACTOR
```

No van der Waals, Redlich-Kwong, SRK, Peng-Robinson, fugacity or VLE model is implemented.

## State Equation

Ideal gas:

```text
PV = nRT
```

Explicit compressibility factor:

```text
PV = Z n R T
```

`Z` must be supplied explicitly for the explicit-Z model and must be positive. `Z = 1` gives the same result as the ideal-gas model.

## Quantity Rules

Temperature is absolute and must be greater than 0 K. Pressure, volume and amount must be positive. The calculator solves exactly one missing state variable, or validates an overdetermined state by reporting a residual.

No implicit STP or room conditions are assumed. A `StandardConditionDefinition` requires explicit temperature and pressure.

## Mixtures

Mixture semantics use mole amounts only:

```text
y_i = n_i / sum(n)
p_i = y_i * P_total
P_total = sum(p_i)
```

Mole fractions are non-negative and reconcile to one within numerical tolerance. Partial pressures reconcile with the supplied total pressure. Inert and reacting gases are treated identically; no reaction or equilibrium calculation is triggered.

## Density

Density and molar mass use:

```text
rho = P M / (Z R T)
M = rho Z R T / P
```

`M` is kg/mol internally, pressure is Pa, temperature is K and density is kg/m3.

## Transformations

Gas transformations use a constant amount of gas for every supported transformation model. The constraint selects which state variable remains fixed in addition to the shared constant-amount assumption:

```text
P1 V1 / T1 = P2 V2 / T2
```

The request must declare the constraint:

```text
CONSTANT_TEMPERATURE
CONSTANT_PRESSURE
CONSTANT_VOLUME
CONSTANT_AMOUNT
```

The process is never inferred from coincidentally equal values.
