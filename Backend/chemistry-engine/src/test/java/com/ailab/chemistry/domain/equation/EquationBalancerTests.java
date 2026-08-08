package com.ailab.chemistry.domain.equation;

import org.junit.jupiter.api.Test;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import com.ailab.chemistry.domain.equation.exception.*;
import com.ailab.chemistry.domain.formula.DefaultFormulaParser;
import com.ailab.chemistry.domain.formula.FormulaParser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EquationBalancerTests {
    private final FormulaParser formulaParser = new DefaultFormulaParser();
    private final EquationParser parser = new EquationParser(formulaParser);
    private final EquationBalancer balancer = new DefaultEquationBalancer();

    @Test
    void testRationalNumberArithmetic() {
        RationalNumber half = RationalNumber.of(1, 2);
        RationalNumber third = RationalNumber.of(1, 3);

        // Reduction
        RationalNumber reduced = RationalNumber.of(2, 4);
        assertThat(reduced).isEqualTo(half);

        // Operations
        assertThat(half.add(third)).isEqualTo(RationalNumber.of(5, 6));
        assertThat(half.subtract(third)).isEqualTo(RationalNumber.of(1, 6));
        assertThat(half.multiply(third)).isEqualTo(RationalNumber.of(1, 6));
        assertThat(half.divide(third)).isEqualTo(RationalNumber.of(3, 2));

        // Division by zero
        assertThatThrownBy(() -> half.divide(RationalNumber.ZERO))
                .isInstanceOf(ArithmeticException.class);
    }

    @Test
    void testSuccessfulEquationBalancing() {
        // H2 + O2 -> H2O
        verifyBalanced("H2 + O2 -> H2O", "2H2 + O2 -> 2H2O", new int[]{2, 1}, new int[]{2});

        // Fe + O2 -> Fe2O3
        verifyBalanced("Fe + O2 -> Fe2O3", "4Fe + 3O2 -> 2Fe2O3", new int[]{4, 3}, new int[]{2});

        // Al + HCl -> AlCl3 + H2
        verifyBalanced("Al + HCl -> AlCl3 + H2", "2Al + 6HCl -> 2AlCl3 + 3H2", new int[]{2, 6}, new int[]{2, 3});

        // C2H6 + O2 -> CO2 + H2O
        verifyBalanced("C2H6 + O2 -> CO2 + H2O", "2C2H6 + 7O2 -> 4CO2 + 6H2O", new int[]{2, 7}, new int[]{4, 6});

        // KMnO4 + HCl -> KCl + MnCl2 + H2O + Cl2
        verifyBalanced("KMnO4 + HCl -> KCl + MnCl2 + H2O + Cl2", 
                "2KMnO4 + 16HCl -> 2KCl + 2MnCl2 + 8H2O + 5Cl2", 
                new int[]{2, 16}, new int[]{2, 2, 8, 5});
    }

    @Test
    void testAlreadyBalancedEquations() {
        // 2H2 + O2 -> 2H2O
        ChemicalEquation eq = parser.parse("2H2 + O2 -> 2H2O");
        BalancedEquation balanced = balancer.balance(eq);
        
        assertThat(balanced.isOriginallyBalanced()).isTrue();
        assertThat(balanced.getCanonicalEquationString()).isEqualTo("2H2 + O2 -> 2H2O");

        // Input with unnormalized ratio (4H2 + 2O2 -> 4H2O)
        ChemicalEquation eqUnnormalized = parser.parse("4H2 + 2O2 -> 4H2O");
        BalancedEquation balancedNorm = balancer.balance(eqUnnormalized);
        assertThat(balancedNorm.isOriginallyBalanced()).isTrue(); // ratio is equivalent, and it's balanced
        assertThat(balancedNorm.getCanonicalEquationString()).isEqualTo("2H2 + O2 -> 2H2O"); // simplified
    }

    @Test
    void testInputWithIncorrectCoefficients() {
        // 2H2 + O2 -> H2O (Incorrect coefficients)
        ChemicalEquation eq = parser.parse("2H2 + O2 -> H2O");
        BalancedEquation balanced = balancer.balance(eq);
        
        assertThat(balanced.isOriginallyBalanced()).isFalse(); // original coefficients were wrong
        assertThat(balanced.getCanonicalEquationString()).isEqualTo("2H2 + O2 -> 2H2O"); // corrected ratio
    }

    @Test
    void testCompactNeutralEquations() {
        verifyBalanced("H2+O2->H2O", "2H2 + O2 -> 2H2O", new int[]{2, 1}, new int[]{2});
        verifyBalanced("Fe+O2->Fe2O3", "4Fe + 3O2 -> 2Fe2O3", new int[]{4, 3}, new int[]{2});
    }

    @Test
    void testIonicEquationChargeBalancing() {
        verifyBalanced("Fe2+ -> Fe3+ + e-", "Fe2+ -> Fe3+ + e-", new int[]{1}, new int[]{1, 1});
        verifyBalanced("Fe2+ + Ce4+ -> Fe3+ + Ce3+", "Fe2+ + Ce4+ -> Fe3+ + Ce3+", new int[]{1, 1}, new int[]{1, 1});
        verifyBalanced("Ag+ + Cl- -> AgCl", "Ag+ + Cl- -> AgCl", new int[]{1, 1}, new int[]{1});
        verifyBalanced("H+ + OH- -> H2O", "H+ + OH- -> H2O", new int[]{1, 1}, new int[]{1});

        // Compact ionic forms
        verifyBalanced("Ag++Cl-->AgCl", "Ag+ + Cl- -> AgCl", new int[]{1, 1}, new int[]{1});
        verifyBalanced("H++OH-->H2O", "H+ + OH- -> H2O", new int[]{1, 1}, new int[]{1});
    }

    @Test
    void testImpossibleEquations() {
        // Element only on one side
        assertThatThrownBy(() -> balancer.balance(parser.parse("H2 -> O2")))
                .isInstanceOf(InvalidChemicalEquationException.class)
                .hasFieldOrPropertyWithValue("errorCode", EquationErrorCode.UNBALANCEABLE_EQUATION);

        // Impossible conversion
        assertThatThrownBy(() -> balancer.balance(parser.parse("H2O -> CO2")))
                .isInstanceOf(InvalidChemicalEquationException.class)
                .hasFieldOrPropertyWithValue("errorCode", EquationErrorCode.UNBALANCEABLE_EQUATION);
    }

    @Test
    void testUnderdeterminedEquations() {
        // Multiple independent solutions (H2 + O2 -> H2O and C + O2 -> CO2 combined)
        assertThatThrownBy(() -> balancer.balance(parser.parse("H2 + O2 + C -> H2O + CO2")))
                .isInstanceOf(InvalidChemicalEquationException.class)
                .hasFieldOrPropertyWithValue("errorCode", EquationErrorCode.MULTIPLE_INDEPENDENT_SOLUTIONS);
    }

    @Test
    void testInvalidEquationSeparatorAndEmptySides() {
        // Multiple separators
        assertThatThrownBy(() -> parser.parse("H2 + O2 -> H2O -> H2"))
                .isInstanceOf(InvalidChemicalEquationException.class)
                .hasFieldOrPropertyWithValue("errorCode", EquationErrorCode.INVALID_EQUATION_SEPARATOR);

        // Missing reactant side
        assertThatThrownBy(() -> parser.parse(" -> H2O"))
                .isInstanceOf(InvalidChemicalEquationException.class)
                .hasFieldOrPropertyWithValue("errorCode", EquationErrorCode.MISSING_REACTANT);

        // Missing product side
        assertThatThrownBy(() -> parser.parse("H2 + O2 -> "))
                .isInstanceOf(InvalidChemicalEquationException.class)
                .hasFieldOrPropertyWithValue("errorCode", EquationErrorCode.MISSING_PRODUCT);

        // Empty terms / tokenization errors
        assertThatThrownBy(() -> parser.parse("H2 + -> H2O"))
                .isInstanceOf(InvalidChemicalEquationException.class)
                .hasFieldOrPropertyWithValue("errorCode", EquationErrorCode.AMBIGUOUS_EQUATION_TOKENIZATION);

        assertThatThrownBy(() -> parser.parse("+ H2 -> H2"))
                .isInstanceOf(InvalidChemicalEquationException.class)
                .hasFieldOrPropertyWithValue("errorCode", EquationErrorCode.AMBIGUOUS_EQUATION_TOKENIZATION);

        assertThatThrownBy(() -> parser.parse("H2 + -> H2"))
                .isInstanceOf(InvalidChemicalEquationException.class)
                .hasFieldOrPropertyWithValue("errorCode", EquationErrorCode.AMBIGUOUS_EQUATION_TOKENIZATION);

        assertThatThrownBy(() -> parser.parse("H2 ++ O2 -> H2O"))
                .isInstanceOf(InvalidChemicalEquationException.class)
                .hasFieldOrPropertyWithValue("errorCode", EquationErrorCode.AMBIGUOUS_EQUATION_TOKENIZATION);
    }

    private void verifyBalanced(String input, String expectedCanonical, int[] expectedReactantCoeffs, int[] expectedProductCoeffs) {
        ChemicalEquation parsed = parser.parse(input);
        BalancedEquation balanced = balancer.balance(parsed);

        assertThat(balanced.getCanonicalEquationString()).isEqualTo(expectedCanonical);
        assertThat(balanced.isAtomBalanced()).isTrue();
        assertThat(balanced.isChargeBalanced()).isTrue();

        for (int i = 0; i < expectedReactantCoeffs.length; i++) {
            assertThat(balanced.getBalancedReactants().get(i).getCoefficient())
                    .isEqualTo(BigInteger.valueOf(expectedReactantCoeffs[i]));
        }

        for (int i = 0; i < expectedProductCoeffs.length; i++) {
            assertThat(balanced.getBalancedProducts().get(i).getCoefficient())
                    .isEqualTo(BigInteger.valueOf(expectedProductCoeffs[i]));
        }
    }
}
