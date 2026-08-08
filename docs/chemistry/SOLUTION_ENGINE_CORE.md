# Solution Composition and Dilution Engine Core

## Overview
The Solution Composition and Dilution Engine Core (`com.ailab.chemistry.domain.solution` and `com.ailab.chemistry.service.SolutionCalculationServiceImpl`) provides pure, framework-independent solution composition and concentration calculations using compound molar masses and the explicit measurement system.

## Concentration Definitions & Formulas

### 1. Molarity ($C$)
- **Formula**: $C = \frac{n_{\text{solute}}}{V_{\text{solution}}}$
- **Units**: $\text{mol/L}$ (Molar)
- Uses final solution volume, NOT solvent volume.

### 2. Molality ($b$)
- **Formula**: $b = \frac{n_{\text{solute}}}{m_{\text{solvent, kg}}}$
- **Units**: $\text{mol/kg}$
- Uses solvent mass in kilograms, NOT total solution mass.

### 3. Mass Concentration ($\rho_i$)
- **Formula**: $\rho_i = \frac{m_{\text{solute}}}{V_{\text{solution}}}$
- **Units**: $\text{g/L}$

### 4. Mass Fraction ($w_i$) & Mass Percentage
- **Formula**: $w_i = \frac{m_i}{m_{\text{solution, total}}}$
- **Percentage**: $w_i \times 100\%$

### 5. Mole Fraction ($x_i$)
- **Formula**: $x_i = \frac{n_i}{n_{\text{total}}}$
- Satisfies $\sum x_i = 1.0$.

### 6. Volume Fraction ($\phi_i$)
- **Formula**: $\phi_i = \frac{V_i}{V_{\text{total}}}$
- **Percentage**: $\phi_i \times 100\% \text{ (v/v)}$

## Dilution Engine
- **Conservation of Solute Amount**: $n_{\text{initial}} = n_{\text{target}}$
- **Equation**: $C_1 V_1 = C_2 V_2$
- **Added Solvent Volume**: $V_{\text{added}} = V_2 - V_1$ under the `ADDITIVE_VOLUMES` policy.
- Enforces $C_2 \le C_1$ (dilution cannot increase concentration).

## Solution Mixing & Volume Additivity Policies
- `ADDITIVE_VOLUMES`: $V_{\text{final}} = \sum V_i$.
- `NON_ADDITIVE_DENSITY_REQUIRED`: Volumes are treated as non-additive; mixture density is strictly required to determine final solution volume. Rejects requests without density.
- `EXPLICIT_FINAL_VOLUME`: Final solution volume is explicitly supplied.

## Interval Molar Mass Bounds Propagation
- **Mass Concentration to Molarity**:
  $$C_{\text{rep}} = \frac{\rho_i}{M_{\text{rep}}}, \quad C_{\text{low}} = \frac{\rho_i}{M_{\text{upp}}}, \quad C_{\text{upp}} = \frac{\rho_i}{M_{\text{low}}}$$
- **Solution Preparation**:
  $$m_{\text{rep}} = C \cdot V \cdot M_{\text{rep}}, \quad m_{\text{low}} = C \cdot V \cdot M_{\text{low}}, \quad m_{\text{upp}} = C \cdot V \cdot M_{\text{upp}}$$

## Deferred Scope
- **pH, pOH, Acid-Base Equilibria, Buffers, Titration Curves**: Deferred to Solution Equilibria / Ionic Equilibria module.
- **Dissociation Constants ($K_a, K_b, K_{sp}$), Solubility Saturation & Precipitation**: Deferred.
- **Ionic Strength, Activity Coefficients, Normality**: Deferred.
- **Osmotic Pressure & Colligative Properties**: Deferred.
- **Thermodynamics & Kinetics**: Deferred.
