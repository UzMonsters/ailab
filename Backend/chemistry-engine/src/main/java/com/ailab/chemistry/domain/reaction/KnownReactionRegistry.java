package com.ailab.chemistry.domain.reaction;

import java.math.BigInteger;
import java.util.*;

public final class KnownReactionRegistry {

    public static final String REACTION_DATASET_VERSION = "reaction-core-v1.0.0";
    public static final String REACTION_DATASET_NAME = "Core Educational Reaction Database Catalogue";

    private static final ReactionSourceDocument SRC_CRC = new ReactionSourceDocument(
            "CRC-HANDBOOK-104",
            SourceDocumentType.TEXTBOOK,
            "CRC Press",
            "CRC Handbook of Chemistry and Physics, 104th Edition",
            "104th",
            "2023",
            "2026-08-06",
            "Standard inorganic and organic chemical reactions",
            List.of("equation", "reactionName", "catalysts", "conditions", "directionality"),
            "en",
            "CRC-104-RXN-CATALOG",
            "Public scientific reference metadata"
    );

    private static final ReactionSourceDocument SRC_NIST = new ReactionSourceDocument(
            "NIST-WEBBOOK-2025",
            SourceDocumentType.AUTHORITATIVE_DATABASE,
            "National Institute of Standards and Technology (NIST)",
            "NIST Chemistry WebBook, SRD 69",
            "2025",
            "2025",
            "2026-08-06",
            "Thermochemical and kinetic reference reactions",
            List.of("equation", "directionality", "speciesStates"),
            "en",
            "NIST-SRD-69",
            "US Government open reference dataset"
    );

    public static List<ReactionSourceDocument> buildAllSourceDocuments() {
        return List.of(SRC_CRC, SRC_NIST);
    }

    public static List<ReactionTypeDefinition> buildAllTypeDefinitions() {
        return List.of(
                new ReactionTypeDefinition(ReactionTypeCode.SYNTHESIS, "Synthesis / Combination", "Two or more substances combine to form a single product", 1),
                new ReactionTypeDefinition(ReactionTypeCode.DECOMPOSITION, "Decomposition", "A single compound breaks down into two or more simpler substances", 2),
                new ReactionTypeDefinition(ReactionTypeCode.COMBUSTION, "Combustion", "Reaction with oxygen gas releasing energy and oxides", 3),
                new ReactionTypeDefinition(ReactionTypeCode.SINGLE_DISPLACEMENT, "Single Displacement", "One element replaces another in a compound", 4),
                new ReactionTypeDefinition(ReactionTypeCode.DOUBLE_DISPLACEMENT, "Double Displacement", "Exchange of ions between two compounds", 5),
                new ReactionTypeDefinition(ReactionTypeCode.ACID_BASE_NEUTRALIZATION, "Acid-Base Neutralization", "Acid reacts with a base producing water and a salt", 6),
                new ReactionTypeDefinition(ReactionTypeCode.PRECIPITATION, "Precipitation", "Formation of an insoluble solid precipitate in solution", 7),
                new ReactionTypeDefinition(ReactionTypeCode.GAS_EVOLUTION, "Gas Evolution", "Reaction producing one or more gas products", 8),
                new ReactionTypeDefinition(ReactionTypeCode.REDOX, "Redox", "Oxidation-reduction electron transfer reaction", 9),
                new ReactionTypeDefinition(ReactionTypeCode.OXIDATION, "Oxidation", "Gain of oxygen or loss of electrons", 10),
                new ReactionTypeDefinition(ReactionTypeCode.REDUCTION, "Reduction", "Loss of oxygen or gain of electrons", 11),
                new ReactionTypeDefinition(ReactionTypeCode.HYDRATION, "Hydration", "Addition of water molecules to a chemical entity", 12),
                new ReactionTypeDefinition(ReactionTypeCode.DEHYDRATION, "Dehydration", "Removal of water molecules from a chemical entity", 13),
                new ReactionTypeDefinition(ReactionTypeCode.HYDROLYSIS, "Hydrolysis", "Cleavage of chemical bonds by addition of water", 14),
                new ReactionTypeDefinition(ReactionTypeCode.REVERSIBLE_REACTION, "Reversible Reaction", "Reaction that can proceed in both forward and reverse directions", 15)
        );
    }

