# AI Laboratory MVP: Authoritative Frontend API Contract & Business-Process Manual

**Document Version:** 3.2.0-FINAL  
**Target Audience:** Frontend Engineers, System Integrators, UI/UX Developers, QA Engineers  
**Release Status:** MVP Complete, 60/60 Endpoints Fully Exposed & Exhaustively Documented  
**Backend Architecture:** Java 21 LTS / Spring Boot 3.4.5 / Dual-Schema PostgreSQL 15+  

---

## 1. Executive Summary & Engine Guarantees

This document is the authoritative backend-to-frontend specification and business-process manual for the **AI Laboratory** Minimum Viable Product (MVP).

### Subsystem Guarantees & Contract Commitments
1. **60 Exposed REST Endpoints:** Exactly 60 HTTP REST endpoints are exposed across the modular monolith (17 for User Identity & Administration, 43 for Chemistry Solvers & Laboratory Simulation).
2. **Zero Client-Side Math:** All scientific calculations, physical unit conversions, stoichiometric balances, equilibrium solves, and safety evaluations are performed server-side with arbitrary-precision decimal math (`BigDecimal`).
3. **Stateless Security & Cookie Governance:** Stateless JWT Bearer token authorization (`ROLE_USER`, `ROLE_ADMIN`). Refresh tokens are stored in HttpOnly, Secure, SameSite=Strict cookies mapped to `/api/v1/auth`.
4. **Typed Scientific Operation Commands:** All virtual laboratory operation commands enforce strict, strongly-typed parameter models (`MixReagentsCommandPayload`, `StoichiometricReactionCommandPayload`, `ThermalOperationCommandPayload`, `GasStateChangeCommandPayload`, `EquilibriumReactionCommandPayload`, `ElectrolysisCommandPayload`). Dynamic event stream endpoints (`AppendEventRequest`) and calculation audit logs use structured key-value maps for UI logging flexibility.
5. **Governed Real-Time Safety Gate:** Experiment operations are pre-evaluated against governed safety rules (`SAFE-FUME-HOOD-REQ`, `SAFE-TEMP-LIMIT-GLASS`, `SAFE-PRESSURE-LIMIT-CONTAINER`). Unsafe operations return HTTP 422 with structured safety evaluations.
6. **100% Deterministic Replay & Immutable Audit:** Every executed operation writes an immutable audit record containing initial state SHA-256 hashes, input parameters, conservation ledgers, and final state SHA-256 hashes to `chemistry.simulation_calculation_audit`.

---

## 2. Global Technical Conventions

### 2.1 Base Path & Headers
* **Base URL:** `http://localhost:8080` (Local) / Custom Domain (Prod)
* **API Prefix:** `/api/v1`
* **Content Type:** `application/json`
* **Authorization Header:** `Authorization: Bearer <accessToken>`

### 2.2 Strongly-Typed Physical Measurement Matrix

| Measurement Domain | Domain Class | Supported Units | Canonical Base Unit | Conversion Rule |
| :--- | :--- | :--- | :--- | :--- |
| **Amount of Substance** | `Quantity` | `MOLE` (`mol`), `MILLIMOLE` (`mmol`), `MICROMOLE` (`umol`) | `MOLE` | $1\text{ mol} = 1000\text{ mmol} = 10^6\text{ }\mu\text{mol}$ |
| **Mass** | `Mass` | `KILOGRAM` (`kg`), `GRAM` (`g`), `MILLIGRAM` (`mg`) | `KILOGRAM` | $1\text{ kg} = 1000\text{ g} = 10^6\text{ mg}$ |
| **Volume** | `Volume` | `CUBIC_METER` (`m3`), `LITER` (`L`), `MILLILITER` (`mL`) | `CUBIC_METER` | $1\text{ m}^3 = 1000\text{ L} = 10^6\text{ mL}$ |
| **Temperature (Absolute)**| `Temperature` | `KELVIN` (`K`), `CELSIUS` (`°C`), `FAHRENHEIT` (`°F`) | `KELVIN` | $T(K) = T(^\circ\text{C}) + 273.15$ |
| **Temperature Delta** | `TemperatureDelta` | `KELVIN` (`K`), `CELSIUS` (`°C`) | `KELVIN` | $\Delta T(K) = \Delta T(^\circ\text{C})$ |
| **Pressure** | `Pressure` | `PASCAL` (`Pa`), `BAR` (`bar`), `ATMOSPHERE` (`atm`), `TORR` (`mmHg`) | `PASCAL` | $1\text{ atm} = 101325\text{ Pa} = 1.01325\text{ bar} = 760\text{ Torr}$ |
| **Molar Concentration** | `MolarConcentration` | `MOL_PER_LITER` (`mol/L`), `MOL_PER_CUBIC_METER` (`mol/m3`) | `MOL_PER_CUBIC_METER` | $1\text{ mol/L} = 1000\text{ mol/m}^3$ |
| **Density** | `Density` | `GRAM_PER_CUBIC_CENTIMETER` (`g/cm3`), `KILOGRAM_PER_CUBIC_METER` (`kg/m3`) | `KILOGRAM_PER_CUBIC_METER` | $1\text{ g/cm}^3 = 1000\text{ kg/m}^3$ |
| **Energy** | `Energy` | `JOULE` (`J`), `KILOJOULE` (`kJ`), `CALORIE` (`cal`) | `JOULE` | $1\text{ kJ} = 1000\text{ J} = 239.006\text{ cal}$ |

> [!IMPORTANT]
> **Absolute Temperature vs Temperature Delta Policy:**  
> Absolute temperatures (`Temperature`) convert $25^\circ\text{C}$ to $298.15\text{ K}$. Temperature deltas (`TemperatureDelta`) convert a $25^\circ\text{C}$ change directly to a $25\text{ K}$ change. The frontend must bind absolute inputs and temperature changes to their respective typed endpoints.

### 2.3 Date, Time & Timezone Policy (`Asia/Tashkent`)
* **Persistence & API:** All timestamps are emitted as ISO-8601 UTC strings (e.g. `2026-08-08T12:00:00Z`).
* **Frontend Localization:** Applications displaying local business time in `Asia/Tashkent` (UTC+5) format strings client-side using `Intl.DateTimeFormat` or `date-fns`:
  ```typescript
  const formattedDate = new Date(isoUtcString).toLocaleString('en-US', {
    timeZone: 'Asia/Tashkent',
    dateStyle: 'medium',
    timeStyle: 'medium'
  });
  ```

### 2.4 Canonical Error Response Schema
All error responses from the backend follow this exact JSON structure:

```typescript
interface ApiError {
  timestamp: string;          // ISO-8601 UTC timestamp (e.g. "2026-08-08T14:00:00Z")
  status: number;             // HTTP status code (400, 401, 403, 404, 409, 422, 500)
  error: string;              // HTTP reason phrase (e.g. "Bad Request", "Unprocessable Entity")
  message: string;            // Human-readable error description
  path: string;               // Target URI requested
  fieldViolations: Array<{
    field: string;
    message: string;
  }>;
}
```

---

## 3. Scientific Business Logic & Subsystem Principles

