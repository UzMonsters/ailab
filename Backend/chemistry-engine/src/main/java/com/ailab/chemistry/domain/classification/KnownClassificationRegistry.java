package com.ailab.chemistry.domain.classification;

import com.ailab.chemistry.domain.compound.Compound;
import com.ailab.chemistry.domain.compound.CompoundCode;
import com.ailab.chemistry.domain.compound.ElementMassProvider;
import com.ailab.chemistry.domain.compound.KnownCompoundRegistry;

import java.util.*;

public final class KnownClassificationRegistry {

    public static final String TAXONOMY_VERSION_ID = "chemical-classification-v1.0.0";
    public static final String TAXONOMY_NAME = "Educational Chemical Classification Taxonomy";
    public static final String TAXONOMY_DATE = "2026-08-05";

    public static final ClassificationTaxonomyVersion VERSION = new ClassificationTaxonomyVersion(
            TAXONOMY_VERSION_ID, TAXONOMY_NAME, TAXONOMY_DATE
    );

    public static final ClassificationProvenance CRC_PROVENANCE = ClassificationProvenance.curatedReference(
            "CRC-HANDBOOK-104",
            "CRC Handbook of Chemistry and Physics, 104th Edition",
            "CRC Press / Taylor & Francis Group",
            "v1.0.0",
            "2026-08-05",
            "Curated chemical classification reference"
    );

