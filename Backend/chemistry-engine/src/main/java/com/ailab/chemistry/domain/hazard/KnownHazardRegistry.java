package com.ailab.chemistry.domain.hazard;

import com.ailab.chemistry.domain.compound.Compound;
import com.ailab.chemistry.domain.compound.ElementMassProvider;
import com.ailab.chemistry.domain.compound.KnownCompoundRegistry;
import com.ailab.chemistry.domain.element.MatterState;

import java.math.BigDecimal;
import java.util.*;

public final class KnownHazardRegistry {

    public static final String DATASET_VERSION = "compound-hazards-v1.1.0";

    // Framework reference document
    public static final HazardSourceDocument UN_GHS_FRAMEWORK_DOC = new HazardSourceDocument(
            "UN-GHS-REV11-2025",
            "AUTHORITATIVE_CLASSIFICATION",
            "United Nations Committee of Experts on GHS",
            "UN GHS Revision 11 Reference Framework",
            "UN GHS Dictionary",
            HazardClassificationSystem.UN_GHS,
            "11th Revised Edition",
            "2025-01-01",
            "2026-08-05",
            HazardJurisdiction.INTERNATIONAL_REFERENCE,
            "en",
            "https://unece.org/transport/dangerous-goods/ghs-rev11-2025",
            "sha256-un-ghs-rev11",
            "United Nations reference framework standard"
    );

    // Compound-specific source documents
    public static final HazardSourceDocument ECHA_CL_DOC = new HazardSourceDocument(
            "ECHA-CL-INVENTORY-2025",
            "REGULATORY_DATABASE",
            "European Chemicals Agency (ECHA)",
            "ECHA C&L Inventory Harmonized Classifications",
            "ECHA Harmonized Data",
            HazardClassificationSystem.EU_CLP,
            "2025.1",
            "2025-01-15",
            "2026-08-05",
            HazardJurisdiction.EU,
            "en",
            "https://echa.europa.eu/information-on-chemicals/cl-inventory-database",
            "sha256-echa-cl-2025",
            "Official EU CLP harmonized classification database"
    );

    public static final HazardSourceDocument OSHA_HCS_DOC = new HazardSourceDocument(
            "OSHA-HCS-2025",
            "AUTHORITATIVE_CLASSIFICATION",
            "US Occupational Safety and Health Administration (OSHA)",
            "OSHA Hazard Communication Standard 2024 Revision",
            "OSHA HCS Data",
            HazardClassificationSystem.OSHA_HCS,
            "2024-Final",
            "2024-07-20",
            "2026-08-05",
            HazardJurisdiction.UNITED_STATES,
            "en",
            "https://www.osha.gov/hazcom",
            "sha256-osha-hcs-2024",
            "US Federal regulatory standard"
    );

    public static final HazardProvenance PROVENANCE = new HazardProvenance(
            ECHA_CL_DOC.getSourceId(),
            "ECHA C&L Inventory Harmonized Regulatory Classification",
            HazardEvidenceStatus.HARMONIZED_CLASSIFICATION
    );

    private KnownHazardRegistry() {}

