# Chemical Formula Parser and Equation Balancer Documentation

This document describes the design, capabilities, syntax, and algorithms of the chemical formula parser and equation balancer implemented in the `chemistry-engine` library.

---

## 1. Chemical Formula Parser

The parser transforms a chemical formula string into a validated, structured composition model (`ChemicalFormula`).

### Supported Grammar & Syntax

1. **Element Symbols**:
   - First letter must be uppercase, optional second letter must be lowercase (e.g. `H`, `O`, `Na`, `Cl`, `Fe`, `Al`).
   - Validated against the 118 known elements in the IUPAC periodic table. Unknown elements (such as `Xx`) are explicitly rejected.
2. **Implicit Counts**:
   - Elements or grouped terms with no subscript default to a count of 1 (e.g. `H2O` $\rightarrow$ `H`=2, `O`=1).
3. **Subscripts**:
   - Accepts positive integers of any scale (represented internally as `BigInteger`). Subscript `0` or negative values are strictly rejected.
   - Normalizes Unicode subscript digits (e.g. `₂` $\rightarrow$ `2`).
4. **Nested Groups**:
   - Supports brackets `(...)` and `[...]` to group elements and apply group multipliers (e.g. `Ca(OH)2` $\rightarrow$ `O`=2, `H`=2; `K4[Fe(CN)6]` $\rightarrow$ `C`=6, `N`=6).
   - Empty brackets (e.g. `()`) or unmatched brackets are rejected.
5. **Hydrates**:
   - Separated by middle dot `·` (U+00B7) or period `.` (e.g. `CuSO4·5H2O`, `CuSO4.5H2O`).
   - Normalizes dot to middle dot internally.
   - Allows optional leading coefficients for hydrate segments (e.g. `5H2O`). If absent, defaults to 1.
6. **Ionic Charges**:
   - Standard ionic charges can be placed at the end of the formula.
   - Supported notations: `Na+`, `Cl-`, `Ca2+`, `SO4^2-`, `NH4+`, `Fe3+`.
   - Normalizes Unicode superscript charge symbols (e.g. `SO₄²⁻` $\rightarrow$ `SO4^2-`).
   - **Charge Ambiguity Resolution**:
     - Caret notation (e.g. `O2^+`, `SO4^2-`) explicitly separates molecular subscripts from charge magnitudes.
     - Legacy monatomic shorthand (e.g., `Ca2+`, `Fe3+`, `Al3+`) is supported for elements that are metals.
     - Ambiguous molecular-ion shorthand without carets (e.g. `O2+`, `N2+`, `Cl2+`, `H2+`) is strictly rejected with error code `AMBIGUOUS_CHARGE_NOTATION` to prevent silent misinterpretation. Callers must use explicit caret notation (e.g., `O2^+`) for these species.
7. **Free Electrons**:
   - Modeled explicitly using the symbol `e-` with empty composition and net charge `-1`.

### Complexity Limits
To protect against pathological input or denial-of-service, the parser enforces the following constraints:
- **Maximum Input Length**: 500 characters.
- **Maximum Nesting Depth**: 20 levels.
- **Maximum Multiplier size**: 1,000,000.
- **Maximum Expanded Atom Count**: 1,000,000,000,000.

---

## 2. Chemical Equation Balancer

The balancer accepts a chemical equation string, parses it into reactant and product terms, builds a conservation matrix, and computes the smallest positive whole-number coefficients using exact rational linear algebra.

### Supported Separators
- Separators: `->`, `→`, `=` (normalized internally to `->`).
- Sides: Reactant side (left) and Product side (right), separated by addition `+` tokens.

