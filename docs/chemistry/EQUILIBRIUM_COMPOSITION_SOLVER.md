# Equilibrium Composition Solver

Phase 8 implements stateless equilibrium-composition calculations for one balanced reaction at a time.

## Mathematical Formulation

For reaction extent $\xi$:

$$n_i(\xi) = n_{i,0} + \nu_i \cdot \xi$$

where signed coefficients are:
- reactants: $\nu_i < 0$
- products: $\nu_i > 0$

Physical extent bounds $[\xi_{\min}, \xi_{\max}]$ are derived from non-negative participant amounts $n_i(\xi) \ge 0$:
- $\xi_{\max} = \min_{i: \nu_i < 0} \left( \frac{n_{i,0}}{|\nu_i|} \right)$
- $\xi_{\min} = \max_{i: \nu_i > 0} \left( -\frac{n_{i,0}}{\nu_i} \right)$

At equilibrium:

$$\ln Q(\xi) = \ln K(T)$$

$$\Delta_r G(\xi) = R \cdot T \cdot (\ln Q(\xi) - \ln K(T)) = 0$$

## System Models Supported

1. **`CONSTANT_TOTAL_PRESSURE`** (Ideal Gas):
   $$p_i = y_i \cdot P_{\text{total}} = \frac{n_i(\xi)}{n_{\text{gas}}(\xi)} \cdot P_{\text{total}}$$
   $$a_i = p_i / p^\circ$$

2. **`CONSTANT_VOLUME_IDEAL_GAS`** (Ideal Gas):
   $$p_i = \frac{n_i(\xi) \cdot R \cdot T}{V}$$
   $$a_i = p_i / p^\circ$$

3. **`AQUEOUS_IDEAL`**:
   $$a_i = c_i / c^\circ = \frac{n_i(\xi)}{V_{\text{solution}} \cdot 1.0\text{ M}}$$

4. **`AQUEOUS_DAVIES`**:
   $$a_i = \gamma_i \cdot \frac{n_i(\xi)}{V_{\text{solution}} \cdot 1.0\text{ M}}$$
   Reuses `IonicActivityService` self-consistently.

## Solver Design

Uses a deterministic bounded root solver (Safeguarded Bisection / Secant) inside $[\xi_{\min}, \xi_{\max}]$.
Primary calculations operate on logarithmic terms $\ln Q$ and $\ln K$, preserving numerical stability under extreme values of $K$.
