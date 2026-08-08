package com.ailab.chemistry;

import com.ailab.chemistry.api.ThermodynamicReferenceService;
import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.PressureUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.thermodynamics.ThermodynamicPropertyType;
import org.flywaydb.core.Flyway;
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

@SpringBootTest(properties = "phase8a.upgrade-context=true")
@ActiveProfiles({"migration-test", "local"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class Phase8AThermodynamicUpgradeIntegrationTest {

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
        Flyway.configure()
                .dataSource(LOCAL_DB_URL, LOCAL_DB_USER, LOCAL_DB_PASS)
                .locations("classpath:db/migration/chemistry")
                .schemas("chemistry")
                .createSchemas(true)
                .table("flyway_schema_history_chemistry")
                .target("28")
                .baselineOnMigrate(true)
                .load()
                .migrate();
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
    private ThermodynamicReferenceService thermodynamicReferenceService;

    @Test
    void upgradesAuthenticV28ChemistrySchemaToV30() {
        Integer latest = jdbcTemplate.queryForObject(
                "SELECT max(cast(version as integer)) FROM chemistry.flyway_schema_history_chemistry WHERE success = true",
                Integer.class);
        Integer v28Applied = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM chemistry.flyway_schema_history_chemistry WHERE version = '28' AND success = true",
                Integer.class);
        Integer v30Applied = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM chemistry.flyway_schema_history_chemistry WHERE version = '30' AND success = true",
                Integer.class);

        assertThat(latest).isGreaterThanOrEqualTo(30);
        assertThat(v28Applied).isEqualTo(1);
        assertThat(v30Applied).isEqualTo(1);

        var water = thermodynamicReferenceService.findExact(
                "COMP-H2O",
                ThermodynamicPropertyType.STANDARD_GIBBS_ENERGY_OF_FORMATION,
                MatterState.LIQUID,
                Temperature.of("25.0", TemperatureUnit.CELSIUS),
                Pressure.of("1.000", PressureUnit.BAR)
        ).orElseThrow();

        assertThat(water.value()).isEqualByComparingTo("-237.129");
    }
}
