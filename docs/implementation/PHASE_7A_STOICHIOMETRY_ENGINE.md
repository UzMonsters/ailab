# Phase 7A Implementation Report — Stoichiometry Engine Core

## Executive Summary

Phase 7A implements the stateless, framework-independent **Stoichiometry Engine Core**:
1. Pure chemistry domain model under `com.ailab.chemistry.domain.stoichiometry` (zero Spring, JPA, Jackson, or Flyway dependencies).
2. Complete support for mass $\leftrightarrow$ moles conversions, stoichiometric mole ratios, required reactant quantities, expected product quantities, limiting reagent identification (with tied reagent support), excess reactant calculations, theoretical yield, actual yield, percent yield ($>100\%$ marked `ABOVE_THEORETICAL`), purity adjustments, and reaction extent.
3. Interval-aware molar mass bounds propagation ($M_{\text{low}}, M_{\text{upp}}$).
4. Full integration into `StoichiometryServiceImpl` using existing `ReactionCatalogService` and `CompoundCatalogService` without creating artificial database tables.
5. All 165 pre-existing tests + 11 new Phase 7A unit and integration tests pass cleanly.

---

## 1. Domain Types Added
- `StoichiometricQuantity`
- `ReactionParticipantQuantity`
- `StoichiometricRatio`
- `ReactionExtent`
- `LimitingReagentResult`
- `ExcessReactantResult`
- `TheoreticalYieldResult`
- `ActualYieldResult`
- `Purity`
- `PercentYield`
- `YieldStatus` (`NORMAL`, `ABOVE_THEORETICAL`, `ZERO_YIELD`)
- `StoichiometryCalculationResult`
- `StoichiometryErrorCode`
- `StoichiometryException`
- `StoichiometryCalculator`

---

## 2. Calculation Operations & Molar Mass Source
- **Molar Mass Source**: Authoritative Compound Catalogue / Periodic Table Core (`CompoundCatalogService` -> `MolarMass`). No duplicated molar mass data.
- **Limiting-Reagent Identification**: Evaluates available extent $\xi_i = \frac{n_{i, \text{pure}}}{\nu_i}$. Identifies minimum extent $\xi_{\text{lim}}$. Supports single, multiple, and tied limiting reagents explicitly.
- **Excess Reactant**: Calculates consumed moles $\xi_{\text{lim}} \times \nu_r$ and remaining quantity $\max(0, n_{r, \text{init}} - n_{r, \text{cons}})$. Clamped to zero to prevent negative excess values due to rounding.
- **Theoretical & Actual Yield**: Computes expected product moles $\xi_{\text{lim}} \times \nu_p$ and mass $n_p \times M_p$. Percent yield uses `BigDecimal` with `MathContext.DECIMAL128`. Yield $>100\%$ is classified as `ABOVE_THEORETICAL`.
- **Purity**: Adjusts input mass/moles via $P \in (0, 100\%]$.
- **Interval Bounds Propagation**: Computes representative, lower, and upper bounds for moles and masses when compound molar mass is an interval.

---

## 3. Representative Calculations Verified

1. **$2\text{H}_2 + \text{O}_2 \rightarrow 2\text{H}_2\text{O}$**:
   - Mass/mole conversion: 36.03056g $\text{H}_2\text{O} = 2.0\text{ mol}$.
   - Limiting oxygen (4g $\text{H}_2$ + 16g $\text{O}_2 \rightarrow \text{O}_2$ limiting).
   - Limiting hydrogen (1g $\text{H}_2$ + 32g $\text{O}_2 \rightarrow \text{H}_2$ limiting).
   - Exact stoichiometric mixture ($2\text{ mol } \text{H}_2 + 1\text{ mol } \text{O}_2 \rightarrow$ tied limiting reagents).
   - Remaining excess reactant and theoretical water yield.
2. **$\text{CH}_4 + 2\text{O}_2 \rightarrow \text{CO}_2 + 2\text{H}_2\text{O}$**:
   - Hydrocarbon combustion with multiple products and limiting reagent.
3. **$\text{HCl} + \text{NaOH} \rightarrow \text{NaCl} + \text{H}_2\text{O}$**:
   - 1:1 acid-base neutralization equivalence.
4. **$2\text{H}_2\text{O}_2 \rightarrow 2\text{H}_2\text{O} + \text{O}_2$**:
   - Single reactant decomposition and product selection.
5. **$2\text{NaHCO}_3 \rightarrow \text{Na}_2\text{CO}_3 + \text{CO}_2 + \text{H}_2\text{O}$**:
   - Single reactant producing three products.

---

## 4. Verification Suite Results

- **Executed Tests**:
  - `identity-module`: 35 tests
  - `chemistry-engine`: 131 tests (added `StoichiometryCalculatorTest` & `StoichiometryServiceTest`)
  - `app`: 11 tests (added `Phase7AReleaseVerificationTest`)
  - **Total**: **177 tests executed** (0 failures, 0 errors, 0 skipped).
- **PostgreSQL Integration**: Verified Spring injection of `StoichiometryService`, `ReactionCatalogService`, and `CompoundCatalogService` against PostgreSQL 17.5 V18 database.
- **Architecture Boundaries**: `com.ailab.chemistry.domain.stoichiometry` has zero framework dependencies.
- **Working Tree**: Clean.

---

## Exact Release Gate Decision

**PASS** — Phase 7A complete; stoichiometric conversion, limiting-reagent, yield, regression and integration gates pass. The Solution Engine may begin.