### 3.1 Formula Parsing Subsystem
* **Supported Syntax:** Standard IUPAC symbols (`H`, `He`, `Cu`), nested parentheses e.g. `(NH4)2SO4`, hydrated notation e.g. `CuSO4·5H2O` or `CuSO4.5H2O`, ionic charges e.g. `Fe3+`, `SO4 2-`. Unicode subscripts (`H₂O`) are normalized automatically.
* **Calculation Engine:** Recursive Descent Grammar Parser computes exact stoichiometry, charge, hydrate multiplier, total molar mass (via IUPAC standard atomic weights), and elemental mass percentages.
* **Failure Modes:** Mismatched parentheses (`UNBALANCED_PARENTHESES`), unrecognized element symbol (`INVALID_ELEMENT_SYMBOL`), invalid charge (`INVALID_CHARGE_SYNTAX`).

### 3.2 Equation Balancing Subsystem
* **Matrix Reduction:** Constructs element conservation matrix $A \cdot x = 0$ alongside charge conservation row. Solves integer linear system via Gaussian elimination to find minimal integer coefficients.
* **Balancing Rules:** Accepts `->`, `=`, `==`. Validates mass & charge conservation. Detects already balanced equations or mathematically underdetermined equations.

### 3.3 Thermodynamics & Calorimetry Subsystem
* **Reference Data:** Looks up standard reference profiles ($H_f^\circ, S^\circ, G_f^\circ, C_p$) from database seed derived from NIST WebBook and CRC Handbook.
* **State Functions:**
  $$\Delta H^\circ_{rxn} = \sum \nu_i \Delta H_{f,prod}^\circ - \sum \nu_i \Delta H_{f,react}^\circ$$
  $$\Delta S^\circ_{rxn} = \sum \nu_i S_{prod}^\circ - \sum \nu_i S_{react}^\circ$$
  $$\Delta G^\circ_{rxn} = \Delta H^\circ_{rxn} - T \Delta S^\circ_{rxn}$$
  $$K_{eq} = \exp\left(-\frac{\Delta G^\circ_{rxn}}{R T}\right)$$
* **Calorimetry Solvers:** Computes sensible heat $q = m C_p \Delta T$, thermal mixing $T_{final} = \frac{\sum m_i C_{p,i} T_i}{\sum m_i C_{p,i}}$, and reaction heat enthalpy.

### 3.4 Acid-Base, Buffer & Titration Subsystem
* **Exact Equilibrium Solver:** Solves exact polynomial equilibria for hydronium $[\text{H}_3\text{O}^+]$ and hydroxide $[\text{OH}^-]$ without arbitrary approximations when concentration approaches $10^{-7}\text{ M}$.
* **Buffer & Titration:** Henderson-Hasselbalch equation $\text{pH} = pK_a + \log\left(\frac{[A^-]}{[HA]}\right)$, buffer capacity $\beta = 2.303 \cdot C \cdot \frac{K_a [\text{H}^+]}{(K_a + [\text{H}^+])^2}$. Monoprotic and polyprotic titration curves compute initial pH, half-equivalence points, equivalence points, and post-equivalence pH.

### 3.5 Kinetics Subsystem
* **Rate Laws:** Differential rate law $r = k [A]^m [B]^n$. Integrated rate laws for order $n \in \{0, 1, 2\}$:
  - Order 0: $[A]_t = [A]_0 - k t$, $t_{1/2} = \frac{[A]_0}{2k}$
  - Order 1: $[A]_t = [A]_0 e^{-k t}$, $t_{1/2} = \frac{\ln 2}{k}$
  - Order 2: $\frac{1}{[A]_t} = \frac{1}{[A]_0} + k t$, $t_{1/2} = \frac{1}{k [A]_0}$
* **Arrhenius Equation:** $k(T) = A \exp\left(-\frac{E_a}{R T}\right)$. Discrete time trajectory simulation computes $[A](t)$ over custom time grids.

### 3.6 Electrochemistry & Faraday Subsystem
* **Galvanic Cell:** $E^\circ_{cell} = E^\circ_{cathode} - E^\circ_{anode}$.
* **Nernst Equation:** $E_{cell} = E^\circ_{cell} - \frac{R T}{n F} \ln Q$.
* **Faraday Electrolysis:** Mass deposited $m = \frac{I \cdot t \cdot M}{n \cdot F}$, where $F = 96485.3321\text{ C/mol}$.

### 3.7 Gas Laws & Phase Behavior Subsystem
* **Ideal Gas Law:** $P V = n R T$ ($R = 8.3144626\text{ J/(mol}\cdot\text{K)}$).
* **Dalton's Law:** Total pressure $P_{total} = \sum P_i$, partial pressures $P_i = x_i P_{total}$.
* **Combined Gas Law:** $\frac{P_1 V_1}{T_1} = \frac{P_2 V_2}{T_2}$.

---

## 4. Exhaustive Contracts for All 60 REST API Endpoints

---

### 4.1 Auth & User Administration (Endpoints 1–17)

#### Endpoint 1: `POST /api/v1/auth/register`
* **Authentication:** Public
* **Business Purpose:** Register a new user account with BCrypt password hashing ($10$ rounds).
* **Request Contract:**
  ```typescript
  interface RegisterRequest {
    username: string; // Required, 3-50 chars
    email: string;    // Required, valid email
    password: string; // Required, min 8 chars
  }
  ```
* **Request Example:**
  ```json
  { "username": "lab_researcher", "email": "researcher@ailab.com", "password": "SecurePassword123!" }
  ```
* **Field Rules:** `username` (string, required), `email` (string, required), `password` (string, required).
* **Success Status:** `201 Created`
* **Response Contract:**
  ```typescript
  interface RegisterResponse {
    id: string;
    username: string;
    email: string;
    message: string;
  }
  ```
* **Response Example:**
  ```json
  { "id": "u-101", "username": "lab_researcher", "email": "researcher@ailab.com", "message": "User registered successfully" }
  ```
* **Errors:** `USERNAME_ALREADY_EXISTS` (409), `EMAIL_ALREADY_EXISTS` (409).
* **Frontend Next Action:** Transition UI to login modal or auto-login.

#### Endpoint 2: `POST /api/v1/auth/login`
* **Authentication:** Public
* **Business Purpose:** Authenticate user credentials, issue JWT access token, set HttpOnly refresh cookie.
* **Request Contract:**
  ```typescript
  interface LoginRequest {
    usernameOrEmail: string; // Required
    password: string;        // Required
  }
  ```
* **Request Example:**
  ```json
  { "usernameOrEmail": "lab_researcher", "password": "SecurePassword123!" }
  ```
* **Success Status:** `200 OK`
* **Response Contract:**
  ```typescript
  interface TokenResponse {
    accessToken: string;
    tokenType: "Bearer";
    expiresInSeconds: number; // 900
  }
  ```
* **Response Example:**
  ```json
  { "accessToken": "eyJhbGciOiJIUzI1NiJ9...", "tokenType": "Bearer", "expiresInSeconds": 900 }
  ```
* **Errors:** `INVALID_CREDENTIALS` (401).
* **Frontend Next Action:** Store `accessToken` in memory state; redirect to lab dashboard.

#### Endpoint 3: `POST /api/v1/auth/refresh`
* **Authentication:** Public (Reads HttpOnly cookie `refresh_token`)
* **Business Purpose:** Re-issue JWT access token and rotate refresh token cookie.
* **Request Contract:** Optional `{ "refreshToken": "string" }`
* **Success Status:** `200 OK`
* **Response Contract:** `TokenResponse`
* **Errors:** `REFRESH_TOKEN_INVALID` (401).
* **Frontend Next Action:** Update memory access token; retry original request.

#### Endpoint 4: `POST /api/v1/auth/logout`
* **Authentication:** Public
* **Business Purpose:** Revoke refresh token in database and clear cookie.
* **Success Status:** `200 OK`
* **Response Contract:** `{ "message": "Successfully logged out" }`
* **Frontend Next Action:** Clear in-memory access token; redirect to login.