    public static List<Reaction> buildAll26Reactions() {
        List<Reaction> list = new ArrayList<>();

        // 1. RXN-WATER-SYNTHESIS
        list.add(createReaction(
                "11111111-1111-1111-1111-111111111101",
                "RXN-WATER-SYNTHESIS",
                "Synthesis of Water",
                List.of(new ReactionAlias("Hydrogen Combustion", "COMMON"), new ReactionAlias("Water Formation", "SCIENTIFIC")),
                "2H2 + O2 -> 2H2O", "2H2 + O2 -> 2H2O", "2H2 + O2 -> 2H2O",
                ReactionDirectionality.IRREVERSIBLE,
                List.of(
                        term("COMP-H2", "H2", ReactionSide.REACTANT, 2, ReactionSpeciesState.GAS, 1),
                        term("COMP-O2", "O2", ReactionSide.REACTANT, 1, ReactionSpeciesState.GAS, 2),
                        term("COMP-H2O", "H2O", ReactionSide.PRODUCT, 2, ReactionSpeciesState.GAS, 3)
                ),
                List.of(new Catalyst(UUID.fromString("11111111-2222-1111-1111-111111111101"), CatalystReferenceType.COMPOUND, "COMP-PT", CatalystRole.PROMOTER, "Solid mesh", "Trace platinum catalyst", ReactionEvidenceStatus.CURATED_AUTHORITATIVE, prov("CRC-HANDBOOK-104"))),
                List.of(new ReactionConditionSet(UUID.fromString("11111111-3333-1111-1111-111111111101"), com.ailab.chemistry.domain.measurement.Temperature.of("773.15", com.ailab.chemistry.domain.measurement.TemperatureUnit.KELVIN), com.ailab.chemistry.domain.measurement.Pressure.of("1.0", com.ailab.chemistry.domain.measurement.PressureUnit.BAR), "Gas phase", ReactionAtmosphere.OXYGEN, null, EnergyInput.HEAT, "OXYGEN", "Spark ignition under high temperature", ReactionEvidenceStatus.CURATED_AUTHORITATIVE, prov("CRC-HANDBOOK-104"))),
                List.of(
                        type(ReactionTypeCode.SYNTHESIS, DerivationBasis.CURATED_REFERENCE, "Direct combination of dihydrogen and dioxygen gas"),
                        type(ReactionTypeCode.COMBUSTION, DerivationBasis.CURATED_REFERENCE, "Rapid exothermic reaction with oxygen producing water oxide"),
                        type(ReactionTypeCode.REDOX, DerivationBasis.CURATED_REFERENCE, "Hydrogen oxidized from 0 to +1, oxygen reduced from 0 to -2")
                ),
                prov("CRC-HANDBOOK-104")
        ));

        // 2. RXN-H2O2-DECOMP
        list.add(createReaction("11111111-1111-1111-1111-111111111102", "RXN-H2O2-DECOMP", "Decomposition of Hydrogen Peroxide", List.of(new ReactionAlias("Peroxide Breakdown", "COMMON")), "2H2O2 -> 2H2O + O2", "2H2O2 -> 2H2O + O2", "2H2O2 -> 2H2O + O2", ReactionDirectionality.IRREVERSIBLE, List.of(term("COMP-H2O2", "H2O2", ReactionSide.REACTANT, 2, ReactionSpeciesState.AQUEOUS, 1), term("COMP-H2O", "H2O", ReactionSide.PRODUCT, 2, ReactionSpeciesState.LIQUID, 2), term("COMP-O2", "O2", ReactionSide.PRODUCT, 1, ReactionSpeciesState.GAS, 3)), List.of(new Catalyst(UUID.randomUUID(), CatalystReferenceType.COMPOUND, "COMP-CUO", CatalystRole.CATALYST, "Solid powder", "Heterogeneous catalytic decomposition", ReactionEvidenceStatus.CURATED_AUTHORITATIVE, prov("CRC-HANDBOOK-104"))), List.of(), List.of(type(ReactionTypeCode.DECOMPOSITION, DerivationBasis.CURATED_REFERENCE, "Single compound breaks down into water and oxygen"), type(ReactionTypeCode.REDOX, DerivationBasis.CURATED_REFERENCE, "Disproportionation of peroxide oxygen"), type(ReactionTypeCode.GAS_EVOLUTION, DerivationBasis.SAFE_RULE_DERIVED, "RULE-EXPLICIT-GAS-PRODUCT: Gas state oxygen produced")), prov("CRC-HANDBOOK-104")));

        // 3. RXN-METHANE-COMBUSTION
        list.add(createReaction("11111111-1111-1111-1111-111111111103", "RXN-METHANE-COMBUSTION", "Combustion of Methane", List.of(new ReactionAlias("Natural Gas Combustion", "COMMON")), "CH4 + 2O2 -> CO2 + 2H2O", "CH4 + 2O2 -> CO2 + 2H2O", "CH4 + 2O2 -> CO2 + 2H2O", ReactionDirectionality.IRREVERSIBLE, List.of(term("COMP-CH4", "CH4", ReactionSide.REACTANT, 1, ReactionSpeciesState.GAS, 1), term("COMP-O2", "O2", ReactionSide.REACTANT, 2, ReactionSpeciesState.GAS, 2), term("COMP-CO2", "CO2", ReactionSide.PRODUCT, 1, ReactionSpeciesState.GAS, 3), term("COMP-H2O", "H2O", ReactionSide.PRODUCT, 2, ReactionSpeciesState.GAS, 4)), List.of(), List.of(), List.of(type(ReactionTypeCode.COMBUSTION, DerivationBasis.CURATED_REFERENCE, "Complete oxidation of methane by oxygen"), type(ReactionTypeCode.REDOX, DerivationBasis.CURATED_REFERENCE, "Carbon oxidized from -4 to +4")), prov("CRC-HANDBOOK-104")));

        // 4. RXN-NEUT-HCL-NAOH
        list.add(createReaction("11111111-1111-1111-1111-111111111104", "RXN-NEUT-HCL-NAOH", "Neutralization of Hydrochloric Acid with Sodium Hydroxide", List.of(new ReactionAlias("Acid-Base Neutralization", "COMMON")), "HCl + NaOH -> NaCl + H2O", "HCl + NaOH -> NaCl + H2O", "HCl + NaOH -> NaCl + H2O", ReactionDirectionality.IRREVERSIBLE, List.of(term("COMP-HCL", "HCl", ReactionSide.REACTANT, 1, ReactionSpeciesState.AQUEOUS, 1), term("COMP-NAOH", "NaOH", ReactionSide.REACTANT, 1, ReactionSpeciesState.AQUEOUS, 2), term("COMP-NACL", "NaCl", ReactionSide.PRODUCT, 1, ReactionSpeciesState.AQUEOUS, 3), term("COMP-H2O", "H2O", ReactionSide.PRODUCT, 1, ReactionSpeciesState.LIQUID, 4)), List.of(), List.of(), List.of(type(ReactionTypeCode.ACID_BASE_NEUTRALIZATION, DerivationBasis.CURATED_REFERENCE, "Strong acid and strong base reaction yielding salt and water"), type(ReactionTypeCode.DOUBLE_DISPLACEMENT, DerivationBasis.CURATED_REFERENCE, "Proton transfer and ion exchange")), prov("CRC-HANDBOOK-104")));

        // 5. RXN-NAHCO3-DECOMP
        list.add(createReaction("11111111-1111-1111-1111-111111111105", "RXN-NAHCO3-DECOMP", "Thermal Decomposition of Sodium Bicarbonate", List.of(new ReactionAlias("Baking Soda Thermal Breakdown", "COMMON")), "2NaHCO3 -> Na2CO3 + CO2 + H2O", "2NaHCO3 -> Na2CO3 + CO2 + H2O", "2NaHCO3 -> Na2CO3 + CO2 + H2O", ReactionDirectionality.IRREVERSIBLE, List.of(term("COMP-NAHCO3", "NaHCO3", ReactionSide.REACTANT, 2, ReactionSpeciesState.SOLID, 1), term("COMP-NA2CO3", "Na2CO3", ReactionSide.PRODUCT, 1, ReactionSpeciesState.SOLID, 2), term("COMP-CO2", "CO2", ReactionSide.PRODUCT, 1, ReactionSpeciesState.GAS, 3), term("COMP-H2O", "H2O", ReactionSide.PRODUCT, 1, ReactionSpeciesState.GAS, 4)), List.of(), List.of(), List.of(type(ReactionTypeCode.DECOMPOSITION, DerivationBasis.CURATED_REFERENCE, "Solid bicarbonate decomposes under heat"), type(ReactionTypeCode.GAS_EVOLUTION, DerivationBasis.SAFE_RULE_DERIVED, "RULE-EXPLICIT-GAS-PRODUCT: Gas state product formed")), prov("CRC-HANDBOOK-104")));

        // 6. RXN-CA-OH-2-CO2
        list.add(createReaction("11111111-1111-1111-1111-111111111106", "RXN-CA-OH-2-CO2", "Reaction of Calcium Hydroxide with Carbon Dioxide", List.of(new ReactionAlias("Limewater Test for Carbon Dioxide", "COMMON")), "Ca(OH)2 + CO2 -> CaCO3 + H2O", "Ca(OH)2 + CO2 -> CaCO3 + H2O", "Ca(OH)2 + CO2 -> CaCO3 + H2O", ReactionDirectionality.IRREVERSIBLE, List.of(term("COMP-CA-OH-2", "Ca(OH)2", ReactionSide.REACTANT, 1, ReactionSpeciesState.AQUEOUS, 1), term("COMP-CO2", "CO2", ReactionSide.REACTANT, 1, ReactionSpeciesState.GAS, 2), term("COMP-CACO3", "CaCO3", ReactionSide.PRODUCT, 1, ReactionSpeciesState.SOLID, 3), term("COMP-H2O", "H2O", ReactionSide.PRODUCT, 1, ReactionSpeciesState.LIQUID, 4)), List.of(), List.of(), List.of(type(ReactionTypeCode.ACID_BASE_NEUTRALIZATION, DerivationBasis.CURATED_REFERENCE, "Basic hydroxide with acidic CO2 gas"), type(ReactionTypeCode.PRECIPITATION, DerivationBasis.SAFE_RULE_DERIVED, "RULE-EXPLICIT-PRECIPITATE-STATE")), prov("CRC-HANDBOOK-104")));

        // 7. RXN-ETHANOL-COMBUSTION
        list.add(createReaction("11111111-1111-1111-1111-111111111107", "RXN-ETHANOL-COMBUSTION", "Combustion of Ethanol", List.of(new ReactionAlias("Ethanol Oxidation", "COMMON")), "C2H5OH + 3O2 -> 2CO2 + 3H2O", "C2H5OH + 3O2 -> 2CO2 + 3H2O", "C2H5OH + 3O2 -> 2CO2 + 3H2O", ReactionDirectionality.IRREVERSIBLE, List.of(term("COMP-ETHANOL", "C2H5OH", ReactionSide.REACTANT, 1, ReactionSpeciesState.LIQUID, 1), term("COMP-O2", "O2", ReactionSide.REACTANT, 3, ReactionSpeciesState.GAS, 2), term("COMP-CO2", "CO2", ReactionSide.PRODUCT, 2, ReactionSpeciesState.GAS, 3), term("COMP-H2O", "H2O", ReactionSide.PRODUCT, 3, ReactionSpeciesState.GAS, 4)), List.of(), List.of(), List.of(type(ReactionTypeCode.COMBUSTION, DerivationBasis.CURATED_REFERENCE, "Ethanol combustion"), type(ReactionTypeCode.REDOX, DerivationBasis.CURATED_REFERENCE, "Ethanol oxidation")), prov("CRC-HANDBOOK-104")));

        // 8. RXN-DIMETHYL-ETHER-COMBUSTION
        list.add(createReaction("11111111-1111-1111-1111-111111111108", "RXN-DIMETHYL-ETHER-COMBUSTION", "Combustion of Dimethyl Ether", List.of(new ReactionAlias("DME Combustion", "COMMON")), "CH3OCH3 + 3O2 -> 2CO2 + 3H2O", "CH3OCH3 + 3O2 -> 2CO2 + 3H2O", "CH3OCH3 + 3O2 -> 2CO2 + 3H2O", ReactionDirectionality.IRREVERSIBLE, List.of(term("COMP-DIMETHYL-ETHER", "CH3OCH3", ReactionSide.REACTANT, 1, ReactionSpeciesState.GAS, 1), term("COMP-O2", "O2", ReactionSide.REACTANT, 3, ReactionSpeciesState.GAS, 2), term("COMP-CO2", "CO2", ReactionSide.PRODUCT, 2, ReactionSpeciesState.GAS, 3), term("COMP-H2O", "H2O", ReactionSide.PRODUCT, 3, ReactionSpeciesState.GAS, 4)), List.of(), List.of(), List.of(type(ReactionTypeCode.COMBUSTION, DerivationBasis.CURATED_REFERENCE, "DME combustion"), type(ReactionTypeCode.REDOX, DerivationBasis.CURATED_REFERENCE, "DME oxidation")), prov("CRC-HANDBOOK-104")));

        // 9. RXN-CUSO4-HYDRATION
        list.add(createReaction("11111111-1111-1111-1111-111111111109", "RXN-CUSO4-HYDRATION", "Hydration of Copper(II) Sulfate", List.of(new ReactionAlias("Anhydrous Copper Sulfate Hydration", "COMMON")), "CuSO4 + 5H2O -> CuSO4·5H2O", "CuSO4 + 5H2O -> CuSO4·5H2O", "CuSO4 + 5H2O -> CuSO4·5H2O", ReactionDirectionality.REVERSIBLE, List.of(term("COMP-CUSO4", "CuSO4", ReactionSide.REACTANT, 1, ReactionSpeciesState.SOLID, 1), term("COMP-H2O", "H2O", ReactionSide.REACTANT, 5, ReactionSpeciesState.LIQUID, 2), term("COMP-CUSO4-5H2O", "CuSO4·5H2O", ReactionSide.PRODUCT, 1, ReactionSpeciesState.SOLID, 3)), List.of(), List.of(), List.of(type(ReactionTypeCode.HYDRATION, DerivationBasis.CURATED_REFERENCE, "Hydration"), type(ReactionTypeCode.REVERSIBLE_REACTION, DerivationBasis.SAFE_RULE_DERIVED, "RULE-DIRECTIONALITY")), prov("CRC-HANDBOOK-104")));

        // 10. RXN-CUSO4-DEHYDRATION
        list.add(createReaction("11111111-1111-1111-1111-111111111110", "RXN-CUSO4-DEHYDRATION", "Dehydration of Copper(II) Sulfate Pentahydrate", List.of(new ReactionAlias("Blue Vitriol Dehydration", "COMMON")), "CuSO4·5H2O -> CuSO4 + 5H2O", "CuSO4·5H2O -> CuSO4 + 5H2O", "CuSO4·5H2O -> CuSO4 + 5H2O", ReactionDirectionality.REVERSIBLE, List.of(term("COMP-CUSO4-5H2O", "CuSO4·5H2O", ReactionSide.REACTANT, 1, ReactionSpeciesState.SOLID, 1), term("COMP-CUSO4", "CuSO4", ReactionSide.PRODUCT, 1, ReactionSpeciesState.SOLID, 2), term("COMP-H2O", "H2O", ReactionSide.PRODUCT, 5, ReactionSpeciesState.GAS, 3)), List.of(), List.of(), List.of(type(ReactionTypeCode.DEHYDRATION, DerivationBasis.CURATED_REFERENCE, "Dehydration"), type(ReactionTypeCode.REVERSIBLE_REACTION, DerivationBasis.SAFE_RULE_DERIVED, "RULE-DIRECTIONALITY")), prov("CRC-HANDBOOK-104")));

        // 11. RXN-HABER-PROCESS
        list.add(createReaction("11111111-1111-1111-1111-111111111111", "RXN-HABER-PROCESS", "Synthesis of Ammonia (Haber-Bosch Process)", List.of(new ReactionAlias("Ammonia Synthesis", "COMMON")), "N2 + 3H2 -> 2NH3", "N2 + 3H2 -> 2NH3", "N2 + 3H2 -> 2NH3", ReactionDirectionality.REVERSIBLE, List.of(term("COMP-N2", "N2", ReactionSide.REACTANT, 1, ReactionSpeciesState.GAS, 1), term("COMP-H2", "H2", ReactionSide.REACTANT, 3, ReactionSpeciesState.GAS, 2), term("COMP-NH3", "NH3", ReactionSide.PRODUCT, 2, ReactionSpeciesState.GAS, 3)), List.of(), List.of(), List.of(type(ReactionTypeCode.SYNTHESIS, DerivationBasis.CURATED_REFERENCE, "Ammonia synthesis"), type(ReactionTypeCode.REVERSIBLE_REACTION, DerivationBasis.SAFE_RULE_DERIVED, "RULE-DIRECTIONALITY")), prov("CRC-HANDBOOK-104")));

        // 11-26... existing reactions preserved
        list.add(createReaction("11111111-1111-1111-1111-111111111112", "RXN-ETHANE-COMBUSTION", "Combustion of Ethane", List.of(new ReactionAlias("Ethane Oxidation", "COMMON")), "2C2H6 + 7O2 -> 4CO2 + 6H2O", "2C2H6 + 7O2 -> 4CO2 + 6H2O", "2C2H6 + 7O2 -> 4CO2 + 6H2O", ReactionDirectionality.IRREVERSIBLE, List.of(term("COMP-C2H6", "C2H6", ReactionSide.REACTANT, 2, ReactionSpeciesState.GAS, 1), term("COMP-O2", "O2", ReactionSide.REACTANT, 7, ReactionSpeciesState.GAS, 2), term("COMP-CO2", "CO2", ReactionSide.PRODUCT, 4, ReactionSpeciesState.GAS, 3), term("COMP-H2O", "H2O", ReactionSide.PRODUCT, 6, ReactionSpeciesState.GAS, 4)), List.of(), List.of(), List.of(type(ReactionTypeCode.COMBUSTION, DerivationBasis.CURATED_REFERENCE, "Complete combustion of ethane gas"), type(ReactionTypeCode.REDOX, DerivationBasis.CURATED_REFERENCE, "Ethane carbon oxidation")), prov("NIST-WEBBOOK-2025")));
        list.add(createReaction("11111111-1111-1111-1111-111111111113", "RXN-PROPANE-COMBUSTION", "Combustion of Propane", List.of(new ReactionAlias("Propane Oxidation", "COMMON")), "C3H8 + 5O2 -> 3CO2 + 4H2O", "C3H8 + 5O2 -> 3CO2 + 4H2O", "C3H8 + 5O2 -> 3CO2 + 4H2O", ReactionDirectionality.IRREVERSIBLE, List.of(term("COMP-C3H8", "C3H8", ReactionSide.REACTANT, 1, ReactionSpeciesState.GAS, 1), term("COMP-O2", "O2", ReactionSide.REACTANT, 5, ReactionSpeciesState.GAS, 2), term("COMP-CO2", "CO2", ReactionSide.PRODUCT, 3, ReactionSpeciesState.GAS, 3), term("COMP-H2O", "H2O", ReactionSide.PRODUCT, 4, ReactionSpeciesState.GAS, 4)), List.of(), List.of(), List.of(type(ReactionTypeCode.COMBUSTION, DerivationBasis.CURATED_REFERENCE, "Complete combustion of propane gas"), type(ReactionTypeCode.REDOX, DerivationBasis.CURATED_REFERENCE, "Propane oxidation")), prov("NIST-WEBBOOK-2025")));
        list.add(createReaction("11111111-1111-1111-1111-111111111114", "RXN-BUTANE-COMBUSTION", "Combustion of Butane", List.of(new ReactionAlias("Butane Oxidation", "COMMON")), "2C4H10 + 13O2 -> 8CO2 + 10H2O", "2C4H10 + 13O2 -> 8CO2 + 10H2O", "2C4H10 + 13O2 -> 8CO2 + 10H2O", ReactionDirectionality.IRREVERSIBLE, List.of(term("COMP-C4H10", "C4H10", ReactionSide.REACTANT, 2, ReactionSpeciesState.GAS, 1), term("COMP-O2", "O2", ReactionSide.REACTANT, 13, ReactionSpeciesState.GAS, 2), term("COMP-CO2", "CO2", ReactionSide.PRODUCT, 8, ReactionSpeciesState.GAS, 3), term("COMP-H2O", "H2O", ReactionSide.PRODUCT, 10, ReactionSpeciesState.GAS, 4)), List.of(), List.of(), List.of(type(ReactionTypeCode.COMBUSTION, DerivationBasis.CURATED_REFERENCE, "Complete combustion of butane gas"), type(ReactionTypeCode.REDOX, DerivationBasis.CURATED_REFERENCE, "Butane oxidation")), prov("NIST-WEBBOOK-2025")));
        list.add(createReaction("11111111-1111-1111-1111-111111111115", "RXN-ETHYLENE-COMBUSTION", "Combustion of Ethylene", List.of(new ReactionAlias("Ethene Combustion", "SCIENTIFIC")), "C2H4 + 3O2 -> 2CO2 + 2H2O", "C2H4 + 3O2 -> 2CO2 + 2H2O", "C2H4 + 3O2 -> 2CO2 + 2H2O", ReactionDirectionality.IRREVERSIBLE, List.of(term("COMP-C2H4", "C2H4", ReactionSide.REACTANT, 1, ReactionSpeciesState.GAS, 1), term("COMP-O2", "O2", ReactionSide.REACTANT, 3, ReactionSpeciesState.GAS, 2), term("COMP-CO2", "CO2", ReactionSide.PRODUCT, 2, ReactionSpeciesState.GAS, 3), term("COMP-H2O", "H2O", ReactionSide.PRODUCT, 2, ReactionSpeciesState.GAS, 4)), List.of(), List.of(), List.of(type(ReactionTypeCode.COMBUSTION, DerivationBasis.CURATED_REFERENCE, "Combustion of unsaturated alkene hydrocarbon"), type(ReactionTypeCode.REDOX, DerivationBasis.CURATED_REFERENCE, "Ethylene oxidation")), prov("NIST-WEBBOOK-2025")));
        list.add(createReaction("11111111-1111-1111-1111-111111111116", "RXN-ACETYLENE-COMBUSTION", "Combustion of Acetylene", List.of(new ReactionAlias("Ethyne Combustion", "SCIENTIFIC")), "2C2H2 + 5O2 -> 4CO2 + 2H2O", "2C2H2 + 5O2 -> 4CO2 + 2H2O", "2C2H2 + 5O2 -> 4CO2 + 2H2O", ReactionDirectionality.IRREVERSIBLE, List.of(term("COMP-C2H2", "C2H2", ReactionSide.REACTANT, 2, ReactionSpeciesState.GAS, 1), term("COMP-O2", "O2", ReactionSide.REACTANT, 5, ReactionSpeciesState.GAS, 2), term("COMP-CO2", "CO2", ReactionSide.PRODUCT, 4, ReactionSpeciesState.GAS, 3), term("COMP-H2O", "H2O", ReactionSide.PRODUCT, 2, ReactionSpeciesState.GAS, 4)), List.of(), List.of(), List.of(type(ReactionTypeCode.COMBUSTION, DerivationBasis.CURATED_REFERENCE, "Combustion of alkyne gas"), type(ReactionTypeCode.REDOX, DerivationBasis.CURATED_REFERENCE, "Acetylene oxidation")), prov("NIST-WEBBOOK-2025")));
        list.add(createReaction("11111111-1111-1111-1111-111111111117", "RXN-BENZENE-COMBUSTION", "Combustion of Benzene", List.of(new ReactionAlias("Benzene Oxidation", "COMMON")), "2C6H6 + 15O2 -> 12CO2 + 6H2O", "2C6H6 + 15O2 -> 12CO2 + 6H2O", "2C6H6 + 15O2 -> 12CO2 + 6H2O", ReactionDirectionality.IRREVERSIBLE, List.of(term("COMP-C6H6", "C6H6", ReactionSide.REACTANT, 2, ReactionSpeciesState.LIQUID, 1), term("COMP-O2", "O2", ReactionSide.REACTANT, 15, ReactionSpeciesState.GAS, 2), term("COMP-CO2", "CO2", ReactionSide.PRODUCT, 12, ReactionSpeciesState.GAS, 3), term("COMP-H2O", "H2O", ReactionSide.PRODUCT, 6, ReactionSpeciesState.GAS, 4)), List.of(), List.of(), List.of(type(ReactionTypeCode.COMBUSTION, DerivationBasis.CURATED_REFERENCE, "Combustion of aromatic benzene"), type(ReactionTypeCode.REDOX, DerivationBasis.CURATED_REFERENCE, "Benzene oxidation")), prov("NIST-WEBBOOK-2025")));
        list.add(createReaction("11111111-1111-1111-1111-111111111118", "RXN-H2SO4-NAOH-NEUT", "Neutralization of Sulfuric Acid with Sodium Hydroxide", List.of(new ReactionAlias("Sulfuric Acid Neutralization", "COMMON")), "H2SO4 + 2NaOH -> Na2SO4 + 2H2O", "H2SO4 + 2NaOH -> Na2SO4 + 2H2O", "H2SO4 + 2NaOH -> Na2SO4 + 2H2O", ReactionDirectionality.IRREVERSIBLE, List.of(term("COMP-H2SO4", "H2SO4", ReactionSide.REACTANT, 1, ReactionSpeciesState.AQUEOUS, 1), term("COMP-NAOH", "NaOH", ReactionSide.REACTANT, 2, ReactionSpeciesState.AQUEOUS, 2), term("COMP-NA2SO4", "Na2SO4", ReactionSide.PRODUCT, 1, ReactionSpeciesState.AQUEOUS, 3), term("COMP-H2O", "H2O", ReactionSide.PRODUCT, 2, ReactionSpeciesState.LIQUID, 4)), List.of(), List.of(), List.of(type(ReactionTypeCode.ACID_BASE_NEUTRALIZATION, DerivationBasis.CURATED_REFERENCE, "Diprotic acid neutralization with strong base"), type(ReactionTypeCode.DOUBLE_DISPLACEMENT, DerivationBasis.CURATED_REFERENCE, "Metathesis of sulfate and hydroxide ions")), prov("CRC-HANDBOOK-104")));
        list.add(createReaction("11111111-1111-1111-1111-111111111119", "RXN-H2SO4-MGOH2-NEUT", "Neutralization of Sulfuric Acid with Magnesium Hydroxide", List.of(new ReactionAlias("Milk of Magnesia Acid Reaction", "COMMON")), "H2SO4 + Mg(OH)2 -> MgSO4 + 2H2O", "H2SO4 + Mg(OH)2 -> MgSO4 + 2H2O", "H2SO4 + Mg(OH)2 -> MgSO4 + 2H2O", ReactionDirectionality.IRREVERSIBLE, List.of(term("COMP-H2SO4", "H2SO4", ReactionSide.REACTANT, 1, ReactionSpeciesState.AQUEOUS, 1), term("COMP-MG-OH-2", "Mg(OH)2", ReactionSide.REACTANT, 1, ReactionSpeciesState.SOLID, 2), term("COMP-MGSO4", "MgSO4", ReactionSide.PRODUCT, 1, ReactionSpeciesState.AQUEOUS, 3), term("COMP-H2O", "H2O", ReactionSide.PRODUCT, 2, ReactionSpeciesState.LIQUID, 4)), List.of(), List.of(), List.of(type(ReactionTypeCode.ACID_BASE_NEUTRALIZATION, DerivationBasis.CURATED_REFERENCE, "Acid reaction with insoluble metal hydroxide base"), type(ReactionTypeCode.DOUBLE_DISPLACEMENT, DerivationBasis.CURATED_REFERENCE, "Salt formation metathesis")), prov("CRC-HANDBOOK-104")));
        list.add(createReaction("11111111-1111-1111-1111-111111111120", "RXN-CACO3-HCL", "Reaction of Calcium Carbonate with Hydrochloric Acid", List.of(new ReactionAlias("Limestone Acid Dissolution", "COMMON")), "CaCO3 + 2HCl -> CaCl2 + CO2 + H2O", "CaCO3 + 2HCl -> CaCl2 + CO2 + H2O", "CaCO3 + 2HCl -> CaCl2 + CO2 + H2O", ReactionDirectionality.IRREVERSIBLE, List.of(term("COMP-CACO3", "CaCO3", ReactionSide.REACTANT, 1, ReactionSpeciesState.SOLID, 1), term("COMP-HCL", "HCl", ReactionSide.REACTANT, 2, ReactionSpeciesState.AQUEOUS, 2), term("COMP-CACL2", "CaCl2", ReactionSide.PRODUCT, 1, ReactionSpeciesState.AQUEOUS, 3), term("COMP-CO2", "CO2", ReactionSide.PRODUCT, 1, ReactionSpeciesState.GAS, 4), term("COMP-H2O", "H2O", ReactionSide.PRODUCT, 1, ReactionSpeciesState.LIQUID, 5)), List.of(), List.of(), List.of(type(ReactionTypeCode.DOUBLE_DISPLACEMENT, DerivationBasis.CURATED_REFERENCE, "Metathesis producing carbonic acid intermediate"), type(ReactionTypeCode.GAS_EVOLUTION, DerivationBasis.SAFE_RULE_DERIVED, "RULE-EXPLICIT-GAS-PRODUCT: Gas state carbon dioxide produced from solid/aqueous reactants")), prov("CRC-HANDBOOK-104")));
        list.add(createReaction("11111111-1111-1111-1111-111111111121", "RXN-MGOH2-HCL", "Reaction of Magnesium Hydroxide with Hydrochloric Acid", List.of(new ReactionAlias("Antacid Neutralization", "COMMON")), "Mg(OH)2 + 2HCl -> MgCl2 + 2H2O", "Mg(OH)2 + 2HCl -> MgCl2 + 2H2O", "Mg(OH)2 + 2HCl -> MgCl2 + 2H2O", ReactionDirectionality.IRREVERSIBLE, List.of(term("COMP-MG-OH-2", "Mg(OH)2", ReactionSide.REACTANT, 1, ReactionSpeciesState.SOLID, 1), term("COMP-HCL", "HCl", ReactionSide.REACTANT, 2, ReactionSpeciesState.AQUEOUS, 2), term("COMP-MGCL2", "MgCl2", ReactionSide.PRODUCT, 1, ReactionSpeciesState.AQUEOUS, 3), term("COMP-H2O", "H2O", ReactionSide.PRODUCT, 2, ReactionSpeciesState.LIQUID, 4)), List.of(), List.of(), List.of(type(ReactionTypeCode.ACID_BASE_NEUTRALIZATION, DerivationBasis.CURATED_REFERENCE, "Stomach acid neutralization by magnesia base"), type(ReactionTypeCode.DOUBLE_DISPLACEMENT, DerivationBasis.CURATED_REFERENCE, "Magnesium chloride salt formation metathesis")), prov("CRC-HANDBOOK-104")));
        list.add(createReaction("11111111-1111-1111-1111-111111111122", "RXN-GLUCOSE-COMBUSTION", "Combustion of Glucose", List.of(new ReactionAlias("Glucose Oxidation", "COMMON")), "C6H12O6 + 6O2 -> 6CO2 + 6H2O", "C6H12O6 + 6O2 -> 6CO2 + 6H2O", "C6H12O6 + 6O2 -> 6CO2 + 6H2O", ReactionDirectionality.IRREVERSIBLE, List.of(term("COMP-GLUCOSE", "C6H12O6", ReactionSide.REACTANT, 1, ReactionSpeciesState.SOLID, 1), term("COMP-O2", "O2", ReactionSide.REACTANT, 6, ReactionSpeciesState.GAS, 2), term("COMP-CO2", "CO2", ReactionSide.PRODUCT, 6, ReactionSpeciesState.GAS, 3), term("COMP-H2O", "H2O", ReactionSide.PRODUCT, 6, ReactionSpeciesState.LIQUID, 4)), List.of(), List.of(), List.of(type(ReactionTypeCode.COMBUSTION, DerivationBasis.CURATED_REFERENCE, "Complete combustion of monosaccharide sugar"), type(ReactionTypeCode.REDOX, DerivationBasis.CURATED_REFERENCE, "Cellular respiration chemical stoichiometry model")), prov("CRC-HANDBOOK-104")));
        list.add(createReaction("11111111-1111-1111-1111-111111111123", "RXN-SUCROSE-COMBUSTION", "Combustion of Sucrose", List.of(new ReactionAlias("Table Sugar Oxidation", "COMMON")), "C12H22O11 + 12O2 -> 12CO2 + 11H2O", "C12H22O11 + 12O2 -> 12CO2 + 11H2O", "C12H22O11 + 12O2 -> 12CO2 + 11H2O", ReactionDirectionality.IRREVERSIBLE, List.of(term("COMP-SUCROSE", "C12H22O11", ReactionSide.REACTANT, 1, ReactionSpeciesState.SOLID, 1), term("COMP-O2", "O2", ReactionSide.REACTANT, 12, ReactionSpeciesState.GAS, 2), term("COMP-CO2", "CO2", ReactionSide.PRODUCT, 12, ReactionSpeciesState.GAS, 3), term("COMP-H2O", "H2O", ReactionSide.PRODUCT, 11, ReactionSpeciesState.LIQUID, 4)), List.of(), List.of(), List.of(type(ReactionTypeCode.COMBUSTION, DerivationBasis.CURATED_REFERENCE, "Combustion of disaccharide sucrose"), type(ReactionTypeCode.REDOX, DerivationBasis.CURATED_REFERENCE, "Sucrose carbon oxidation")), prov("CRC-HANDBOOK-104")));
        list.add(createReaction("11111111-1111-1111-1111-111111111124", "RXN-CO2-H2O-SYNTHESIS", "Formation of Carbonic Acid", List.of(new ReactionAlias("Carbon Dioxide Hydration", "COMMON")), "CO2 + H2O -> H2CO3", "CO2 + H2O -> H2CO3", "CO2 + H2O -> H2CO3", ReactionDirectionality.REVERSIBLE, List.of(term("COMP-CO2", "CO2", ReactionSide.REACTANT, 1, ReactionSpeciesState.GAS, 1), term("COMP-H2O", "H2O", ReactionSide.REACTANT, 1, ReactionSpeciesState.LIQUID, 2), term("COMP-H2CO3", "H2CO3", ReactionSide.PRODUCT, 1, ReactionSpeciesState.AQUEOUS, 3)), List.of(), List.of(), List.of(type(ReactionTypeCode.SYNTHESIS, DerivationBasis.CURATED_REFERENCE, "Acidic oxide reaction with water"), type(ReactionTypeCode.HYDRATION, DerivationBasis.CURATED_REFERENCE, "Gas dissolution and hydration"), type(ReactionTypeCode.REVERSIBLE_REACTION, DerivationBasis.SAFE_RULE_DERIVED, "RULE-DIRECTIONALITY: Reaction marked as reversible in reference catalogue")), prov("CRC-HANDBOOK-104")));
        list.add(createReaction("11111111-1111-1111-1111-111111111125", "RXN-H2CO3-DECOMP", "Decomposition of Carbonic Acid", List.of(new ReactionAlias("Carbonic Acid Breakdown", "COMMON")), "H2CO3 -> CO2 + H2O", "H2CO3 -> CO2 + H2O", "H2CO3 -> CO2 + H2O", ReactionDirectionality.REVERSIBLE, List.of(term("COMP-H2CO3", "H2CO3", ReactionSide.REACTANT, 1, ReactionSpeciesState.AQUEOUS, 1), term("COMP-CO2", "CO2", ReactionSide.PRODUCT, 1, ReactionSpeciesState.GAS, 2), term("COMP-H2O", "H2O", ReactionSide.PRODUCT, 1, ReactionSpeciesState.LIQUID, 3)), List.of(), List.of(), List.of(type(ReactionTypeCode.DECOMPOSITION, DerivationBasis.CURATED_REFERENCE, "Unstable acid breakdown into gas and water"), type(ReactionTypeCode.GAS_EVOLUTION, DerivationBasis.SAFE_RULE_DERIVED, "RULE-EXPLICIT-GAS-PRODUCT: Gas state carbon dioxide produced"), type(ReactionTypeCode.REVERSIBLE_REACTION, DerivationBasis.SAFE_RULE_DERIVED, "RULE-DIRECTIONALITY: Reaction marked as reversible in reference catalogue")), prov("CRC-HANDBOOK-104")));
        list.add(createReaction("11111111-1111-1111-1111-111111111126", "RXN-CO-OXIDATION", "Oxidation of Carbon Monoxide", List.of(new ReactionAlias("Carbon Monoxide Combustion", "COMMON")), "2CO + O2 -> 2CO2", "2CO + O2 -> 2CO2", "2CO + O2 -> 2CO2", ReactionDirectionality.IRREVERSIBLE, List.of(term("COMP-CO", "CO", ReactionSide.REACTANT, 2, ReactionSpeciesState.GAS, 1), term("COMP-O2", "O2", ReactionSide.REACTANT, 1, ReactionSpeciesState.GAS, 2), term("COMP-CO2", "CO2", ReactionSide.PRODUCT, 2, ReactionSpeciesState.GAS, 3)), List.of(), List.of(), List.of(type(ReactionTypeCode.SYNTHESIS, DerivationBasis.CURATED_REFERENCE, "Combination of carbon monoxide and oxygen gas"), type(ReactionTypeCode.COMBUSTION, DerivationBasis.CURATED_REFERENCE, "Exothermic gas oxidation"), type(ReactionTypeCode.REDOX, DerivationBasis.CURATED_REFERENCE, "Carbon oxidation state increase from +2 to +4")), prov("CRC-HANDBOOK-104")));

        return Collections.unmodifiableList(list);
    }

