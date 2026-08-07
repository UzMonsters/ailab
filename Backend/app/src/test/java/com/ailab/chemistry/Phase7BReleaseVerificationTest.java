package com.ailab.chemistry;

import com.ailab.chemistry.api.*;
import com.ailab.chemistry.domain.measurement.*;
import com.ailab.chemistry.domain.solution.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
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
class Phase7BReleaseVerificationTest {

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
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SolutionCalculationService solutionCalculationService;

    @Autowired
    private StoichiometryService stoichiometryService;

    @Autowired
    private ReactionCatalogService reactionCatalogService;

    @Autowired
    private CompoundCatalogService compoundCatalogService;

    @Test
    @DisplayName("Verify SolutionCalculationService is Injectable and Works with PostgreSQL V18 Database")
    void testSolutionCalculationServiceIntegration() {
        assertThat(solutionCalculationService).isNotNull();
        assertThat(stoichiometryService).isNotNull();
        assertThat(reactionCatalogService).isNotNull();
        assertThat(compoundCatalogService).isNotNull();

        // 1. Solution Molarity calculation using PostgreSQL catalogue compound NaCl
        AmountOfSubstance soluteAmount = AmountOfSubstance.of("1.0", AmountOfSubstanceUnit.MOLE);
        Volume finalVolume = Volume.of("1.0", VolumeUnit.LITER);
        MolarConcentration molarity = solutionCalculationService.calculateMolarity("COMP-NACL", soluteAmount, finalVolume);
        assertThat(molarity.in(MolarConcentrationUnit.MOL_PER_LITER)).isEqualByComparingTo(BigDecimal.ONE);

        // 2. Solution Preparation calculation using PostgreSQL molar mass for NaCl (~58.443 g/mol)
        SolutionPreparationResult prep = solutionCalculationService.calculatePreparation(
                "COMP-NACL",
                MolarConcentration.of("1.0", MolarConcentrationUnit.MOL_PER_LITER),
                Volume.of("1.0", VolumeUnit.LITER)
        );
        assertThat(prep.getRequiredSoluteMass().in(MassUnit.GRAM)).isCloseTo(new BigDecimal("58.443"), org.assertj.core.data.Offset.offset(new BigDecimal("0.5")));

        // 3. Dilution calculation
        DilutionRequest dilutionRequest = DilutionRequest.fromInitialToTargetConcentration(
                "COMP-NACL",
                MolarConcentration.of("2.0", MolarConcentrationUnit.MOL_PER_LITER),
                Volume.of("250.0", VolumeUnit.MILLILITER),
                MolarConcentration.of("0.5", MolarConcentrationUnit.MOL_PER_LITER)
        );
        DilutionResult dilutionResult = solutionCalculationService.calculateDilution(dilutionRequest);
        assertThat(dilutionResult.getTargetVolume().in(VolumeUnit.LITER)).isEqualByComparingTo(BigDecimal.ONE);

        // 4. Verify StoichiometryService remains fully functional
        Mass massH2O = Mass.of("36.03056", MassUnit.GRAM);
        AmountOfSubstance molesH2O = stoichiometryService.convertMassToMoles("COMP-H2O", massH2O);
        assertThat(molesH2O.in(AmountOfSubstanceUnit.MOLE)).isCloseTo(new BigDecimal("2.0"), org.assertj.core.data.Offset.offset(new BigDecimal("0.1")));
    }
}