#### Endpoint 5: `GET /api/v1/users/me`
* **Authentication:** `ROLE_USER` / `ROLE_ADMIN`
* **Business Purpose:** Fetch current user identity profile.
* **Success Status:** `200 OK`
* **Response Contract:**
  ```typescript
  interface UserMeResponse {
    id: string;
    username: string;
    email: string;
    role: "ROLE_USER" | "ROLE_ADMIN";
    avatarUrl: string | null;
    createdAt: string;
  }
  ```

#### Endpoint 6: `PUT /api/v1/users/me`
* **Authentication:** `ROLE_USER` / `ROLE_ADMIN`
* **Request Contract:** `{ "username": "new_name", "avatarUrl": "https://..." }`
* **Success Status:** `200 OK`

#### Endpoint 7: `GET /api/v1/users/me/preferences`
* **Authentication:** `ROLE_USER` / `ROLE_ADMIN`
* **Success Status:** `200 OK`
* **Response Contract:**
  ```typescript
  interface UserPreferencesResponse {
    theme: "LIGHT" | "DARK" | "SYSTEM";
    defaultTemperatureUnit: "KELVIN" | "CELSIUS" | "FAHRENHEIT";
    defaultPressureUnit: "PASCAL" | "BAR" | "ATMOSPHERE";
    defaultVolumeUnit: "LITER" | "MILLILITER" | "CUBIC_METER";
    autoSaveEnabled: boolean;
  }
  ```

#### Endpoint 8: `PUT /api/v1/users/me/preferences`
* **Authentication:** `ROLE_USER` / `ROLE_ADMIN`
* **Request Contract:** `UserPreferencesResponse`
* **Success Status:** `200 OK`

#### Endpoint 9: `GET /api/v1/users/me/statistics`
* **Authentication:** `ROLE_USER` / `ROLE_ADMIN`
* **Success Status:** `200 OK`
* **Response Contract:**
  ```typescript
  interface UserStatisticsResponse {
    totalExperimentsRun: number;
    totalFormulasParsed: number;
    totalEquationsBalanced: number;
    safetyViolationsTriggered: number;
    lastActiveTimestamp: string;
  }
  ```

#### Endpoint 10: `DELETE /api/v1/users/me`
* **Authentication:** `ROLE_USER` / `ROLE_ADMIN`
* **Success Status:** `200 OK` — Self-delete user account.

#### Endpoint 11: `GET /api/v1/users/{id}`
* **Authentication:** `ROLE_USER` / `ROLE_ADMIN`
* **Success Status:** `200 OK` — Public profile lookup.

#### Endpoint 12: `PUT /api/v1/users/avatar`
* **Authentication:** `ROLE_USER` / `ROLE_ADMIN`
* **Request Contract:** `{ "avatarUrl": "https://..." }`
* **Success Status:** `200 OK`

#### Endpoint 13: `DELETE /api/v1/users/avatar`
* **Authentication:** `ROLE_USER` / `ROLE_ADMIN`
* **Success Status:** `200 OK` — Clears avatar URL.

#### Endpoint 14: `GET /api/v1/admin/users`
* **Authentication:** `ROLE_ADMIN`
* **Success Status:** `200 OK` — Returns `Array<AdminUserResponse>`.

#### Endpoint 15: `GET /api/v1/admin/users/{id}`
* **Authentication:** `ROLE_ADMIN`
* **Success Status:** `200 OK` — Admin detailed user view.

#### Endpoint 16: `PUT /api/v1/admin/users/{id}`
* **Authentication:** `ROLE_ADMIN`
* **Request Contract:** `{ "role": "ROLE_ADMIN", "active": true }`
* **Success Status:** `200 OK`

#### Endpoint 17: `DELETE /api/v1/admin/users/{id}`
* **Authentication:** `ROLE_ADMIN`
* **Success Status:** `200 OK` — Delete user by ID.

---

### 4.2 Chemical Formula Engine (Endpoint 18)

#### Endpoint 18: `POST /api/v1/chemistry/formulas/parse`
* **Authentication:** `ROLE_USER`
* **Business Purpose:** Parse raw chemical formula string into element atom counts, molar mass, ionic charge, and hydrate multiplier.
* **Request Contract:**
  ```typescript
  interface ParseFormulaRequest {
    formula: string; // Required, e.g. "CuSO4·5H2O", "Fe3+", "(NH4)2SO4"
  }
  ```
* **Request Example:** `{ "formula": "CuSO4·5H2O" }`
* **Success Status:** `200 OK`
* **Response Contract:**
  ```typescript
  interface ChemicalFormula {
    rawFormula: string;
    cleanFormula: string;
    molarMass: number;                     // 249.685 g/mol
    charge: number;                        // 0
    isHydrated: boolean;                   // true
    hydrateCount: number;                  // 5
    elementCounts: Record<string, number>; // {"Cu": 1, "S": 1, "O": 9, "H": 10}
  }
  ```
* **Response Example:**
  ```json
  { "rawFormula": "CuSO4·5H2O", "cleanFormula": "CuSO4·5H2O", "molarMass": 249.685, "charge": 0, "isHydrated": true, "hydrateCount": 5, "elementCounts": { "Cu": 1, "S": 1, "O": 9, "H": 10 } }
  ```
* **Errors:** `INVALID_FORMULA` (400), `UNBALANCED_PARENTHESES` (400).
* **Frontend Next Action:** Display molar mass and element breakdown pie chart.

---

### 4.3 Chemical Equation Engine (Endpoint 19)

#### Endpoint 19: `POST /api/v1/chemistry/equations/balance`
* **Authentication:** `ROLE_USER`
* **Business Purpose:** Automatically balance chemical equation while conserving atom counts and electrical charge.
* **Request Contract:** `{ "equation": "KMnO4 + HCl -> KCl + MnCl2 + H2O + Cl2" }`
* **Success Status:** `200 OK`
* **Response Contract:**
  ```typescript
  interface BalancedEquation {
    originalEquation: string;
    balancedEquation: string;
    isBalanced: boolean;
    reactants: Array<{ formula: string; coefficient: number }>;
    products: Array<{ formula: string; coefficient: number }>;
  }
  ```
* **Response Example:**
  ```json
  {
    "originalEquation": "KMnO4 + HCl -> KCl + MnCl2 + H2O + Cl2",
    "balancedEquation": "2KMnO4 + 16HCl -> 2KCl + 2MnCl2 + 8H2O + 5Cl2",
    "isBalanced": true,
    "reactants": [{ "formula": "KMnO4", "coefficient": 2 }, { "formula": "HCl", "coefficient": 16 }],
    "products": [{ "formula": "KCl", "coefficient": 2 }, { "formula": "MnCl2", "coefficient": 2 }, { "formula": "H2O", "coefficient": 8 }, { "formula": "Cl2", "coefficient": 5 }]
  }
  ```
* **Errors:** `UNBALANCED_EQUATION` (400).

---

### 4.4 Element Catalogue & Periodic Table (Endpoints 20–22)

#### Endpoint 20: `GET /api/v1/chemistry/elements`
* **Authentication:** `ROLE_USER`
* **Business Purpose:** Retrieve summary listing of all 118 periodic table elements (ordered by atomic number $1 \rightarrow 118$).
* **Success Status:** `200 OK` (Returns `[]` if empty)
* **Response Contract:** `Array<ElementSummary>`:
  ```typescript
  interface ElementSummary {
    atomicNumber: number; // 1 - 118
    symbol: string;       // e.g. "H", "Fe"
    name: string;         // e.g. "Hydrogen", "Iron"
    atomicMass: number;   // IUPAC weight
  }
  ```

