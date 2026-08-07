# Phase 2 Implementation Report — Chemical Formula Parser and Equation Balancer

This report documents the implementation of the chemical formula parser and linear-algebra-based equation balancer in the `chemistry-engine` library.

---

## 1. Summary

### What was implemented:
- **Chemical Formula Parser**:
  - Implemented `ElementSymbol` with syntax checking and an internal registry containing the 118 IUPAC elements.
  - Implemented `FormulaNormalizer` to translate Unicode subscripts and superscript charges to canonical ASCII representations.
  - Implemented a stack-based parser in `DefaultFormulaParser` to parse nested parentheses, square brackets, subscripts, charge sign/magnitudes, and hydrate coefficients.
  - Handled charge-subscript ambiguities (e.g. `NH4+` vs `Fe3+`) using structural analysis.
- **Equation Balancer**:
  - Implemented `RationalNumber` exact rational arithmetic to eliminate floating-point drift.
  - Implemented `EquationParser` to extract equation terms and filter addition `+` signs from charge indicators.
  - Implemented `DefaultEquationBalancer` utilizing Gaussian elimination to compute RREF, resolve null-spaces, and scale variables to minimal positive whole-number integers.
  - Verified exact atom and charge conservation on both sides of the balanced equation.
  - Checked whether the user-supplied input was already balanced.
- Exposed interfaces in `com.ailab.chemistry.api`:
  - `ChemicalFormulaService.java`
  - `ChemicalEquationService.java`
- Added 17 unit test cases covering all parser grammar, normalization, charge parsing, invalid syntax rejections, complexity bounds, rational arithmetic, balancing ratios, charge-conserving ionic equations, and property determinism.
- Extended the Spring Boot context integration test to verify the injection and execution of both API services.

### What was excluded:
- Periodic-table persistence or compound databases.
- Stoichiometric mass calculations, pH, thermodynamics, kinetics, or simulations.
- REST controllers, gRPC protocols, or database changes.

---

## 2. Design Decisions

1. **Stack-Based Parser**: A stack matches the parent-child relationship of nested brackets `(OH)2` and `[Fe(CN)6]`. This allows multiplying grouped compositions cleanly upon pop operations.
2. **Comparable Element Symbols**: Implementing `Comparable<ElementSymbol>` allows sorting chemical elements alphabetically inside `TreeMap` collections, which guarantees deterministic iteration and stable outputs.
3. **Exact Rational Echelon Form**: Floating-point rounding introduces errors that corrupt linear algebra calculations. Computing null spaces via custom `RationalNumber` arithmetic ensures exact integer solutions.
4. **Ambiguity Resolution**: Without carets, `NH4+` and `Fe3+` look syntactically similar. Checking whether the prefix of the digit is a single IUPAC element symbol differentiates monoatomic charges from polyatomic subscripts.
5. **Grammar-Aware Equation Tokenizer**: Whitespace-dependent splits are replaced with a character-by-character scan inside `EquationParser.parseSide` using lookahead and lookbehind to isolate addition `+` separators from charge signs (`+`/`-`). Both spaced and compact equation forms are parsed deterministically.

---

## 3. Files Created/Modified

