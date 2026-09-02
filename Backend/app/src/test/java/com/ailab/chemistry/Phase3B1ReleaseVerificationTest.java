package com.ailab.chemistry;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.ailab.chemistry.api.ElementPropertyService;
import com.ailab.chemistry.api.ElementPropertyDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles({"migration-test", "local"})
class Phase3B1ReleaseVerificationTest {

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
    private ElementPropertyService propertyService;

    @Test
    void testPropertyCoverageAndEvidenceCounts() {
        // Query exact counts
        Integer profiles = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM chemistry.element_property_profiles", Integer.class);
        Integer valencies = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM chemistry.element_valencies", Integer.class);
        Integer elementsWithValency = jdbcTemplate.queryForObject("SELECT COUNT(DISTINCT profile_id) FROM chemistry.element_valencies", Integer.class);
        Integer commonValencies = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM chemistry.element_valencies WHERE is_common = true", Integer.class);

        Integer oxStates = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM chemistry.element_oxidation_states", Integer.class);
        Integer elementsWithOxState = jdbcTemplate.queryForObject("SELECT COUNT(DISTINCT profile_id) FROM chemistry.element_oxidation_states", Integer.class);
        Integer commonOxStates = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM chemistry.element_oxidation_states WHERE is_common = true", Integer.class);
        Integer predictedOxStates = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM chemistry.element_oxidation_states WHERE is_predicted = true", Integer.class);

        Integer electronegativities = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM chemistry.element_electronegativities", Integer.class);
        Integer elementsWithPauling = jdbcTemplate.queryForObject("SELECT COUNT(DISTINCT profile_id) FROM chemistry.element_electronegativities WHERE scale = 'PAULING'", Integer.class);

        Integer radii = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM chemistry.element_radii", Integer.class);
        Integer ionicRadii = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM chemistry.element_radii WHERE kind = 'IONIC'", Integer.class);
        Integer densities = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM chemistry.element_density_data", Integer.class);
        Integer meltingTransitions = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM chemistry.element_phase_transitions WHERE kind = 'MELTING'", Integer.class);
        Integer boilingTransitions = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM chemistry.element_phase_transitions WHERE kind = 'BOILING'", Integer.class);
        Integer sublimationTransitions = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM chemistry.element_phase_transitions WHERE kind = 'SUBLIMATION'", Integer.class);
        Integer appearances = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM chemistry.element_appearance", Integer.class);

        assertThat(profiles).isEqualTo(118);
        assertThat(valencies).isGreaterThanOrEqualTo(118);
        assertThat(elementsWithValency).isEqualTo(118);
        assertThat(oxStates).isGreaterThanOrEqualTo(118);
        assertThat(elementsWithOxState).isEqualTo(118);
        assertThat(electronegativities).isGreaterThanOrEqualTo(80);
        assertThat(radii).isGreaterThanOrEqualTo(118);
        assertThat(densities).isEqualTo(118);
        assertThat(meltingTransitions).isGreaterThanOrEqualTo(110);
        assertThat(appearances).isEqualTo(118);

        System.out.println("=== Phase 3B.1 Database Property Statistics ===");
        System.out.println("Profiles: " + profiles);
        System.out.println("Valencies: " + valencies + " (distinct elements: " + elementsWithValency + ", common: " + commonValencies + ")");
        System.out.println("Oxidation States: " + oxStates + " (distinct elements: " + elementsWithOxState + ", common: " + commonOxStates + ", predicted: " + predictedOxStates + ")");
        System.out.println("Electronegativities: " + electronegativities + " (Pauling elements: " + elementsWithPauling + ")");
        System.out.println("Radii: " + radii + " (ionic: " + ionicRadii + ")");
        System.out.println("Densities: " + densities);
        System.out.println("Phase Transitions: melting=" + meltingTransitions + ", boiling=" + boilingTransitions + ", sublimation=" + sublimationTransitions);
        System.out.println("Appearances: " + appearances);
    }

    @Test
    void testRepresentativeServiceLookups() {
        String[] syms = {"H", "C", "O", "Na", "Cl", "Fe", "Cu", "Br", "Ag", "Au", "Hg", "Pb", "U", "Og"};
        for (String sym : syms) {
            ElementPropertyDetails details = propertyService.getBySymbol(sym);
            assertThat(details).isNotNull();
            assertThat(details.getSymbol()).isEqualTo(sym);
            assertThat(details.getAtomicNumber()).isGreaterThanOrEqualTo(1);
            assertThat(details.getValencies()).isNotEmpty();
        }
    }

    @Test
    void testDatabaseConstraintsEnforcedByPostgres() {
        // Attempt duplicate profile for Z=1 -> should fail UK
        String hProfileId = jdbcTemplate.queryForObject("SELECT id FROM chemistry.element_property_profiles WHERE atomic_number = 1", String.class);
        String hElementId = jdbcTemplate.queryForObject("SELECT element_id FROM chemistry.element_property_profiles WHERE atomic_number = 1", String.class);

        assertThatThrownBy(() -> jdbcTemplate.execute(String.format(
                "INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id) " +
                "VALUES ('%s', '%s', 1, 'H', 'extended-properties-v1.0.0')",
                UUID.randomUUID(), hElementId
        ))).hasMessageContaining("uk_element_property_profile");

        // Attempt non-positive radius -> should fail CHECK
        assertThatThrownBy(() -> jdbcTemplate.execute(String.format(
                "INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, evidence_status, source_identifier, source_title) " +
                "VALUES ('%s', '%s', 'EMPIRICAL_ATOMIC', -10, 'EVALUATED', 'SRC', 'TITLE')",
                UUID.randomUUID(), hProfileId
        ))).hasMessageContaining("element_radii_radius_pm_check");

        // Attempt ionic radius without charge -> should fail CHECK
        assertThatThrownBy(() -> jdbcTemplate.execute(String.format(
                "INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, evidence_status, source_identifier, source_title) " +
                "VALUES ('%s', '%s', 'IONIC', 50, 'EVALUATED', 'SRC', 'TITLE')",
                UUID.randomUUID(), hProfileId
        ))).hasMessageContaining("chk_ionic_charge_required");

        // Attempt duplicate compound code -> should fail UK
        assertThatThrownBy(() -> jdbcTemplate.execute(String.format(
                "INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, composition_formula, net_charge, molar_mass_value, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title) " +
                "VALUES ('%s', 'COMP-H2O', 'Water Duplicate', 'H2O', 'H2O', 'H2O', 0, 18.015, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'SRC', 'TITLE')",
                UUID.randomUUID()
        ))).hasMessageContaining("uk_compound_code");
    }
}
