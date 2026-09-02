package com.ailab;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.mockito.Mockito;

import javax.sql.DataSource;

import com.ailab.chemistry.api.ChemicalFormulaService;
import com.ailab.chemistry.api.ChemicalEquationService;
import com.ailab.chemistry.domain.formula.ChemicalFormula;
import com.ailab.chemistry.domain.equation.BalancedEquation;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class AiMonolithApplicationTests {

    @Autowired
    private ChemicalFormulaService formulaService;

    @Autowired
    private ChemicalEquationService equationService;

    @Autowired
    private com.ailab.chemistry.api.ElementCatalogService catalogService;

    @Autowired
    private com.ailab.chemistry.api.ElementPropertyService propertyService;

    @Test
    void contextLoads() {
        assertThat(formulaService).isNotNull();
        assertThat(equationService).isNotNull();
        assertThat(catalogService).isNotNull();
        assertThat(propertyService).isNotNull();

        ChemicalFormula formula = formulaService.parseFormula("H2O");
        assertThat(formula.getNormalizedFormula()).isEqualTo("H2O");

        BalancedEquation balanced = equationService.balanceEquation("H2 + O2 -> H2O");
        assertThat(balanced.getCanonicalEquationString()).isEqualTo("2H2 + O2 -> 2H2O");

        com.ailab.chemistry.api.ElementPropertyDetails props = propertyService.getBySymbol("Fe");
        assertThat(props).isNotNull();
        assertThat(props.getAtomicNumber()).isEqualTo(26);
    }
}
