package com.ailab.chemistry;

import com.ailab.chemistry.api.TemperatureDependentThermodynamicsService;
import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.thermodynamics.TemperatureCorrectionStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "phase8c.temperature-thermodynamics-context=true")
@ActiveProfiles({"migration-test", "local"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class Phase8CTemperatureDependentThermodynamicsIntegrationTest {

    static final String LOCAL_DB_URL = "jdbc:postgresql://localhost:5432/ai_laboratory";
    static final String LOCAL_DB_USER = "postgres";
    static final String LOCAL_DB_PASS = "Sardorbek.01";

    @BeforeAll
    static void setUpClass() throws Exception {
        try (Connection conn = DriverManager.getConnection(LOCAL_DB_URL, LOCAL_DB_USER, LOCAL_DB_PASS)) {
            conn.createStatement().execute("DROP SCHEMA IF EXISTS chemistry CASCADE;");
            conn.createStatement().execute("CREATE SCHEMA chemistry;");
            conn.createStatement().execute("DROP TABLE IF EXISTS users CASCADE;");
            conn.createStatement().execute("DROP TABLE IF EXISTS refresh_tokens CASCADE;");
            conn.createStatement().execute("DROP TABLE IF EXISTS flyway_schema_history CASCADE;");
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
    private TemperatureDependentThermodynamicsService service;

    @Test
    void temperatureDependentServiceIsInjectableAgainstPostgresV30() {
        Integer latest = jdbcTemplate.queryForObject(
                "SELECT max(cast(version as integer)) FROM chemistry.flyway_schema_history_chemistry WHERE success = true",
                Integer.class);
        Integer correlations = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM chemistry.thermodynamic_temperature_correlations WHERE dataset_version = 'thermodynamic-temperature-functions-v1.0.0'",
                Integer.class);

        var result = service.calculateSpeciesProperties("COMP-H2O", MatterState.LIQUID,
                Temperature.of("400.0", TemperatureUnit.KELVIN));

        assertThat(latest).isGreaterThanOrEqualTo(30);
        assertThat(correlations).isGreaterThanOrEqualTo(7);
        assertThat(result.status()).isEqualTo(TemperatureCorrectionStatus.CALCULABLE);
    }
}
