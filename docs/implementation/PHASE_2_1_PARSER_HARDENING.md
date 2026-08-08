# Phase 2.1 Implementation Report — Formula Charge and Equation Tokenization Hardening

This report documents the implementation of the parser stabilization and hardening step in Phase 2.1.

---

## 1. Baseline

Prior to Phase 2.1, the parser:
- Relied on whitespace-dependent splits for equations (meaning `H2+O2->H2O` failed, and spaces around `+` were strictly required).
- Interchanged subscripts and charge magnitudes using a simple monatomic fallback, causing ambiguous non-metal molecular ions like `O2+` to be silently parsed as a monatomic Oxygen ion with charge +2.

---

## 2. Summary

### What was implemented:
- **Explicit Charge Grammar**:
  - Enforced a rule separating monatomic legacy charge shorthand from ambiguous molecular ions.
  - Molecular ions without carets (e.g. `O2+`, `N2+`, `Cl2+`, `H2+`) are now explicitly rejected with error code `AMBIGUOUS_CHARGE_NOTATION`.
  - Caret notation (e.g. `O2^+`) is parsed successfully as $O_2^+$.
  - Legacy shorthand (e.g. `Ca2+`, `Fe3+`) remains supported for metal element symbols.
  - Duplicate signs (e.g. `Na++`) or trailing carets (`SO4^`) are validated and rejected as `INVALID_CHARGE`.
- **Grammar-Aware Tokenizer**:
  - Replaced the regex splitting logic with a character-by-character scan that extracts terms while skipping signs used as charges (`+` or `-` in `Fe2+`, `Cl-`, `e-`).
  - Supports compact neutral equations (e.g., `H2+O2->H2O`) and compact ionic equations (e.g., `Ag++Cl-->AgCl`).
  - Catches syntax errors (leading/trailing/duplicate separators, charge-only tokens) and raises `AMBIGUOUS_EQUATION_TOKENIZATION` instead of mapping them to balancing failures.
- **Verification and Regressions**:
  - Added new unit test cases covering all parser charge ambiguity checks, compact neutral/ionic equations, and syntax rejections.
  - Ensured all 35 existing Identity tests and all previous 15 Measurement tests remain passing.

---

## 3. Files Created/Modified

### Created Files:
- [`docs/implementation/PHASE_2_1_PARSER_HARDENING.md`](file:///c:/Users/User/Documents/ailab/docs/implementation/PHASE_2_1_PARSER_HARDENING.md)

### Modified Files:
- [`FormulaErrorCode.java`](file:///c:/Users/User/Documents/ailab/Backend/chemistry-engine/src/main/java/com/ailab/chemistry/domain/formula/exception/FormulaErrorCode.java)
- [`EquationErrorCode.java`](file:///c:/Users/User/Documents/ailab/Backend/chemistry-engine/src/main/java/com/ailab/chemistry/domain/equation/exception/EquationErrorCode.java)
- [`ElementSymbol.java`](file:///c:/Users/User/Documents/ailab/Backend/chemistry-engine/src/main/java/com/ailab/chemistry/domain/formula/ElementSymbol.java) (implemented `Comparable`)
- [`DefaultFormulaParser.java`](file:///c:/Users/User/Documents/ailab/Backend/chemistry-engine/src/main/java/com/ailab/chemistry/domain/formula/DefaultFormulaParser.java)
- [`EquationParser.java`](file:///c:/Users/User/Documents/ailab/Backend/chemistry-engine/src/main/java/com/ailab/chemistry/domain/equation/EquationParser.java)
- [`FormulaParserTests.java`](file:///c:/Users/User/Documents/ailab/Backend/chemistry-engine/src/test/java/com/ailab/chemistry/domain/formula/FormulaParserTests.java)
- [`EquationBalancerTests.java`](file:///c:/Users/User/Documents/ailab/Backend/chemistry-engine/src/test/java/com/ailab/chemistry/domain/equation/EquationBalancerTests.java)
- [`FORMULA_AND_EQUATION_ENGINE.md`](file:///c:/Users/User/Documents/ailab/docs/chemistry/FORMULA_AND_EQUATION_ENGINE.md)
- [`PHASE_2_FORMULA_PARSER_AND_BALANCER.md`](file:///c:/Users/User/Documents/ailab/docs/implementation/PHASE_2_FORMULA_PARSER_AND_BALANCER.md)

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
[INFO] ai-laboratory-backend .............................. SUCCESS [  0.141 s]
[INFO] identity-module .................................... SUCCESS [  7.353 s]
[INFO] chemistry-engine ................................... SUCCESS [  8.704 s]
[INFO] app ................................................ SUCCESS [  9.567 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
```

### 3. Module test execution status
- **`identity-module`**: 35 tests, 0 failures, 0 errors, 0 skipped.
- **`chemistry-engine`**: 37 tests (increase of 18 from baseline), 0 failures, 0 errors, 0 skipped.
  - Details:
    - `ArchitectureTests`: 2 tests PASS
    - `ChemistryEngineApplicationTests`: 1 test PASS
    - `ChemistryEngineInterfaceTests`: 1 test PASS
    - `EquationBalancerTests`: 9 tests PASS (covers compact forms, ionic charge splitting, underdetermined systems, and tokenizer syntax)
    - `FormulaParserTests`: 9 tests PASS (covers nested parsing, charge ambiguity checks, caret overrides, and invalid structure validation)
    - `MeasurementTests`: 15 tests PASS
- **`app`**: 1 test, 0 failures, 1 skipped (`FlywayMigrationTests` skipped due to lack of local Docker environment).

---

## 5. Known Limitations
- Underdetermined systems (e.g., combustion with multiple carbon products) throw `MULTIPLE_INDEPENDENT_SOLUTIONS`.
- Electron terms must use explicit `e-` representation.

---

## 6. Release-Gate Decision

```text
PASS — Phase 2.1 complete; Phase 3 may begin.
```
