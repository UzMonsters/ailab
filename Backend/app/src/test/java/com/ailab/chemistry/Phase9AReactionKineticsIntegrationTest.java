package com.ailab.chemistry;

import com.ailab.chemistry.api.ReactionKineticsService;
import com.ailab.chemistry.domain.kinetics.ArrheniusRequest;
import com.ailab.chemistry.domain.kinetics.ArrheniusResult;
import com.ailab.chemistry.domain.kinetics.KineticErrorCode;
import com.ailab.chemistry.domain.kinetics.KineticException;
import com.ailab.chemistry.domain.kinetics.KineticProfile;
import com.ailab.chemistry.domain.kinetics.KineticProfileRepository;
import com.ailab.chemistry.domain.kinetics.RateEvaluationRequest;
import com.ailab.chemistry.domain.kinetics.RateEvaluationResult;
import com.ailab.chemistry.domain.kinetics.ReactionKineticsCalculator;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
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

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = "phase9.kinetics-context=true")
@ActiveProfiles({"migration-test", "local"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class Phase9AReactionKineticsIntegrationTest {
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
    private ReactionKineticsService kineticsService;

    @Autowired
    private KineticProfileRepository profileRepository;

    private final ReactionKineticsCalculator calculator = new ReactionKineticsCalculator();

    @Test
    void reactionKineticsServiceIsInjectableAfterAuthenticV33ToLatestUpgradeWithV34Applied() {
        Integer latest = jdbcTemplate.queryForObject(
                "SELECT max(cast(version as integer)) FROM chemistry.flyway_schema_history_chemistry WHERE success = true",
                Integer.class);
        Integer v33Applied = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM chemistry.flyway_schema_history_chemistry WHERE version = '33' AND success = true",
                Integer.class);
        Integer v34Applied = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM chemistry.flyway_schema_history_chemistry WHERE version = '34' AND success = true",
                Integer.class);

        assertThat(latest).isGreaterThanOrEqualTo(34);
        assertThat(v33Applied).isEqualTo(1);
        assertThat(v34Applied).isEqualTo(1);

        // Verify inactive old profiles
        Integer oldActiveCount = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM chemistry.kinetic_profiles WHERE profile_id IN ('KP-ELEM-H-O2-PIRRAGLIA-1989', 'KP-ELEM-OH-CO-WOOLDRIDGE-1994') AND is_active = TRUE",
                Integer.class);
        assertThat(oldActiveCount).isEqualTo(0);

        // Verify authentic replacement elementary profiles in database
        KineticProfile hO2Profile = profileRepository.findByProfileId("KP-ELEM-H-O2-PIRRAGLIA-1989-REC3").orElseThrow();
        assertThat(hO2Profile.provenance().nistSquib()).isEqualTo("1989PIR/MIC282:3");
        assertThat(hO2Profile.reactionCode()).isEqualTo("RXN-ELEM-H-O2-PROPAGATION");
        assertThat(hO2Profile.provenance().originalAValue()).isEqualTo("2.79E-10");
        assertThat(hO2Profile.provenance().originalAUnit()).isEqualTo("cm3 molecule-1 s-1");
        assertThat(hO2Profile.provenance().originalKValue()).isEqualTo("1.22E-13");
        assertThat(hO2Profile.provenance().conversionFactor()).isEqualTo("6.02214076E20");
        Map<String, Object> hO2SourceRange = jdbcTemplate.queryForMap("""
                SELECT min_pressure_bar, max_pressure_bar, bath_gas
                FROM chemistry.kinetic_profiles
                WHERE profile_id = 'KP-ELEM-H-O2-PIRRAGLIA-1989-REC3'
                """);
        assertThat(hO2SourceRange.get("min_pressure_bar")).isEqualTo(new BigDecimal("0.0133"));
        assertThat(hO2SourceRange.get("max_pressure_bar")).isEqualTo(new BigDecimal("0.0413"));
        assertThat(hO2SourceRange.get("bath_gas")).isEqualTo("Ar");

        // Verify H radical COMP-RAD-H is used and not confused with H2 COMP-H2
        boolean hasRadicalH = hO2Profile.rateLaw().terms().stream().anyMatch(t -> t.compoundCode().equals("COMP-RAD-H"));
        boolean hasH2 = hO2Profile.rateLaw().terms().stream().anyMatch(t -> t.compoundCode().equals("COMP-H2"));
        assertThat(hasRadicalH).isTrue();
        assertThat(hasH2).isFalse();

        RateEvaluationResult rateRes = kineticsService.calculateRate(new RateEvaluationRequest(
                "RXN-ELEM-H-O2-PROPAGATION",
                hO2Profile.rateLaw(),
                hO2Profile.referenceRateConstant(),
                Map.of("COMP-RAD-H", new BigDecimal("2.0"), "COMP-O2", new BigDecimal("1.0"))
        ));
        assertThat(rateRes.reactionRate().value()).isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    void activeSourcedProfilesMatchExactElementaryReactionRecordsAndParticipants() {
        Integer globalReactionBindings = jdbcTemplate.queryForObject("""
                SELECT count(*)
                FROM chemistry.kinetic_profiles
                WHERE is_active = TRUE
                  AND profile_id IN ('KP-ELEM-H-O2-PIRRAGLIA-1989-REC3', 'KP-ELEM-CO-OH-WOOLDRIDGE-1994-REC1')
                  AND reaction_code IN ('RXN-WATER-SYNTHESIS', 'RXN-CO-OXIDATION')
                """, Integer.class);
        assertThat(globalReactionBindings).isEqualTo(0);

        String hO2ReactionTerms = jdbcTemplate.queryForObject("""
                SELECT string_agg(compound_code || ':' || side || ':' || coefficient::text, ',' ORDER BY term_order)
                FROM chemistry.reaction_terms rt
                JOIN chemistry.reactions r ON r.id = rt.reaction_id
                WHERE r.reaction_code = 'RXN-ELEM-H-O2-PROPAGATION'
                """, String.class);
        assertThat(hO2ReactionTerms)
                .isEqualTo("COMP-RAD-H:REACTANT:1,COMP-O2:REACTANT:1,COMP-RAD-OH:PRODUCT:1,COMP-RAD-O:PRODUCT:1");

        String coOhReactionTerms = jdbcTemplate.queryForObject("""
                SELECT string_agg(compound_code || ':' || side || ':' || coefficient::text, ',' ORDER BY term_order)
                FROM chemistry.reaction_terms rt
                JOIN chemistry.reactions r ON r.id = rt.reaction_id
                WHERE r.reaction_code = 'RXN-ELEM-CO-OH-PROPAGATION'
                """, String.class);
        assertThat(coOhReactionTerms)
                .isEqualTo("COMP-CO:REACTANT:1,COMP-RAD-OH:REACTANT:1,COMP-CO2:PRODUCT:1,COMP-RAD-H:PRODUCT:1");

        String hO2RateTerms = jdbcTemplate.queryForObject("""
                SELECT string_agg(compound_code, ',' ORDER BY compound_code)
                FROM chemistry.kinetic_rate_law_terms
                WHERE profile_id = 'KP-ELEM-H-O2-PIRRAGLIA-1989-REC3'
                """, String.class);
        assertThat(hO2RateTerms).isEqualTo("COMP-O2,COMP-RAD-H");

        String coOhRateTerms = jdbcTemplate.queryForObject("""
                SELECT string_agg(compound_code, ',' ORDER BY compound_code)
                FROM chemistry.kinetic_rate_law_terms
                WHERE profile_id = 'KP-ELEM-CO-OH-WOOLDRIDGE-1994-REC1'
                """, String.class);
        assertThat(coOhRateTerms).isEqualTo("COMP-CO,COMP-RAD-OH");

        Integer participantMismatchCount = jdbcTemplate.queryForObject("""
                SELECT count(*)
                FROM chemistry.kinetic_profiles kp
                JOIN chemistry.kinetic_rate_law_terms krlt ON krlt.profile_id = kp.profile_id
                WHERE kp.is_active = TRUE
                  AND kp.provenance_source_id = 'NIST-CHEMICAL-KINETICS'
                  AND NOT EXISTS (
                      SELECT 1
                      FROM chemistry.reactions r
                      JOIN chemistry.reaction_terms rt ON rt.reaction_id = r.id
                      WHERE r.reaction_code = kp.reaction_code
                        AND rt.side = 'REACTANT'
                        AND rt.compound_code = krlt.compound_code
                  )
                """, Integer.class);
        assertThat(participantMismatchCount).isEqualTo(0);

        Integer rateLawReactantSetMismatchCount = jdbcTemplate.queryForObject("""
                WITH reaction_reactants AS (
                    SELECT r.reaction_code, array_agg(rt.compound_code ORDER BY rt.compound_code) AS compounds
                    FROM chemistry.reactions r
                    JOIN chemistry.reaction_terms rt ON rt.reaction_id = r.id
                    WHERE rt.side = 'REACTANT'
                    GROUP BY r.reaction_code
                ),
                rate_law_reactants AS (
                    SELECT profile_id, array_agg(compound_code ORDER BY compound_code) AS compounds
                    FROM chemistry.kinetic_rate_law_terms
                    GROUP BY profile_id
                )
                SELECT count(*)
                FROM chemistry.kinetic_profiles kp
                JOIN reaction_reactants rr ON rr.reaction_code = kp.reaction_code
                JOIN rate_law_reactants lr ON lr.profile_id = kp.profile_id
                WHERE kp.is_active = TRUE
                  AND kp.provenance_source_id = 'NIST-CHEMICAL-KINETICS'
                  AND rr.compounds <> lr.compounds
                """, Integer.class);
        assertThat(rateLawReactantSetMismatchCount).isEqualTo(0);
    }

    @Test
    void unitConversionAndExactAnchorsValidation() {
        // Unit conversion invariant: 1 cm3 molecule-1 s-1 = 6.02214076E20 L mol-1 s-1
        BigDecimal origA = new BigDecimal("2.79E-10");
        BigDecimal convFactor = new BigDecimal("6.02214076E20");
        BigDecimal expectedA = origA.multiply(convFactor);

        assertThat(expectedA).isCloseTo(new BigDecimal("1.68017727204E11"), org.assertj.core.data.Offset.offset(new BigDecimal("100")));

        KineticProfile hO2Profile = profileRepository.findByProfileId("KP-ELEM-H-O2-PIRRAGLIA-1989-REC3").orElseThrow();
        assertThat(hO2Profile.referenceRateConstant().value()).isCloseTo(new BigDecimal("7.3470117272E7"), org.assertj.core.data.Offset.offset(new BigDecimal("1000")));

        KineticProfile coOhProfile = profileRepository.findByProfileId("KP-ELEM-CO-OH-WOOLDRIDGE-1994-REC1").orElseThrow();
        assertThat(coOhProfile.arrheniusParameters().preExponentialFactorA()).isCloseTo(new BigDecimal("2.11979354752E9"), org.assertj.core.data.Offset.offset(new BigDecimal("1000")));
        assertThat(coOhProfile.provenance().nistSquib()).isEqualTo("1994WOO/HAN741-748:1");
        assertThat(coOhProfile.provenance().originalAValue()).isEqualTo("3.52E-12");
        assertThat(coOhProfile.provenance().originalAUnit()).isEqualTo("cm3 molecule-1 s-1");
        assertThat(coOhProfile.provenance().conversionFactor()).isEqualTo("6.02214076E20");
        assertThat(coOhProfile.provenance().dataType()).isEqualTo("DIRECT_ABSOLUTE_EXPERIMENTAL_VALUE");
        Map<String, Object> coOhSourceRange = jdbcTemplate.queryForMap("""
                SELECT min_pressure_bar, max_pressure_bar, bath_gas
                FROM chemistry.kinetic_profiles
                WHERE profile_id = 'KP-ELEM-CO-OH-WOOLDRIDGE-1994-REC1'
                """);
        assertThat(coOhSourceRange.get("min_pressure_bar")).isEqualTo(new BigDecimal("0.1900"));
        assertThat(coOhSourceRange.get("max_pressure_bar")).isEqualTo(new BigDecimal("0.8300"));
        assertThat(coOhSourceRange.get("bath_gas")).isEqualTo("Ar");

        // Verify OH radical COMP-RAD-OH is used and not confused with O2 COMP-O2
        boolean hasRadicalOH = coOhProfile.rateLaw().terms().stream().anyMatch(t -> t.compoundCode().equals("COMP-RAD-OH"));
        boolean hasO2 = coOhProfile.rateLaw().terms().stream().anyMatch(t -> t.compoundCode().equals("COMP-O2"));
        assertThat(hasRadicalOH).isTrue();
        assertThat(hasO2).isFalse();
    }

    @Test
    void validityRangeEnforcementTest() {
        KineticProfile hO2Profile = profileRepository.findByProfileId("KP-ELEM-H-O2-PIRRAGLIA-1989-REC3").orElseThrow();

        // 800 K is below min temperature 962 K
        assertThatThrownBy(() -> calculator.calculateRateConstant(new ArrheniusRequest(
                hO2Profile.arrheniusParameters(), Temperature.of("800.0", TemperatureUnit.KELVIN))))
                .isInstanceOf(KineticException.class)
                .extracting("errorCode")
                .isEqualTo(KineticErrorCode.OUT_OF_TEMPERATURE_RANGE);

        // 2000 K is above max temperature 1700 K
        assertThatThrownBy(() -> calculator.calculateRateConstant(new ArrheniusRequest(
                hO2Profile.arrheniusParameters(), Temperature.of("2000.0", TemperatureUnit.KELVIN))))
                .isInstanceOf(KineticException.class)
                .extracting("errorCode")
                .isEqualTo(KineticErrorCode.OUT_OF_TEMPERATURE_RANGE);
    }
}