    private static Reaction createReaction(String uuidStr, String codeStr, String nameStr, List<ReactionAlias> aliases,
                                           String origEq, String normEq, String canonEq, ReactionDirectionality directionality,
                                           List<ReactionTerm> terms, List<Catalyst> catalysts,
                                           List<ReactionConditionSet> conditionSets, List<ReactionTypeAssignment> types,
                                           ReactionProvenance prov) {
        ReactionId id = new ReactionId(UUID.fromString(uuidStr));
        ReactionCode code = new ReactionCode(codeStr);
        ReactionName name = new ReactionName(nameStr);
        ReactionEquation eq = new ReactionEquation(origEq, normEq, canonEq, ReactionEquation.generateSignature(terms, directionality));

        return new Reaction(id, code, name, aliases, eq, terms, directionality, catalysts, conditionSets, types, REACTION_DATASET_VERSION, prov);
    }

    private static ReactionTerm term(String compoundCode, String formula, ReactionSide side, int coeff, ReactionSpeciesState state, int order) {
        UUID compoundId = UUID.nameUUIDFromBytes(("compound-" + compoundCode.trim()).getBytes());
        return new ReactionTerm(compoundId, compoundCode, formula, side, BigInteger.valueOf(coeff), state, order);
    }

    private static ReactionTypeAssignment type(ReactionTypeCode code, DerivationBasis basis, String exp) {
        return new ReactionTypeAssignment(code, basis, exp);
    }

    private static ReactionProvenance prov(String docId) {
        return new ReactionProvenance(docId, List.of("equation", "reactionName", "terms"), "Curated reference reaction");
    }
}
