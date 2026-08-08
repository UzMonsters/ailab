# Calorimetry and Thermal Balance

Phase 8 closure implements stateless calorimetry calculations, thermal mixing energy balancing, reaction calorimetry, and adiabatic final-temperature solving.

## Mathematical Formulations

### 1. Sensible Heat

- **Mass basis**:
  $$q = m \cdot c_p \cdot (T_f - T_i)$$

- **Molar basis**:
  $$q = n \cdot C_{p,m} \cdot (T_f - T_i)$$

- **Temperature-dependent Shomate integration**:
  $$q = n \cdot \int_{T_i}^{T_f} C_p(T) \, dT = n \cdot [\Delta H_{\text{molar}}(T_f) - \Delta H_{\text{molar}}(T_i)]$$

- **Calorimeter sensible heat**:
  $$q_{\text{cal}} = C_{\text{cal}} \cdot (T_f - T_i)$$

### 2. Sign Conventions

- Positive $q_{\text{system}}$: System absorbs heat ($T_f > T_i$).
- Negative $q_{\text{system}}$: System releases heat ($T_f < T_i$).
- Isolated system thermal balance: $\sum q_k + q_{\text{calorimeter}} = 0$.

### 3. Thermal Mixing

Solves $\sum q_k(T_f) + q_{\text{calorimeter}}(T_f) = 0$ for $T_f \in [\min T_{i,k}, \max T_{i,k}]$.
For example, mixing equal masses of water at $20\text{ }^\circ\text{C}$ ($293.15\text{ K}$) and $80\text{ }^\circ\text{C}$ ($353.15\text{ K}$) with a calorimeter ($C_{\text{cal}} = 1000\text{ J/K}$) having an explicit initial calorimeter temperature $T_{\text{cal},i} = 20\text{ }^\circ\text{C}$ ($293.15\text{ K}$) shifts final temperature below $50\text{ }^\circ\text{C}$.
Supports constant heat capacities and temperature-dependent Shomate $C_p(T)$ integration. `CalorimetryServiceImpl` is implemented in the service layer (`com.ailab.chemistry.service`).

### 4. Reaction Calorimetry

- Constant-pressure reaction heat:
  $$q_{\text{reaction}} = \xi \cdot \Delta_r H(T)$$
  where $\xi$ is applied extent in moles.
- Heat transferred to surroundings: $q_{\text{surroundings}} = -q_{\text{reaction}}$.

### 5. Adiabatic Final Temperature

Solves constant-pressure enthalpy balance:
$$q_{\text{reaction}}(T_i) + \sum_k n_{k,\text{final}} \int_{T_i}^{T_f} C_{p,k}(T) \, dT + C_{\text{cal}} (T_f - T_i) = 0$$
Strictly within Shomate correlation validity ranges without extrapolation or phase change.