### Grammar-Aware Equation Tokenizer
Rather than performing a whitespace-dependent string split, the balancer tokenizes the equation side strings character-by-character.
- It distinguishes `+` when used as an equation term separator from `+` (or `-`) when used inside an ionic charge (e.g., `Fe2+`, `Ag+`, `Cl-`, `e-`).
- Compact neutral equations (without spaces, e.g. `H2+O2->H2O` or `Fe+O2->Fe2O3`) are parsed successfully and produce equivalent results to their spaced counterparts.
- Compact ionic equations with multiple signs (e.g., `Ag++Cl-->AgCl` or `H++OH-->H2O`) are parsed deterministically.
- Invalid tokenizations (e.g. duplicate separators `H2 ++ O2`, leading `+ H2`, or trailing `H2 +` separators) throw `AMBIGUOUS_EQUATION_TOKENIZATION` instead of mapping to generic balancing failures.

### Balancing Algorithm
1. **Matrix Construction**:
   - Gathers all unique elements and checks that they appear on both sides of the equation.
   - Builds a conservation matrix with columns corresponding to terms (reactants as positive, products as negative) and rows corresponding to elements.
   - Adds a charge conservation row if any term is charged or is an electron.
2. **Exact Rational Gauss-Jordan Elimination**:
   - Reduces the matrix to Row Echelon Form (RREF) using `RationalNumber` exact arithmetic (`BigInteger` numerator and denominator).
   - Identifies the null space.
3. **Least Common Multiple Scaling**:
   - Obtains a basis vector from the null space.
   - Discards systems with nullity > 1 (underdetermined) or nullity = 0 (unbalanceable).
   - Multiplies the rational solutions by the LCM of all denominators, then divides by the GCD of all numerators to obtain the minimal positive whole-number integer coefficients.
4. **Conservation Verification**:
   - Asserts exact conservation of all elements on reactant vs product sides.
   - Asserts exact conservation of charge on reactant vs product sides.

---

## 3. Error Codes Summary

| Error Code | Category | Description |
| :--- | :--- | :--- |
| `EMPTY_FORMULA` | Formula | Input formula is empty or blank. |
| `INVALID_ELEMENT_SYMBOL` | Formula | Invalid syntax (e.g. lowercase initial, three-letter symbols, digits). |
| `UNKNOWN_ELEMENT_SYMBOL` | Formula | Valid syntax but unknown element symbol (not in registry of 118 elements). |
| `INVALID_SUBSCRIPT` | Formula | Malformed subscript number. |
| `ZERO_ELEMENT_COUNT` | Formula | Subscript count is 0. |
| `UNMATCHED_GROUP` | Formula | Mismatched parentheses or square brackets. |
| `EMPTY_GROUP` | Formula | Empty brackets (e.g. `[]` or `()`). |
| `UNEXPECTED_TOKEN` | Formula | Unexpected character found. |
| `INVALID_HYDRATE` | Formula | Trailing dot, duplicate dots, or empty hydrate segments. |
| `INVALID_CHARGE` | Formula | Malformed charge (trailing caret, duplicate signs). |
| `FORMULA_TOO_COMPLEX` | Formula | Exceeded length, nesting depth, or multiplier limits. |
| `AMBIGUOUS_CHARGE_NOTATION` | Formula | Ambiguous molecular charge (e.g. `O2+`, `N2+`, `Cl2+`). Requires caret. |
| `EMPTY_EQUATION` | Equation | Input equation side is empty or contains empty terms. |
| `MISSING_REACTANT` | Equation | Reactant side is empty. |
| `MISSING_PRODUCT` | Equation | Product side is empty. |
| `INVALID_EQUATION_SEPARATOR`| Equation | Mismatched or multiple equation separators. |
| `UNBALANCEABLE_EQUATION` | Equation | No positive whole-number null-space solution exists. |
| `MULTIPLE_INDEPENDENT_SOLUTIONS` | Equation | System has multiple independent balancing options. |
| `CHARGE_NOT_CONSERVED` | Equation | Balanced checks failed to conserve elements/charges. |
| `AMBIGUOUS_EQUATION_TOKENIZATION` | Equation | Tokenizer syntax error (leading/trailing/duplicate `+`, charge-only tokens). |
