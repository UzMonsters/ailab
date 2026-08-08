package com.ailab.chemistry;

import com.ailab.chemistry.api.ElectrochemistryService;
import com.ailab.chemistry.domain.electrochemistry.ElectrochemicalCellRequest;
import com.ailab.chemistry.domain.electrochemistry.ElectrochemicalCellType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles({"test", "local"})
class Phase11ElectrochemistryIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ElectrochemistryService electrochemistryService;

    @Autowired
    @org.springframework.beans.factory.annotation.Qualifier("chemistryFlyway")
    private Flyway chemistryFlyway;

    @Test
    void serviceInjectsAndLatestMigrationSeedsElectrochemicalReference() throws Exception {
        assertThat(electrochemistryService).isNotNull();
        assertThat(Integer.parseInt(chemistryFlyway.info().current().getVersion().getVersion())).isGreaterThanOrEqualTo(39);

        assertThat(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chemistry.electrochemical_dataset_versions WHERE dataset_id = 'electrochemical-reference-v1.0.0'",
                Integer.class)).isEqualTo(1);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chemistry.standard_reduction_potentials WHERE is_active = TRUE",
                Integer.class)).isEqualTo(6);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chemistry.half_reaction_participants WHERE species_code IN ('COMP-CU2-PLUS','COMP-ZN2-PLUS','COMP-AG-PLUS','COMP-FE3-PLUS','COMP-FE2-PLUS','COMP-CL-MINUS','COMP-H-PLUS')",
                Integer.class)).isGreaterThanOrEqualTo(7);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chemistry.standard_reduction_potentials WHERE record_id = 'SRP-H2-REFERENCE' AND standard_potential_v = 0.000000",
                Integer.class)).isEqualTo(1);

        var result = electrochemistryService.calculateStandardCell(new ElectrochemicalCellRequest(
                "SRP-CU2-CU", "SRP-ZN2-ZN", ElectrochemicalCellType.GALVANIC, BigDecimal.ONE));
        assertThat(result.cellNotation().value()).isEqualTo("Zn(s) | Zn2+(aq) || Cu2+(aq) | Cu(s)");
        assertThat(result.electronCount().value()).isEqualByComparingTo("2");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode manifest = mapper.readTree(new ClassPathResource("chemistry-data/electrochemical-reference-v1.json").getInputStream());
        assertThat(manifest.get("datasetId").asText()).isEqualTo("electrochemical-reference-v1.0.0");
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM chemistry.standard_reduction_potentials", Integer.class))
                .isEqualTo(manifest.get("standardReductionPotentials").size());

        Set<String> manifestIds = StreamSupport.stream(manifest.get("standardReductionPotentials").spliterator(), false)
                .map(node -> node.get("recordId").asText())
                .collect(Collectors.toSet());
        Set<String> sqlIds = Set.copyOf(jdbcTemplate.queryForList("SELECT record_id FROM chemistry.standard_reduction_potentials", String.class));
        assertThat(sqlIds).isEqualTo(manifestIds);

        assertThat(java.util.Arrays.stream(chemistryFlyway.info().all())
                .filter(info -> info.getVersion() != null)
                .filter(info -> Integer.parseInt(info.getVersion().getVersion()) <= 37)
                .filter(info -> info.getState().isApplied())
                .count()).isGreaterThanOrEqualTo(37);
    }
}
