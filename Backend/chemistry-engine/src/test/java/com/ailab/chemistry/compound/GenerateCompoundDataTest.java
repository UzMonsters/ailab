package com.ailab.chemistry.compound;

import com.ailab.chemistry.domain.compound.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GenerateCompoundDataTest {

    @Test
    void generateAndVerifyCompoundDataFiles(@TempDir Path tempDir) throws IOException {
        List<Compound> compounds = KnownCompoundRegistry.buildAll55CoreCompounds(new TestElementMassProvider());
        assertThat(compounds).hasSize(55);

        // 1. Build JSON manifest
        String json = buildJsonManifest(compounds);

        // 2. Build SQL migration
        String sql = buildSqlMigration(compounds);

        File outputDir = new File("target/generated-compound-core");
        outputDir.mkdirs();

        File jsonFile = new File(outputDir, "compound-core-v1.json");
        try (FileWriter fw = new FileWriter(jsonFile)) {
            fw.write(json);
        }

        File sqlFile = new File(outputDir, "V8__seed_compound_core_catalogue.sql");
        try (FileWriter fw = new FileWriter(sqlFile)) {
            fw.write(sql);
        }

        assertThat(jsonFile).exists();
        assertThat(sqlFile).exists();

        // 3. Verify checked-in resources match if they exist
        File checkedInSql = new File("src/main/resources/db/migration/chemistry/V8__seed_compound_core_catalogue.sql");
        File checkedInJson = new File("src/main/resources/chemistry-data/compound-core-v1.json");

        if (checkedInSql.exists()) {
            String existingSql = Files.readString(checkedInSql.toPath()).replace("\r\n", "\n");
            assertThat(sql.replace("\r\n", "\n")).isEqualTo(existingSql);
        }

        if (checkedInJson.exists()) {
            String existingJson = Files.readString(checkedInJson.toPath()).replace("\r\n", "\n");
            assertThat(json.replace("\r\n", "\n")).isEqualTo(existingJson);
        }
    }

    private String buildJsonManifest(List<Compound> compounds) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"version\": \"").append(KnownCompoundRegistry.COMPOUND_DATASET_VERSION).append("\",\n");
        sb.append("  \"description\": \"").append(KnownCompoundRegistry.COMPOUND_DATASET_NAME).append("\",\n");
        sb.append("  \"publicationDate\": \"").append(KnownCompoundRegistry.COMPOUND_DATASET_DATE).append("\",\n");
        sb.append("  \"compounds\": [\n");

        for (int i = 0; i < compounds.size(); i++) {
            Compound c = compounds.get(i);
            sb.append("    {\n");
            sb.append("      \"id\": \"").append(c.getId().getValue()).append("\",\n");
            sb.append("      \"compoundCode\": \"").append(c.getCode().getValue()).append("\",\n");
            sb.append("      \"primaryName\": \"").append(c.getPrimaryName()).append("\",\n");
            sb.append("      \"originalFormula\": \"").append(c.getFormula().getOriginalFormula()).append("\",\n");
            sb.append("      \"normalizedFormula\": \"").append(c.getFormula().getNormalizedFormula()).append("\",\n");
            sb.append("      \"compositionFormula\": \"").append(c.getFormula().getCompositionFormula()).append("\",\n");
            sb.append("      \"netCharge\": ").append(c.getNetCharge().getValue()).append(",\n");
            sb.append("      \"molarMass\": {\n");
            sb.append("        \"representativeValue\": \"").append(c.getMolarMass().getRepresentativeValue().toPlainString()).append("\",\n");
            sb.append("        \"lowerBound\": ").append(c.getMolarMass().getLowerBound() == null ? "null" : "\"" + c.getMolarMass().getLowerBound().toPlainString() + "\"").append(",\n");
            sb.append("        \"upperBound\": ").append(c.getMolarMass().getUpperBound() == null ? "null" : "\"" + c.getMolarMass().getUpperBound().toPlainString() + "\"").append(",\n");
            sb.append("        \"kind\": \"").append(c.getMolarMass().getKind().name()).append("\"\n");
            sb.append("      }\n");
            sb.append("    }").append(i < compounds.size() - 1 ? "," : "").append("\n");
        }

        sb.append("  ]\n}\n");
        return sb.toString();
    }

    private String buildSqlMigration(List<Compound> compounds) {
        StringBuilder sql = new StringBuilder();
        sql.append("-- V8: Seed core educational compound catalogue\n");
        sql.append("-- Dataset Version: ").append(KnownCompoundRegistry.COMPOUND_DATASET_VERSION).append("\n\n");

        sql.append("INSERT INTO chemistry.compound_catalog_versions (id, name, publication_date)\n");
        sql.append("VALUES ('").append(KnownCompoundRegistry.COMPOUND_DATASET_VERSION).append("', '")
           .append(KnownCompoundRegistry.COMPOUND_DATASET_NAME).append("', '")
           .append(KnownCompoundRegistry.COMPOUND_DATASET_DATE).append("')\n");
        sql.append("ON CONFLICT (id) DO NOTHING;\n\n");

        for (Compound c : compounds) {
            String lowerBoundSql = c.getMolarMass().getLowerBound() == null ? "NULL" : c.getMolarMass().getLowerBound().toPlainString();
            String upperBoundSql = c.getMolarMass().getUpperBound() == null ? "NULL" : c.getMolarMass().getUpperBound().toPlainString();
            String hydrateSql = c.getFormula().getHydrateInfo() == null ? "NULL" : "'" + c.getFormula().getHydrateInfo() + "'";

            sql.append(String.format(
                    "INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)\n" +
                    "VALUES ('%s', '%s', '%s', '%s', '%s', %d, %s, %s, %s, %s, '%s', '%s', '%s', '%s', '%s')\n" +
                    "ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;\n",
                    c.getId().getValue(),
                    c.getCode().getValue(),
                    c.getPrimaryName().replace("'", "''"),
                    c.getFormula().getOriginalFormula().replace("'", "''"),
                    c.getFormula().getNormalizedFormula().replace("'", "''"),
                    c.getNetCharge().getValue(),
                    hydrateSql,
                    c.getMolarMass().getRepresentativeValue().toPlainString(),
                    lowerBoundSql,
                    upperBoundSql,
                    c.getMolarMass().getKind().name(),
                    c.getMolarMass().getCalculationBasis().getElementDatasetVersion(),
                    KnownCompoundRegistry.COMPOUND_DATASET_VERSION,
                    c.getProvenance().getSourceIdentifier(),
                    c.getProvenance().getSourceTitle().replace("'", "''")
            ));

            // Aliases
            for (CompoundAlias alias : c.getAliases()) {
                String aliasId = UUID.nameUUIDFromBytes(("alias-" + c.getCode().getValue() + "-" + alias.getName()).getBytes()).toString();
                sql.append(String.format(
                        "INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)\n" +
                        "VALUES ('%s', '%s', '%s', '%s') ON CONFLICT DO NOTHING;\n",
                        aliasId, c.getId().getValue(), alias.getName().replace("'", "''"), alias.getRole().name()
                ));
            }

            // Components
            for (CompoundElementCount count : c.getComposition().getElementCounts()) {
                String compId = UUID.nameUUIDFromBytes(("component-" + c.getCode().getValue() + "-" + count.getSymbol()).getBytes()).toString();
                String elementId = UUID.nameUUIDFromBytes((count.getSymbol() + "_" + count.getAtomicNumber()).getBytes()).toString();
                sql.append(String.format(
                        "INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)\n" +
                        "VALUES ('%s', '%s', '%s', %d, '%s', %s) ON CONFLICT DO NOTHING;\n",
                        compId, c.getId().getValue(), elementId, count.getAtomicNumber(), count.getSymbol(), count.getAtomCount().toString()
                ));
            }

            // External identifiers
            for (CompoundExternalIdentifier extId : c.getExternalIdentifiers()) {
                String extIdUuid = UUID.nameUUIDFromBytes(("extid-" + c.getCode().getValue() + "-" + extId.getScheme().name()).getBytes()).toString();
                sql.append(String.format(
                        "INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)\n" +
                        "VALUES ('%s', '%s', '%s', '%s') ON CONFLICT DO NOTHING;\n",
                        extIdUuid, c.getId().getValue(), extId.getScheme().name(), extId.getValue().replace("'", "''")
                ));
            }
            sql.append("\n");
        }

        return sql.toString();
    }
}
