# Stoichiometry Engine Core Architecture

## Overview
The Stoichiometry Engine Core (`com.ailab.chemistry.domain.stoichiometry` and `com.ailab.chemistry.service.StoichiometryServiceImpl`) provides pure, framework-independent reaction stoichiometric calculations using evaluated reaction balanced equations, compound molar masses, and the interval-aware measurement system.

## Mathematical Formulas & Calculation Flow

### 1. Mass ↔ Moles Conversion
- **Mass to Moles**:
  $$n = \frac{m \times \text{purity}}{M_{\text{rep}}}$$
  For interval molar masses ($[M_{\text{low}}, M_{\text{upp}}]$):
  $$n_{\text{low}} = \frac{m \times \text{purity}}{M_{\text{upp}}}, \quad n_{\text{upp}} = \frac{m \times \text{purity}}{M_{\text{low}}}$$
- **Moles to Mass**:
  $$m = n \times M_{\text{rep}}$$

### 2. Stoichiometric Mole Ratios & Reaction Extent
- **Stoichiometric Ratio**:
  $$r_{i \to j} = \frac{\nu_j}{\nu_i}$$
- **Available Reaction Extent**:
  $$\xi_i = \frac{n_{i, \text{pure}}}{\nu_i}$$

### 3. Limiting Reagent Semantics
- **Limiting Reagent**:
  The reactant(s) with the minimum available reaction extent:
  $$\xi_{\text{lim}} = \min_{i} (\xi_i)$$
- **Tied Limiting Reagents**:
  If multiple reactants yield identical minimum extent $\xi_{\text{lim}}$ within calculation precision (`MathContext.DECIMAL128`), all tied reactants are recorded explicitly in `LimitingReagentResult.getLimitingCompoundCodes()`.

### 4. Excess Reactant & Remaining Quantities
- **Consumed Moles**: $n_{i, \text{cons}} = \xi_{\text{lim}} \times \nu_i$
- **Remaining Moles**: $n_{i, \text{rem}} = \max(0, n_{i, \text{init}} - n_{i, \text{cons}})$
- Excess quantities are clamped to 0 to prevent negative values from rounding precision.

### 5. Theoretical Yield, Actual Yield & Percent Yield
- **Theoretical Moles**: $n_{\text{theo}, p} = \xi_{\text{lim}} \times \nu_p$
- **Theoretical Mass**: $m_{\text{theo}, p} = n_{\text{theo}, p} \times M_{\text{rep}, p}$
- **Percent Yield**:
  $$\text{PercentYield} = \frac{m_{\text{actual}}}{m_{\text{theoretical}}} \times 100\%$$
- **Yield Status Classification**:
  - `NORMAL`: $0\% < \text{yield} \le 100\%$
  - `ABOVE_THEORETICAL`: $\text{yield} > 100\%$
  - `ZERO_YIELD`: $\text{yield} = 0\%$

## Deferred Scope
- **Gas Volume Calculations**: Deferred to Gas Laws module.
- **Solution Concentrations & Titration**: Deferred to Solution Engine.
- **pH / Acid-Base Equilibria**: Deferred to Ionic Equilibria module.
- **Thermodynamic Feasibility, Kinetics, Electrochemistry, Safety Blocking**: Excluded from Phase 7A.