### Created Files:
- [`FormulaErrorCode.java`](file:///c:/Users/User/Documents/ailab/Backend/chemistry-engine/src/main/java/com/ailab/chemistry/domain/formula/exception/FormulaErrorCode.java)
- [`InvalidChemicalFormulaException.java`](file:///c:/Users/User/Documents/ailab/Backend/chemistry-engine/src/main/java/com/ailab/chemistry/domain/formula/exception/InvalidChemicalFormulaException.java)
- [`UnknownElementSymbolException.java`](file:///c:/Users/User/Documents/ailab/Backend/chemistry-engine/src/main/java/com/ailab/chemistry/domain/formula/exception/UnknownElementSymbolException.java)
- [`FormulaSyntaxException.java`](file:///c:/Users/User/Documents/ailab/Backend/chemistry-engine/src/main/java/com/ailab/chemistry/domain/formula/exception/FormulaSyntaxException.java)
- [`FormulaComplexityException.java`](file:///c:/Users/User/Documents/ailab/Backend/chemistry-engine/src/main/java/com/ailab/chemistry/domain/formula/exception/FormulaComplexityException.java)
- [`ChemicalFormula.java`](file:///c:/Users/User/Documents/ailab/Backend/chemistry-engine/src/main/java/com/ailab/chemistry/domain/formula/ChemicalFormula.java)
- [`ElementSymbol.java`](file:///c:/Users/User/Documents/ailab/Backend/chemistry-engine/src/main/java/com/ailab/chemistry/domain/formula/ElementSymbol.java)
- [`ElementCount.java`](file:///c:/Users/User/Documents/ailab/Backend/chemistry-engine/src/main/java/com/ailab/chemistry/domain/formula/ElementCount.java)
- [`FormulaComposition.java`](file:///c:/Users/User/Documents/ailab/Backend/chemistry-engine/src/main/java/com/ailab/chemistry/domain/formula/FormulaComposition.java)
- [`FormulaNormalizer.java`](file:///c:/Users/User/Documents/ailab/Backend/chemistry-engine/src/main/java/com/ailab/chemistry/domain/formula/FormulaNormalizer.java)
- [`FormulaParser.java`](file:///c:/Users/User/Documents/ailab/Backend/chemistry-engine/src/main/java/com/ailab/chemistry/domain/formula/FormulaParser.java)
- [`DefaultFormulaParser.java`](file:///c:/Users/User/Documents/ailab/Backend/chemistry-engine/src/main/java/com/ailab/chemistry/domain/formula/DefaultFormulaParser.java)
- [`EquationErrorCode.java`](file:///c:/Users/User/Documents/ailab/Backend/chemistry-engine/src/main/java/com/ailab/chemistry/domain/equation/exception/EquationErrorCode.java)
- [`InvalidChemicalEquationException.java`](file:///c:/Users/User/Documents/ailab/Backend/chemistry-engine/src/main/java/com/ailab/chemistry/domain/equation/exception/InvalidChemicalEquationException.java)
- [`RationalNumber.java`](file:///c:/Users/User/Documents/ailab/Backend/chemistry-engine/src/main/java/com/ailab/chemistry/domain/equation/RationalNumber.java)
- [`EquationSide.java`](file:///c:/Users/User/Documents/ailab/Backend/chemistry-engine/src/main/java/com/ailab/chemistry/domain/equation/EquationSide.java)
- [`EquationTerm.java`](file:///c:/Users/User/Documents/ailab/Backend/chemistry-engine/src/main/java/com/ailab/chemistry/domain/equation/EquationTerm.java)
- [`ChemicalEquation.java`](file:///c:/Users/User/Documents/ailab/Backend/chemistry-engine/src/main/java/com/ailab/chemistry/domain/equation/ChemicalEquation.java)
- [`BalancedEquation.java`](file:///c:/Users/User/Documents/ailab/Backend/chemistry-engine/src/main/java/com/ailab/chemistry/domain/equation/BalancedEquation.java)
- [`EquationParser.java`](file:///c:/Users/User/Documents/ailab/Backend/chemistry-engine/src/main/java/com/ailab/chemistry/domain/equation/EquationParser.java)
- [`EquationBalancer.java`](file:///c:/Users/User/Documents/ailab/Backend/chemistry-engine/src/main/java/com/ailab/chemistry/domain/equation/EquationBalancer.java)
- [`DefaultEquationBalancer.java`](file:///c:/Users/User/Documents/ailab/Backend/chemistry-engine/src/main/java/com/ailab/chemistry/domain/equation/DefaultEquationBalancer.java)
- [`ChemicalFormulaService.java`](file:///c:/Users/User/Documents/ailab/Backend/chemistry-engine/src/main/java/com/ailab/chemistry/api/ChemicalFormulaService.java)
- [`ChemicalEquationService.java`](file:///c:/Users/User/Documents/ailab/Backend/chemistry-engine/src/main/java/com/ailab/chemistry/api/ChemicalEquationService.java)
- [`ChemicalFormulaServiceImpl.java`](file:///c:/Users/User/Documents/ailab/Backend/chemistry-engine/src/main/java/com/ailab/chemistry/service/ChemicalFormulaServiceImpl.java)
- [`ChemicalEquationServiceImpl.java`](file:///c:/Users/User/Documents/ailab/Backend/chemistry-engine/src/main/java/com/ailab/chemistry/service/ChemicalEquationServiceImpl.java)
- [`FormulaParserTests.java`](file:///c:/Users/User/Documents/ailab/Backend/chemistry-engine/src/test/java/com/ailab/chemistry/domain/formula/FormulaParserTests.java)
- [`EquationBalancerTests.java`](file:///c:/Users/User/Documents/ailab/Backend/chemistry-engine/src/test/java/com/ailab/chemistry/domain/equation/EquationBalancerTests.java)
- [`docs/chemistry/FORMULA_AND_EQUATION_ENGINE.md`](file:///c:/Users/User/Documents/ailab/docs/chemistry/FORMULA_AND_EQUATION_ENGINE.md)

### Modified Files:
- [`AiMonolithApplicationTests.java`](file:///c:/Users/User/Documents/ailab/Backend/app/src/test/java/com/ailab/AiMonolithApplicationTests.java)

---

## 4. Verification Evidence

### 1. Verification command
```powershell
$env:JAVA_HOME="C:\Users\User\.jdks\ms-21.0.11"
.\mvnw.cmd clean verify
```

### 2. Reactor result
```text
[INFO] Reactor Summary for ai-laboratory-backend 0.0.1-SNAPSHOT:
[INFO] 
[INFO] ai-laboratory-backend .............................. SUCCESS [  0.117 s]
[INFO] identity-module .................................... SUCCESS [ 10.894 s]
[INFO] chemistry-engine ................................... SUCCESS [ 15.513 s]
[INFO] app ................................................ SUCCESS [ 15.396 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
```

### 3. Module test execution status
- **`identity-module`**:
  - Executed tests: **35**, Failures: 0, Errors: 0, Skipped: 0
- **`chemistry-engine`**:
  - Executed tests: **37** (increase of 18 tests from baseline), Failures: 0, Errors: 0, Skipped: 0
  - Details:
    - `ArchitectureTests`: 2 tests PASS
    - `ChemistryEngineApplicationTests`: 1 test PASS
    - `ChemistryEngineInterfaceTests`: 1 test PASS
    - `EquationBalancerTests`: 9 tests PASS (rational arithmetic, basic/compact neutral balancing, compact/spaced ionic equations, underdetermined and impossible equations validation)
    - `FormulaParserTests`: 9 tests PASS (basic, nested groups, hydrates, Unicode subscript and superscript charges, free electrons, bounds, syntax checks, molecular ambiguities)
    - `MeasurementTests`: 15 tests PASS
- **`app`**:
  - Executed tests: **1** (monolith load test checks that `ChemicalFormulaService` and `ChemicalEquationService` inject and work correctly)
  - Separately skipped Flyway/Testcontainers tests: **1** (`FlywayMigrationTests` skipped due to no local Docker daemon)
- **Total Executed Tests**: **73**
- **Total Skipped Tests**: **1**

### 4. Architecture-boundary result
- **PASSED**: ArchUnit checks verify that `com.ailab.chemistry.domain..` has no dependency on Spring, JPA, Jackson, Auth, or User.

### 5. Representative parser evidence
- Input `"K4[Fe(CN)6]"` $\rightarrow$ parsed composition `K:4, Fe:1, C:6, N:6`.
- Input `"CuSO4·5H2O"` $\rightarrow$ parsed composition `Cu:1, S:1, O:9, H:10`.
- Input `"SO₄²⁻"` $\rightarrow$ parsed composition `S:1, O:4`, charge `-2`.
- Input `"NH4+"` $\rightarrow$ parsed composition `N:1, H:4`, charge `1`.

### 6. Representative balanced equations
- `H2 + O2 -> H2O` $\rightarrow$ `2H2 + O2 -> 2H2O`
- `KMnO4 + HCl -> KCl + MnCl2 + H2O + Cl2` $\rightarrow$ `2KMnO4 + 16HCl -> 2KCl + 2MnCl2 + 8H2O + 5Cl2`
- `Fe2+ -> Fe3+ + e-` $\rightarrow$ `Fe2+ -> Fe3+ + e-`

---

## 5. Known Limitations
- Underdetermined systems with multiple independent products (such as mixed carbon combustion products) throw `MULTIPLE_INDEPENDENT_SOLUTIONS`.
- Electron transfer balancing requires the explicit use of `e-` terms on either side.

---

## 7. Parser Stabilization (Phase 2.1)

A hardening step was executed in Phase 2.1 to resolve two issues:
1. **Ambiguous Molecular Ions**: Ambiguous trailing digit-charge shorthand for non-metals (e.g. `O2+`, `N2+`, `Cl2+`, `H2+`) is now explicitly rejected with `AMBIGUOUS_CHARGE_NOTATION` instead of being silently parsed as a monatomic charge (e.g. `O2+` as oxygen with charge +2). Caret notation (e.g. `O2^+`) is required for molecular ions. Legacy monatomic shorthand (e.g., `Ca2+`, `Fe3+`) remains fully supported for metals.
2. **Grammar-Aware Equation Tokenizer**: Whitespace-dependent term splitting has been replaced with a character-by-character scan. This allows balancing compact equations (e.g., `H2+O2->H2O`) and compact ionic equations (e.g. `Ag++Cl-->AgCl`) while raising `AMBIGUOUS_EQUATION_TOKENIZATION` on malformed separators or charge-only terms.

---

## 8. Release-Gate Decision

```text
PASS — Phase 2 complete; Phase 3 may begin.
```