    public static List<HazardProfile> buildAll55Profiles(ElementMassProvider massProvider) {
        List<Compound> compounds = KnownCompoundRegistry.buildAll55CoreCompounds(massProvider);
        List<HazardProfile> profiles = new ArrayList<>();

        for (Compound c : compounds) {
            String code = c.getCode().getValue();
            Map<String, HazardAvailability> availability = new HashMap<>();

            List<HazardClassification> classifications = new ArrayList<>();
            List<HazardLabelSummary> labelSummaries = new ArrayList<>();
            List<SupplementalLaboratoryHazard> supplementalHazards = new ArrayList<>();
            List<SafetyInstruction> safetyInstructions = new ArrayList<>();
            List<PersonalProtectiveEquipmentRecommendation> ppeRecommendations = new ArrayList<>();
            List<HazardSourceDocument> sourceDocs = new ArrayList<>();

            switch (code) {
                case "COMP-H2" -> {
                    availability.put("EU_CLP", HazardAvailability.CLASSIFIED);
                    sourceDocs.add(ECHA_CL_DOC);
                    HazardScope scope = new HazardScope(code, MatterState.GAS, PhysicalForm.COMPRESSED_GAS, "Pure gas", new BigDecimal("100.0"), new BigDecimal("100.0"), "% v/v", null, null, null, "Anhydrous compressed hydrogen gas");

                    HazardClassification h1 = new HazardClassification(
                            UUID.nameUUIDFromBytes((code + "_FLAM_GAS").getBytes()),
                            HazardClassificationSystem.EU_CLP, "2025.1", HazardJurisdiction.EU,
                            scope, "FLAMMABLE_GASES", "1A", List.of(GhsPictogram.GHS02), SignalWord.DANGER,
                            List.of(new HazardStatement("H220", "Extremely flammable gas", HazardClassificationSystem.EU_CLP, "2025.1", "en", ECHA_CL_DOC.getSourceId())),
                            List.of(new PrecautionaryStatement("P210", PrecautionaryStatement.PrecautionaryType.PREVENTION, "Keep away from heat, hot surfaces, sparks, open flames.", HazardClassificationSystem.EU_CLP, "2025.1", ECHA_CL_DOC.getSourceId())),
                            HazardEvidenceStatus.HARMONIZED_CLASSIFICATION, ECHA_CL_DOC
                    );
                    HazardClassification h2 = new HazardClassification(
                            UUID.nameUUIDFromBytes((code + "_PRESS_GAS").getBytes()),
                            HazardClassificationSystem.EU_CLP, "2025.1", HazardJurisdiction.EU,
                            scope, "GASES_UNDER_PRESSURE", "COMPRESSED_GAS", List.of(GhsPictogram.GHS04), SignalWord.WARNING,
                            List.of(new HazardStatement("H280", "Contains gas under pressure; may explode if heated", HazardClassificationSystem.EU_CLP, "2025.1", "en", ECHA_CL_DOC.getSourceId())),
                            List.of(new PrecautionaryStatement("P403", PrecautionaryStatement.PrecautionaryType.STORAGE, "Store in a well-ventilated place.", HazardClassificationSystem.EU_CLP, "2025.1", ECHA_CL_DOC.getSourceId())),
                            HazardEvidenceStatus.HARMONIZED_CLASSIFICATION, ECHA_CL_DOC
                    );
                    classifications.addAll(List.of(h1, h2));

                    ppeRecommendations.add(new PersonalProtectiveEquipmentRecommendation(
                            PpeType.SAFETY_GLASSES, ProtectionLevel.REQUIRED, scope, "ANSI Z87.1 approved goggles", "Handling compressed gas cylinders", ECHA_CL_DOC.getSourceId(), HazardEvidenceStatus.HARMONIZED_CLASSIFICATION
                    ));
                }

                case "COMP-O2" -> {
                    availability.put("EU_CLP", HazardAvailability.CLASSIFIED);
                    sourceDocs.add(ECHA_CL_DOC);
                    HazardScope scope = new HazardScope(code, MatterState.GAS, PhysicalForm.COMPRESSED_GAS, "Pure gas", new BigDecimal("100.0"), new BigDecimal("100.0"), "% v/v", null, null, null, "Compressed oxygen gas");

                    HazardClassification h1 = new HazardClassification(
                            UUID.nameUUIDFromBytes((code + "_OX_GAS").getBytes()),
                            HazardClassificationSystem.EU_CLP, "2025.1", HazardJurisdiction.EU,
                            scope, "OXIDIZING_GASES", "1", List.of(GhsPictogram.GHS03), SignalWord.DANGER,
                            List.of(new HazardStatement("H270", "May cause or intensify fire; oxidizer", HazardClassificationSystem.EU_CLP, "2025.1", "en", ECHA_CL_DOC.getSourceId())),
                            List.of(new PrecautionaryStatement("P220", PrecautionaryStatement.PrecautionaryType.PREVENTION, "Keep away from clothing and other combustible materials.", HazardClassificationSystem.EU_CLP, "2025.1", ECHA_CL_DOC.getSourceId())),
                            HazardEvidenceStatus.HARMONIZED_CLASSIFICATION, ECHA_CL_DOC
                    );
                    classifications.add(h1);
                }

                case "COMP-H2O", "COMP-NACL", "COMP-GLUCOSE" -> {
                    availability.put("EU_CLP", HazardAvailability.NOT_CLASSIFIED_BY_SOURCE);
                    sourceDocs.add(ECHA_CL_DOC);
                }

                case "COMP-ETHANOL" -> {
                    availability.put("EU_CLP", HazardAvailability.CLASSIFIED);
                    sourceDocs.add(ECHA_CL_DOC);
                    HazardScope scope = new HazardScope(code, MatterState.LIQUID, PhysicalForm.LIQUID, "Pure liquid (>99%)", new BigDecimal("99.0"), new BigDecimal("100.0"), "% w/w", null, null, null, "Pure liquid ethanol solvent");

                    HazardClassification h1 = new HazardClassification(
                            UUID.nameUUIDFromBytes((code + "_FLAM_LIQ").getBytes()),
                            HazardClassificationSystem.EU_CLP, "2025.1", HazardJurisdiction.EU,
                            scope, "FLAMMABLE_LIQUIDS", "2", List.of(GhsPictogram.GHS02), SignalWord.DANGER,
                            List.of(new HazardStatement("H225", "Highly flammable liquid and vapor", HazardClassificationSystem.EU_CLP, "2025.1", "en", ECHA_CL_DOC.getSourceId())),
                            List.of(new PrecautionaryStatement("P210", PrecautionaryStatement.PrecautionaryType.PREVENTION, "Keep away from heat, hot surfaces, sparks.", HazardClassificationSystem.EU_CLP, "2025.1", ECHA_CL_DOC.getSourceId())),
                            HazardEvidenceStatus.HARMONIZED_CLASSIFICATION, ECHA_CL_DOC
                    );
                    HazardClassification h2 = new HazardClassification(
                            UUID.nameUUIDFromBytes((code + "_EYE_IRR").getBytes()),
                            HazardClassificationSystem.EU_CLP, "2025.1", HazardJurisdiction.EU,
                            scope, "EYE_IRRITATION", "2", List.of(GhsPictogram.GHS07), SignalWord.WARNING,
                            List.of(new HazardStatement("H319", "Causes serious eye irritation", HazardClassificationSystem.EU_CLP, "2025.1", "en", ECHA_CL_DOC.getSourceId())),
                            List.of(new PrecautionaryStatement("P305+P351+P338", PrecautionaryStatement.PrecautionaryType.RESPONSE, "IF IN EYES: Rinse cautiously with water.", HazardClassificationSystem.EU_CLP, "2025.1", ECHA_CL_DOC.getSourceId())),
                            HazardEvidenceStatus.HARMONIZED_CLASSIFICATION, ECHA_CL_DOC
                    );
                    classifications.addAll(List.of(h1, h2));
                }

                case "COMP-DIMETHYL-ETHER" -> {
                    availability.put("EU_CLP", HazardAvailability.CLASSIFIED);
                    sourceDocs.add(ECHA_CL_DOC);
                    HazardScope scope = new HazardScope(code, MatterState.GAS, PhysicalForm.COMPRESSED_GAS, "Pure gas", new BigDecimal("100.0"), new BigDecimal("100.0"), "% v/v", null, null, null, "Compressed dimethyl ether gas");

                    HazardClassification h1 = new HazardClassification(
                            UUID.nameUUIDFromBytes((code + "_FLAM_GAS").getBytes()),
                            HazardClassificationSystem.EU_CLP, "2025.1", HazardJurisdiction.EU,
                            scope, "FLAMMABLE_GASES", "1A", List.of(GhsPictogram.GHS02, GhsPictogram.GHS04), SignalWord.DANGER,
                            List.of(new HazardStatement("H220", "Extremely flammable gas", HazardClassificationSystem.EU_CLP, "2025.1", "en", ECHA_CL_DOC.getSourceId())),
                            List.of(new PrecautionaryStatement("P210", PrecautionaryStatement.PrecautionaryType.PREVENTION, "Keep away from heat, sparks.", HazardClassificationSystem.EU_CLP, "2025.1", ECHA_CL_DOC.getSourceId())),
                            HazardEvidenceStatus.HARMONIZED_CLASSIFICATION, ECHA_CL_DOC
                    );
                    classifications.add(h1);
                }

                case "COMP-CO2" -> {
                    availability.put("EU_CLP", HazardAvailability.CLASSIFIED);
                    sourceDocs.add(ECHA_CL_DOC);
                    HazardScope scope = new HazardScope(code, MatterState.GAS, PhysicalForm.COMPRESSED_GAS, "Pure gas", new BigDecimal("100.0"), new BigDecimal("100.0"), "% v/v", null, null, null, "Compressed carbon dioxide gas");

                    HazardClassification h1 = new HazardClassification(
                            UUID.nameUUIDFromBytes((code + "_PRESS_GAS").getBytes()),
                            HazardClassificationSystem.EU_CLP, "2025.1", HazardJurisdiction.EU,
                            scope, "GASES_UNDER_PRESSURE", "LIQUEFIED_GAS", List.of(GhsPictogram.GHS04), SignalWord.WARNING,
                            List.of(new HazardStatement("H280", "Contains gas under pressure; may explode if heated", HazardClassificationSystem.EU_CLP, "2025.1", "en", ECHA_CL_DOC.getSourceId())),
                            List.of(new PrecautionaryStatement("P403", PrecautionaryStatement.PrecautionaryType.STORAGE, "Store in a well-ventilated place.", HazardClassificationSystem.EU_CLP, "2025.1", ECHA_CL_DOC.getSourceId())),
                            HazardEvidenceStatus.HARMONIZED_CLASSIFICATION, ECHA_CL_DOC
                    );
                    classifications.add(h1);
                    supplementalHazards.addAll(List.of(SupplementalLaboratoryHazard.SIMPLE_ASPHYXIANT, SupplementalLaboratoryHazard.OXYGEN_DISPLACEMENT));
                }

                case "COMP-HCL" -> {
                    availability.put("EU_CLP", HazardAvailability.CLASSIFIED);
                    sourceDocs.add(ECHA_CL_DOC);
                    HazardScope scopeSolution = new HazardScope(code, MatterState.LIQUID, PhysicalForm.SOLUTION, "Aqueous hydrochloric acid (>25% w/w)", new BigDecimal("25.0"), new BigDecimal("37.0"), "% w/w", null, null, null, "Concentrated aqueous hydrochloric acid solution");

                    HazardClassification h1 = new HazardClassification(
                            UUID.nameUUIDFromBytes((code + "_SKIN_CORR").getBytes()),
                            HazardClassificationSystem.EU_CLP, "2025.1", HazardJurisdiction.EU,
                            scopeSolution, "SKIN_CORROSION", "1A", List.of(GhsPictogram.GHS05), SignalWord.DANGER,
                            List.of(new HazardStatement("H314", "Causes severe skin burns and eye damage", HazardClassificationSystem.EU_CLP, "2025.1", "en", ECHA_CL_DOC.getSourceId())),
                            List.of(new PrecautionaryStatement("P280", PrecautionaryStatement.PrecautionaryType.PREVENTION, "Wear protective gloves/clothing/eye protection.", HazardClassificationSystem.EU_CLP, "2025.1", ECHA_CL_DOC.getSourceId())),
                            HazardEvidenceStatus.HARMONIZED_CLASSIFICATION, ECHA_CL_DOC
                    );
                    HazardClassification h2 = new HazardClassification(
                            UUID.nameUUIDFromBytes((code + "_ACUTE_TOX").getBytes()),
                            HazardClassificationSystem.EU_CLP, "2025.1", HazardJurisdiction.EU,
                            scopeSolution, "ACUTE_TOXICITY", "3", List.of(GhsPictogram.GHS06), SignalWord.DANGER,
                            List.of(new HazardStatement("H331", "Toxic if inhaled", HazardClassificationSystem.EU_CLP, "2025.1", "en", ECHA_CL_DOC.getSourceId())),
                            List.of(new PrecautionaryStatement("P261", PrecautionaryStatement.PrecautionaryType.PREVENTION, "Avoid breathing vapors.", HazardClassificationSystem.EU_CLP, "2025.1", ECHA_CL_DOC.getSourceId())),
                            HazardEvidenceStatus.HARMONIZED_CLASSIFICATION, ECHA_CL_DOC
                    );
                    classifications.addAll(List.of(h1, h2));
                }

                default -> {
                    // Remaining 34 compounds without compound-level seed sources in current dataset are marked NOT_INCLUDED_IN_DATASET
                    availability.put("EU_CLP", HazardAvailability.NOT_INCLUDED_IN_DATASET);
                }
            }

            Set<HazardSummaryFlag> summaryFlags = HazardSummaryDerivationEngine.deriveSummaryFlags(classifications, supplementalHazards);

            if (!classifications.isEmpty()) {
                SignalWord maxSignal = classifications.stream().map(HazardClassification::getSignalWord)
                        .filter(s -> s == SignalWord.DANGER).findFirst().orElse(SignalWord.WARNING);

                List<GhsPictogram> allPictograms = classifications.stream().flatMap(cl -> cl.getPictograms().stream()).distinct().toList();
                List<HazardStatement> allH = classifications.stream().flatMap(cl -> cl.getHazardStatements().stream()).distinct().toList();
                List<PrecautionaryStatement> allP = classifications.stream().flatMap(cl -> cl.getPrecautionaryStatements().stream()).distinct().toList();

                labelSummaries.add(new HazardLabelSummary(
                        code, classifications.get(0).getClassificationSystem(), classifications.get(0).getRevision(),
                        classifications.get(0).getJurisdiction(), scope(classifications.get(0)), maxSignal, allPictograms, allH, allP,
                        classifications.get(0).getSourceDocument().getSourceId()
                ));
            }

            profiles.add(new HazardProfile(
                    c.getId(),
                    DATASET_VERSION,
                    availability,
                    classifications,
                    labelSummaries,
                    summaryFlags,
                    supplementalHazards,
                    safetyInstructions,
                    ppeRecommendations,
                    sourceDocs,
                    PROVENANCE
            ));
        }

        return Collections.unmodifiableList(profiles);
    }

    private static HazardScope scope(HazardClassification c) {
        return c.getScope();
    }
}
