package com.ailab.chemistry;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.flywaydb.core.Flyway;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Flyway Migration Integration Test.
 *
 * Execution strategy:
 * 1. If Docker is available → Testcontainers (isolated, ephemeral)
 * 2. If Docker is unavailable → Local PostgreSQL at localhost:5432/ai_laboratory
 *    (credentials from application-local.properties defaults)
 *
 * Skips entirely only if neither Docker nor local PostgreSQL is accessible.
 */
@SpringBootTest
@ActiveProfiles({"migration-test", "local"})
class FlywayMigrationTests {

    static final String LOCAL_DB_URL = "jdbc:postgresql://localhost:5432/ai_laboratory";
    static final String LOCAL_DB_USER = "postgres";
    static final String LOCAL_DB_PASS = "Sardorbek.01";

    static PostgreSQLContainer<?> postgres;
    static boolean useLocalDb = false;

    @BeforeAll
    static void setUpClass() {
        boolean dockerAvailable = isDockerAvailable();

        if (dockerAvailable) {
            postgres = new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("ai_laboratory")
                    .withUsername("postgres")
                    .withPassword("password");
            postgres.start();

            // Pre-create chemistry schema
            try (var conn = postgres.createConnection("")) {
                conn.createStatement().execute("CREATE SCHEMA IF NOT EXISTS chemistry");
            } catch (Exception e) {
                throw new RuntimeException("Failed to pre-create chemistry schema in container", e);
            }
        } else {
            // Try local PostgreSQL
            boolean localAvailable = isLocalPostgresAvailable();
            Assumptions.assumeTrue(localAvailable,
                    "Neither Docker nor local PostgreSQL is available. Skipping FlywayMigrationTests.");
            useLocalDb = true;
            System.out.println("[FlywayMigrationTests] Docker unavailable, using local PostgreSQL at " + LOCAL_DB_URL);
            try (Connection conn = DriverManager.getConnection(LOCAL_DB_URL, LOCAL_DB_USER, LOCAL_DB_PASS)) {
                conn.createStatement().execute("DROP SCHEMA IF EXISTS chemistry CASCADE;");
                conn.createStatement().execute("CREATE SCHEMA chemistry;");
                conn.createStatement().execute("DROP TABLE IF EXISTS refresh_tokens CASCADE;");
                conn.createStatement().execute("DROP TABLE IF EXISTS workspace_events CASCADE;");
                conn.createStatement().execute("DROP TABLE IF EXISTS workspace_states CASCADE;");
                conn.createStatement().execute("DROP TABLE IF EXISTS workspaces CASCADE;");
                conn.createStatement().execute("DROP TABLE IF EXISTS users CASCADE;");
                conn.createStatement().execute("DROP TABLE IF EXISTS flyway_schema_history CASCADE;");
                conn.createStatement().execute("DROP TABLE IF EXISTS flyway_schema_history_workspace CASCADE;");
            } catch (Exception e) {
                // schema creation attempted
            }
        }
    }

