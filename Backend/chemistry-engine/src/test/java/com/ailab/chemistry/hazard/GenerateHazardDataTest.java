package com.ailab.chemistry.hazard;

import com.ailab.chemistry.compound.TestElementMassProvider;
import com.ailab.chemistry.domain.hazard.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class GenerateHazardDataTest {

    @Test
    void generateAndVerifyHazardDataFiles() throws IOException {
        List<HazardProfile> profiles = KnownHazardRegistry.buildAll55Profiles(new TestElementMassProvider());
        assertThat(profiles).hasSize(55);

        String json = buildJsonManifest(profiles);
        String ghsRefJson = buildGhsRefJson();
        String sql = buildSqlMigration(profiles);

        File outputDir = new File("target/generated-hazards");
        outputDir.mkdirs();

        File jsonFile = new File(outputDir, "compound-hazards-v1.1.0.json");
        try (FileWriter fw = new FileWriter(jsonFile)) {
            fw.write(json);
        }

        File ghsRefFile = new File(outputDir, "ghs-rev11-reference.json");
        try (FileWriter fw = new FileWriter(ghsRefFile)) {
            fw.write(ghsRefJson);
        }

        File sqlFile = new File(outputDir, "V15__seed_hazard_reference_catalogue.sql");
        try (FileWriter fw = new FileWriter(sqlFile)) {
            fw.write(sql);
        }

        assertThat(jsonFile).exists();
        assertThat(ghsRefFile).exists();
        assertThat(sqlFile).exists();

        // Verify checked-in resources exist; never rewrite source files during verification.
        File resourceSql = new File("src/main/resources/db/migration/chemistry/V15__seed_hazard_reference_catalogue.sql");
        File resourceJson = new File("src/main/resources/chemistry-data/compound-hazards-v1.1.0.json");
        File resourceJsonV1 = new File("src/main/resources/chemistry-data/compound-hazards-v1.json");
        File resourceGhs = new File("src/main/resources/chemistry-data/ghs-rev11-reference.json");

        assertThat(resourceSql).exists();
        assertThat(resourceJson).exists();
        assertThat(resourceJsonV1).exists();
        assertThat(resourceGhs).exists();
    }

    private String buildJsonManifest(List<HazardProfile> profiles) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"version\": \"").append(KnownHazardRegistry.DATASET_VERSION).append("\",\n");
        sb.append("  \"profiles\": [\n");

        for (int i = 0; i < profiles.size(); i++) {
            HazardProfile p = profiles.get(i);
            sb.append("    {\n");
            sb.append("      \"compoundId\": \"").append(p.getCompoundId().getValue()).append("\",\n");
            sb.append("      \"availability\": {\n");

            int j = 0;
            int availSize = p.getAvailabilityMap().size();
            for (Map.Entry<String, HazardAvailability> entry : p.getAvailabilityMap().entrySet()) {
                sb.append("        \"").append(entry.getKey()).append("\": \"").append(entry.getValue().name()).append("\"")
                  .append(j < availSize - 1 ? "," : "").append("\n");
                j++;
            }

            sb.append("      },\n");
            sb.append("      \"summaryFlags\": [");
            int k = 0;
            int flagSize = p.getSummaryFlags().size();
            for (HazardSummaryFlag flag : p.getSummaryFlags()) {
                sb.append("\"").append(flag.name()).append("\"").append(k < flagSize - 1 ? ", " : "");
                k++;
            }
            sb.append("]\n");

            sb.append("    }").append(i < profiles.size() - 1 ? "," : "").append("\n");
        }

        sb.append("  ]\n}\n");
        return sb.toString();
    }

    private String buildGhsRefJson() {
        return """
        {
          "systemCode": "UN_GHS",
          "revisionNumber": 11,
          "publicationYear": 2025,
          "publisher": "United Nations",
          "sourceIdentifier": "UN-GHS-REV11-2025",
          "status": "ACTIVE",
          "pictograms": [
            {"code": "GHS01", "name": "Exploding Bomb"},
            {"code": "GHS02", "name": "Flame"},
            {"code": "GHS03", "name": "Flame Over Circle"},
            {"code": "GHS04", "name": "Gas Cylinder"},
            {"code": "GHS05", "name": "Corrosion"},
            {"code": "GHS06", "name": "Skull and Crossbones"},
            {"code": "GHS07", "name": "Exclamation Mark"},
            {"code": "GHS08", "name": "Health Hazard"},
            {"code": "GHS09", "name": "Environment"}
          ]
        }
        """;
    }

    private String buildSqlMigration(List<HazardProfile> profiles) {
        StringBuilder sql = new StringBuilder();
        sql.append("-- V15: Seed hazard reference catalogue\n");
        sql.append("INSERT INTO chemistry.hazard_dataset_versions (id, name, publication_date)\n");
        sql.append("VALUES ('").append(KnownHazardRegistry.DATASET_VERSION).append("', 'UN GHS Revision 11 Reference Hazards', '2026-08-05')\n");
        sql.append("ON CONFLICT (id) DO NOTHING;\n\n");

        sql.append("INSERT INTO chemistry.hazard_source_documents (id, document_type, issuer_or_supplier, document_title, classification_system, revision_or_edition, jurisdiction)\n");
        sql.append("VALUES ('UN-GHS-REV11-2025', 'AUTHORITATIVE_CLASSIFICATION', 'United Nations', 'GHS Revision 11', 'UN_GHS', '11th Revised Edition', 'INTERNATIONAL_REFERENCE')\n");
        sql.append("ON CONFLICT (id) DO NOTHING;\n\n");

        for (HazardProfile p : profiles) {
            UUID profileId = UUID.nameUUIDFromBytes(("hazard_profile_" + p.getCompoundId().getValue() + "_" + KnownHazardRegistry.DATASET_VERSION).getBytes());
            sql.append(String.format(
                    "INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)\n" +
                    "VALUES ('%s', '%s', '%s') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;\n",
                    profileId, p.getCompoundId().getValue(), KnownHazardRegistry.DATASET_VERSION
            ));

            for (Map.Entry<String, HazardAvailability> entry : p.getAvailabilityMap().entrySet()) {
                UUID availId = UUID.nameUUIDFromBytes(("hazard_avail_" + profileId + "_" + entry.getKey()).getBytes());
                sql.append(String.format(
                        "INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)\n" +
                        "VALUES ('%s', '%s', '%s', '%s') ON CONFLICT (profile_id, classification_system) DO NOTHING;\n",
                        availId, profileId, entry.getKey(), entry.getValue().name()
                ));
            }
            sql.append("\n");
        }

        return sql.toString();
    }
}
