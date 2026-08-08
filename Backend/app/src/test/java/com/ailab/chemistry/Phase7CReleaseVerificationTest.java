package com.ailab.chemistry;

import com.ailab.chemistry.api.*;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles({"migration-test", "local"})
class Phase7CReleaseVerificationTest {

    static final String LOCAL_DB_URL = "jdbc:postgresql://localhost:5432/ai_laboratory";
    static final String LOCAL_DB_USER = "postgres";
    static final String LOCAL_DB_PASS = "Sardorbek.01";

    @BeforeAll
    static void setUpClass() {
        try (Connection conn = DriverManager.getConnection(LOCAL_DB_URL, LOCAL_DB_USER, LOCAL_DB_PASS)) {
            conn.createStatement().execute("DROP SCHEMA IF EXISTS chemistry CASCADE;");
            conn.createStatement().execute("CREATE SCHEMA chemistry;");
            conn.createStatement().execute("DROP TABLE IF EXISTS users CASCADE;");
            conn.createStatement().execute("DROP TABLE IF EXISTS refresh_tokens CASCADE;");
            conn.createStatement().execute("DROP TABLE IF EXISTS flyway_schema_history CASCADE;");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> LOCAL_DB_URL);
        registry.add("spring.datasource.username", () -> LOCAL_DB_USER);
        registry.add("spring.datasource.password", () -> LOCAL_DB_PASS);
    }

    @Autowired
    private AcidBaseReferenceService acidBaseReferenceService;

    @Autowired
    private SolutionCalculationService solutionCalculationService;

    @Autowired
    private StoichiometryService stoichiometryService;

    @Autowired
    private ReactionCatalogService reactionCatalogService;

    @Autowired
    private CompoundCatalogService compoundCatalogService;

    @Test
    @DisplayName("Verify AcidBaseReferenceService is Injectable and Retrieves Aqueous Acid-Base Reference Data from PostgreSQL")
    void testAcidBaseReferenceServiceIntegration() {
        assertThat(acidBaseReferenceService).isNotNull();
        assertThat(solutionCalculationService).isNotNull();
        assertThat(stoichiometryService).isNotNull();
        assertThat(reactionCatalogService).isNotNull();
        assertThat(compoundCatalogService).isNotNull();

        // 1. Retrieve species details from PostgreSQL database
        ChemicalSpeciesDetails ch3cooh = acidBaseReferenceService.getSpecies("SPEC-CH3COOH");
        assertThat(ch3cooh.getName()).isEqualTo("Acetic Acid");
        assertThat(ch3cooh.getFormula()).isEqualTo("CH3COOH");
        assertThat(ch3cooh.getCharge()).isEqualTo(0);
        assertThat(ch3cooh.getAssociatedCompoundCode()).isEqualTo("COMP-CH3COOH");

        // 2. Retrieve Ka for Acetic Acid from PostgreSQL
        Temperature t25 = Temperature.of("25.0", TemperatureUnit.CELSIUS);
        EquilibriumConstantDetails kaAcetic = acidBaseReferenceService.findKa("SPEC-CH3COOH", t25, "COMP-H2O").orElseThrow();
        assertThat(kaAcetic.getValue()).isCloseTo(new BigDecimal("0.0000175"), org.assertj.core.data.Offset.offset(new BigDecimal("0.000001")));
        assertThat(kaAcetic.getPValue()).isCloseTo(new BigDecimal("4.7567"), org.assertj.core.data.Offset.offset(new BigDecimal("0.01")));

        // 3. Verify polyprotic steps (H2CO3 -> HCO3- -> CO3^2-)
        List<DissociationStepDetails> steps = acidBaseReferenceService.getDissociationSteps("SPEC-H2CO3");
        assertThat(steps).hasSize(1);
        assertThat(steps.get(0).getDeprotonatedSpeciesCode()).isEqualTo("SPEC-HCO3-MINUS");

        // 4. Verify Conjugate Pair lookup
        ConjugatePairDetails pair = acidBaseReferenceService.getConjugatePair("SPEC-CH3COOH");
        assertThat(pair.getBaseSpeciesCode()).isEqualTo("SPEC-CH3COO-MINUS");

        // 5. Verify Strong Electrolyte Representation (Role ACID, Behavior STRONG_ELECTROLYTE)
        ChemicalSpeciesDetails hcl = acidBaseReferenceService.getSpecies("SPEC-HCL");
        assertThat(hcl.getPrimaryRole()).isEqualTo("ACID");
        assertThat(hcl.getDissociationBehavior()).isEqualTo("STRONG_ELECTROLYTE");
        assertThat(acidBaseReferenceService.findKa("SPEC-HCL", t25, "COMP-H2O")).isEmpty();
    }
}