#### Endpoint 21: `GET /api/v1/chemistry/elements/{identifier}`
* **Authentication:** `ROLE_USER`
* **Path Parameter:** Atomic number (`26`) or symbol (`Fe`).
* **Success Status:** `200 OK`
* **Response Contract:** `ElementDetails` (Includes group, period, block, category, electronegativity, electronConfiguration).
* **Errors:** `ELEMENT_NOT_FOUND` (404).

#### Endpoint 22: `GET /api/v1/chemistry/elements/{identifier}/properties`
* **Authentication:** `ROLE_USER`
* **Success Status:** `200 OK`
* **Response Contract:** `ElementPropertyDetails` (Includes ionization energy, atomic radius, density, mp, bp, standardState).

---

### 4.5 Compound Catalogue & Physical Properties (Endpoints 23–25)

#### Endpoint 23: `GET /api/v1/chemistry/compounds`
* **Authentication:** `ROLE_USER`
* **Query Options:** Optional `?name=water`, `?formula=H2O`, `?composition=H2O`.
* **Success Status:** `200 OK` (Returns `[]` if empty search results)
* **Response Contract:** `Array<CompoundSummary>`:
  ```typescript
  interface CompoundSummary {
    id: string;
    code: string;                // e.g. "COMP-H2O"
    name: string;                // e.g. "Water"
    iupacName: string;           // e.g. "oxidane"
    normalizedFormula: string;   // e.g. "H2O"
    compositionFormula: string;
    casNumber: string | null;    // e.g. "7732-18-5"
    molecularWeight: number;     // 18.015
  }
  ```

#### Endpoint 24: `GET /api/v1/chemistry/compounds/{identifier}`
* **Authentication:** `ROLE_USER`
* **Path Parameter:** UUID or compound code (`COMP-H2O`).
* **Success Status:** `200 OK`
* **Response Contract:** `CompoundDetails` (Includes pubchemCid, smiles, inChI, description).

#### Endpoint 25: `GET /api/v1/chemistry/compounds/{identifier}/properties`
* **Authentication:** `ROLE_USER`
* **Success Status:** `200 OK`
* **Response Contract:** `CompoundPhysicalPropertyDetails` (Includes density, mp, bp, enthalpy of formation, molar entropy, heat capacity).

---

### 4.6 Thermodynamics & Calorimetry Engine (Endpoints 26–31)

#### Endpoint 26: `GET /api/v1/chemistry/thermodynamics/reference/{compoundCode}`
* **Authentication:** `ROLE_USER`
* **Success Status:** `200 OK`
* **Response Contract:**
  ```typescript
  interface ThermodynamicProfileDetails {
    compoundCode: string;
    datasetVersion: string;
    records: Array<{
      propertyType: "STANDARD_ENTHALPY_OF_FORMATION" | "STANDARD_GIBBS_ENERGY_OF_FORMATION" | "STANDARD_MOLAR_ENTROPY" | "MOLAR_HEAT_CAPACITY";
      numericValue: number;
      unitSymbol: string;
      physicalState: "SOLID" | "LIQUID" | "GAS";
      temperatureKelvin: number;
      pressurePascal: number;
      evidenceStatus: string;
      citation: string;
    }>;
  }
  ```

#### Endpoint 27: `POST /api/v1/chemistry/thermodynamics/calculate`
* **Authentication:** `ROLE_USER`
* **Request Contract:**
  ```typescript
  interface ReactionThermodynamicsRequest {
    reactionCode: string; // e.g. "RXN-WATER-SYNTHESIS"
    conditions?: {
      temperature: { value: number; unit: "KELVIN" | "CELSIUS" };
      pressure: { value: number; unit: "PASCAL" | "BAR" | "ATMOSPHERE" };
      physicalState: "SOLID" | "LIQUID" | "GAS";
      standardStateConvention: string;
    };
    stateOverrides?: Record<string, "SOLID" | "LIQUID" | "GAS">;
  }
  ```
* **Success Status:** `200 OK`
* **Response Contract:**
  ```typescript
  interface ReactionThermodynamicsResult {
    deltaHEnthalpyKj: number;
    deltaSEntropyJPerK: number;
    deltaGGibbsEnergyKj: number;
    equilibriumConstantKeq: number;
    isSpontaneous: boolean;
    reactionType: "EXOTHERMIC" | "ENDOTHERMIC";
  }
  ```

#### Endpoint 28: `POST /api/v1/chemistry/thermodynamics/hess-law`
* **Authentication:** `ROLE_USER`
* **Request Contract:**
  ```typescript
  interface HessLawRequest {
    targetReactionCode: string;
    elementaryStepReactions: Array<{
      reactionCode: string;
      multiplier: number; // e.g. 1.0, -2.0 for reversed step
    }>;
  }
  ```
* **Success Status:** `200 OK`
* **Response Contract:**
  ```typescript
  interface HessLawResult {
    targetReactionCode: string;
    netEnthalpyOfReactionKj: number;
    stepContributions: Array<{
      reactionCode: string;
      multiplier: number;
      individualEnthalpyKj: number;
      scaledContributionKj: number;
    }>;
  }
  ```

#### Endpoint 29: `POST /api/v1/chemistry/thermodynamics/calorimetry/sensible-heat`
* **Authentication:** `ROLE_USER`
* **Request Contract:**
  ```typescript
  interface SensibleHeatRequest {
    massGrams: number;
    specificHeatCapacityJPerGK: number;
    initialTemperatureKelvin: number;
    finalTemperatureKelvin: number;
  }
  ```
* **Success Status:** `200 OK`
* **Response Contract:**
  ```typescript
  interface SensibleHeatResult {
    heatTransferJoules: number;
    heatTransferKilojoules: number;
    temperatureDeltaKelvin: number;
    direction: "HEATING_ABSORBED" | "COOLING_RELEASED";
  }
  ```

#### Endpoint 30: `POST /api/v1/chemistry/thermodynamics/calorimetry/thermal-mixing`
* **Authentication:** `ROLE_USER`
* **Request Contract:**
  ```typescript
  interface ThermalMixingRequest {
    fluids: Array<{
      fluidName: string;
      massGrams: number;
      specificHeatCapacityJPerGK: number;
      initialTemperatureKelvin: number;
    }>;
  }
  ```
* **Success Status:** `200 OK`
* **Response Contract:**
  ```typescript
  interface ThermalMixingResult {
    finalEquilibriumTemperatureKelvin: number;
    finalEquilibriumTemperatureCelsius: number;
    totalHeatExchangedJoules: number;
  }
  ```

#### Endpoint 31: `POST /api/v1/chemistry/thermodynamics/calorimetry/reaction-heat`
* **Authentication:** `ROLE_USER`
* **Request Contract:**
  ```typescript
  interface ReactionCalorimetryRequest {
    reactionCode: string;
    limitingReactantMoles: number;
    calorimeterMassGrams: number;
    calorimeterHeatCapacityJPerGK: number;
    initialTemperatureKelvin: number;
  }
  ```
* **Success Status:** `200 OK`
* **Response Contract:**
  ```typescript
  interface ReactionCalorimetryResult {
    totalHeatReleasedJoules: number;
    enthalpyOfReactionKjPerMol: number;
    finalTemperatureKelvin: number;
    temperatureRiseKelvin: number;
  }
  ```

