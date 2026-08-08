package com.ailab.chemistry;

import com.ailab.chemistry.api.GasLawService;
import com.ailab.chemistry.api.PhaseBehaviorService;
import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.AmountOfSubstance;
import com.ailab.chemistry.domain.measurement.AmountOfSubstanceUnit;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.PressureUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.phasebehavior.PhaseTransitionRequest;
import com.ailab.chemistry.domain.phasebehavior.PhaseTransitionType;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles({"test", "local"})
class Phase10GasAndPhaseBehaviorIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private GasLawService gasLawService;

    @Autowired
    private PhaseBehaviorService phaseBehaviorService;

    @Autowired
    @org.springframework.beans.factory.annotation.Qualifier("chemistryFlyway")
    private Flyway chemistryFlyway;

    @Test
    void servicesInjectAndLatestMigrationSeedsPhaseBehaviorReference() throws Exception {
        assertThat(gasLawService).isNotNull();
        assertThat(phaseBehaviorService).isNotNull();
        assertThat(Integer.parseInt(chemistryFlyway.info().current().getVersion().getVersion())).isGreaterThanOrEqualTo(37);

        assertThat(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chemistry.kinetic_dataset_versions WHERE dataset_id = 'kinetic-reference-v1.1.0'",
                Integer.class)).isEqualTo(1);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chemistry.phase_transition_dataset_versions WHERE dataset_id = 'phase-behavior-reference-v1.0.0'",
                Integer.class)).isEqualTo(1);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chemistry.phase_transition_records WHERE compound_code = 'COMP-H2O'",
                Integer.class)).isGreaterThanOrEqualTo(2);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chemistry.antoine_correlations WHERE compound_code = 'COMP-H2O' AND pressure_unit = 'mmHg' AND temperature_unit = 'degC'",
                Integer.class)).isEqualTo(1);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chemistry.phase_boundary_points WHERE compound_code = 'COMP-CO2' AND boundary_type = 'TRIPLE_POINT'",
                Integer.class)).isEqualTo(1);

        assertThat(phaseBehaviorService.calculateTransition(new PhaseTransitionRequest(
                        "COMP-H2O", PhaseTransitionType.FUSION, MatterState.SOLID, MatterState.LIQUID,
                        AmountOfSubstance.of("1", AmountOfSubstanceUnit.MOLE),
                        Temperature.of("273.15", TemperatureUnit.KELVIN),
                        Pressure.of("1", PressureUnit.ATMOSPHERE)))
                .heat()).isNotNull();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode manifest = mapper.readTree(new ClassPathResource("chemistry-data/phase-behavior-reference-v1.json").getInputStream());
        assertThat(manifest.get("datasetId").asText()).isEqualTo("phase-behavior-reference-v1.0.0");
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM chemistry.phase_transition_records", Integer.class))
                .isEqualTo(manifest.get("transitionRecords").size());
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM chemistry.antoine_correlations", Integer.class))
                .isEqualTo(manifest.get("antoineCorrelations").size());
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM chemistry.phase_boundary_points", Integer.class))
                .isEqualTo(manifest.get("boundaryPoints").size());

        Set<String> manifestTransitionIds = StreamSupport.stream(manifest.get("transitionRecords").spliterator(), false)
                .map(node -> node.get("recordId").asText())
                .collect(Collectors.toSet());
        Set<String> sqlTransitionIds = Set.copyOf(jdbcTemplate.queryForList("SELECT record_id FROM chemistry.phase_transition_records", String.class));
        assertThat(sqlTransitionIds).isEqualTo(manifestTransitionIds);
    }
}
