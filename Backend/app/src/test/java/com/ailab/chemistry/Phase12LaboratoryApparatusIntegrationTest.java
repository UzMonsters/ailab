package com.ailab.chemistry;

import com.ailab.chemistry.api.ContainerService;
import com.ailab.chemistry.api.EquipmentService;
import com.ailab.chemistry.api.LabEnvironmentService;
import com.ailab.chemistry.api.LaboratoryOperationService;
import com.ailab.chemistry.domain.container.ContainerErrorCode;
import com.ailab.chemistry.domain.container.ContainerProfileSuitabilityRequest;
import com.ailab.chemistry.domain.container.ContainerReferenceRepository;
import com.ailab.chemistry.domain.container.ContainerSuitabilityStatus;
import com.ailab.chemistry.domain.equipment.CalibrationRecord;
import com.ailab.chemistry.domain.equipment.EquipmentErrorCode;
import com.ailab.chemistry.domain.equipment.EquipmentProfileSuitabilityRequest;
import com.ailab.chemistry.domain.equipment.EquipmentReferenceRepository;
import com.ailab.chemistry.domain.equipment.EquipmentRequirement;
import com.ailab.chemistry.domain.equipment.EquipmentSuitabilityStatus;
import com.ailab.chemistry.domain.measurement.MeasurementResolution;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.PressureUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.measurement.Volume;
import com.ailab.chemistry.domain.measurement.VolumeUnit;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles({"test", "local"})
class Phase12LaboratoryApparatusIntegrationTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private ContainerService containerService;

    @Autowired
    private LabEnvironmentService labEnvironmentService;

    @Autowired
    private LaboratoryOperationService laboratoryOperationService;

    @Autowired
    private EquipmentReferenceRepository equipmentReferenceRepository;

    @Autowired
    private ContainerReferenceRepository containerReferenceRepository;

    @Autowired
    @org.springframework.beans.factory.annotation.Qualifier("chemistryFlyway")
    private Flyway chemistryFlyway;

    @org.junit.jupiter.api.BeforeEach
    void checkPostgres() {
        TestPostgresUtils.assumePostgresAvailable();
    }

    @Test
    void servicesInjectAndLatestMigrationSeedsPhaseTwelveReferenceData() throws Exception {
        assertThat(equipmentService).isNotNull();
        assertThat(containerService).isNotNull();
        assertThat(labEnvironmentService).isNotNull();
        assertThat(laboratoryOperationService).isNotNull();
        assertThat(Integer.parseInt(chemistryFlyway.info().current().getVersion().getVersion())).isGreaterThanOrEqualTo(43);

        assertThat(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chemistry.laboratory_dataset_versions WHERE dataset_id IN ('laboratory-equipment-reference-v1.0.0','laboratory-container-reference-v1.0.0','laboratory-equipment-reference-v1.1.0','laboratory-container-reference-v1.1.0')",
                Integer.class)).isEqualTo(4);
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM chemistry.equipment_reference_profiles WHERE is_active = TRUE", Integer.class))
                .isGreaterThanOrEqualTo(6);
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM chemistry.container_reference_profiles WHERE is_active = TRUE", Integer.class))
                .isGreaterThanOrEqualTo(5);
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM chemistry.container_compatibility_records WHERE compatibility_status = 'UNKNOWN'", Integer.class))
                .isZero();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode equipment = mapper.readTree(new ClassPathResource("chemistry-data/laboratory-equipment-reference-v1.json").getInputStream());
        JsonNode containers = mapper.readTree(new ClassPathResource("chemistry-data/laboratory-container-reference-v1.json").getInputStream());
        assertThat(equipment.get("datasetId").asText()).isEqualTo("laboratory-equipment-reference-v1.1.0");
        assertThat(containers.get("datasetId").asText()).isEqualTo("laboratory-container-reference-v1.1.0");

        Set<String> manifestEquipmentIds = StreamSupport.stream(equipment.get("profiles").spliterator(), false)
                .map(node -> node.get("profileId").asText()).collect(Collectors.toSet());
        Set<String> sqlEquipmentIds = Set.copyOf(jdbcTemplate.queryForList(
                "SELECT profile_id FROM chemistry.equipment_reference_profiles WHERE dataset_id = ? AND is_active = TRUE",
                String.class,
                equipment.get("datasetId").asText()));
        assertThat(sqlEquipmentIds).isEqualTo(manifestEquipmentIds);

        Set<String> manifestContainerIds = StreamSupport.stream(containers.get("profiles").spliterator(), false)
                .map(node -> node.get("profileId").asText()).collect(Collectors.toSet());
        Set<String> sqlContainerIds = Set.copyOf(jdbcTemplate.queryForList(
                "SELECT profile_id FROM chemistry.container_reference_profiles WHERE dataset_id = ? AND is_active = TRUE",
                String.class,
                containers.get("datasetId").asText()));
        assertThat(sqlContainerIds).isEqualTo(manifestContainerIds);
    }

    @Test
    void postgresEquipmentRepositoryBacksOperationalServiceEvaluation() {
        assertThat(equipmentReferenceRepository.getClass().getName()).contains("Jdbc");
        assertThat(equipmentReferenceRepository.findByProfileId("EQ-INACTIVE-BALANCE-FIXTURE")).isEmpty();

        var calibration = List.of(new CalibrationRecord("CALLER-CAL-1", Instant.parse("2026-08-01T00:00:00Z"), "caller supplied calibration certificate"));
        var massRequest = new EquipmentProfileSuitabilityRequest(
                "EQ-OHAUS-PX224-MASS",
                List.of(new EquipmentRequirement("MEASURE", "MASS", new BigDecimal("100"), "g", MeasurementResolution.of("0.001", "g"), true)),
                calibration,
                Instant.parse("2026-08-06T10:00:00Z"));
        assertThat(equipmentService.evaluate(massRequest).status()).isEqualTo(EquipmentSuitabilityStatus.SUITABLE);

        var overCapacity = new EquipmentProfileSuitabilityRequest(
                "EQ-OHAUS-PX224-MASS",
                List.of(new EquipmentRequirement("MEASURE", "MASS", new BigDecimal("221"), "g", MeasurementResolution.of("0.001", "g"), true)),
                calibration,
                Instant.parse("2026-08-06T10:00:00Z"));
        assertThat(equipmentService.evaluate(overCapacity).errorCodes()).contains(EquipmentErrorCode.VALUE_OUTSIDE_OPERATING_RANGE);

        var tooPrecise = new EquipmentProfileSuitabilityRequest(
                "EQ-OHAUS-PX224-MASS",
                List.of(new EquipmentRequirement("MEASURE", "MASS", new BigDecimal("1"), "g", MeasurementResolution.of("0.00001", "g"), true)),
                calibration,
                Instant.parse("2026-08-06T10:00:00Z"));
        assertThat(equipmentService.evaluate(tooPrecise).errorCodes()).contains(EquipmentErrorCode.INSUFFICIENT_RESOLUTION);

        var temperature = new EquipmentProfileSuitabilityRequest(
                "EQ-THERMO-ORION-A211-PH-METER",
                List.of(new EquipmentRequirement("MEASURE", "TEMPERATURE", new BigDecimal("25"), "degC", MeasurementResolution.of("0.5", "degC"), true)),
                calibration,
                Instant.parse("2026-08-06T10:00:00Z"));
        assertThat(equipmentService.evaluate(temperature).status()).isIn(EquipmentSuitabilityStatus.SUITABLE, EquipmentSuitabilityStatus.SUITABLE_WITH_WARNINGS);

        var volumetric = new EquipmentProfileSuitabilityRequest(
                "EQ-DWK-KIMAX-28014B-100-VOLUMETRIC",
                List.of(new EquipmentRequirement("MEASURE", "VOLUME", new BigDecimal("100"), "mL", MeasurementResolution.of("0.08", "mL"), true)),
                List.of(),
                Instant.parse("2026-08-06T10:00:00Z"));
        assertThat(equipmentService.evaluate(volumetric).status()).isEqualTo(EquipmentSuitabilityStatus.SUITABLE);

        var ph = new EquipmentProfileSuitabilityRequest(
                "EQ-THERMO-ORION-A211-PH-METER",
                List.of(new EquipmentRequirement("MEASURE", "PH", new BigDecimal("7.01"), "pH", MeasurementResolution.of("0.01", "pH"), true)),
                calibration,
                Instant.parse("2026-08-06T10:00:00Z"));
        assertThat(equipmentService.evaluate(ph).status()).isIn(EquipmentSuitabilityStatus.SUITABLE, EquipmentSuitabilityStatus.SUITABLE_WITH_WARNINGS);

        var heat = new EquipmentProfileSuitabilityRequest(
                "EQ-IKA-CMAG-HS7",
                List.of(new EquipmentRequirement("HEAT", "TEMPERATURE", new BigDecimal("80"), "degC", null, false)),
                List.of(),
                Instant.parse("2026-08-06T10:00:00Z"));
        assertThat(equipmentService.evaluate(heat).status()).isEqualTo(EquipmentSuitabilityStatus.SUITABLE);

        var missing = new EquipmentProfileSuitabilityRequest(
                "EQ-NOT-SEEDED",
                List.of(new EquipmentRequirement("MEASURE", "MASS", BigDecimal.ONE, "g", null, false)),
                List.of(),
                Instant.parse("2026-08-06T10:00:00Z"));
        assertThat(equipmentService.evaluate(missing).errorCodes()).contains(EquipmentErrorCode.PROFILE_UNAVAILABLE);
    }

    @Test
    void postgresContainerRepositoryBacksOperationalServiceEvaluation() {
        assertThat(containerReferenceRepository.getClass().getName()).contains("Jdbc");
        assertThat(containerReferenceRepository.findByProfileId("CON-INACTIVE-FIXTURE")).isEmpty();

        var compatible = new ContainerProfileSuitabilityRequest(
                "CON-DWK-KIMAX-28014B-100-VOLUMETRIC",
                Volume.of("80", VolumeUnit.MILLILITER),
                false,
                Temperature.of("20", TemperatureUnit.CELSIUS),
                null,
                Volume.of("10", VolumeUnit.MILLILITER),
                "COMP-H2O",
                "AQUEOUS",
                null,
                null);
        assertThat(containerService.evaluate(compatible).status()).isEqualTo(ContainerSuitabilityStatus.SUITABLE);

        var headspace = new ContainerProfileSuitabilityRequest(
                "CON-DWK-KIMAX-28014B-100-VOLUMETRIC",
                Volume.of("95", VolumeUnit.MILLILITER),
                true,
                Temperature.of("20", TemperatureUnit.CELSIUS),
                null,
                Volume.of("10", VolumeUnit.MILLILITER),
                "COMP-H2O",
                "AQUEOUS",
                null,
                null);
        assertThat(containerService.evaluate(headspace).errorCodes()).contains(ContainerErrorCode.INSUFFICIENT_HEADSPACE);

        var pressure = new ContainerProfileSuitabilityRequest(
                "CON-DWK-KIMAX-28014B-100-VOLUMETRIC",
                Volume.of("80", VolumeUnit.MILLILITER),
                false,
                null,
                Pressure.of("1.2", PressureUnit.BAR),
                null,
                "COMP-H2O",
                "AQUEOUS",
                null,
                null);
        assertThat(containerService.evaluate(pressure).errorCodes()).contains(ContainerErrorCode.OPEN_CONTAINER_NOT_PRESSURE_RATED);

        var incompatible = new ContainerProfileSuitabilityRequest(
                "CON-HDPE-NARROW-MOUTH-500",
                Volume.of("100", VolumeUnit.MILLILITER),
                false,
                Temperature.of("20", TemperatureUnit.CELSIUS),
                null,
                null,
                "FAMILY-AROMATIC-HYDROCARBONS",
                "LIQUID",
                null,
                null);
        assertThat(containerService.evaluate(incompatible).errorCodes()).contains(ContainerErrorCode.INCOMPATIBLE_MATERIAL);

        var missing = new ContainerProfileSuitabilityRequest(
                "CON-NOT-SEEDED",
                Volume.of("1", VolumeUnit.MILLILITER),
                false,
                null,
                null,
                null,
                "COMP-H2O",
                "AQUEOUS",
                null,
                null);
        assertThat(containerService.evaluate(missing).errorCodes()).contains(ContainerErrorCode.PROFILE_UNAVAILABLE);
    }
}
