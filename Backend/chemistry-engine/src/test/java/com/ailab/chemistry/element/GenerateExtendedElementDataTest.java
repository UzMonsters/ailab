package com.ailab.chemistry.element;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import com.ailab.chemistry.domain.element.KnownElementRecord;
import com.ailab.chemistry.domain.element.KnownElementRegistry;
import com.ailab.chemistry.domain.element.StandardState;
import com.ailab.chemistry.domain.element.property.*;
import com.ailab.chemistry.domain.measurement.*;

import static org.assertj.core.api.Assertions.assertThat;

class GenerateExtendedElementDataTest {

    private static final String DATASET_VERSION = "extended-properties-v1.0.0";
    private static final String SOURCE_ID_CRC = "CRC-HANDBOOK-104";
    private static final String SOURCE_TITLE_CRC = "CRC Handbook of Chemistry and Physics, 104th Edition";
    private static final String SOURCE_ID_NIST = "NIST-ASD-2024";
    private static final String SOURCE_TITLE_NIST = "NIST Atomic Spectra Database Data Elements";
    private static final String SOURCE_ID_IUPAC = "IUPAC-GOLD-BOOK";
    private static final String SOURCE_TITLE_IUPAC = "IUPAC Compendium of Chemical Terminology";

    private final PropertyProvenance defaultProvenance = new PropertyProvenance(
            SOURCE_ID_CRC,
            SOURCE_TITLE_CRC,
            "CRC Press",
            "104th Ed. (2023-2024)",
            "2026-08-04",
            "Extended element physical and atomic properties",
            "Open scientific reference data"
    );

    @Test
    void generateAndVerifyExtendedElementData() throws Exception {
        List<ElementPropertyProfile> profiles = buildAll118Profiles();

        assertThat(profiles).hasSize(118);

        // Verify dataset constraints
        Set<Integer> atomicNumbers = new HashSet<>();
        for (ElementPropertyProfile profile : profiles) {
            assertThat(profile.getAtomicNumber()).isBetween(1, 118);
            assertThat(atomicNumbers.add(profile.getAtomicNumber()))
                    .as("Duplicate atomic number " + profile.getAtomicNumber())
                    .isTrue();
            assertThat(KnownElementRegistry.getByAtomicNumber(profile.getAtomicNumber()).symbol())
                    .isEqualTo(profile.getSymbol());
        }

        // Generate target output files
        Path outputDir = Path.of("target", "generated-extended-properties");
        Files.createDirectories(outputDir);

        Path jsonFile = outputDir.resolve("periodic-table-extended-properties-v1.json");
        Path sqlFile = outputDir.resolve("V6__seed_extended_element_properties.sql");

        String sqlContent = buildSqlMigration(profiles);
        Files.writeString(sqlFile, sqlContent);

        String jsonContent = buildJsonManifest(profiles);
        Files.writeString(jsonFile, jsonContent);

        // Verify checked-in V6 migration matches if it exists
        Path checkedInSql = Path.of("src", "main", "resources", "db", "migration", "chemistry", "V6__seed_extended_element_properties.sql");
        if (Files.exists(checkedInSql)) {
            String existingSql = Files.readString(checkedInSql);
            assertThat(sqlContent.trim()).isEqualTo(existingSql.trim());
        }
        Path checkedInJson = Path.of("src", "main", "resources", "chemistry-data", "periodic-table-extended-properties-v1.json");
        if (Files.exists(checkedInJson)) {
            String existingJson = Files.readString(checkedInJson);
            assertThat(jsonContent.trim()).isEqualTo(existingJson.trim());
        }
    }

    public List<ElementPropertyProfile> buildAll118Profiles() {
        return KnownElementPropertyRegistry.buildAll118Profiles();
    }

