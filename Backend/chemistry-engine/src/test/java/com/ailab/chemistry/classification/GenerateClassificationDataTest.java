package com.ailab.chemistry.classification;

import com.ailab.chemistry.compound.TestElementMassProvider;
import com.ailab.chemistry.domain.classification.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class GenerateClassificationDataTest {

    @Test
    void generateAndVerifyClassificationDataFiles() throws IOException {
        List<ClassificationProfile> profiles = KnownClassificationRegistry.buildAll55Profiles(new TestElementMassProvider());
        assertThat(profiles).hasSize(55);

        ClassificationTaxonomy taxonomy = KnownClassificationRegistry.TAXONOMY;
        assertThat(taxonomy.getDefinitions()).hasSize(41);

        String json = buildJsonManifest(taxonomy, profiles);
        String sql = buildSqlMigration(taxonomy, profiles);

        File outputDir = new File("target/generated-classification");
        outputDir.mkdirs();

        File jsonFile = new File(outputDir, "chemical-classification-v1.json");
        try (FileWriter fw = new FileWriter(jsonFile)) {
            fw.write(json);
        }

        File sqlFile = new File(outputDir, "V11__seed_chemical_classification.sql");
        try (FileWriter fw = new FileWriter(sqlFile)) {
            fw.write(sql);
        }

        assertThat(jsonFile).exists();
        assertThat(sqlFile).exists();

        // Verify checked-in resources exist; never rewrite source files during verification.
        File resourceSql = new File("src/main/resources/db/migration/chemistry/V11__seed_chemical_classification.sql");
        File resourceJson = new File("src/main/resources/chemistry-data/chemical-classification-v1.json");

        assertThat(resourceSql).exists();
        assertThat(resourceJson).exists();
    }

    private String buildJsonManifest(ClassificationTaxonomy taxonomy, List<ClassificationProfile> profiles) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"version\": \"").append(taxonomy.getVersion().getVersion()).append("\",\n");
        sb.append("  \"name\": \"").append(taxonomy.getVersion().getName()).append("\",\n");
        sb.append("  \"publicationDate\": \"").append(taxonomy.getVersion().getPublicationDate()).append("\",\n");

        sb.append("  \"definitions\": [\n");
        for (int i = 0; i < taxonomy.getDefinitions().size(); i++) {
            ClassificationDefinition d = taxonomy.getDefinitions().get(i);
            sb.append("    {\n");
            sb.append("      \"code\": \"").append(d.getCode().getValue()).append("\",\n");
            sb.append("      \"dimension\": \"").append(d.getDimension().name()).append("\",\n");
            sb.append("      \"name\": \"").append(d.getName()).append("\",\n");
            sb.append("      \"description\": \"").append(d.getDescription()).append("\",\n");
            sb.append("      \"sortOrder\": ").append(d.getSortOrder()).append(",\n");
            sb.append("      \"parentCode\": ").append(d.getParentCode() == null ? "null" : "\"" + d.getParentCode().getValue() + "\"").append("\n");
            sb.append("    }").append(i < taxonomy.getDefinitions().size() - 1 ? "," : "").append("\n");
        }
        sb.append("  ],\n");

        sb.append("  \"profiles\": [\n");
        for (int i = 0; i < profiles.size(); i++) {
            ClassificationProfile p = profiles.get(i);
            sb.append("    {\n");
            sb.append("      \"compoundId\": \"").append(p.getCompoundId().getValue()).append("\",\n");
            sb.append("      \"assignments\": [\n");
            for (int j = 0; j < p.getAssignments().size(); j++) {
                ClassificationAssignment a = p.getAssignments().get(j);
                sb.append("        {\n");
                sb.append("          \"code\": \"").append(a.getCode().getValue()).append("\",\n");
                sb.append("          \"dimension\": \"").append(a.getDimension().name()).append("\",\n");
                sb.append("          \"basis\": \"").append(a.getBasis().name()).append("\",\n");
                sb.append("          \"evidenceStatus\": \"").append(a.getEvidenceStatus().name()).append("\"\n");
                sb.append("        }").append(j < p.getAssignments().size() - 1 ? "," : "").append("\n");
            }
            sb.append("      ]\n");
            sb.append("    }").append(i < profiles.size() - 1 ? "," : "").append("\n");
        }

        sb.append("  ]\n}\n");
        return sb.toString();
    }

    private String buildSqlMigration(ClassificationTaxonomy taxonomy, List<ClassificationProfile> profiles) {
        StringBuilder sql = new StringBuilder();
        sql.append("-- V11: Seed chemical classification taxonomy and compound profiles\n");
        sql.append("INSERT INTO chemistry.classification_taxonomy_versions (id, name, publication_date)\n");
        sql.append("VALUES ('").append(taxonomy.getVersion().getVersion()).append("', '")
           .append(taxonomy.getVersion().getName()).append("', '")
           .append(taxonomy.getVersion().getPublicationDate()).append("')\n");
        sql.append("ON CONFLICT (id) DO NOTHING;\n\n");

        for (ClassificationDefinition d : taxonomy.getDefinitions()) {
            UUID defId = UUID.nameUUIDFromBytes((taxonomy.getVersion().getVersion() + "_" + d.getCode().getValue()).getBytes());
            String parentSql = d.getParentCode() == null ? "NULL" : "'" + d.getParentCode().getValue() + "'";
            sql.append(String.format(
                    "INSERT INTO chemistry.classification_definitions (id, taxonomy_version_id, dimension, code, name, description, sort_order, parent_code)\n" +
                    "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', %d, %s) ON CONFLICT (code, taxonomy_version_id) DO NOTHING;\n",
                    defId, taxonomy.getVersion().getVersion(), d.getDimension().name(), d.getCode().getValue(),
                    d.getName().replace("'", "''"), d.getDescription().replace("'", "''"), d.getSortOrder(), parentSql
            ));
        }
        sql.append("\n");

        for (ClassificationProfile p : profiles) {
            UUID profileId = UUID.nameUUIDFromBytes(("profile_" + p.getCompoundId().getValue() + "_" + taxonomy.getVersion().getVersion()).getBytes());
            sql.append(String.format(
                    "INSERT INTO chemistry.compound_classification_profiles (id, compound_id, taxonomy_version_id)\n" +
                    "VALUES ('%s', '%s', '%s') ON CONFLICT (compound_id, taxonomy_version_id) DO NOTHING;\n",
                    profileId, p.getCompoundId().getValue(), taxonomy.getVersion().getVersion()
            ));

            for (ClassificationAssignment a : p.getAssignments()) {
                UUID assignId = UUID.nameUUIDFromBytes(("assign_" + profileId + "_" + a.getCode().getValue()).getBytes());
                String ruleSql = a.getRuleCode() == null ? "NULL" : "'" + a.getRuleCode().getCode() + "'";
                String srcIdSql = a.getProvenance() == null ? "NULL" : "'" + a.getProvenance().getSourceIdentifier().replace("'", "''") + "'";
                String srcTitleSql = a.getProvenance() == null ? "NULL" : "'" + a.getProvenance().getSourceTitle().replace("'", "''") + "'";
                String noteSql = a.getExplanatoryNote() == null ? "NULL" : "'" + a.getExplanatoryNote().replace("'", "''") + "'";

                sql.append(String.format(
                        "INSERT INTO chemistry.compound_classification_assignments (id, profile_id, dimension, code, basis, evidence_status, rule_code, source_identifier, source_title, explanatory_note)\n" +
                        "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', %s, %s, %s, %s) ON CONFLICT (profile_id, code) DO NOTHING;\n",
                        assignId, profileId, a.getDimension().name(), a.getCode().getValue(), a.getBasis().name(),
                        a.getEvidenceStatus().name(), ruleSql, srcIdSql, srcTitleSql, noteSql
                ));
            }
            sql.append("\n");
        }

        return sql.toString();
    }
}
