package com.ailab.chemistry;

import com.ailab.chemistry.api.AcidBaseEquilibriumService;
import com.ailab.chemistry.api.BufferCalculationService;
import com.ailab.chemistry.api.IonicActivityService;
import com.ailab.chemistry.api.PolyproticEquilibriumService;
import com.ailab.chemistry.api.PolyproticTitrationService;
import com.ailab.chemistry.api.SolubilityEquilibriumService;
import com.ailab.chemistry.api.TitrationCalculationService;
import com.ailab.chemistry.api.ThermodynamicReferenceService;
import com.ailab.chemistry.domain.acidbase.ActivityCorrectionRequest;
import com.ailab.chemistry.domain.acidbase.ActivityEquilibriumSystemType;
import com.ailab.chemistry.domain.acidbase.ActivityModel;
import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.acidbase.PolyproticEquilibriumRequest;
import com.ailab.chemistry.domain.acidbase.PolyproticInitialForm;
import com.ailab.chemistry.domain.acidbase.PolyproticTitrationRequest;
import com.ailab.chemistry.domain.acidbase.PolyproticTitrationSystemType;
import com.ailab.chemistry.domain.acidbase.TitrationRegion;
import com.ailab.chemistry.domain.acidbase.TitrationRequest;
import com.ailab.chemistry.domain.solubility.SaturationRequest;
import com.ailab.chemistry.domain.solubility.SaturationStatus;
import com.ailab.chemistry.domain.thermodynamics.ThermodynamicPropertyType;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.PressureUnit;
import com.ailab.chemistry.domain.measurement.MolarConcentration;
import com.ailab.chemistry.domain.measurement.MolarConcentrationUnit;
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
import org.springframework.test.annotation.DirtiesContext;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class Phase7FMonoproticTitrationIntegrationTest {

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
    private TitrationCalculationService titrationCalculationService;

    @Autowired
    private BufferCalculationService bufferCalculationService;

    @Autowired
    private AcidBaseEquilibriumService acidBaseEquilibriumService;

    @Autowired
    private PolyproticEquilibriumService polyproticEquilibriumService;

    @Autowired
    private PolyproticTitrationService polyproticTitrationService;

    @Autowired
    private IonicActivityService ionicActivityService;

    @Autowired
    private SolubilityEquilibriumService solubilityEquilibriumService;

    @Autowired
    private ThermodynamicReferenceService thermodynamicReferenceService;

    @Test
    @DisplayName("Phase 7F titration service is injectable and calculates against PostgreSQL V22")
    void titrationServiceIsInjectableAgainstPostgresV22() {
        assertLatestChemistryMigrationAndV22Present();
        assertThat(bufferCalculationService).isNotNull();
        assertThat(acidBaseEquilibriumService).isNotNull();

        var result = titrationCalculationService.calculatePoint(
                new TitrationRequest(
                        "SPEC-HCL",
                        "SPEC-NAOH",
                        MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                        Volume.of("25.00", VolumeUnit.MILLILITER),
                        MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                        Temperature.of("25.0", TemperatureUnit.CELSIUS),
                        "COMP-H2O"
                ),
                Volume.of("25.00", VolumeUnit.MILLILITER)
        );

        assertThat(result.getRegion()).isEqualTo(TitrationRegion.EQUIVALENCE);
        assertThat(result.getPh().getValue()).isEqualByComparingTo(new BigDecimal("7.0000"));
    }

    @Test
    @DisplayName("Phase 7G polyprotic service is injectable and calculates against PostgreSQL V22")
    void polyproticServiceIsInjectableAgainstPostgresV22() {
        assertLatestChemistryMigrationAndV22Present();
        assertThat(polyproticEquilibriumService).isNotNull();

        var result = polyproticEquilibriumService.calculate(new PolyproticEquilibriumRequest(
                "FAMILY-CARBONIC",
                PolyproticInitialForm.INTERMEDIATE_AMPHIPROTIC_SALT,
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                Temperature.of("25.0", TemperatureUnit.CELSIUS),
                "COMP-H2O",
                "SPEC-NA-PLUS",
                BigDecimal.ONE
        ));

        assertThat(result.getPh().getValue()).isCloseTo(new BigDecimal("8.3398"), org.assertj.core.data.Offset.offset(new BigDecimal("0.0008")));
        assertThat(result.getDistribution().dominantSpeciesCode()).isEqualTo("SPEC-HCO3-MINUS");
    }

    @Test
    @DisplayName("Phase 7H polyprotic titration service is injectable and calculates against PostgreSQL V22")
    void polyproticTitrationServiceIsInjectableAgainstPostgresV22() {
        assertLatestChemistryMigrationAndV22Present();
        assertThat(polyproticTitrationService).isNotNull();

        var result = polyproticTitrationService.calculateCharacteristicPoints(new PolyproticTitrationRequest(
                "FAMILY-CARBONIC",
                PolyproticTitrationSystemType.DIPROTIC_ACID_WITH_STRONG_MONOBASIC_BASE,
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                Volume.of("25.00", VolumeUnit.MILLILITER),
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                Temperature.of("25.0", TemperatureUnit.CELSIUS),
                "COMP-H2O",
                null,
                "SPEC-NA-PLUS",
                new BigDecimal("0.00001")
        ));

        assertThat(result.equivalencePoints()).hasSize(2);
        assertThat(result.equivalencePoints().get(0).volume().in(VolumeUnit.MILLILITER)).isEqualByComparingTo("25.00");
        assertThat(result.equivalencePoints().get(1).volume().in(VolumeUnit.MILLILITER)).isEqualByComparingTo("50.00");
        assertThat(result.points().get(2).distribution().dominantSpeciesCode()).isEqualTo("SPEC-HCO3-MINUS");
        assertThat(result.points().get(4).distribution().dominantSpeciesCode()).isEqualTo("SPEC-CO3-2MINUS");
    }

    @Test
    @DisplayName("Phase 7I ionic activity service is injectable and calculates against PostgreSQL V24")
    void ionicActivityServiceIsInjectableAgainstPostgresV24() {
        assertLatestChemistryMigrationAndV22Present();
        assertThat(ionicActivityService).isNotNull();

        var result = ionicActivityService.calculateEquilibrium(ActivityCorrectionRequest.forSpecies(
                ActivityModel.DAVIES,
                ActivityEquilibriumSystemType.WEAK_ACID,
                "SPEC-CH3COOH",
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                Temperature.of("25.0", TemperatureUnit.CELSIUS),
                "COMP-H2O"
        ));

        assertThat(result.activityPh().getValue()).isNotEqualByComparingTo(result.idealPh().getValue());
        assertThat(result.ionicStrength().value()).isLessThanOrEqualTo(new BigDecimal("0.5"));
    }

    @Test
    @DisplayName("Phase 7J solubility service is injectable and calculates against PostgreSQL V26")
    void solubilityServiceIsInjectableAgainstPostgresV26() {
        assertLatestChemistryMigrationAndV22Present();
        assertThat(solubilityEquilibriumService).isNotNull();

        Integer seededEquilibria = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM chemistry.solubility_equilibria WHERE dataset_version = 'solubility-ksp-v1.0.0'",
                Integer.class
        );
        Integer unbalancedRecords = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM (" +
                        "SELECT e.equilibrium_code, sum(t.charge * t.coefficient) AS net_charge " +
                        "FROM chemistry.solubility_equilibria e " +
                        "JOIN chemistry.solubility_dissolution_terms t ON t.equilibrium_id = e.id " +
                        "GROUP BY e.equilibrium_code" +
                        ") q WHERE q.net_charge <> 0",
                Integer.class
        );
        assertThat(seededEquilibria).isEqualTo(3);
        assertThat(unbalancedRecords).isEqualTo(0);

        var result = solubilityEquilibriumService.calculateSaturation(SaturationRequest.forEquilibriumCode(
                "KSP-CACO3-CALCITE",
                List.of(
                        new com.ailab.chemistry.domain.acidbase.IonicSpeciesConcentration("SPEC-CA-2PLUS", new BigDecimal("0.00010"), 2),
                        new com.ailab.chemistry.domain.acidbase.IonicSpeciesConcentration("SPEC-CO3-2MINUS", new BigDecimal("0.00010"), -2)
                ),
                List.of(),
                Temperature.of("25.0", TemperatureUnit.CELSIUS),
                "COMP-H2O",
                ActivityModel.IDEAL
        ));

        assertThat(result.status()).isEqualTo(SaturationStatus.SUPERSATURATED);
    }

    @Test
    @DisplayName("Phase 8A thermodynamic reference service is injectable and reads PostgreSQL V28")
    void thermodynamicReferenceServiceIsInjectableAgainstPostgresV28() {
        assertLatestChemistryMigrationAndV22Present();
        assertThat(thermodynamicReferenceService).isNotNull();

        Integer profileCount = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM chemistry.thermodynamic_profiles WHERE dataset_version = 'thermodynamic-reference-v1.0.0'",
                Integer.class
        );
        Integer recordCount = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM chemistry.thermodynamic_property_records",
                Integer.class
        );
        Integer badCompoundReferences = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM chemistry.thermodynamic_profiles p " +
                        "LEFT JOIN chemistry.compounds c ON c.compound_code = p.compound_code " +
                        "AND c.compound_catalog_version_id = p.compound_catalog_version_id " +
                        "WHERE c.id IS NULL",
                Integer.class
        );
        Integer missingProvenance = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM chemistry.thermodynamic_property_records " +
                        "WHERE source_identifier IS NULL OR citation = '' OR reuse_limitations = ''",
                Integer.class
        );
        Integer nonPositiveHeatCapacity = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM chemistry.thermodynamic_property_records " +
                        "WHERE property_type = 'MOLAR_HEAT_CAPACITY' AND numeric_value <= 0",
                Integer.class
        );

        assertThat(profileCount).isEqualTo(16);
        assertThat(recordCount).isEqualTo(68);
        assertThat(badCompoundReferences).isZero();
        assertThat(missingProvenance).isZero();
        assertThat(nonPositiveHeatCapacity).isZero();

        var liquidWater = thermodynamicReferenceService.findExact(
                "COMP-H2O",
                ThermodynamicPropertyType.STANDARD_ENTHALPY_OF_FORMATION,
                MatterState.LIQUID,
                Temperature.of("25.0", TemperatureUnit.CELSIUS),
                Pressure.of("1.000", PressureUnit.BAR)
        ).orElseThrow();
        var gasWater = thermodynamicReferenceService.findExact(
                "COMP-H2O",
                ThermodynamicPropertyType.STANDARD_ENTHALPY_OF_FORMATION,
                MatterState.GAS,
                Temperature.of("25.0", TemperatureUnit.CELSIUS),
                Pressure.of("1.000", PressureUnit.BAR)
        ).orElseThrow();

        assertThat(liquidWater.value()).isEqualByComparingTo("-285.830");
        assertThat(gasWater.value()).isEqualByComparingTo("-241.826");
        assertThat(thermodynamicReferenceService.findExact(
                "COMP-CH4",
                ThermodynamicPropertyType.STANDARD_ENTHALPY_OF_FORMATION,
                MatterState.LIQUID,
                Temperature.of("25.0", TemperatureUnit.CELSIUS),
                Pressure.of("1.000", PressureUnit.BAR)
        )).isEmpty();
    }

    private void assertLatestChemistryMigrationAndV22Present() {
        Integer flywayVersion = jdbcTemplate.queryForObject(
                "SELECT max(cast(version as integer)) FROM chemistry.flyway_schema_history_chemistry WHERE success = true",
                Integer.class
        );
        Integer v22Applied = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM chemistry.flyway_schema_history_chemistry WHERE version = '22' AND success = true",
                Integer.class
        );
        assertThat(flywayVersion).isGreaterThanOrEqualTo(26);
        assertThat(v22Applied).isEqualTo(1);
    }
}