---

### 4.7 Acid-Base, Buffer & Titration Engine (Endpoints 32–42)

#### Endpoint 32: `POST /api/v1/chemistry/acid-base/water`
* **Authentication:** `ROLE_USER`
* **Request Contract:** Optional `{ "temperatureKelvin": 298.15 }`
* **Success Status:** `200 OK`
* **Response Contract:**
  ```typescript
  interface AcidBaseResponse {
    systemType: "PURE_WATER" | "STRONG_ACID" | "STRONG_BASE" | "WEAK_ACID" | "WEAK_BASE" | "CONJUGATE_ACID_SALT" | "CONJUGATE_BASE_SALT";
    ph: number;                      // 7.0000
    poh: number;                     // 7.0000
    hydroniumConcentration: number;  // 1.00e-7
    hydroxideConcentration: number;  // 1.00e-7
    kw: number;                      // 1.00e-14
    pKw: number;                     // 14.0000
    kActive: number | null;          // null
    calculationMethod: "EXACT" | "APPROXIMATE_QUADRATIC" | "HENDERSON_HASSELBALCH";
    solverStatus: "CONVERGED" | "UNCONVERGED";
  }
  ```

#### Endpoint 33: `POST /api/v1/chemistry/acid-base/strong-acid`
* **Request Contract:** `{ "speciesCode": "SPEC-HCL", "concentrationMolar": 0.1 }`
* **Success Status:** `200 OK` $\rightarrow$ `AcidBaseResponse` (pH = 1.0000).

#### Endpoint 34: `POST /api/v1/chemistry/acid-base/strong-base`
* **Request Contract:** `{ "speciesCode": "SPEC-NAOH", "concentrationMolar": 0.1 }`
* **Success Status:** `200 OK` $\rightarrow$ `AcidBaseResponse` (pH = 13.0000).

#### Endpoint 35: `POST /api/v1/chemistry/acid-base/weak-acid`
* **Request Contract:** `{ "speciesCode": "SPEC-CH3COOH", "concentrationMolar": 0.1 }`
* **Success Status:** `200 OK` $\rightarrow$ `AcidBaseResponse` (pH = 2.8790).

#### Endpoint 36: `POST /api/v1/chemistry/acid-base/weak-base`
* **Request Contract:** `{ "speciesCode": "SPEC-NH3", "concentrationMolar": 0.1 }`
* **Success Status:** `200 OK` $\rightarrow$ `AcidBaseResponse` (pH = 11.1240).

#### Endpoint 37: `POST /api/v1/chemistry/acid-base/salt-hydrolysis`
* **Request Contract:** `{ "speciesCode": "SPEC-NH4-PLUS", "concentrationMolar": 0.1 }`
* **Success Status:** `200 OK` $\rightarrow$ `AcidBaseResponse`.

#### Endpoint 38: `POST /api/v1/chemistry/acid-base/buffer`
* **Request Contract:**
  ```typescript
  interface BufferCalculationRequest {
    acidSpeciesCode: string;
    baseSpeciesCode: string;
    acidConcentrationMolar: number;
    baseConcentrationMolar: number;
    temperatureKelvin?: number;
  }
  ```
* **Success Status:** `200 OK`
* **Response Contract:**
  ```typescript
  interface BufferCalculationResult {
    ph: number;
    pKa: number;
    bufferCapacityBeta: number;
    ratioBaseToAcid: number;
  }
  ```

#### Endpoint 39: `POST /api/v1/chemistry/acid-base/buffer/preparation`
* **Request Contract:**
  ```typescript
  interface BufferPreparationRequest {
    targetPh: number;
    totalVolumeLiters: number;
    targetConcentrationMolar: number;
    acidSpeciesCode: string;
    baseSpeciesCode: string;
  }
  ```
* **Success Status:** `200 OK`
* **Response Contract:**
  ```typescript
  interface BufferPreparationResult {
    targetPh: number;
    acidMassRequiredGrams: number;
    baseMassRequiredGrams: number;
    requiredAcidMoles: number;
    requiredBaseMoles: number;
    recipeInstructions: string;
  }
  ```

#### Endpoint 40: `POST /api/v1/chemistry/acid-base/buffer/perturbation`
* **Request Contract:**
  ```typescript
  interface BufferPerturbationRequest {
    initialAcidMoles: number;
    initialBaseMoles: number;
    pKa: number;
    addedStrongSpeciesType: "STRONG_ACID" | "STRONG_BASE";
    addedMoles: number;
    volumeLiters: number;
  }
  ```
* **Success Status:** `200 OK`
* **Response Contract:**
  ```typescript
  interface BufferPerturbationResult {
    initialPh: number;
    finalPh: number;
    deltaPh: number;
    remainingAcidMoles: number;
    remainingBaseMoles: number;
    isBufferExhausted: boolean;
  }
  ```

#### Endpoint 41: `POST /api/v1/chemistry/acid-base/titration/characteristic-points`
* **Request Contract:**
  ```typescript
  interface TitrationRequest {
    analyteSpeciesCode: string;
    analyteVolumeMilliliters: number;
    analyteConcentrationMolar: number;
    titrantSpeciesCode: string;
    titrantConcentrationMolar: number;
  }
  ```
* **Success Status:** `200 OK`
* **Response Contract:**
  ```typescript
  interface TitrationCurveResult {
    equivalenceVolumeMilliliters: number;
    initialPh: number;
    halfEquivalencePh: number;
    equivalencePointPh: number;
    curvePoints: Array<{
      titrantVolumeMilliliters: number;
      ph: number;
      stage: "INITIAL" | "PRE_EQUIVALENCE" | "HALF_EQUIVALENCE" | "EQUIVALENCE" | "POST_EQUIVALENCE";
    }>;
  }
  ```

#### Endpoint 42: `POST /api/v1/chemistry/acid-base/polyprotic-titration/characteristic-points`
* **Request Contract:**
  ```typescript
  interface PolyproticTitrationRequest {
    polyproticAcidSpeciesCode: string;
    analyteVolumeMilliliters: number;
    analyteConcentrationMolar: number;
    strongBaseTitrantConcentrationMolar: number;
  }
  ```
* **Success Status:** `200 OK`
* **Response Contract:**
  ```typescript
  interface PolyproticTitrationCurveResult {
    numberOfEquivalencePoints: number;
    equivalenceVolumesMilliliters: Array<number>;
    equivalencePhs: Array<number>;
    halfEquivalencePhs: Array<number>;
    curvePoints: Array<{
      titrantVolumeMilliliters: number;
      ph: number;
    }>;
  }
  ```

---

### 4.8 Reaction Kinetics Engine (Endpoints 43–47)

#### Endpoint 43: `POST /api/v1/chemistry/kinetics/rate`
* **Request Contract:**
  ```typescript
  interface RateEvaluationRequest {
    rateConstant: number;
    reactantConcentrations: Record<string, number>;
    reactionOrders: Record<string, number>;
  }
  ```
* **Success Status:** `200 OK`
* **Response Contract:**
  ```typescript
  interface RateEvaluationResult {
    reactionRateMolPerLSec: number;
    overallOrder: number;
  }
  ```

#### Endpoint 44: `POST /api/v1/chemistry/kinetics/integrated-law`
* **Request Contract:**
  ```typescript
  interface IntegratedRateLawRequest {
    initialConcentrationMolar: number;
    rateConstant: number;
    reactionOrder: 0 | 1 | 2;
    timeSeconds: number;
  }
  ```
