package com.ailab.chemistry.service;

import com.ailab.chemistry.api.ThermodynamicReferenceService;
import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.PressureUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.thermodynamics.ThermodynamicErrorCode;
import com.ailab.chemistry.domain.thermodynamics.ThermodynamicException;
import com.ailab.chemistry.domain.thermodynamics.ThermodynamicPropertyType;
import com.ailab.chemistry.infrastructure.persistence.thermodynamics.InMemoryThermodynamicReferenceRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ThermodynamicReferenceServiceTest {

    private static final Temperature T25 = Temperature.of("25.0", TemperatureUnit.CELSIUS);
    private static final Pressure P1BAR = Pressure.of("1.000", PressureUnit.BAR);

    private final ThermodynamicReferenceService service =
            new ThermodynamicReferenceServiceImpl(new InMemoryThermodynamicReferenceRepository());

    @Test
    void returnsProfileAndExactConditionPropertiesFromInMemoryRepository() {
        var profile = service.getProfile("COMP-H2O");
        var exact = service.findExact("COMP-H2O", ThermodynamicPropertyType.STANDARD_GIBBS_ENERGY_OF_FORMATION,
                MatterState.LIQUID, T25, P1BAR).orElseThrow();

        assertThat(profile.compoundCode()).isEqualTo("COMP-H2O");
        assertThat(profile.properties()).hasSizeGreaterThanOrEqualTo(6);
        assertThat(exact.value()).isEqualByComparingTo(new BigDecimal("-237.129"));
        assertThat(exact.unitSymbol()).isEqualTo("kJ/mol");
        assertThat(exact.conditions().state()).isEqualTo(MatterState.LIQUID);
        assertThat(exact.provenance().sourceIdentifier()).isEqualTo("NIST-WEBBOOK");
    }

    @Test
    void filtersByTypeAndDoesNotInventMissingData() {
        var entropyRecords = service.findProperties("COMP-H2O", ThermodynamicPropertyType.STANDARD_MOLAR_ENTROPY);
        var missing = service.findExact("COMP-CH4", ThermodynamicPropertyType.STANDARD_ENTHALPY_OF_FORMATION,
                MatterState.LIQUID, T25, P1BAR);

        assertThat(entropyRecords).extracting("type")
                .containsOnly(ThermodynamicPropertyType.STANDARD_MOLAR_ENTROPY);
        assertThat(missing).isEmpty();
    }

    @Test
    void rejectsUnknownCompoundProfileLookup() {
        assertThatThrownBy(() -> service.getProfile("COMP-NOT-PRESENT"))
                .isInstanceOf(ThermodynamicException.class)
                .extracting("errorCode")
                .isEqualTo(ThermodynamicErrorCode.MISSING_PROFILE);
    }

    @Test
    void checkedInManifestMatchesRepositoryCoverageAndCounts() throws IOException {
        var repository = new InMemoryThermodynamicReferenceRepository();
        JsonNode manifest = new ObjectMapper().readTree(
                getClass().getResourceAsStream("/chemistry-data/thermodynamic-reference-v1.json"));

        long recordCount = repository.findAllProfiles().stream()
                .flatMap(profile -> profile.records().stream())
                .count();
        long enthalpyCount = repository.findAllProfiles().stream()
                .flatMap(profile -> profile.records().stream())
                .filter(record -> record.type() == ThermodynamicPropertyType.STANDARD_ENTHALPY_OF_FORMATION)
                .count();

        assertThat(manifest.get("datasetVersion").asText()).isEqualTo(InMemoryThermodynamicReferenceRepository.DATASET_VERSION);
        assertThat(manifest.get("profileCount").asInt()).isEqualTo(repository.findAllProfiles().size());
        assertThat(manifest.get("propertyRecordCount").asInt()).isEqualTo((int) recordCount);
        assertThat(manifest.get("propertyCounts").get("STANDARD_ENTHALPY_OF_FORMATION").asInt()).isEqualTo((int) enthalpyCount);
    }
}
