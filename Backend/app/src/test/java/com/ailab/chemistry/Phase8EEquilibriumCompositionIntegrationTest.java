package com.ailab.chemistry;

import com.ailab.chemistry.api.EquilibriumCompositionService;
import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.PressureUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.thermodynamics.EquilibriumCompositionMethod;
import com.ailab.chemistry.domain.thermodynamics.EquilibriumCompositionRequest;
import com.ailab.chemistry.domain.thermodynamics.EquilibriumCompositionResult;
import com.ailab.chemistry.domain.thermodynamics.EquilibriumCompositionStatus;
import com.ailab.chemistry.domain.thermodynamics.InitialParticipantAmount;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "phase8e.equilibrium-composition-context=true")
@ActiveProfiles({"migration-test", "local"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class Phase8EEquilibriumCompositionIntegrationTest {
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
    private EquilibriumCompositionService service;

    @Test
    void compositionServiceIsInjectableAgainstPostgresV30WithoutNewMigration() {
        Integer latest = jdbcTemplate.queryForObject(
                "SELECT max(cast(version as integer)) FROM chemistry.flyway_schema_history_chemistry WHERE success = true",
                Integer.class);

        assertThat(latest).isGreaterThanOrEqualTo(30);

        EquilibriumCompositionRequest request = new EquilibriumCompositionRequest(
                "RXN-CO-OXIDATION",
                Temperature.of("298.15", TemperatureUnit.KELVIN),
                Pressure.of("1.000", PressureUnit.BAR),
                EquilibriumCompositionMethod.CONSTANT_TOTAL_PRESSURE,
                List.of(
                        new InitialParticipantAmount("COMP-CO", MatterState.GAS, new BigDecimal("2.0")),
                        new InitialParticipantAmount("COMP-O2", MatterState.GAS, new BigDecimal("1.0")),
                        new InitialParticipantAmount("COMP-CO2", MatterState.GAS, new BigDecimal("0.0"))
                ),
                Pressure.of("1.000", PressureUnit.BAR),
                null,
                BigDecimal.ZERO,
                List.of(),
                Map.of()
        );

        EquilibriumCompositionResult result = service.calculate(request);

        assertThat(result.status()).isIn(EquilibriumCompositionStatus.CONVERGED, EquilibriumCompositionStatus.BOUNDED_AT_FORWARD_LIMIT);
        assertThat(result.extent().extent()).isGreaterThan(new BigDecimal("0.99"));
        assertThat(result.residual().maxMassBalanceError()).isLessThan(new BigDecimal("1e-10"));
    }
}