* **Success Status:** `200 OK`
* **Response Contract:**
  ```typescript
  interface IntegratedRateLawResult {
    concentrationAtTimeMolar: number;
    amountReactedMolar: number;
    fractionRemaining: number;
  }
  ```

#### Endpoint 45: `POST /api/v1/chemistry/kinetics/half-life`
* **Request Contract:** Same shape as `IntegratedRateLawRequest`.
* **Success Status:** `200 OK`
* **Response Contract:**
  ```typescript
  interface HalfLifeResult {
    halfLifeSeconds: number;
    reactionOrder: number;
  }
  ```

#### Endpoint 46: `POST /api/v1/chemistry/kinetics/arrhenius`
* **Request Contract:**
  ```typescript
  interface ArrheniusRequest {
    preExponentialFactorA: number;
    activationEnergyJPerMol: number;
    temperatureKelvin: number;
  }
  ```
* **Success Status:** `200 OK`
* **Response Contract:**
  ```typescript
  interface ArrheniusResult {
    rateConstantK: number;
    temperatureKelvin: number;
  }
  ```

#### Endpoint 47: `POST /api/v1/chemistry/kinetics/progress`
* **Request Contract:**
  ```typescript
  interface KineticProgressRequest {
    initialConcentrationMolar: number;
    rateConstant: number;
    reactionOrder: 0 | 1 | 2;
    totalTimeSeconds: number;
    timeStepSeconds: number;
  }
  ```
* **Success Status:** `200 OK`
* **Response Contract:**
  ```typescript
  interface KineticProgressResult {
    timePoints: Array<{
      timeSeconds: number;
      reactantConcentrationMolar: number;
      productConcentrationMolar: number;
    }>;
  }
  ```

---

### 4.9 Electrochemistry & Faraday Engine (Endpoints 48–50)

#### Endpoint 48: `POST /api/v1/chemistry/electrochemistry/standard-cell`
* **Request Contract:**
  ```typescript
  interface ElectrochemicalCellRequest {
    anodeHalfCellCode: string;   // e.g. "HALF-ZN-ZN2"
    cathodeHalfCellCode: string; // e.g. "HALF-CU-CU2"
  }
  ```
* **Success Status:** `200 OK`
* **Response Contract:**
  ```typescript
  interface ElectrochemicalCellResult {
    standardCellPotentialVolts: number; // e.g. 1.10 V
    anodeReaction: string;
    cathodeReaction: string;
    overallCellReaction: string;
    isSpontaneous: boolean;
  }
  ```

#### Endpoint 49: `POST /api/v1/chemistry/electrochemistry/nernst`
* **Request Contract:**
  ```typescript
  interface NernstRequest {
    standardCellPotentialVolts: number;
    numberOfElectronsTransferred: number;
    temperatureKelvin: number;
    reactionQuotientQ: number;
  }
  ```
* **Success Status:** `200 OK`
* **Response Contract:**
  ```typescript
  interface NernstResult {
    cellPotentialVolts: number;
    isSpontaneousUnderConditions: boolean;
  }
  ```

#### Endpoint 50: `POST /api/v1/chemistry/electrochemistry/electrolysis`
* **Request Contract:**
  ```typescript
  interface ElectrolysisRequest {
    currentAmperes: number;
    timeSeconds: number;
    molarMassGPerMol: number;
    electronsPerIon: number;
  }
  ```
* **Success Status:** `200 OK`
* **Response Contract:**
  ```typescript
  interface ElectrolysisResult {
    massDepositedGrams: number;
    chargeTransferredCoulombs: number;
    molesTransferred: number;
  }
  ```

---

### 4.10 Gas Laws & Phase Behavior Engine (Endpoints 51–53)

#### Endpoint 51: `POST /api/v1/chemistry/gas/state`
* **Request Contract:**
  ```typescript
  interface GasStateRequest {
    pressurePascals?: number;
    volumeCubicMeters?: number;
    moles?: number;
    temperatureKelvin?: number;
  }
  ```
* **Success Status:** `200 OK`
* **Response Contract:**
  ```typescript
  interface GasStateResult {
    pressurePascals: number;
    volumeCubicMeters: number;
    moles: number;
    temperatureKelvin: number;
  }
  ```

#### Endpoint 52: `POST /api/v1/chemistry/gas/mixture`
* **Request Contract:**
  ```typescript
  interface GasMixture {
    components: Array<{ gasName: string; moles: number }>;
    totalVolumeCubicMeters: number;
    temperatureKelvin: number;
  }
  ```
* **Success Status:** `200 OK`
* **Response Contract:**
  ```typescript
  interface GasMixtureResult {
    totalPressurePascals: number;
    componentPartialPressures: Record<string, number>;
    componentMoleFractions: Record<string, number>;
  }
  ```

#### Endpoint 53: `POST /api/v1/chemistry/gas/transformation`
* **Request Contract:**
  ```typescript
  interface GasStateTransformation {
    initialState: GasStateResult;
    targetPressurePascals?: number;
    targetVolumeCubicMeters?: number;
    targetTemperatureKelvin?: number;
  }
  ```
* **Success Status:** `200 OK`
* **Response Contract:** `GasStateResult`

---

### 4.11 Governed Laboratory Safety Engine (Endpoint 54)

#### Endpoint 54: `POST /api/v1/chemistry/safety/evaluate`
* **Authentication:** `ROLE_USER`
* **Request Contract:**
  ```typescript
  interface LaboratorySafetyEvaluationRequest {
    operationType: "MIX_REAGENTS" | "HEAT_COOL_SYSTEM" | "ADJUST_PRESSURE" | "TITRATE" | "EXECUTE_ELECTROLYSIS";
    reagents: Array<{ speciesCode: string; concentrationMolar: number }>;
    apparatusCode: string;
    hasFumeHood: boolean;
    targetTemperatureKelvin: number;
    targetPressurePascals: number;
  }
  ```
* **Success Status:** `200 OK`
* **Response Contract:**
  ```typescript
  interface LaboratorySafetyEvaluationResult {
    isSafe: boolean;
    highestSeverity: "ADVISORY" | "WARNING" | "CRITICAL_PROHIBITION";
    evaluations: Array<{
      ruleCode: string;
      ruleName: string;
      severity: "ADVISORY" | "WARNING" | "CRITICAL_PROHIBITION";
      passed: boolean;
      message: string;
    }>;
  }
  ```

---

### 4.12 Laboratory Experiment & Event Stream Engine (Endpoints 55–60)

#### Endpoint 55: `POST /api/v1/chemistry/experiments`
* **Authentication:** `ROLE_USER`
* **Success Status:** `201 Created`
* **Request Contract:**
  ```typescript
  interface CreateSimulationSessionRequest {
    processCode: string;    // e.g. "PROC-ACID-BASE-TITRATION"
    processVersion: number; // e.g. 1
    sessionId: { value: string };
    requestedAt: string;    // ISO timestamp
  }
  ```
* **Response Contract:** `SimulationState` (Initial status: `READY`, Version: 0)

#### Endpoint 56: `GET /api/v1/chemistry/experiments/{sessionId}`
* **Authentication:** `ROLE_USER`
* **Success Status:** `200 OK`
* **Response Contract:** `SimulationState`

#### Endpoint 57: `POST /api/v1/chemistry/experiments/{sessionId}/operations`
* **Authentication:** `ROLE_USER`
* **Success Status:** `200 OK`
* **Request Contract:**
  ```typescript
  interface SimulationOperationRequest {
    expectedStateVersion: number;
    idempotencyKey?: string;
    command: SimulationCommand;
  }
  ```
