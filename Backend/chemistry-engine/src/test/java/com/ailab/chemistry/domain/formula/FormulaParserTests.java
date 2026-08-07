package com.ailab.chemistry.domain.formula;

import org.junit.jupiter.api.Test;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import com.ailab.chemistry.domain.formula.exception.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FormulaParserTests {
    private final FormulaParser parser = new DefaultFormulaParser();

    @Test
    void testBasicFormulas() {
        // H2O
        ChemicalFormula h2o = parser.parse("H2O");
        assertThat(h2o.getElementCounts())
                .containsEntry(new ElementSymbol("H"), BigInteger.valueOf(2))
                .containsEntry(new ElementSymbol("O"), BigInteger.ONE)
                .hasSize(2);
        assertThat(h2o.getNetCharge()).isZero();
        assertThat(h2o.isElectron()).isFalse();

        // CO2
        ChemicalFormula co2 = parser.parse("CO2");
        assertThat(co2.getElementCounts())
                .containsEntry(new ElementSymbol("C"), BigInteger.ONE)
                .containsEntry(new ElementSymbol("O"), BigInteger.valueOf(2))
                .hasSize(2);

        // H2SO4
        ChemicalFormula h2so4 = parser.parse("H2SO4");
        assertThat(h2so4.getElementCounts())
                .containsEntry(new ElementSymbol("H"), BigInteger.valueOf(2))
                .containsEntry(new ElementSymbol("S"), BigInteger.ONE)
                .containsEntry(new ElementSymbol("O"), BigInteger.valueOf(4))
                .hasSize(3);
    }

    @Test
    void testGroupedAndNestedFormulas() {
        // Ca(OH)2
        ChemicalFormula ca_oh_2 = parser.parse("Ca(OH)2");
        assertThat(ca_oh_2.getElementCounts())
                .containsEntry(new ElementSymbol("Ca"), BigInteger.ONE)
                .containsEntry(new ElementSymbol("O"), BigInteger.valueOf(2))
                .containsEntry(new ElementSymbol("H"), BigInteger.valueOf(2))
                .hasSize(3);

        // Al2(SO4)3
        ChemicalFormula al2_so4_3 = parser.parse("Al2(SO4)3");
        assertThat(al2_so4_3.getElementCounts())
                .containsEntry(new ElementSymbol("Al"), BigInteger.valueOf(2))
                .containsEntry(new ElementSymbol("S"), BigInteger.valueOf(3))
                .containsEntry(new ElementSymbol("O"), BigInteger.valueOf(12))
                .hasSize(3);

        // K4[Fe(CN)6]
        ChemicalFormula k4fe_cn_6 = parser.parse("K4[Fe(CN)6]");
        assertThat(k4fe_cn_6.getElementCounts())
                .containsEntry(new ElementSymbol("K"), BigInteger.valueOf(4))
                .containsEntry(new ElementSymbol("Fe"), BigInteger.ONE)
                .containsEntry(new ElementSymbol("C"), BigInteger.valueOf(6))
                .containsEntry(new ElementSymbol("N"), BigInteger.valueOf(6))
                .hasSize(4);

        // (NH4)2Cr2O7
        ChemicalFormula nh4_2cr2o7 = parser.parse("(NH4)2Cr2O7");
        assertThat(nh4_2cr2o7.getElementCounts())
                .containsEntry(new ElementSymbol("N"), BigInteger.valueOf(2))
                .containsEntry(new ElementSymbol("H"), BigInteger.valueOf(8))
                .containsEntry(new ElementSymbol("Cr"), BigInteger.valueOf(2))
                .containsEntry(new ElementSymbol("O"), BigInteger.valueOf(7))
                .hasSize(4);
    }

    @Test
    void testHydrates() {
        // CuSO4·5H2O
        ChemicalFormula copperHydrate = parser.parse("CuSO4·5H2O");
        assertThat(copperHydrate.getElementCounts())
                .containsEntry(new ElementSymbol("Cu"), BigInteger.ONE)
                .containsEntry(new ElementSymbol("S"), BigInteger.ONE)
                .containsEntry(new ElementSymbol("O"), BigInteger.valueOf(9)) // 4 from SO4 + 5 from 5H2O
                .containsEntry(new ElementSymbol("H"), BigInteger.valueOf(10)) // 10 from 5H2O
                .hasSize(4);

        // Dot separator CuSO4.5H2O
        ChemicalFormula dotSeparator = parser.parse("CuSO4.5H2O");
        assertThat(dotSeparator).isEqualTo(copperHydrate);
    }

    @Test
    void testUnicodeNormalization() {
        // Subscripts H₂O
        ChemicalFormula unicodeH2O = parser.parse("H₂O");
        assertThat(unicodeH2O.getNormalizedFormula()).isEqualTo("H2O");

        // Superscript charge SO₄²⁻
        ChemicalFormula unicodeSO4 = parser.parse("SO₄²⁻");
        assertThat(unicodeSO4.getNormalizedFormula()).isEqualTo("SO4^2-");
        assertThat(unicodeSO4.getNetCharge()).isEqualTo(-2);

        // Superscript charge Fe³⁺
        ChemicalFormula unicodeFe = parser.parse("Fe³⁺");
        assertThat(unicodeFe.getNormalizedFormula()).isEqualTo("Fe^3+");
        assertThat(unicodeFe.getNetCharge()).isEqualTo(3);
    }

    @Test
    void testCharges() {
        assertThat(parser.parse("Na+").getNetCharge()).isEqualTo(1);
        assertThat(parser.parse("Cl-").getNetCharge()).isEqualTo(-1);
        assertThat(parser.parse("Ca2+").getNetCharge()).isEqualTo(2);
        assertThat(parser.parse("SO4^2-").getNetCharge()).isEqualTo(-2);
        assertThat(parser.parse("NH4+").getNetCharge()).isEqualTo(1);
        assertThat(parser.parse("Fe3+").getNetCharge()).isEqualTo(3);
    }

    @Test
    void testFreeElectron() {
        ChemicalFormula eMinus = parser.parse("e-");
        assertThat(eMinus.isElectron()).isTrue();
        assertThat(eMinus.getNetCharge()).isEqualTo(-1);
        assertThat(eMinus.getElementCounts()).isEmpty();
    }

    @Test
    void testInvalidFormulas() {
        // Empty
        assertThatThrownBy(() -> parser.parse(""))
                .isInstanceOf(FormulaSyntaxException.class)
                .hasFieldOrPropertyWithValue("errorCode", FormulaErrorCode.EMPTY_FORMULA);
        
        // Zero count
        assertThatThrownBy(() -> parser.parse("H0"))
                .isInstanceOf(FormulaSyntaxException.class)
                .hasFieldOrPropertyWithValue("errorCode", FormulaErrorCode.ZERO_ELEMENT_COUNT);

        // Unmatched opening group
        assertThatThrownBy(() -> parser.parse("Ca(OH"))
                .isInstanceOf(FormulaSyntaxException.class)
                .hasFieldOrPropertyWithValue("errorCode", FormulaErrorCode.UNMATCHED_GROUP);

        // Unmatched closing group
        assertThatThrownBy(() -> parser.parse("CaOH)"))
                .isInstanceOf(FormulaSyntaxException.class)
                .hasFieldOrPropertyWithValue("errorCode", FormulaErrorCode.UNMATCHED_GROUP);

        // Empty group
        assertThatThrownBy(() -> parser.parse("()"))
                .isInstanceOf(FormulaSyntaxException.class)
                .hasFieldOrPropertyWithValue("errorCode", FormulaErrorCode.EMPTY_GROUP);

        // Trailing hydrate separator
        assertThatThrownBy(() -> parser.parse("H2O·"))
                .isInstanceOf(FormulaSyntaxException.class)
                .hasFieldOrPropertyWithValue("errorCode", FormulaErrorCode.INVALID_HYDRATE);

        // Leading hydrate separator
        assertThatThrownBy(() -> parser.parse("·H2O"))
                .isInstanceOf(FormulaSyntaxException.class)
                .hasFieldOrPropertyWithValue("errorCode", FormulaErrorCode.INVALID_HYDRATE);

        // Repeated hydrate separator
        assertThatThrownBy(() -> parser.parse("CuSO4··5H2O"))
                .isInstanceOf(FormulaSyntaxException.class)
                .hasFieldOrPropertyWithValue("errorCode", FormulaErrorCode.INVALID_HYDRATE);

        // Unknown element symbol
        assertThatThrownBy(() -> parser.parse("Xx2"))
                .isInstanceOf(UnknownElementSymbolException.class)
                .hasFieldOrPropertyWithValue("errorCode", FormulaErrorCode.UNKNOWN_ELEMENT_SYMBOL);

        // Ambiguous charge notation molecular ions
        assertThatThrownBy(() -> parser.parse("O2+"))
                .isInstanceOf(FormulaSyntaxException.class)
                .hasFieldOrPropertyWithValue("errorCode", FormulaErrorCode.AMBIGUOUS_CHARGE_NOTATION);
        assertThatThrownBy(() -> parser.parse("N2+"))
                .isInstanceOf(FormulaSyntaxException.class)
                .hasFieldOrPropertyWithValue("errorCode", FormulaErrorCode.AMBIGUOUS_CHARGE_NOTATION);
        assertThatThrownBy(() -> parser.parse("Cl2+"))
                .isInstanceOf(FormulaSyntaxException.class)
                .hasFieldOrPropertyWithValue("errorCode", FormulaErrorCode.AMBIGUOUS_CHARGE_NOTATION);
        assertThatThrownBy(() -> parser.parse("H2+"))
                .isInstanceOf(FormulaSyntaxException.class)
                .hasFieldOrPropertyWithValue("errorCode", FormulaErrorCode.AMBIGUOUS_CHARGE_NOTATION);

        // Unambiguous molecular ion with caret
        ChemicalFormula o2Plus = parser.parse("O2^+");
        assertThat(o2Plus.getElementCounts())
                .containsEntry(new ElementSymbol("O"), BigInteger.valueOf(2))
                .hasSize(1);
        assertThat(o2Plus.getNetCharge()).isEqualTo(1);

        // Invalid casing of element
        assertThatThrownBy(() -> parser.parse("h"))
                .isInstanceOf(FormulaSyntaxException.class)
                .hasFieldOrPropertyWithValue("errorCode", FormulaErrorCode.INVALID_ELEMENT_SYMBOL);

        // Three-letter element symbol
        assertThatThrownBy(() -> parser.parse("Naa"))
                .isInstanceOf(FormulaSyntaxException.class)
                .hasFieldOrPropertyWithValue("errorCode", FormulaErrorCode.INVALID_ELEMENT_SYMBOL);

        // Invalid charge mag format
        assertThatThrownBy(() -> parser.parse("Na++"))
                .isInstanceOf(FormulaSyntaxException.class)
                .hasFieldOrPropertyWithValue("errorCode", FormulaErrorCode.INVALID_CHARGE);

        // Caret without charge details
        assertThatThrownBy(() -> parser.parse("SO4^"))
                .isInstanceOf(FormulaSyntaxException.class)
                .hasFieldOrPropertyWithValue("errorCode", FormulaErrorCode.INVALID_CHARGE);
    }

    @Test
    void testComplexityLimits() {
        // Length limit
        StringBuilder largeFormula = new StringBuilder();
        for (int i = 0; i < 600; i++) {
            largeFormula.append("H");
        }
        assertThatThrownBy(() -> parser.parse(largeFormula.toString()))
                .isInstanceOf(FormulaComplexityException.class);

        // Nesting depth limit
        StringBuilder nested = new StringBuilder();
        for (int i = 0; i < 21; i++) {
            nested.append("(");
        }
        nested.append("H");
        for (int i = 0; i < 21; i++) {
            nested.append(")");
        }
        assertThatThrownBy(() -> parser.parse(nested.toString()))
                .isInstanceOf(FormulaComplexityException.class);

        // Multiplier too large
        assertThatThrownBy(() -> parser.parse("H10000000"))
                .isInstanceOf(FormulaComplexityException.class);
    }

    @Test
    void testPropertyRoundTripAndDeterminism() {
        String[] samples = { "H2O", "CO2", "H2SO4", "Ca(OH)2", "Al2(SO4)3", "CuSO4·5H2O", "K4[Fe(CN)6]" };
        for (String sample : samples) {
            ChemicalFormula f1 = parser.parse(sample);
            ChemicalFormula f2 = parser.parse(f1.getNormalizedFormula());

            // Rendered formula has the exact same composition
            assertThat(f1.getElementCounts()).isEqualTo(f2.getElementCounts());
            assertThat(f1.getNetCharge()).isEqualTo(f2.getNetCharge());
            
            // Determinism across repeated calls
            ChemicalFormula f3 = parser.parse(sample);
            assertThat(f1).isEqualTo(f3);
            assertThat(f1.hashCode()).isEqualTo(f3.hashCode());
        }
    }
}
