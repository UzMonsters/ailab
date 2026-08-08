package com.ailab.chemistry.element;

import com.ailab.chemistry.domain.formula.DefaultFormulaParser;
import com.ailab.chemistry.domain.formula.ElementSymbol;
import com.ailab.chemistry.domain.formula.FormulaParser;
import com.ailab.chemistry.domain.formula.exception.FormulaErrorCode;
import com.ailab.chemistry.domain.formula.exception.FormulaSyntaxException;
import com.ailab.chemistry.domain.element.KnownElementRegistry;
import org.junit.jupiter.api.Test;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ElementParserAlignmentTests {

    private final FormulaParser parser = new DefaultFormulaParser();

    @Test
    void testParserRegistryAlignment() {
        Set<String> registrySymbols = KnownElementRegistry.getKnownSymbols();
        assertThat(registrySymbols).hasSize(118);

        for (String sym : registrySymbols) {
            ElementSymbol parsed = new ElementSymbol(sym);
            assertThat(parsed.getSymbol()).isEqualTo(sym);
        }

        assertThatThrownBy(() -> new ElementSymbol("Xx"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void testPhase2And21RegressionBehavior() {
        assertThat(parser.parse("H₂O").getElementCounts().get(new ElementSymbol("H"))).isEqualTo(2);

        assertThatThrownBy(() -> parser.parse("O2+"))
                .isInstanceOf(FormulaSyntaxException.class)
                .hasFieldOrPropertyWithValue("errorCode", FormulaErrorCode.AMBIGUOUS_CHARGE_NOTATION);

        assertThatThrownBy(() -> parser.parse("N2+"))
                .isInstanceOf(FormulaSyntaxException.class)
                .hasFieldOrPropertyWithValue("errorCode", FormulaErrorCode.AMBIGUOUS_CHARGE_NOTATION);

        assertThat(parser.parse("O2^+").getNetCharge()).isEqualTo(1);

        assertThat(parser.parse("Fe3+").getNetCharge()).isEqualTo(3);
        assertThat(parser.parse("Ca2+").getNetCharge()).isEqualTo(2);
    }
}