    @AfterAll
    static void tearDownClass() {
        if (postgres != null) {
            postgres.stop();
        }
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        if (postgres != null && postgres.isRunning()) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl);
            registry.add("spring.datasource.username", postgres::getUsername);
            registry.add("spring.datasource.password", postgres::getPassword);
        } else if (useLocalDb) {
            registry.add("spring.datasource.url", () -> LOCAL_DB_URL);
            registry.add("spring.datasource.username", () -> LOCAL_DB_USER);
            registry.add("spring.datasource.password", () -> LOCAL_DB_PASS);
        }
    }

    private static boolean isDockerAvailable() {
        try {
            return org.testcontainers.DockerClientFactory.instance().isDockerAvailable();
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isLocalPostgresAvailable() {
        try (Connection conn = DriverManager.getConnection(LOCAL_DB_URL, LOCAL_DB_USER, LOCAL_DB_PASS)) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("chemistryFlyway")
    private Flyway chemistryFlyway;

    @Autowired
    @Qualifier("identityFlyway")
    private Flyway identityFlyway;

    @Test
    void testMigrationsRunSuccessfully() {
        // Chemistry: metadata entry exists
        Integer metaCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chemistry.chemistry_engine_metadata", Integer.class);
        assertThat(metaCount).isGreaterThanOrEqualTo(1);

        String version = jdbcTemplate.queryForObject(
                "SELECT engine_version FROM chemistry.chemistry_engine_metadata LIMIT 1", String.class);
        assertThat(version).isEqualTo("1.0.0");

        // Chemistry: elements table present
        Integer elementCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chemistry.elements", Integer.class);
        assertThat(elementCount).isEqualTo(118);

        // Chemistry: Bismuth is PRIMORDIAL_RADIOACTIVE after V4 migration
        String bismuthStatus = jdbcTemplate.queryForObject(
                "SELECT radioactivity_status FROM chemistry.elements WHERE atomic_number = 83", String.class);
        assertThat(bismuthStatus).isEqualTo("PRIMORDIAL_RADIOACTIVE");

        // Chemistry: electron_configuration_status column exists
        String hStatus = jdbcTemplate.queryForObject(
                "SELECT electron_configuration_status FROM chemistry.elements WHERE atomic_number = 1", String.class);
        assertThat(hStatus).isEqualTo("EVALUATED");

        // Chemistry: catalog version v1.1.0 exists
        Integer v11Count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chemistry.periodic_table_catalog_versions WHERE version = '1.1.0'", Integer.class);
        assertThat(v11Count).isEqualTo(1);

        // Phase 3B: Extended property dataset version exists
        Integer propVersionCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chemistry.element_property_dataset_versions WHERE id = 'extended-properties-v1.0.0'", Integer.class);
        assertThat(propVersionCount).isEqualTo(1);

        // Phase 3B: 118 extended property profiles
        Integer profileCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chemistry.element_property_profiles", Integer.class);
        assertThat(profileCount).isEqualTo(118);

        // Phase 3B: Representative element properties assertions
        // H (Z=1)
        Integer hValencyCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chemistry.element_valencies v JOIN chemistry.element_property_profiles p ON v.profile_id = p.id WHERE p.atomic_number = 1", Integer.class);
        assertThat(hValencyCount).isEqualTo(1);

        // Fe (Z=26)
        Integer feValencyCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chemistry.element_valencies v JOIN chemistry.element_property_profiles p ON v.profile_id = p.id WHERE p.atomic_number = 26", Integer.class);
        assertThat(feValencyCount).isEqualTo(3);

        // Phase 4A/4A.1 plus additive Phase 11 electrochemistry species.
        Integer compoundCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chemistry.compounds", Integer.class);
        assertThat(compoundCount).isEqualTo(68);

        // Searching composition_formula = 'C2H6O' returns both isomers
        Integer c2h6oCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chemistry.compounds WHERE composition_formula = 'C2H6O'", Integer.class);
        assertThat(c2h6oCount).isEqualTo(2);

        // Ethanol normalized formula preserves input representation
        String ethanolNorm = jdbcTemplate.queryForObject(
                "SELECT normalized_formula FROM chemistry.compounds WHERE compound_code = 'COMP-ETHANOL'", String.class);
        assertThat(ethanolNorm).isEqualTo("C2H5OH");

        // Dimethyl ether normalized formula preserves input representation
        String dmeNorm = jdbcTemplate.queryForObject(
                "SELECT normalized_formula FROM chemistry.compounds WHERE compound_code = 'COMP-DIMETHYL-ETHER'", String.class);
        assertThat(dmeNorm).isEqualTo("CH3OCH3");

        // Copper sulfate pentahydrate preserves hydrate notation in normalized and Hill in composition
        String hydrateNorm = jdbcTemplate.queryForObject(
                "SELECT normalized_formula FROM chemistry.compounds WHERE compound_code = 'COMP-CUSO4-5H2O'", String.class);
        assertThat(hydrateNorm).isEqualTo("CuSO4·5H2O");

        String hydrateComp = jdbcTemplate.queryForObject(
                "SELECT composition_formula FROM chemistry.compounds WHERE compound_code = 'COMP-CUSO4-5H2O'", String.class);
        assertThat(hydrateComp).isEqualTo("CuH10O9S");

        // Phase 4B: Chemical Classification assertions
        Integer taxonomyVersionCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chemistry.classification_taxonomy_versions", Integer.class);
        assertThat(taxonomyVersionCount).isEqualTo(1);

        Integer defCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chemistry.classification_definitions", Integer.class);
        assertThat(defCount).isEqualTo(41);

        Integer classificationProfileCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chemistry.compound_classification_profiles", Integer.class);
        assertThat(classificationProfileCount).isEqualTo(55);

        // Ethanol ALCOHOL classification
        Integer ethanolAlcoholCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chemistry.compound_classification_assignments a " +
                "JOIN chemistry.compound_classification_profiles p ON a.profile_id = p.id " +
                "JOIN chemistry.compounds c ON p.compound_id = c.id " +
                "WHERE c.compound_code = 'COMP-ETHANOL' AND a.code = 'ALCOHOL'", Integer.class);
        assertThat(ethanolAlcoholCount).isEqualTo(1);

        // Dimethyl ether ETHER classification
        Integer dmeEtherCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chemistry.compound_classification_assignments a " +
                "JOIN chemistry.compound_classification_profiles p ON a.profile_id = p.id " +
                "JOIN chemistry.compounds c ON p.compound_id = c.id " +
                "WHERE c.compound_code = 'COMP-DIMETHYL-ETHER' AND a.code = 'ETHER'", Integer.class);
        assertThat(dmeEtherCount).isEqualTo(1);

        // Phase 4C: Physical Properties assertions
        Integer datasetVersionCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chemistry.compound_physical_property_dataset_versions", Integer.class);
        assertThat(datasetVersionCount).isEqualTo(1);

        Integer physicalProfileCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chemistry.compound_physical_property_profiles", Integer.class);
        assertThat(physicalProfileCount).isEqualTo(55);

        Integer availabilityCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chemistry.compound_property_availability", Integer.class);
        assertThat(availabilityCount).isEqualTo(990); // 55 profiles * 18 property types

        // Phase 5A / 5A.1: Hazard Reference Catalogue assertions
        Integer hazardDatasetVersionCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chemistry.hazard_dataset_versions", Integer.class);
        assertThat(hazardDatasetVersionCount).isEqualTo(1);

        Integer hazardSourceDocCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chemistry.hazard_source_documents", Integer.class);
        assertThat(hazardSourceDocCount).isEqualTo(3);

        Integer hazardProfileCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chemistry.hazard_profiles", Integer.class);
        assertThat(hazardProfileCount).isEqualTo(55);

        Integer hazardAvailabilityCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chemistry.hazard_availability", Integer.class);
        assertThat(hazardAvailabilityCount).isEqualTo(55);

        // Identity: users table present
        Integer userCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users", Integer.class);
        assertThat(userCount).isNotNull();

        System.out.println("[FlywayMigrationTests] All assertions passed. Element count=" + elementCount
                + " Property profile count=" + profileCount + " Compound count=" + compoundCount + " Bi radioactivity=" + bismuthStatus + " H config_status=" + hStatus);
    }

    @Test
    void testNoOldRadioactivityEnumValues() {
        // Verify no old enum values survive
        Integer oldValueCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chemistry.elements WHERE radioactivity_status IN ('STABLE_OR_HAS_STABLE_ISOTOPES', 'RADIOACTIVE')",
                Integer.class);
        assertThat(oldValueCount).isEqualTo(0);
    }
}
