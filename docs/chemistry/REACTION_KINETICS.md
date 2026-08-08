# Reaction Kinetics

Phase 9 implements stateless empirical reaction kinetics, rate evaluation, species formation/consumption rates, analytical integrated rate laws, half-life calculation, modified Arrhenius temperature dependence, and numerical progress simulation.

## Scientific Principles

### 1. Empirical Rate Law vs Stoichiometry
The reaction rate order is strictly empirical and **MUST NEVER** be inferred automatically from stoichiometric coefficients $\nu_i$.

For a general rate law:
$$r = k \cdot \prod_i c_i^{\alpha_i}$$
where $\alpha_i \ge 0$ is an explicitly supplied empirical order.

Species rates follow:
$$\frac{dc_i}{dt} = \nu_i \cdot r$$

### 2. Radical Participant Integrity
Elementary reaction rate laws must reference exact radical/molecular participant species (e.g. $\text{H}^\bullet$ `COMP-RAD-H`, $\text{OH}^\bullet$ `COMP-RAD-OH`, $\text{O}^\bullet$ `COMP-RAD-O`), and must not confuse atomic radicals with molecular diatomic species ($\text{H}_2, \text{O}_2$). Active sourced elementary profiles must reference exact elementary reaction records, not global catalogue reactions such as `RXN-WATER-SYNTHESIS` or `RXN-CO-OXIDATION`.

### 3. Unit Conversion Invariant
Original source units ($\text{cm}^3\text{ molecule}^{-1}\text{s}^{-1}$) are converted to internal SI molar units ($\text{L mol}^{-1}\text{s}^{-1}$) via Avogadro constant factor:
$$1\text{ cm}^3\text{ molecule}^{-1}\text{s}^{-1} = 6.02214076 \times 10^{20}\text{ L mol}^{-1}\text{s}^{-1}$$

### 4. Modified Arrhenius Model
$$k(T) = A \cdot \left( \frac{T}{T_{\text{ref}}} \right)^n \cdot \exp\left( -\frac{E_a}{R T} \right)$$

### 5. Numerical Progress Solver
Solves constant-volume $\frac{d\xi_V}{dt} = r(\vec{c}(t))$ using RK4 adaptive numerical integration with non-negativity reactant depletion safeguards.

## Implementation Boundary

`ReactionKineticsServiceImpl` is a service-layer implementation in `com.ailab.chemistry.service`; it is not a domain type. Domain kinetics objects and calculators remain framework-independent under `com.ailab.chemistry.domain.kinetics`.
