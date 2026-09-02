package com.ailab.chemistry;

import com.ailab.chemistry.api.CalorimetryService;
import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.AmountOfSubstance;
import com.ailab.chemistry.domain.measurement.AmountOfSubstanceUnit;
import com.ailab.chemistry.domain.measurement.EnergyUnit;
import com.ailab.chemistry.domain.measurement.Mass;
import com.ailab.chemistry.domain.measurement.MassUnit;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.PressureUnit;
import com.ailab.chemistry.domain.measurement.SpecificHeatCapacity;
import com.ailab.chemistry.domain.measurement.SpecificHeatCapacityUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.thermodynamics.CalorimetryMethod;
import com.ailab.chemistry.domain.thermodynamics.CalorimetryStatus;
import com.ailab.chemistry.domain.thermodynamics.ReactionCalorimetryRequest;
import com.ailab.chemistry.domain.thermodynamics.ReactionCalorimetryResult;
import com.ailab.chemistry.domain.thermodynamics.SensibleHeatRequest;
import com.ailab.chemistry.domain.thermodynamics.SensibleHeatResult;
import com.ailab.chemistry.domain.thermodynamics.ThermalMixingRequest;
import com.ailab.chemistry.domain.thermodynamics.ThermalMixingResult;
import com.ailab.chemistry.domain.thermodynamics.ThermalSample;
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

@SpringBootTest(properties = "phase8f.calorimetry-context=true")
@ActiveProfiles({"migration-test", "local"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class Phase8FCalorimetryIntegrationTest {
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
    private CalorimetryService service;

    @Test
    void calorimetryServiceIsInjectableAgainstPostgresV30WithoutNewMigration() {
        Integer latest = jdbcTemplate.queryForObject(
                "SELECT max(cast(version as integer)) FROM chemistry.flyway_schema_history_chemistry WHERE success = true",
                Integer.class);

        assertThat(latest).isGreaterThanOrEqualTo(30);

        // Test Reaction Calorimetry
        ReactionCalorimetryResult rxnRes = service.calculateReactionHeat(new ReactionCalorimetryRequest(
                "RXN-WATER-SYNTHESIS",
                new BigDecimal("1.0"),
                Temperature.of("298.15", TemperatureUnit.KELVIN),
                Pressure.of("1.000", PressureUnit.BAR),
                Map.of()
        ));

        assertThat(rxnRes.status()).isEqualTo(CalorimetryStatus.SUCCESS);
        assertThat(rxnRes.totalReactionHeatJoules().in(EnergyUnit.JOULE)).isLessThan(BigDecimal.ZERO);

        // Test Thermal Mixing
        ThermalSample s1 = new ThermalSample(
                "COMP-H2O", MatterState.LIQUID,
                Mass.of("1.0", MassUnit.KILOGRAM), null,
                SpecificHeatCapacity.of("4184", SpecificHeatCapacityUnit.JOULE_PER_KILOGRAM_KELVIN), null,
                Temperature.of("293.15", TemperatureUnit.KELVIN)
        );

        ThermalSample s2 = new ThermalSample(
                "COMP-H2O", MatterState.LIQUID,
                Mass.of("1.0", MassUnit.KILOGRAM), null,
                SpecificHeatCapacity.of("4184", SpecificHeatCapacityUnit.JOULE_PER_KILOGRAM_KELVIN), null,
                Temperature.of("353.15", TemperatureUnit.KELVIN)
        );

        ThermalMixingResult mixRes = service.calculateFinalTemperature(new ThermalMixingRequest(
                List.of(s1, s2), null, CalorimetryMethod.CONSTANT_SPECIFIC_HEAT_CAPACITY
        ));

        assertThat(mixRes.status()).isEqualTo(CalorimetryStatus.CONVERGED);
        assertThat(mixRes.finalTemperature().in(TemperatureUnit.CELSIUS)).isCloseTo(new BigDecimal("50.0"), org.assertj.core.data.Offset.offset(new BigDecimal("0.01")));
        assertThat(mixRes.energyBalance().isBalanced()).isTrue();
    }
}
