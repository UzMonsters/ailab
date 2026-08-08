# Acid-Base Equilibrium Calculator Core

## Overview
The Acid-Base Equilibrium Calculator (`com.ailab.chemistry.domain.acidbase.AcidBaseEquilibriumCalculator` and `com.ailab.chemistry.service.AcidBaseEquilibriumServiceImpl`) performs exact, stateless ideal-solution calculations for aqueous acid-base systems without crude approximations (such as assuming $x \ll C$).

## Governing Scientific Equations

### 1. Pure Water Autoionization
- **Equilibrium**: $H_2O \rightleftharpoons H_3O^+ + OH^-$
- **Governing Law**: $[H_3O^+][OH^-] = K_w(T)$
- **Pure Water Condition**: $[H_3O^+] = [OH^-] = \sqrt{K_w}$
- **pH / pOH**: $pH = -\log_{10}([H_3O^+])$, $pOH = pK_w - pH$.

### 2. Strong Monoprotic Acids ($HA + H_2O \rightarrow H_3O^+ + A^-$)
- Complete 100% dissociation + water autoionization mass/charge balance:
  $[H_3O^+]^2 - C_a [H_3O^+] - K_w = 0$
- **Exact Closed-Form Solution**:
  $[H_3O^+] = \frac{C_a + \sqrt{C_a^2 + 4 K_w}}{2}$
- Exact for both concentrated ($0.1\text{ M } HCl \rightarrow pH = 1.0000$) and extremely dilute solutions ($10^{-8}\text{ M } HCl \rightarrow pH \approx 6.98$).

### 3. Strong Monobasic Bases ($BOH \rightarrow B^+ + OH^-$)
- Complete 100% dissociation + water autoionization mass/charge balance:
  $[OH^-]^2 - C_b [OH^-] - K_w = 0$
- **Exact Closed-Form Solution**:
  $[OH^-] = \frac{C_b + \sqrt{C_b^2 + 4 K_w}}{2}, \quad [H_3O^+] = \frac{K_w}{[OH^-]}$

### 4. Weak Monoprotic Acids / Conjugate Acid Salts
- Equilibrium: $HA + H_2O \rightleftharpoons H_3O^+ + A^-$
- **Exact Polynomial Equation**:
  $f(h) = h^3 + K_a h^2 - (C_a K_a + K_w) h - K_a K_w = 0, \quad h = [H_3O^+]$
- **Root Solver**: Deterministic bounded root solver using Newton-Raphson with bisection fallback down to residual tolerance $|f(h)| < 10^{-12}$.

### 5. Weak Monobasic Bases / Conjugate Base Salts
- Equilibrium: $B + H_2O \rightleftharpoons BH^+ + OH^-$
- **Exact Polynomial Equation**:
  $g(oh) = (oh)^3 + K_b (oh)^2 - (C_b K_b + K_w) (oh) - K_b K_w = 0, \quad oh = [OH^-]$
- $[H_3O^+] = \frac{K_w}{[OH^-]}$, $pH = -\log_{10}([H_3O^+])$.

## Solver Policy & Error Handling
- **No Approximations**: $x \ll C$ is never assumed; exact cubic equations with water autoionization are solved for all weak systems.
- **Convergence Tolerance**: Residual $|f(x)| \le 10^{-10}$ M.
- **Error Codes**: `INVALID_TEMPERATURE`, `UNSUPPORTED_SOLVENT`, `NON_POSITIVE_CONCENTRATION`, `MISSING_EQUILIBRIUM_CONSTANT`, `SPECIES_ROLE_MISMATCH`, `SOLVER_CONVERGENCE_FAILED`.
- **Negative / Out-of-Range pH**: pH values outside $0-14$ (e.g. concentrated acids $pH < 0$) are preserved mathematically.

## Deferred Functionality
- Buffers and Henderson-Hasselbalch equation.
- Polyprotic equilibrium solving ($K_{a1}, K_{a2}, K_{a3}$ simultaneous equilibrium).
- Titration curves and indicator color transitions.
- Ionic strength and Davies/Extended Debye-Hückel activity coefficient corrections.