* **Response Contract:**
  ```typescript
  interface SimulationExecutionResult {
    sessionId: string;
    previousVersion: number;
    newVersion: number;
    safetyEvaluation: LaboratorySafetyEvaluationResult;
    updatedState: SimulationState;
    auditEventId: string;
  }
  ```

#### Endpoint 58: `POST /api/v1/chemistry/experiments/{sessionId}/events`
* **Authentication:** `ROLE_USER`
* **Success Status:** `200 OK`
* **Business Purpose:** Append Laboratory Event Stream API used by UI loggers to record discrete lab events (`SESSION_STARTED`, `MATERIAL_DISPENSED`, `MATERIAL_TRANSFERRED`, `STEP_STARTED`, `SESSION_PAUSED`, `SESSION_RESUMED`, `SESSION_COMPLETED`).
* **Request Contract:**
  ```typescript
  interface AppendEventRequest {
    expectedVersion: number;
    idempotencyKey: string;
    payload: {
      eventType: "SESSION_STARTED" | "MATERIAL_DISPENSED" | "MATERIAL_TRANSFERRED" | "STEP_STARTED" | "SESSION_PAUSED" | "SESSION_RESUMED" | "SESSION_COMPLETED";
      containerProfileId?: string;
      compoundCode?: string;
      quantity?: number;
      physicalState?: "SOLID" | "LIQUID" | "GAS";
      exclusiveEquipmentProfileIds?: Array<string>;
    };
  }
  ```
* **Response Contract:** `SimulationState`

#### Endpoint 59: `POST /api/v1/chemistry/experiments/{sessionId}/replay`
* **Authentication:** `ROLE_USER`
* **Success Status:** `200 OK` — Replays session events deterministically.
* **Response Contract:** `SimulationState`

#### Endpoint 60: `GET /api/v1/chemistry/experiments/{sessionId}/audit/{eventId}`
* **Authentication:** `ROLE_USER`
* **Success Status:** `200 OK`
* **Response Contract:**
  ```typescript
  interface SimulationCalculationAudit {
    auditEventId: string;
    sessionId: string;
    timestampUtc: string;
    initialStateHash: string;
    operationType: string;
    inputParameters: Record<string, any>;
    safetyEvaluations: LaboratorySafetyEvaluationResult;
    conservationLedger: {
      elementAtomsResidual: number;
      chargeResidual: number;
      isConserved: boolean;
    };
    finalStateHash: string;
  }
  ```

---

## 5. Operation Enum Mapping & Typed Command Models

### 5.1 Authoritative Operation Enum Mapping Table

| Safety Evaluation Category (`LaboratorySafetyEvaluationRequest.operationType`) | Simulation Engine Operation Type (`SimulationOperationType`) | Primary Model Selection | Description |
| :--- | :--- | :--- | :--- |
| `MIX_REAGENTS` | `BOOKKEEPING_MIX` | `bookkeeping` / `MIX-NO-CHEMISTRY` | Mixing reagents without instant chemical transformation |
| `MIX_REAGENTS` | `STOICHIOMETRIC_REACTION` | `stoichiometry` / `RXN-WATER-SYNTHESIS` | Mass-balanced chemical reaction step |
| `HEAT_COOL_SYSTEM` | `THERMAL_OPERATION` | `thermodynamics` / `THERMAL-HEAT-TRANSFER` | Applying or removing thermal energy ($\Delta T$) |
| `ADJUST_PRESSURE` | `GAS_STATE_CHANGE` | `gas-law` / `GAS-IDEAL-EXPANSION` | Compression or expansion of gas volume |
| `TITRATE` | `EQUILIBRIUM_REACTION` | `acid-base-equilibrium` / `TITRATION-NEUTRALIZATION` | Incremental titrant addition & pH equilibrium solve |
| `EXECUTE_ELECTROLYSIS` | `ELECTROLYSIS` | `electrochemistry` / `ELECTROLYSIS-WATER-SPLITTING` | Direct current electrolysis mass yield |

---

### 5.2 Typed Simulation Operation Command Schemas

```typescript
interface SimulationCommand {
  commandId: { value: string };
  stepId: string;
  targetVesselId: string;
  operation: ScientificOperationSpecification;
  inputs: Record<string, string>;
  materialDeltas: Array<MaterialStateDelta>;
}

interface MaterialStateDelta {
  vesselId: string;
  compoundCode: string;
  quantityDelta: number; // positive for addition, negative for consumption
  unit: "mol" | "mmol" | "g" | "kg";
  physicalState: "SOLID" | "LIQUID" | "GAS";
}
```

#### 1. `BOOKKEEPING_MIX` (`MIX_REAGENTS`)
```typescript
interface MixReagentsCommandPayload {
  operation: {
    operationType: "BOOKKEEPING_MIX";
    modelSelection: {
      method: "bookkeeping";
      reactionOrProfileId: "MIX-NO-CHEMISTRY";
      modelReference: { modelIdentifier: "bookkeeping-model"; version: "1.0.0" };
      datasetReferences: [{ datasetIdentifier: "bookkeeping-dataset"; version: "1.0.0" }];
      parameters: { phaseAssumption: "explicit" };
    };
  };
  inputs: { mixingNote: string };
  materialDeltas: Array<MaterialStateDelta>;
}
```

#### 2. `STOICHIOMETRIC_REACTION`
```typescript
interface StoichiometricReactionCommandPayload {
  operation: {
    operationType: "STOICHIOMETRIC_REACTION";
    modelSelection: {
      method: "stoichiometry";
      reactionOrProfileId: string; // e.g. "RXN-WATER-SYNTHESIS"
      modelReference: { modelIdentifier: "stoichiometry-model"; version: "1.0.0" };
      datasetReferences: [{ datasetIdentifier: "stoichiometry-dataset"; version: "1.0.0" }];
      parameters: { phaseAssumption: "explicit" };
    };
  };
  inputs: { extentMol: string }; // e.g. "1.0"
  materialDeltas: Array<MaterialStateDelta>;
}
```

#### 3. `THERMAL_OPERATION` (`HEAT_COOL_SYSTEM`)
```typescript
interface ThermalOperationCommandPayload {
  operation: {
    operationType: "THERMAL_OPERATION";
    modelSelection: {
      method: "thermodynamics";
      reactionOrProfileId: "THERMAL-HEAT-TRANSFER";
      modelReference: { modelIdentifier: "thermodynamics-model"; version: "1.0.0" };
      datasetReferences: [{ datasetIdentifier: "thermodynamics-dataset"; version: "1.0.0" }];
      parameters: { phaseAssumption: "explicit" };
    };
  };
  inputs: { heatAddedJoules: string; targetTemperatureKelvin: string };
  materialDeltas: [];
}
```

#### 4. `GAS_STATE_CHANGE` (`ADJUST_PRESSURE`)
```typescript
interface GasStateChangeCommandPayload {
  operation: {
    operationType: "GAS_STATE_CHANGE";
    modelSelection: {
      method: "gas-law";
      reactionOrProfileId: "GAS-IDEAL-EXPANSION";
      modelReference: { modelIdentifier: "gas-model"; version: "1.0.0" };
      datasetReferences: [{ datasetIdentifier: "gas-dataset"; version: "1.0.0" }];
      parameters: { phaseAssumption: "gas" };
    };
  };
  inputs: { targetPressurePascals: string };
  materialDeltas: [];
}
```

