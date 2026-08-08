# Buffer Solution Calculator

## Supported Systems
Phase 7E supports stateless aqueous, monoprotic buffer calculations for:

- weak acid / conjugate base buffers, such as acetic acid / acetate;
- weak base / conjugate acid buffers, such as ammonia / ammonium.

The solvent must be water (`COMP-H2O`), temperature is mandatory, and the species must be a validated conjugate pair in the acid-base reference catalogue. Amphiprotic and polyprotic buffer systems are intentionally deferred.

## Henderson-Hasselbalch Applicability
The calculator uses Henderson-Hasselbalch only while both conjugate components remain positive. pH values outside 0-14 are allowed when the mathematics produces them. Results outside about one pH unit from the governing pKa are marked `OUTSIDE_RECOMMENDED_BUFFER_RANGE` instead of being rejected.

For weak acid buffers:

```text
pH = pKa + log10(nA- / nHA)
```

For weak base buffers:

```text
pOH = pKb + log10(nBH+ / nB)
pH = pKw - pOH
```

`pKw` is calculated from the requested temperature-specific `Kw`; no arbitrary-temperature `pKw = 14` assumption is used.

## Preparation
For target pH, total buffer concentration, final volume, and a validated conjugate pair, the calculator returns required ratio, component concentrations, and component moles.

Weak acid buffer:

```text
r = 10^(pH - pKa)
[HA] = Ctotal / (1 + r)
[A-] = Ctotal * r / (1 + r)
```

Weak base buffer:

```text
pOH = pKw - pH
r = 10^(pOH - pKb)
[B] = Ctotal / (1 + r)
[BH+] = Ctotal * r / (1 + r)
```

## Perturbation And Exhaustion
Strong reagent additions are applied stoichiometrically before any pH recalculation.

For `HA/A-`:

```text
A- + H+ -> HA
HA + OH- -> A- + H2O
```

For `B/BH+`:

```text
B + H+ -> BH+
BH+ + OH- -> B + H2O
```

At exact component exhaustion, Phase 7E delegates to the Phase 7D weak-acid, weak-base, or salt-hydrolysis calculation for the remaining single weak species. If excess strong acid or strong base remains after exhaustion, the calculator returns `EXCESS_STRONG_ACID_UNSUPPORTED` or `EXCESS_STRONG_BASE_UNSUPPORTED` and does not fabricate a pH; mixed strong/weak equilibrium solving is deferred.

Strong-acid/base additions require an explicit volume policy:

- `NEGLIGIBLE_ADDED_VOLUME`: final volume is explicitly the initial buffer volume.
- `EXPLICIT_FINAL_VOLUME`: caller provides the post-addition final volume.

Post-perturbation component concentrations, exact-exhaustion Phase 7D delegation concentration, total buffer concentration, and capacity all use the policy-selected final volume. Explicit final volume must be positive and cannot be smaller than the initial buffer volume.

## Dilution
Dilution is proportional and nonreactive:

- component moles remain constant;
- final concentrations are recalculated from the new volume;
- ideal Henderson-Hasselbalch pH is unchanged because the mole ratio is unchanged;
- total buffer concentration and ideal capacity decrease.

## Capacity Approximation
Phase 7E returns an ideal monoprotic approximation:

```text
beta = 2.303 * ([H+] + Kw/[H+] + Ctotal * Ka * [H+] / (Ka + [H+])^2)
```

For weak base buffers, the conjugate-acid Ka is derived consistently from `Kw / Kb`. This is not an activity-corrected thermodynamic buffer capacity.

## Decimal Transcendental Contract
Buffer logarithm and power operations are centralized in the internal `AcidBaseDecimalMath` utility. This is the only acid-base-domain location that converts `BigDecimal` inputs to Java `double` for `Math.log10` and `Math.pow`.

Contract:

- inputs are validated before conversion: `log10(x)` requires `x > 0`; `10^x` requires a non-null exponent;
- non-finite Java results (`NaN`, infinity) and non-positive power results are rejected as `NUMERICALLY_UNSAFE_REQUEST`;
- outputs are converted back with `BigDecimal.valueOf`;
- surrounding buffer arithmetic uses `MathContext.DECIMAL128`;
- display pH/pOH values are rounded to four decimals with half-up rounding;
- representative buffer ratios from `1E-12` through `1E12` are covered by round-trip and monotonicity tests;
- documented round-trip tolerance is relative `1E-12` for representative ratios.

## Deferred Work
The public service does not expose titration curves, polyprotic buffers, mixed excess-strong-reagent equilibria, or activity-coefficient corrections.
