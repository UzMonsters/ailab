package com.ailab.chemistry;

import com.ailab.chemistry.api.*;
import com.ailab.chemistry.domain.acidbase.*;
import com.ailab.chemistry.domain.measurement.*;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles({"migration-test", "local"})
class Phase7DReleaseVerificationTest {

    static final String LOCAL_DB_URL = "jdbc:postgresql://localhost:5432/ai_laboratory";
    static final String LOCAL_DB_USER = "postgres";
    static final String LOCAL_DB_PASS = "Sardorbek.01";

    @org.junit.jupiter.api.BeforeEach
    void checkPostgres() {
        TestPostgresUtils.assumePostgresAvailable();
    }

    @BeforeAll
    static void setUpClass() {
        if (!TestPostgresUtils.isPostgresAvailable()) {
            return;
        }
        try (Connection conn = DriverManager.getConnection(TestPostgresUtils.LOCAL_DB_URL, TestPostgresUtils.LOCAL_DB_USER, TestPostgresUtils.LOCAL_DB_PASS)) {
            conn.createStatement().execute("DROP SCHEMA IF EXISTS chemistry CASCADE;");
            conn.createStatement().execute("CREATE SCHEMA chemistry;");
            conn.createStatement().execute("DROP TABLE IF EXISTS users CASCADE;");
            conn.createStatement().execute("DROP TABLE IF EXISTS refresh_tokens CASCADE;");
            conn.createStatement().execute("DROP TABLE IF EXISTS flyway_schema_history CASCADE;");
        } catch (Exception ignored) {
        }
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        if (TestPostgresUtils.isPostgresAvailable()) {
            registry.add("spring.datasource.url", () -> TestPostgresUtils.LOCAL_DB_URL);
            registry.add("spring.datasource.username", () -> TestPostgresUtils.LOCAL_DB_USER);
            registry.add("spring.datasource.password", () -> TestPostgresUtils.LOCAL_DB_PASS);
        }
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AcidBaseEquilibriumService acidBaseEquilibriumService;

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
    @DisplayName("Verify AcidBaseEquilibriumService is Injectable and Performs Aqueous Acid-Base Calculations against PostgreSQL V21")
    void testAcidBaseEquilibriumServiceIntegration() {
        assertThat(acidBaseEquilibriumService).isNotNull();
        assertThat(acidBaseReferenceService).isNotNull();
        assertThat(solutionCalculationService).isNotNull();
        assertThat(stoichiometryService).isNotNull();
        assertThat(reactionCatalogService).isNotNull();
        assertThat(compoundCatalogService).isNotNull();

        // 1. Flyway migration V21 check in chemistry.flyway_schema_history_chemistry table
        Integer flywayVersion = jdbcTemplate.queryForObject("SELECT max(cast(version as integer)) FROM chemistry.flyway_schema_history_chemistry WHERE success = true", Integer.class);
        assertThat(flywayVersion).isGreaterThanOrEqualTo(21);

        // 2. NaOH reference check
        ChemicalSpeciesDetails naohSpecies = acidBaseReferenceService.getSpecies("SPEC-NAOH");
        assertThat(naohSpecies.getPrimaryRole()).isEqualTo("BASE");
        assertThat(naohSpecies.getDissociationBehavior()).isEqualTo("STRONG_ELECTROLYTE");

        Temperature t25 = Temperature.of("25.0", TemperatureUnit.CELSIUS);
        MolarConcentration conc01 = MolarConcentration.of("0.1", MolarConcentrationUnit.MOL_PER_LITER);

        // 3. Pure water calculation
        AcidBaseEquilibriumResult pureWater = acidBaseEquilibriumService.calculatePureWater(t25);
        assertThat(pureWater.getPh().getValue()).isEqualByComparingTo(new BigDecimal("7.0000"));

        // 4. Strong acid (0.1 M HCl)
        AcidBaseEquilibriumResult hcl = acidBaseEquilibriumService.calculateStrongAcid("SPEC-HCL", conc01, t25);
        assertThat(hcl.getPh().getValue()).isEqualByComparingTo(new BigDecimal("1.0000"));

        // 5. Strong base (0.1 M NaOH)
        AcidBaseEquilibriumResult naohResult = acidBaseEquilibriumService.calculateStrongBase("SPEC-NAOH", conc01, t25);
        assertThat(naohResult.getPh().getValue()).isEqualByComparingTo(new BigDecimal("13.0000"));

        // 6. Weak acid (0.1 M CH3COOH)
        AcidBaseEquilibriumResult acetic = acidBaseEquilibriumService.calculateWeakAcid("SPEC-CH3COOH", conc01, t25);
        assertThat(acetic.getPh().getValue()).isCloseTo(new BigDecimal("2.879"), org.assertj.core.data.Offset.offset(new BigDecimal("0.005")));

        // 7. Weak base (0.1 M NH3)
        AcidBaseEquilibriumResult ammonia = acidBaseEquilibriumService.calculateWeakBase("SPEC-NH3", conc01, t25);
        assertThat(ammonia.getPh().getValue()).isCloseTo(new BigDecimal("11.124"), org.assertj.core.data.Offset.offset(new BigDecimal("0.005")));

        // 8. Salt Hydrolysis (0.1 M NH4+)
        AcidBaseEquilibriumResult ammonium = acidBaseEquilibriumService.calculateSaltHydrolysis("SPEC-NH4-PLUS", conc01, t25);
        assertThat(ammonium.getPh().getValue()).isCloseTo(new BigDecimal("5.122"), org.assertj.core.data.Offset.offset(new BigDecimal("0.01")));
    }
}
