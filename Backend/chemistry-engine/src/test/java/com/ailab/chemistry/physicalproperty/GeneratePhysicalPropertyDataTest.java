package com.ailab.chemistry.physicalproperty;

import com.ailab.chemistry.compound.TestElementMassProvider;
import com.ailab.chemistry.domain.physicalproperty.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class GeneratePhysicalPropertyDataTest {

    @Test
    void generateAndVerifyPhysicalPropertyDataFiles() throws IOException {
        List<CompoundPhysicalPropertyProfile> profiles = KnownCompoundPhysicalPropertyRegistry.buildAll55Profiles(new TestElementMassProvider());
        assertThat(profiles).hasSize(55);

        String json = buildJsonManifest(profiles);
        String sql = buildSqlMigration(profiles);

        File outputDir = new File("target/generated-physical-properties");
        outputDir.mkdirs();

        File jsonFile = new File(outputDir, "compound-physical-properties-v1.json");
        try (FileWriter fw = new FileWriter(jsonFile)) {
            fw.write(json);
        }

        File sqlFile = new File(outputDir, "V13__seed_compound_physical_properties.sql");
        try (FileWriter fw = new FileWriter(sqlFile)) {
            fw.write(sql);
        }

        assertThat(jsonFile).exists();
        assertThat(sqlFile).exists();

        // Verify checked-in resources exist; never rewrite source files during verification.
        File resourceSql = new File("src/main/resources/db/migration/chemistry/V13__seed_compound_physical_properties.sql");
        File resourceJson = new File("src/main/resources/chemistry-data/compound-physical-properties-v1.json");

        assertThat(resourceSql).exists();
        assertThat(resourceJson).exists();
    }

    private String buildJsonManifest(List<CompoundPhysicalPropertyProfile> profiles) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"version\": \"").append(KnownCompoundPhysicalPropertyRegistry.DATASET_VERSION).append("\",\n");
        sb.append("  \"profiles\": [\n");

        for (int i = 0; i < profiles.size(); i++) {
            CompoundPhysicalPropertyProfile p = profiles.get(i);
            sb.append("    {\n");
            sb.append("      \"compoundId\": \"").append(p.getCompoundId().getValue()).append("\",\n");
            sb.append("      \"availability\": {\n");

            int j = 0;
            int availSize = p.getAvailabilityMap().size();
            for (Map.Entry<PhysicalPropertyType, PropertyAvailability> entry : p.getAvailabilityMap().entrySet()) {
                sb.append("        \"").append(entry.getKey().name()).append("\": \"").append(entry.getValue().name()).append("\"")
                  .append(j < availSize - 1 ? "," : "").append("\n");
                j++;
            }

            sb.append("      }\n");
            sb.append("    }").append(i < profiles.size() - 1 ? "," : "").append("\n");
        }

        sb.append("  ]\n}\n");
        return sb.toString();
    }

    private String buildSqlMigration(List<CompoundPhysicalPropertyProfile> profiles) {
        StringBuilder sql = new StringBuilder();
        sql.append("-- V13: Seed compound physical properties\n");
        sql.append("INSERT INTO chemistry.compound_physical_property_dataset_versions (id, name, publication_date)\n");
        sql.append("VALUES ('").append(KnownCompoundPhysicalPropertyRegistry.DATASET_VERSION).append("', 'CRC Handbook 104th Edition Compound Physical Properties', '2026-08-05')\n");
        sql.append("ON CONFLICT (id) DO NOTHING;\n\n");

        for (CompoundPhysicalPropertyProfile p : profiles) {
            UUID profileId = UUID.nameUUIDFromBytes(("prop_profile_" + p.getCompoundId().getValue() + "_" + KnownCompoundPhysicalPropertyRegistry.DATASET_VERSION).getBytes());
            sql.append(String.format(
                    "INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)\n" +
                    "VALUES ('%s', '%s', '%s') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;\n",
                    profileId, p.getCompoundId().getValue(), KnownCompoundPhysicalPropertyRegistry.DATASET_VERSION
            ));

            for (Map.Entry<PhysicalPropertyType, PropertyAvailability> entry : p.getAvailabilityMap().entrySet()) {
                UUID availId = UUID.nameUUIDFromBytes(("avail_" + profileId + "_" + entry.getKey().name()).getBytes());
                sql.append(String.format(
                        "INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)\n" +
                        "VALUES ('%s', '%s', '%s', '%s') ON CONFLICT (profile_id, property_type) DO NOTHING;\n",
                        availId, profileId, entry.getKey().name(), entry.getValue().name()
                ));
            }
            sql.append("\n");
        }

        return sql.toString();
    }
}