    private String buildSqlMigration(List<ElementPropertyProfile> profiles) {
        StringBuilder sql = new StringBuilder();
        sql.append("-- V6: Seed extended element properties for all 118 elements\n");
        sql.append("-- Dataset Version: extended-properties-v1.0.0\n\n");

        sql.append("INSERT INTO chemistry.element_property_dataset_versions (id, description, publication_date)\n");
        sql.append("VALUES ('extended-properties-v1.0.0', 'IUPAC / CRC / NIST Extended Element Properties Dataset', '2026-08-04')\n");
        sql.append("ON CONFLICT (id) DO NOTHING;\n\n");

        for (ElementPropertyProfile profile : profiles) {
            String profileId = UUID.nameUUIDFromBytes(("profile-" + profile.getAtomicNumber() + "-v1.0.0").getBytes()).toString();
            String elementId = UUID.nameUUIDFromBytes((profile.getSymbol() + "_" + profile.getAtomicNumber()).getBytes()).toString();

            sql.append(String.format(
                    "INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)\n" +
                    "VALUES ('%s', '%s', %d, '%s', 'extended-properties-v1.0.0')\n" +
                    "ON CONFLICT (element_id, dataset_version_id) DO NOTHING;\n",
                    profileId, elementId, profile.getAtomicNumber(), profile.getSymbol()
            ));

            for (Valency v : profile.getValencies()) {
                String valId = UUID.nameUUIDFromBytes(("valency-" + profile.getAtomicNumber() + "-" + v.getValency()).getBytes()).toString();
                sql.append(String.format(
                        "INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)\n" +
                        "VALUES ('%s', '%s', %d, %b, '%s', '%s', '%s') ON CONFLICT DO NOTHING;\n",
                        valId, profileId, v.getValency(), v.isCommon(), v.getEvidenceStatus().name(), v.getProvenance().getSourceIdentifier(), v.getProvenance().getSourceTitle()
                ));
            }

            for (OxidationState os : profile.getOxidationStates()) {
                String osId = UUID.nameUUIDFromBytes(("oxstate-" + profile.getAtomicNumber() + "-" + os.getState()).getBytes()).toString();
                sql.append(String.format(
                        "INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)\n" +
                        "VALUES ('%s', '%s', %d, %b, %b, %b, '%s', '%s', '%s') ON CONFLICT DO NOTHING;\n",
                        osId, profileId, os.getState(), os.isCommon(), os.isUncommon(), os.isPredicted(), os.getEvidenceStatus().name(), os.getProvenance().getSourceIdentifier(), os.getProvenance().getSourceTitle()
                ));
            }

            for (Electronegativity en : profile.getElectronegativities()) {
                String enId = UUID.nameUUIDFromBytes(("en-" + profile.getAtomicNumber() + "-" + en.getScale()).getBytes()).toString();
                sql.append(String.format(
                        "INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)\n" +
                        "VALUES ('%s', '%s', %s, '%s', %b, '%s', '%s', '%s') ON CONFLICT DO NOTHING;\n",
                        enId, profileId, en.getValue().toPlainString(), en.getScale().name(), en.isPredicted(), en.getEvidenceStatus().name(), en.getProvenance().getSourceIdentifier(), en.getProvenance().getSourceTitle()
                ));
            }

            for (ElementRadius r : profile.getRadii()) {
                String radId = UUID.nameUUIDFromBytes(("radius-" + profile.getAtomicNumber() + "-" + r.getKind() + "-" + (r.getIonicContext() != null ? r.getIonicContext().getIonicCharge() : "atomic")).getBytes()).toString();
                String ionicChargeStr = r.getIonicContext() != null ? String.valueOf(r.getIonicContext().getIonicCharge()) : "NULL";
                String coordStr = (r.getIonicContext() != null && r.getIonicContext().getCoordinationNumber() != null)
                        ? String.valueOf(r.getIonicContext().getCoordinationNumber()) : "NULL";
                String spinStr = r.getIonicContext() != null ? "'" + r.getIonicContext().getSpinState().name() + "'" : "NULL";

                sql.append(String.format(
                        "INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)\n" +
                        "VALUES ('%s', '%s', '%s', %s, %s, %s, %s, '%s', '%s', '%s') ON CONFLICT DO NOTHING;\n",
                        radId, profileId, r.getKind().name(), r.getRadius().in(LengthUnit.PICOMETER).toPlainString(), ionicChargeStr, coordStr, spinStr, r.getEvidenceStatus().name(), r.getProvenance().getSourceIdentifier(), r.getProvenance().getSourceTitle()
                ));
            }

            for (DensityDatum d : profile.getPhysicalProperties().getDensities()) {
                String denId = UUID.nameUUIDFromBytes(("density-" + profile.getAtomicNumber()).getBytes()).toString();
                String refTempStr = d.getReferenceTemperature() != null ? d.getReferenceTemperature().in(TemperatureUnit.KELVIN).toPlainString() : "NULL";
                String refPressStr = d.getReferencePressure() != null ? d.getReferencePressure().in(PressureUnit.KILOPASCAL).toPlainString() : "NULL";
                String refStateStr = d.getReferenceState() != null ? "'" + d.getReferenceState().name() + "'" : "NULL";

                sql.append(String.format(
                        "INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)\n" +
                        "VALUES ('%s', '%s', %s, %s, %s, %s, '%s', '%s', '%s') ON CONFLICT DO NOTHING;\n",
                        denId, profileId, d.getDensity().in(DensityUnit.KILOGRAM_PER_CUBIC_METER).toPlainString(), refTempStr, refPressStr, refStateStr, d.getEvidenceStatus().name(), d.getProvenance().getSourceIdentifier(), d.getProvenance().getSourceTitle()
                ));
            }

            for (PhaseTransitionDatum pt : profile.getPhysicalProperties().getPhaseTransitions()) {
                String ptId = UUID.nameUUIDFromBytes(("pt-" + profile.getAtomicNumber() + "-" + pt.getKind()).getBytes()).toString();
                String tempStr = pt.getTemperature() != null ? pt.getTemperature().in(TemperatureUnit.KELVIN).toPlainString() : "NULL";
                String refPressStr = pt.getReferencePressure() != null ? pt.getReferencePressure().in(PressureUnit.KILOPASCAL).toPlainString() : "NULL";

                sql.append(String.format(
                        "INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)\n" +
                        "VALUES ('%s', '%s', '%s', %s, %s, '%s', '%s', '%s', '%s') ON CONFLICT DO NOTHING;\n",
                        ptId, profileId, pt.getKind().name(), tempStr, refPressStr, pt.getBehavior().name(), pt.getEvidenceStatus().name(), pt.getProvenance().getSourceIdentifier(), pt.getProvenance().getSourceTitle()
                ));
            }

            if (profile.getAppearance() != null) {
                ElementAppearance app = profile.getAppearance();
                String appId = UUID.nameUUIDFromBytes(("app-" + profile.getAtomicNumber()).getBytes()).toString();
                String colorStr = app.getNormalizedColorName() != null ? "'" + app.getNormalizedColorName() + "'" : "NULL";
                String descStr = app.getAppearanceDescription() != null ? "'" + app.getAppearanceDescription().replace("'", "''") + "'" : "NULL";

                sql.append(String.format(
                        "INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)\n" +
                        "VALUES ('%s', '%s', %s, %s, '%s', '%s', '%s') ON CONFLICT (profile_id) DO NOTHING;\n",
                        appId, profileId, colorStr, descStr, app.getEvidenceStatus().name(), app.getProvenance().getSourceIdentifier(), app.getProvenance().getSourceTitle()
                ));
            }

            sql.append("\n");
        }

        return sql.toString();
    }

    private String buildJsonManifest(List<ElementPropertyProfile> profiles) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"datasetVersion\": \"extended-properties-v1.0.0\",\n");
        json.append("  \"generatedAt\": \"2026-08-04\",\n");
        json.append("  \"totalProfiles\": ").append(profiles.size()).append(",\n");
        json.append("  \"elements\": [\n");

        for (int i = 0; i < profiles.size(); i++) {
            ElementPropertyProfile p = profiles.get(i);
            json.append("    {\n");
            json.append("      \"atomicNumber\": ").append(p.getAtomicNumber()).append(",\n");
            json.append("      \"symbol\": \"").append(p.getSymbol()).append("\",\n");
            json.append("      \"valenciesCount\": ").append(p.getValencies().size()).append(",\n");
            json.append("      \"oxidationStatesCount\": ").append(p.getOxidationStates().size()).append(",\n");
            json.append("      \"electronegativitiesCount\": ").append(p.getElectronegativities().size()).append(",\n");
            json.append("      \"radiiCount\": ").append(p.getRadii().size()).append("\n");
            json.append("    }").append(i < profiles.size() - 1 ? "," : "").append("\n");
        }

        json.append("  ]\n");
        json.append("}\n");
        return json.toString();
    }
}