#### 5. `EQUILIBRIUM_REACTION` (`TITRATE`)
```typescript
interface EquilibriumReactionCommandPayload {
  operation: {
    operationType: "EQUILIBRIUM_REACTION";
    modelSelection: {
      method: "acid-base-equilibrium";
      reactionOrProfileId: "TITRATION-NEUTRALIZATION";
      modelReference: { modelIdentifier: "acid-base-model"; version: "1.0.0" };
      datasetReferences: [{ datasetIdentifier: "acid-base-dataset"; version: "1.0.0" }];
      parameters: { phaseAssumption: "aqueous" };
    };
  };
  inputs: { titrantSpeciesCode: string; addedVolumeMilliliters: string };
  materialDeltas: Array<MaterialStateDelta>;
}
```

#### 6. `ELECTROLYSIS`
```typescript
interface ElectrolysisCommandPayload {
  operation: {
    operationType: "ELECTROLYSIS";
    modelSelection: {
      method: "electrochemistry";
      reactionOrProfileId: "ELECTROLYSIS-WATER-SPLITTING";
      modelReference: { modelIdentifier: "electrochemistry-model"; version: "1.0.0" };
      datasetReferences: [{ datasetIdentifier: "electrochemistry-dataset"; version: "1.0.0" }];
      parameters: { phaseAssumption: "aqueous-gas" };
    };
  };
  inputs: { currentAmperes: string; durationSeconds: string };
  materialDeltas: Array<MaterialStateDelta>;
}
```

---

## 6. Experiment Lifecycle State Transition Matrix

| Current Status (`SimulationSessionStatus`) | Target API Endpoint / Action | Preconditions | Allowed? | Next Status | Version Change | Failure Behavior |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| N/A | `POST /experiments` | Valid `processCode` & `processVersion` | Yes | `READY` | Initialized to `0` | Returns 404 if process definition missing |
| `READY` | `POST /experiments/{id}/events` (`MATERIAL_DISPENSED`, `STEP_STARTED`) | Valid session & version `0` | Yes | `RUNNING` | Increments $0 \rightarrow 1$ | Returns 400 if container unsuitable |
| `READY` | `POST /experiments/{id}/operations` | Valid command & version `0` | Yes | `RUNNING` | Increments $0 \rightarrow 1$ | Auto-evaluates safety; rejects if unsafe |
| `RUNNING` | `POST /experiments/{id}/operations` | `expectedStateVersion` matches | Yes | `RUNNING` | Increments $v \rightarrow v+1$ | Rejects with 422 if safety fails; preserves state |
| `RUNNING` | Operation triggers Safety Rule (`CRITICAL`) | Unsafe parameters | Yes | `RUNNING` | Version preserved | HTTP 422 with `LaboratorySafetyEvaluationResult` |
| `RUNNING` | `POST /experiments/{id}/events` (`SESSION_PAUSED`) | Active running session | Yes | `PAUSED` | Increments $v \rightarrow v+1$ | Version mismatch returns 400 `STALE_STATE_VERSION` |
| `PAUSED` | `POST /experiments/{id}/events` (`SESSION_RESUMED`) | Session status is `PAUSED` | Yes | `RUNNING` | Increments $v \rightarrow v+1$ | Returns 400 if version stale |
| `RUNNING` | `POST /experiments/{id}/events` (`SESSION_COMPLETED`) | Final step reached | Yes | `COMPLETED` | Increments $v \rightarrow v+1$ | Freezes session state |
| `RUNNING` | Physical Solver Exception | Numeric divergence | Yes | `FAILED` | State marked `FAILED` | Execution halts |
| `COMPLETED` | `POST /experiments/{id}/operations` | Session completed | No | `COMPLETED` | Version unchanged | Returns 400: Session is read-only |
| `COMPLETED` | `POST /experiments/{id}/replay` | Session completed | Yes | `COMPLETED` | Version unchanged | Verifies event log SHA-256 hashes match |

---

## 7. Governed Safety Rule Catalogue & Provenance Metadata

| Rule Code | Rule Type | Severity | Condition & Trigger Threshold | Applicable Operations | Governed Source Identifier & Citation |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `SAFE-FUME-HOOD-REQ` | `FUME_HOOD_REQUIREMENT` | `CRITICAL` | `fumeHoodOperating == false` during volatile gas generation/heating. | `STOICHIOMETRIC_REACTION`, `EQUILIBRIUM_REACTION`, `KINETIC_PROGRESS` | `OSHA-1910-1450` — OSHA Laboratory Safety Standard (29 CFR 1910.1450) |
| `SAFE-TEMP-LIMIT-GLASS` | `CONTAINER_TEMPERATURE_LIMIT` | `CRITICAL` | `temperatureK > 773.15 K` ($500^\circ\text{C}$) for glass apparatus. | `THERMAL_OPERATION`, `STOICHIOMETRIC_REACTION`, `EQUILIBRIUM_REACTION` | `ASTM-E438-92` — ASTM E438-92 Standard Specification for Glasses in Laboratory Apparatus |
| `SAFE-PRESSURE-LIMIT-CONTAINER` | `CONTAINER_PRESSURE_LIMIT` | `CRITICAL` | `pressureKPa > 200.0 kPa` ($2.0\text{ bar}$) for sealed vessel. | `GAS_STATE_CHANGE`, `THERMAL_OPERATION` | `ISO-11114-1` — ISO Gas Cylinders and Vessels Compatibility and Limits |

---

## 8. Business Error Code Matrix

| Subsystem | Error Code | HTTP Status | Root Cause | Frontend Recommended Action |
| :--- | :--- | :--- | :--- | :--- |
| **Auth** | `INVALID_CREDENTIALS` | 401 | Incorrect username/email or password | Prompt user to check login credentials |
| **Auth** | `TOKEN_EXPIRED` | 401 | Access token expired ($> 15\text{ min}$) | Call `POST /api/v1/auth/refresh` |
| **Auth** | `REFRESH_TOKEN_INVALID` | 401 | Refresh token revoked or expired | Redirect to Login page |
| **User** | `USERNAME_ALREADY_EXISTS` | 409 | Requested username taken | Highlight username field with inline validation |
| **Formulas** | `INVALID_FORMULA` | 400 | Formula syntax invalid (e.g. `CuSO4??`) | Render parse error message at invalid token position |
| **Equations**| `UNBALANCED_EQUATION` | 400 | Equation cannot be balanced | Display species violating mass/charge conservation |
| **Catalogue**| `ELEMENT_NOT_FOUND` | 404 | Invalid atomic number or symbol | Show "Element not found" toast |
| **Catalogue**| `COMPOUND_NOT_FOUND` | 404 | Invalid compound code or UUID | Show "Compound not found" search result |
| **Safety** | `SAFETY_VIOLATION` | 422 | Safety rule triggered (`CRITICAL`) | Render Safety Violation Modal with rule details |
| **Engine** | `STALE_STATE_VERSION` | 400 | `expectedStateVersion` does not match server version | Re-fetch state via `GET /experiments/{id}` and retry |
| **Engine** | `EQUIPMENT_UNSUITABLE` | 400 | Equipment calibration expired or rating exceeded | Select suitable calibrated equipment profile |
| **Engine** | `CONTAINER_UNSUITABLE` | 400 | Dispensed volume exceeds vessel volume | Select larger capacity container profile |

---

## 9. Final Release Verdict

> [!IMPORTANT]
> **FINAL RELEASE VERDICT: PASS — The v3.2 frontend handoff preserves the full detailed contract while incorporating all verified reconciliation corrections; no integration detail was lost during cleanup.**