    private static final List<ClassificationDefinition> DEFINITIONS = List.of(
        // Substance Domain
        new ClassificationDefinition(new ClassificationCode("ELEMENTAL_SUBSTANCE"), ClassificationDimension.SUBSTANCE_DOMAIN, "Elemental Substance", "Pure single-element substance", 1, null),
        new ClassificationDefinition(new ClassificationCode("INORGANIC_COMPOUND"), ClassificationDimension.SUBSTANCE_DOMAIN, "Inorganic Compound", "Chemical compound not classified as organic", 2, null),
        new ClassificationDefinition(new ClassificationCode("ORGANIC_COMPOUND"), ClassificationDimension.SUBSTANCE_DOMAIN, "Organic Compound", "Carbon-containing compound (excluding simple oxides/carbonates)", 3, null),
        new ClassificationDefinition(new ClassificationCode("ORGANOMETALLIC_COMPOUND"), ClassificationDimension.SUBSTANCE_DOMAIN, "Organometallic Compound", "Compound containing direct metal-carbon bond", 4, null),
        new ClassificationDefinition(new ClassificationCode("COORDINATION_COMPOUND"), ClassificationDimension.SUBSTANCE_DOMAIN, "Coordination Compound", "Complex compound with central metal atom and ligands", 5, null),
        new ClassificationDefinition(new ClassificationCode("UNKNOWN"), ClassificationDimension.SUBSTANCE_DOMAIN, "Unknown Domain", "Unclassified substance domain", 6, null),

        // Composition Pattern
        new ClassificationDefinition(new ClassificationCode("MONOATOMIC_OR_ELEMENTAL"), ClassificationDimension.COMPOSITION_PATTERN, "Monoatomic or Elemental", "Composed of atoms of a single element type", 10, null),
        new ClassificationDefinition(new ClassificationCode("BINARY_COMPOSITION"), ClassificationDimension.COMPOSITION_PATTERN, "Binary Composition", "Composed of exactly two distinct element types", 11, null),
        new ClassificationDefinition(new ClassificationCode("TERNARY_COMPOSITION"), ClassificationDimension.COMPOSITION_PATTERN, "Ternary Composition", "Composed of exactly three distinct element types", 12, null),
        new ClassificationDefinition(new ClassificationCode("QUATERNARY_OR_HIGHER_COMPOSITION"), ClassificationDimension.COMPOSITION_PATTERN, "Quaternary or Higher Composition", "Composed of four or more distinct element types", 13, null),
        new ClassificationDefinition(new ClassificationCode("HYDRATE"), ClassificationDimension.COMPOSITION_PATTERN, "Hydrate", "Compound containing bound water of crystallization", 14, null),
        new ClassificationDefinition(new ClassificationCode("CHARGED_SPECIES"), ClassificationDimension.COMPOSITION_PATTERN, "Charged Species", "Ion or charged chemical species", 15, null),
        new ClassificationDefinition(new ClassificationCode("NEUTRAL_SPECIES"), ClassificationDimension.COMPOSITION_PATTERN, "Neutral Species", "Electrically neutral molecule or formula unit", 16, null),

        // Inorganic Functional Class
        new ClassificationDefinition(new ClassificationCode("OXIDE"), ClassificationDimension.INORGANIC_FUNCTIONAL_CLASS, "Oxide", "Binary or complex oxygen-containing inorganic compound", 20, new ClassificationCode("INORGANIC_COMPOUND")),
        new ClassificationDefinition(new ClassificationCode("PEROXIDE"), ClassificationDimension.INORGANIC_FUNCTIONAL_CLASS, "Peroxide", "Compound containing an oxygen-oxygen single bond (O2 2-)", 21, new ClassificationCode("OXIDE")),
        new ClassificationDefinition(new ClassificationCode("HYDRIDE"), ClassificationDimension.INORGANIC_FUNCTIONAL_CLASS, "Hydride", "Compound of hydrogen with another element", 22, new ClassificationCode("INORGANIC_COMPOUND")),
        new ClassificationDefinition(new ClassificationCode("HYDROXIDE"), ClassificationDimension.INORGANIC_FUNCTIONAL_CLASS, "Hydroxide", "Compound containing the hydroxide anion (OH-)", 23, new ClassificationCode("INORGANIC_COMPOUND")),
        new ClassificationDefinition(new ClassificationCode("ACID"), ClassificationDimension.INORGANIC_FUNCTIONAL_CLASS, "Acid", "Proton donor (Brønsted-Lowry acid)", 24, new ClassificationCode("INORGANIC_COMPOUND")),
        new ClassificationDefinition(new ClassificationCode("BASE"), ClassificationDimension.INORGANIC_FUNCTIONAL_CLASS, "Base", "Proton acceptor or hydroxide donor", 25, new ClassificationCode("INORGANIC_COMPOUND")),
        new ClassificationDefinition(new ClassificationCode("SALT"), ClassificationDimension.INORGANIC_FUNCTIONAL_CLASS, "Salt", "Ionic compound composed of cations and anions", 26, new ClassificationCode("INORGANIC_COMPOUND")),
        new ClassificationDefinition(new ClassificationCode("OTHER_INORGANIC"), ClassificationDimension.INORGANIC_FUNCTIONAL_CLASS, "Other Inorganic", "Inorganic compound not fitting primary classes", 27, new ClassificationCode("INORGANIC_COMPOUND")),

        // Acid Subtype
        new ClassificationDefinition(new ClassificationCode("BINARY_ACID"), ClassificationDimension.ACID_SUBTYPE, "Binary Acid", "Acid composed of hydrogen and one nonmetal element", 30, new ClassificationCode("ACID")),
        new ClassificationDefinition(new ClassificationCode("OXYACID"), ClassificationDimension.ACID_SUBTYPE, "Oxyacid", "Acid containing hydrogen, oxygen, and another element", 31, new ClassificationCode("ACID")),
        new ClassificationDefinition(new ClassificationCode("OTHER_ACID"), ClassificationDimension.ACID_SUBTYPE, "Other Acid", "Acid subtype not falling under binary acid or oxyacid", 32, new ClassificationCode("ACID")),

        // Salt Subtype
        new ClassificationDefinition(new ClassificationCode("NORMAL_SALT"), ClassificationDimension.SALT_SUBTYPE, "Normal Salt", "Salt formed by complete neutralization of an acid by a base", 40, new ClassificationCode("SALT")),
        new ClassificationDefinition(new ClassificationCode("ACID_SALT"), ClassificationDimension.SALT_SUBTYPE, "Acid Salt", "Salt containing replaceable hydrogen atoms from a polyprotic acid", 41, new ClassificationCode("SALT")),
        new ClassificationDefinition(new ClassificationCode("BASIC_SALT"), ClassificationDimension.SALT_SUBTYPE, "Basic Salt", "Salt containing hydroxide ions or basic anions", 42, new ClassificationCode("SALT")),
        new ClassificationDefinition(new ClassificationCode("DOUBLE_SALT"), ClassificationDimension.SALT_SUBTYPE, "Double Salt", "Salt containing more than one cation or anion", 43, new ClassificationCode("SALT")),
        new ClassificationDefinition(new ClassificationCode("HYDRATED_SALT"), ClassificationDimension.SALT_SUBTYPE, "Hydrated Salt", "Salt incorporating water molecules into its crystal lattice", 44, new ClassificationCode("SALT")),
        new ClassificationDefinition(new ClassificationCode("OTHER_SALT"), ClassificationDimension.SALT_SUBTYPE, "Other Salt", "Salt subtype not falling under primary categories", 45, new ClassificationCode("SALT")),

        // Organic Functional Class
        new ClassificationDefinition(new ClassificationCode("HYDROCARBON"), ClassificationDimension.ORGANIC_FUNCTIONAL_CLASS, "Hydrocarbon", "Organic compound composed entirely of hydrogen and carbon", 50, new ClassificationCode("ORGANIC_COMPOUND")),
        new ClassificationDefinition(new ClassificationCode("ALCOHOL"), ClassificationDimension.ORGANIC_FUNCTIONAL_CLASS, "Alcohol", "Organic compound containing a hydroxyl group (-OH) bound to saturated carbon", 51, new ClassificationCode("ORGANIC_COMPOUND")),
        new ClassificationDefinition(new ClassificationCode("ETHER"), ClassificationDimension.ORGANIC_FUNCTIONAL_CLASS, "Ether", "Organic compound containing an ether group (R-O-R)", 52, new ClassificationCode("ORGANIC_COMPOUND")),
        new ClassificationDefinition(new ClassificationCode("ALDEHYDE"), ClassificationDimension.ORGANIC_FUNCTIONAL_CLASS, "Aldehyde", "Organic compound containing a carbonyl group (-CHO)", 53, new ClassificationCode("ORGANIC_COMPOUND")),
        new ClassificationDefinition(new ClassificationCode("KETONE"), ClassificationDimension.ORGANIC_FUNCTIONAL_CLASS, "Ketone", "Organic compound containing a carbonyl group (R-CO-R)", 54, new ClassificationCode("ORGANIC_COMPOUND")),
        new ClassificationDefinition(new ClassificationCode("CARBOXYLIC_ACID"), ClassificationDimension.ORGANIC_FUNCTIONAL_CLASS, "Carboxylic Acid", "Organic compound containing a carboxyl group (-COOH)", 55, new ClassificationCode("ORGANIC_COMPOUND")),
        new ClassificationDefinition(new ClassificationCode("ESTER"), ClassificationDimension.ORGANIC_FUNCTIONAL_CLASS, "Ester", "Organic compound derived from an acid where OH is replaced by O-alkyl", 56, new ClassificationCode("ORGANIC_COMPOUND")),
        new ClassificationDefinition(new ClassificationCode("AMINE"), ClassificationDimension.ORGANIC_FUNCTIONAL_CLASS, "Amine", "Organic compound derived from ammonia by replacing hydrogen with alkyl/aryl groups", 57, new ClassificationCode("ORGANIC_COMPOUND")),
        new ClassificationDefinition(new ClassificationCode("AMIDE"), ClassificationDimension.ORGANIC_FUNCTIONAL_CLASS, "Amide", "Organic compound containing a carbonyl group bound to nitrogen", 58, new ClassificationCode("ORGANIC_COMPOUND")),
        new ClassificationDefinition(new ClassificationCode("CARBOHYDRATE"), ClassificationDimension.ORGANIC_FUNCTIONAL_CLASS, "Carbohydrate", "Organic compound composed of carbon, hydrogen, and oxygen (polyhydroxy aldehydes/ketones)", 59, new ClassificationCode("ORGANIC_COMPOUND")),
        new ClassificationDefinition(new ClassificationCode("OTHER_ORGANIC"), ClassificationDimension.ORGANIC_FUNCTIONAL_CLASS, "Other Organic", "Organic compound not fitting primary functional classes", 60, new ClassificationCode("ORGANIC_COMPOUND"))
    );

