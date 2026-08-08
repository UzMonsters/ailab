package com.ailab.chemistry.element;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import com.ailab.chemistry.domain.element.*;

import static org.assertj.core.api.Assertions.assertThat;

class ElementCatalogSeedValidationTest {

    /**
     * Validate that the bundled periodic-table-core-v1.1.0.json satisfies all
     * ElementCatalogValidator rules including the Bismuth correction.
     */
    @Test
    void testSeedCatalogMatchesValidationRules() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getResourceAsStream("/chemistry-data/periodic-table-core-v1.1.0.json");
        assertThat(is).withFailMessage(
                "Chemistry seed JSON not found at classpath:/chemistry-data/periodic-table-core-v1.1.0.json. " +
                "Run GenerateElementDataTest.generateDataFilesToTargetDirectory() and copy the file to " +
                "chemistry-engine/src/test/resources/chemistry-data/"
        ).isNotNull();

        JsonNode root = mapper.readTree(is);
        String version = root.get("datasetVersion").asText();
        assertThat(version).isEqualTo("v1.1.0");

        JsonNode records = root.get("elementRecords");
        assertThat(records.isArray()).isTrue();
        assertThat(records.size()).isEqualTo(118);

        List<Element> elements = new ArrayList<>();
        for (JsonNode node : records) {
            int num = node.get("atomicNumber").asInt();
            String symbol = node.get("symbol").asText();
            String name = node.get("name").asText();
            String latinName = node.has("latinName") && !node.get("latinName").isNull() ? node.get("latinName").asText() : null;

            JsonNode massNode = node.get("atomicMass");
            BigDecimal repVal = new BigDecimal(massNode.get("representativeValue").asText());
            AtomicMassKind kind = AtomicMassKind.valueOf(massNode.get("kind").asText());
            BigDecimal lower = massNode.has("lowerBound") && !massNode.get("lowerBound").isNull() ? new BigDecimal(massNode.get("lowerBound").asText()) : null;
            BigDecimal upper = massNode.has("upperBound") && !massNode.get("upperBound").isNull() ? new BigDecimal(massNode.get("upperBound").asText()) : null;
            AtomicMass atomicMass = new AtomicMass(repVal, kind, lower, upper);

            int period = node.get("period").asInt();
            Integer group = node.has("group") && !node.get("group").isNull() ? node.get("group").asInt() : null;
            ElementBlock block = ElementBlock.valueOf(node.get("block").asText());
            String config = node.get("electronConfiguration").asText();
            ElectronConfigurationStatus configStatus = ElectronConfigurationStatus.valueOf(node.get("electronConfigurationStatus").asText());
            StandardState state = StandardState.valueOf(node.get("standardState").asText());
            RadioactivityStatus rad = RadioactivityStatus.valueOf(node.get("radioactivityStatus").asText());
            ElementCategory category = ElementCategory.valueOf(node.get("category").asText());
            ElementSeries series = ElementSeries.valueOf(node.get("series").asText());

            Element element = new Element(
                    ElementId.generate(),
                    num, symbol, name, latinName,
                    atomicMass,
                    period, group, block, config, configStatus,
                    state, rad, category, series,
                    version, "IUPAC/NIST Reference"
            );
            elements.add(element);
        }

        // Full catalogue validation including Bismuth check
        ElementCatalogValidator.validate(elements, "v1.1.0");

        // Spot-check: Bismuth must be PRIMORDIAL_RADIOACTIVE
        Element bismuth = elements.stream()
                .filter(e -> e.getAtomicNumber() == 83)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Bismuth (Z=83) not found in catalogue"));
        assertThat(bismuth.getRadioactivityStatus()).isEqualTo(RadioactivityStatus.PRIMORDIAL_RADIOACTIVE);
    }
}
