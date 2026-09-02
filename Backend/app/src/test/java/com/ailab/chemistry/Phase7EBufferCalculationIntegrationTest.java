package com.ailab.chemistry;

import com.ailab.chemistry.api.BufferCalculationService;
import com.ailab.chemistry.domain.acidbase.BufferCalculationRequest;
import com.ailab.chemistry.domain.acidbase.BufferRegionStatus;
import com.ailab.chemistry.domain.measurement.AmountOfSubstance;
import com.ailab.chemistry.domain.measurement.AmountOfSubstanceUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.measurement.Volume;
import com.ailab.chemistry.domain.measurement.VolumeUnit;
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
class Phase7EBufferCalculationIntegrationTest {

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
            conn.createStatement().execute("DROP TABLE IF EXISTS chemistry.flyway_schema_history_chemistry CASCADE;");
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
    private BufferCalculationService bufferCalculationService;

    @Test
    @DisplayName("Phase 7E buffer service is injectable and uses PostgreSQL V22 reference semantics")
    void phase7eBufferServiceIsInjectableAgainstPostgres() {
        Integer flywayVersion = jdbcTemplate.queryForObject(
                "SELECT max(cast(version as integer)) FROM chemistry.flyway_schema_history_chemistry WHERE success = true",
                Integer.class
        );
        assertThat(flywayVersion).isGreaterThanOrEqualTo(22);

        assertThat(jdbcTemplate.queryForObject(
                "SELECT dissociation_behavior FROM chemistry.chemical_species WHERE species_code = 'SPEC-H2O'",
                String.class
        )).isEqualTo("AUTOIONIZING_SOLVENT");
        assertThat(jdbcTemplate.queryForObject(
                "SELECT dissociation_behavior FROM chemistry.chemical_species WHERE species_code = 'SPEC-H3O-PLUS'",
                String.class
        )).isEqualTo("NOT_APPLICABLE");

        var result = bufferCalculationService.calculateBuffer(BufferCalculationRequest.fromSpeciesAmounts(
                "SPEC-CH3COOH",
                "SPEC-CH3COO-MINUS",
                AmountOfSubstance.of("0.100", AmountOfSubstanceUnit.MOLE),
                AmountOfSubstance.of("0.100", AmountOfSubstanceUnit.MOLE),
                Volume.of("1.0", VolumeUnit.LITER),
                Temperature.of("25.0", TemperatureUnit.CELSIUS),
                "COMP-H2O"
        ));

        assertThat(result.getStatus()).isEqualTo(BufferRegionStatus.VALID_BUFFER);
        assertThat(result.getPh().getValue()).isCloseTo(new BigDecimal("4.7567"), org.assertj.core.data.Offset.offset(new BigDecimal("0.0005")));
    }
}