    public static final ClassificationTaxonomy TAXONOMY = new ClassificationTaxonomy(VERSION, DEFINITIONS);

    private KnownClassificationRegistry() {}

    public static List<ClassificationProfile> buildAll55Profiles(ElementMassProvider massProvider) {
        List<Compound> compounds = KnownCompoundRegistry.buildAll55CoreCompounds(massProvider);
        List<ClassificationProfile> profiles = new ArrayList<>();

        for (Compound c : compounds) {
            String code = c.getCode().getValue();
            List<ClassificationAssignment> assignments = new ArrayList<>(ClassificationDerivationEngine.deriveSafeAssignments(c));

            // Curated functional assignments based on compound identity
            switch (code) {
                // Elemental Substances
                case "COMP-H2", "COMP-O2", "COMP-N2", "COMP-F2", "COMP-CL2", "COMP-BR2", "COMP-I2" -> {
                    // Handled via deriveSafeAssignments -> ELEMENTAL_SUBSTANCE
                }

                // Inorganics - Oxides & Peroxides
                case "COMP-H2O" -> {
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("INORGANIC_COMPOUND"), ClassificationDimension.SUBSTANCE_DOMAIN, CRC_PROVENANCE, "Curated inorganic compound"));
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("OXIDE"), ClassificationDimension.INORGANIC_FUNCTIONAL_CLASS, CRC_PROVENANCE, "Curated hydrogen oxide"));
                }
                case "COMP-H2O2" -> {
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("INORGANIC_COMPOUND"), ClassificationDimension.SUBSTANCE_DOMAIN, CRC_PROVENANCE, "Curated inorganic compound"));
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("OXIDE"), ClassificationDimension.INORGANIC_FUNCTIONAL_CLASS, CRC_PROVENANCE, "Curated oxide parent"));
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("PEROXIDE"), ClassificationDimension.INORGANIC_FUNCTIONAL_CLASS, CRC_PROVENANCE, "Curated hydrogen peroxide"));
                }
                case "COMP-CO2", "COMP-CO" -> {
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("INORGANIC_COMPOUND"), ClassificationDimension.SUBSTANCE_DOMAIN, CRC_PROVENANCE, "Curated inorganic carbon oxide"));
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("OXIDE"), ClassificationDimension.INORGANIC_FUNCTIONAL_CLASS, CRC_PROVENANCE, "Curated oxide"));
                }
                case "COMP-NH3" -> {
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("INORGANIC_COMPOUND"), ClassificationDimension.SUBSTANCE_DOMAIN, CRC_PROVENANCE, "Curated inorganic hydride"));
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("HYDRIDE"), ClassificationDimension.INORGANIC_FUNCTIONAL_CLASS, CRC_PROVENANCE, "Curated nitrogen hydride (azane)"));
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("BASE"), ClassificationDimension.INORGANIC_FUNCTIONAL_CLASS, CRC_PROVENANCE, "Curated weak base"));
                }

                // Inorganic Acids
                case "COMP-HCL" -> {
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("INORGANIC_COMPOUND"), ClassificationDimension.SUBSTANCE_DOMAIN, CRC_PROVENANCE, "Curated inorganic acid"));
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("ACID"), ClassificationDimension.INORGANIC_FUNCTIONAL_CLASS, CRC_PROVENANCE, "Curated mineral acid"));
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("BINARY_ACID"), ClassificationDimension.ACID_SUBTYPE, CRC_PROVENANCE, "Curated binary acid"));
                }
                case "COMP-HNO3", "COMP-H2SO4", "COMP-H3PO4", "COMP-H2CO3" -> {
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("INORGANIC_COMPOUND"), ClassificationDimension.SUBSTANCE_DOMAIN, CRC_PROVENANCE, "Curated inorganic oxyacid"));
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("ACID"), ClassificationDimension.INORGANIC_FUNCTIONAL_CLASS, CRC_PROVENANCE, "Curated mineral acid"));
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("OXYACID"), ClassificationDimension.ACID_SUBTYPE, CRC_PROVENANCE, "Curated oxyacid"));
                }

                // Hydroxides & Bases
                case "COMP-NAOH", "COMP-KOH", "COMP-CA-OH-2", "COMP-MG-OH-2", "COMP-AL-OH-3" -> {
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("INORGANIC_COMPOUND"), ClassificationDimension.SUBSTANCE_DOMAIN, CRC_PROVENANCE, "Curated inorganic hydroxide"));
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("HYDROXIDE"), ClassificationDimension.INORGANIC_FUNCTIONAL_CLASS, CRC_PROVENANCE, "Curated metal hydroxide"));
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("BASE"), ClassificationDimension.INORGANIC_FUNCTIONAL_CLASS, CRC_PROVENANCE, "Curated base"));
                }

                // Normal Salts
                case "COMP-NACL", "COMP-KCL", "COMP-NABR", "COMP-KI", "COMP-CACL2", "COMP-MGCL2", "COMP-NA2CO3", "COMP-CACO3", "COMP-NA2SO4", "COMP-MGSO4", "COMP-AGNO3", "COMP-BACL2", "COMP-KMNO4", "COMP-K2CR2O7", "COMP-CUSO4" -> {
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("INORGANIC_COMPOUND"), ClassificationDimension.SUBSTANCE_DOMAIN, CRC_PROVENANCE, "Curated inorganic salt"));
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("SALT"), ClassificationDimension.INORGANIC_FUNCTIONAL_CLASS, CRC_PROVENANCE, "Curated ionic salt"));
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("NORMAL_SALT"), ClassificationDimension.SALT_SUBTYPE, CRC_PROVENANCE, "Curated normal salt"));
                }

                // Acid Salts
                case "COMP-NAHCO3" -> {
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("INORGANIC_COMPOUND"), ClassificationDimension.SUBSTANCE_DOMAIN, CRC_PROVENANCE, "Curated inorganic acid salt"));
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("SALT"), ClassificationDimension.INORGANIC_FUNCTIONAL_CLASS, CRC_PROVENANCE, "Curated ionic salt"));
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("ACID_SALT"), ClassificationDimension.SALT_SUBTYPE, CRC_PROVENANCE, "Curated bicarbonate acid salt"));
                }

                // Hydrated Salts
                case "COMP-CUSO4-5H2O" -> {
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("INORGANIC_COMPOUND"), ClassificationDimension.SUBSTANCE_DOMAIN, CRC_PROVENANCE, "Curated inorganic hydrated salt"));
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("SALT"), ClassificationDimension.INORGANIC_FUNCTIONAL_CLASS, CRC_PROVENANCE, "Curated ionic salt"));
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("HYDRATED_SALT"), ClassificationDimension.SALT_SUBTYPE, CRC_PROVENANCE, "Curated copper sulfate pentahydrate salt"));
                }

                // Metal Oxides
                case "COMP-AL2O3", "COMP-FE2O3", "COMP-FE3O4", "COMP-CUO" -> {
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("INORGANIC_COMPOUND"), ClassificationDimension.SUBSTANCE_DOMAIN, CRC_PROVENANCE, "Curated metal oxide"));
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("OXIDE"), ClassificationDimension.INORGANIC_FUNCTIONAL_CLASS, CRC_PROVENANCE, "Curated inorganic oxide"));
                }

                // Hydrocarbons
                case "COMP-CH4", "COMP-C2H6", "COMP-C3H8", "COMP-C4H10", "COMP-C2H4", "COMP-C2H2", "COMP-C6H6" -> {
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("ORGANIC_COMPOUND"), ClassificationDimension.SUBSTANCE_DOMAIN, CRC_PROVENANCE, "Curated organic hydrocarbon"));
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("HYDROCARBON"), ClassificationDimension.ORGANIC_FUNCTIONAL_CLASS, CRC_PROVENANCE, "Curated hydrocarbon"));
                }

                // Organic Acids
                case "COMP-CH3COOH" -> {
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("ORGANIC_COMPOUND"), ClassificationDimension.SUBSTANCE_DOMAIN, CRC_PROVENANCE, "Curated organic carboxylic acid"));
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("CARBOXYLIC_ACID"), ClassificationDimension.ORGANIC_FUNCTIONAL_CLASS, CRC_PROVENANCE, "Curated acetic acid"));
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("OXYACID"), ClassificationDimension.ACID_SUBTYPE, CRC_PROVENANCE, "Curated organic oxyacid"));
                }

                // ISOMERS Differing in Functional Class
                case "COMP-ETHANOL" -> {
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("ORGANIC_COMPOUND"), ClassificationDimension.SUBSTANCE_DOMAIN, CRC_PROVENANCE, "Curated organic alcohol"));
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("ALCOHOL"), ClassificationDimension.ORGANIC_FUNCTIONAL_CLASS, CRC_PROVENANCE, "Curated ethanol alcohol"));
                }
                case "COMP-DIMETHYL-ETHER" -> {
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("ORGANIC_COMPOUND"), ClassificationDimension.SUBSTANCE_DOMAIN, CRC_PROVENANCE, "Curated organic ether"));
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("ETHER"), ClassificationDimension.ORGANIC_FUNCTIONAL_CLASS, CRC_PROVENANCE, "Curated dimethyl ether"));
                }

                // Carbohydrates
                case "COMP-GLUCOSE", "COMP-SUCROSE" -> {
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("ORGANIC_COMPOUND"), ClassificationDimension.SUBSTANCE_DOMAIN, CRC_PROVENANCE, "Curated organic carbohydrate"));
                    assignments.add(ClassificationAssignment.curated(new ClassificationCode("CARBOHYDRATE"), ClassificationDimension.ORGANIC_FUNCTIONAL_CLASS, CRC_PROVENANCE, "Curated saccharide carbohydrate"));
                }
            }

            profiles.add(new ClassificationProfile(c.getId(), VERSION, assignments));
        }

        return Collections.unmodifiableList(profiles);
    }
}
