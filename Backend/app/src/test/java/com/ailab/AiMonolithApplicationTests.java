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

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:postgresql://localhost:5432/ai_laboratory",
        "spring.datasource.username=postgres",
        "spring.datasource.password=Sardorbek.01",
        "app.flyway.enabled=true"
})
@ActiveProfiles("test")
class AiMonolithApplicationTests {

    @org.junit.jupiter.api.BeforeAll
    static void cleanDb() {
        try (java.sql.Connection conn = java.sql.DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/ai_laboratory", "postgres", "Sardorbek.01")) {
            conn.createStatement().execute("DROP SCHEMA IF EXISTS chemistry CASCADE;");
            conn.createStatement().execute("CREATE SCHEMA chemistry;");
            conn.createStatement().execute("DROP TABLE IF EXISTS users CASCADE;");
            conn.createStatement().execute("DROP TABLE IF EXISTS refresh_tokens CASCADE;");
            conn.createStatement().execute("DROP TABLE IF EXISTS flyway_schema_history CASCADE;");
        } catch (Exception ignored) {
        }
    }

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
