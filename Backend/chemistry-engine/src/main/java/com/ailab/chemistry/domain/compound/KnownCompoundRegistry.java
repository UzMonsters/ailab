package com.ailab.chemistry.domain.compound;

import com.ailab.chemistry.domain.element.KnownElementRegistry;
import com.ailab.chemistry.domain.formula.ChemicalFormula;
import com.ailab.chemistry.domain.formula.DefaultFormulaParser;
import com.ailab.chemistry.domain.formula.FormulaParser;

import java.math.BigInteger;
import java.util.*;

public final class KnownCompoundRegistry {

    public static final String COMPOUND_DATASET_VERSION = "compound-core-v1.0.0";
    public static final String COMPOUND_DATASET_NAME = "Core Educational Compound Catalogue";
    public static final String COMPOUND_DATASET_DATE = "2026-08-04";

    private static final FormulaParser PARSER = new DefaultFormulaParser();
    private static final MolarMassCalculator CALCULATOR = new MolarMassCalculatorImpl();

    private static final CompoundCatalogVersion CATALOG_VERSION = new CompoundCatalogVersion(
            COMPOUND_DATASET_VERSION, COMPOUND_DATASET_NAME, COMPOUND_DATASET_DATE
    );

    private static final CompoundProvenance PROVENANCE = new CompoundProvenance(
            "CRC-HANDBOOK-104",
            "CRC Handbook of Chemistry and Physics, 104th Edition",
            "CRC Press / Taylor & Francis Group",
            COMPOUND_DATASET_VERSION,
            COMPOUND_DATASET_DATE,
            "Scientific attribution recorded for data provenance and reference transparency."
    );

    private static final List<SeedCompoundSpec> SPECS = List.of(
        // Elemental molecules
        new SeedCompoundSpec("COMP-H2", "Hydrogen gas", "H2", List.of(new CompoundAlias("Dihydrogen", CompoundAliasRole.SYSTEMATIC)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "1333-74-0"), new CompoundExternalIdentifier(ExternalIdentifierScheme.PUBCHEM_CID, "783"))),
        new SeedCompoundSpec("COMP-O2", "Oxygen gas", "O2", List.of(new CompoundAlias("Dioxygen", CompoundAliasRole.SYSTEMATIC)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "7782-44-7"), new CompoundExternalIdentifier(ExternalIdentifierScheme.PUBCHEM_CID, "977"))),
        new SeedCompoundSpec("COMP-N2", "Nitrogen gas", "N2", List.of(new CompoundAlias("Dinitrogen", CompoundAliasRole.SYSTEMATIC)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "7727-37-9"), new CompoundExternalIdentifier(ExternalIdentifierScheme.PUBCHEM_CID, "947"))),
        new SeedCompoundSpec("COMP-F2", "Fluorine gas", "F2", List.of(new CompoundAlias("Difluorine", CompoundAliasRole.SYSTEMATIC)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "7782-41-4"))),
        new SeedCompoundSpec("COMP-CL2", "Chlorine gas", "Cl2", List.of(new CompoundAlias("Dichlorine", CompoundAliasRole.SYSTEMATIC)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "7782-50-5"))),
        new SeedCompoundSpec("COMP-BR2", "Bromine liquid", "Br2", List.of(new CompoundAlias("Dibromine", CompoundAliasRole.SYSTEMATIC)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "7726-95-6"))),
        new SeedCompoundSpec("COMP-I2", "Iodine solid", "I2", List.of(new CompoundAlias("Diiodine", CompoundAliasRole.SYSTEMATIC)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "7553-56-2"))),

        // Inorganic compounds
        new SeedCompoundSpec("COMP-H2O", "Water", "H2O", List.of(new CompoundAlias("Oxidane", CompoundAliasRole.SYSTEMATIC), new CompoundAlias("Dihydrogen monoxide", CompoundAliasRole.COMMON)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "7732-18-5"), new CompoundExternalIdentifier(ExternalIdentifierScheme.PUBCHEM_CID, "962"))),
        new SeedCompoundSpec("COMP-H2O2", "Hydrogen peroxide", "H2O2", List.of(new CompoundAlias("Dihydrogen dioxide", CompoundAliasRole.SYSTEMATIC)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "7722-84-1"))),
        new SeedCompoundSpec("COMP-CO2", "Carbon dioxide", "CO2", List.of(new CompoundAlias("Carbonic acid gas", CompoundAliasRole.HISTORICAL)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "124-38-9"), new CompoundExternalIdentifier(ExternalIdentifierScheme.PUBCHEM_CID, "280"))),
        new SeedCompoundSpec("COMP-CO", "Carbon monoxide", "CO", List.of(new CompoundAlias("Carbon oxide", CompoundAliasRole.SYSTEMATIC)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "630-08-0"))),
        new SeedCompoundSpec("COMP-NH3", "Ammonia", "NH3", List.of(new CompoundAlias("Azane", CompoundAliasRole.SYSTEMATIC)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "7664-41-7"), new CompoundExternalIdentifier(ExternalIdentifierScheme.PUBCHEM_CID, "222"))),
        new SeedCompoundSpec("COMP-CH4", "Methane", "CH4", List.of(new CompoundAlias("Marsh gas", CompoundAliasRole.HISTORICAL), new CompoundAlias("Carbane", CompoundAliasRole.SYSTEMATIC)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "74-82-8"), new CompoundExternalIdentifier(ExternalIdentifierScheme.PUBCHEM_CID, "297"))),

        // Hydrocarbons & Organic
        new SeedCompoundSpec("COMP-C2H6", "Ethane", "C2H6", List.of(new CompoundAlias("Bicarbon", CompoundAliasRole.HISTORICAL)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "74-84-0"))),
        new SeedCompoundSpec("COMP-C3H8", "Propane", "C3H8", List.of(new CompoundAlias("Dimethylmethane", CompoundAliasRole.COMMON)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "74-98-6"))),
        new SeedCompoundSpec("COMP-C4H10", "Butane", "C4H10", List.of(new CompoundAlias("n-Butane", CompoundAliasRole.COMMON)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "106-97-8"))),
        new SeedCompoundSpec("COMP-C2H4", "Ethylene", "C2H4", List.of(new CompoundAlias("Ethene", CompoundAliasRole.SYSTEMATIC)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "74-85-1"))),
        new SeedCompoundSpec("COMP-C2H2", "Acetylene", "C2H2", List.of(new CompoundAlias("Ethyne", CompoundAliasRole.SYSTEMATIC)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "74-86-2"))),
        new SeedCompoundSpec("COMP-C6H6", "Benzene", "C6H6", List.of(new CompoundAlias("[6]Annulene", CompoundAliasRole.SYSTEMATIC)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "71-43-2"))),

        // Acids
        new SeedCompoundSpec("COMP-HCL", "Hydrochloric acid", "HCl", List.of(new CompoundAlias("Chlorane", CompoundAliasRole.SYSTEMATIC), new CompoundAlias("Muriatic acid", CompoundAliasRole.HISTORICAL)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "7647-01-0"))),
        new SeedCompoundSpec("COMP-HNO3", "Nitric acid", "HNO3", List.of(new CompoundAlias("Aqua fortis", CompoundAliasRole.HISTORICAL)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "7697-37-2"))),
        new SeedCompoundSpec("COMP-H2SO4", "Sulfuric acid", "H2SO4", List.of(new CompoundAlias("Oil of vitriol", CompoundAliasRole.HISTORICAL)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "7664-93-9"), new CompoundExternalIdentifier(ExternalIdentifierScheme.PUBCHEM_CID, "1118"))),
        new SeedCompoundSpec("COMP-H3PO4", "Phosphoric acid", "H3PO4", List.of(new CompoundAlias("Orthophosphoric acid", CompoundAliasRole.SYSTEMATIC)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "7664-38-2"))),
        new SeedCompoundSpec("COMP-H2CO3", "Carbonic acid", "H2CO3", List.of(new CompoundAlias("Dihydrogen carbonate", CompoundAliasRole.SYSTEMATIC)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "463-79-6"))),
        new SeedCompoundSpec("COMP-CH3COOH", "Acetic acid", "CH3COOH", List.of(new CompoundAlias("Ethanoic acid", CompoundAliasRole.SYSTEMATIC), new CompoundAlias("Vinegar acid", CompoundAliasRole.COMMON)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "64-19-7"))),

        // Bases & Hydroxides
        new SeedCompoundSpec("COMP-NAOH", "Sodium hydroxide", "NaOH", List.of(new CompoundAlias("Caustic soda", CompoundAliasRole.COMMON), new CompoundAlias("Lye", CompoundAliasRole.HISTORICAL)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "1310-73-2"))),
        new SeedCompoundSpec("COMP-KOH", "Potassium hydroxide", "KOH", List.of(new CompoundAlias("Caustic potash", CompoundAliasRole.COMMON)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "1310-58-3"))),
        new SeedCompoundSpec("COMP-CA-OH-2", "Calcium hydroxide", "Ca(OH)2", List.of(new CompoundAlias("Slaked lime", CompoundAliasRole.COMMON)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "1305-62-0"))),
        new SeedCompoundSpec("COMP-MG-OH-2", "Magnesium hydroxide", "Mg(OH)2", List.of(new CompoundAlias("Milk of magnesia", CompoundAliasRole.COMMON)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "1309-42-8"))),
        new SeedCompoundSpec("COMP-AL-OH-3", "Aluminium hydroxide", "Al(OH)3", List.of(new CompoundAlias("Alumina trihydrate", CompoundAliasRole.COMMON)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "21645-51-2"))),

        // Salts & Inorganics
        new SeedCompoundSpec("COMP-NACL", "Sodium chloride", "NaCl", List.of(new CompoundAlias("Table salt", CompoundAliasRole.COMMON), new CompoundAlias("Halite", CompoundAliasRole.HISTORICAL)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "7647-14-5"), new CompoundExternalIdentifier(ExternalIdentifierScheme.PUBCHEM_CID, "5234"))),
        new SeedCompoundSpec("COMP-KCL", "Potassium chloride", "KCl", List.of(new CompoundAlias("Sylvite", CompoundAliasRole.HISTORICAL)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "7447-40-7"))),
        new SeedCompoundSpec("COMP-NABR", "Sodium bromide", "NaBr", List.of(new CompoundAlias("Sedoneural", CompoundAliasRole.OTHER)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "7647-15-6"))),
        new SeedCompoundSpec("COMP-KI", "Potassium iodide", "KI", List.of(new CompoundAlias("Potassii iodidum", CompoundAliasRole.HISTORICAL)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "7681-11-0"))),
        new SeedCompoundSpec("COMP-CACL2", "Calcium chloride", "CaCl2", List.of(new CompoundAlias("Calcium dichloride", CompoundAliasRole.SYSTEMATIC)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "10043-52-4"))),
        new SeedCompoundSpec("COMP-MGCL2", "Magnesium chloride", "MgCl2", List.of(new CompoundAlias("Magnesium dichloride", CompoundAliasRole.SYSTEMATIC)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "7786-30-3"))),
        new SeedCompoundSpec("COMP-NAHCO3", "Sodium bicarbonate", "NaHCO3", List.of(new CompoundAlias("Baking soda", CompoundAliasRole.COMMON), new CompoundAlias("Sodium hydrogen carbonate", CompoundAliasRole.SYSTEMATIC)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "144-55-8"))),
        new SeedCompoundSpec("COMP-NA2CO3", "Sodium carbonate", "Na2CO3", List.of(new CompoundAlias("Washing soda", CompoundAliasRole.COMMON), new CompoundAlias("Soda ash", CompoundAliasRole.HISTORICAL)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "497-19-8"))),
        new SeedCompoundSpec("COMP-CACO3", "Calcium carbonate", "CaCO3", List.of(new CompoundAlias("Calcite", CompoundAliasRole.COMMON), new CompoundAlias("Limestone", CompoundAliasRole.HISTORICAL)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "471-34-1"))),
        new SeedCompoundSpec("COMP-NA2SO4", "Sodium sulfate", "Na2SO4", List.of(new CompoundAlias("Glauber's salt (anhydrous)", CompoundAliasRole.HISTORICAL)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "7757-82-6"))),
        new SeedCompoundSpec("COMP-MGSO4", "Magnesium sulfate", "MgSO4", List.of(new CompoundAlias("Epsom salt (anhydrous)", CompoundAliasRole.HISTORICAL)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "7487-88-9"))),

        // Oxides
        new SeedCompoundSpec("COMP-AL2O3", "Aluminium oxide", "Al2O3", List.of(new CompoundAlias("Alumina", CompoundAliasRole.COMMON), new CompoundAlias("Corundum", CompoundAliasRole.HISTORICAL)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "1344-28-1"))),
        new SeedCompoundSpec("COMP-FE2O3", "Iron(III) oxide", "Fe2O3", List.of(new CompoundAlias("Hematite", CompoundAliasRole.COMMON), new CompoundAlias("Rust", CompoundAliasRole.HISTORICAL)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "1309-37-1"))),
        new SeedCompoundSpec("COMP-FE3O4", "Magnetite", "Fe3O4", List.of(new CompoundAlias("Iron(II,III) oxide", CompoundAliasRole.SYSTEMATIC)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "1317-61-9"))),
        new SeedCompoundSpec("COMP-CUO", "Copper(II) oxide", "CuO", List.of(new CompoundAlias("Tenorite", CompoundAliasRole.HISTORICAL)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "1317-38-0"))),

        // Hydrates & Complex Inorganics
        new SeedCompoundSpec("COMP-CUSO4", "Copper(II) sulfate", "CuSO4", List.of(new CompoundAlias("Anhydrous cupric sulfate", CompoundAliasRole.SYSTEMATIC)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "7758-98-7"))),
        new SeedCompoundSpec("COMP-CUSO4-5H2O", "Copper(II) sulfate pentahydrate", "CuSO4.5H2O", List.of(new CompoundAlias("Blue vitriol", CompoundAliasRole.HISTORICAL), new CompoundAlias("Bluestone", CompoundAliasRole.COMMON)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "7758-99-8"))),
        new SeedCompoundSpec("COMP-AGNO3", "Silver nitrate", "AgNO3", List.of(new CompoundAlias("Lunar caustic", CompoundAliasRole.HISTORICAL)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "7761-88-8"))),
        new SeedCompoundSpec("COMP-BACL2", "Barium chloride", "BaCl2", List.of(new CompoundAlias("Barium dichloride", CompoundAliasRole.SYSTEMATIC)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "10361-37-2"))),
        new SeedCompoundSpec("COMP-KMNO4", "Potassium permanganate", "KMnO4", List.of(new CompoundAlias("Condy's crystals", CompoundAliasRole.HISTORICAL)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "7722-64-7"))),
        new SeedCompoundSpec("COMP-K2CR2O7", "Potassium dichromate", "K2Cr2O7", List.of(new CompoundAlias("Potassium bichromate", CompoundAliasRole.HISTORICAL)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "7778-50-9"))),

        // ISOMERS Sharing Formula C2H6O
        new SeedCompoundSpec("COMP-ETHANOL", "Ethanol", "C2H5OH", List.of(new CompoundAlias("Ethyl alcohol", CompoundAliasRole.COMMON), new CompoundAlias("Grain alcohol", CompoundAliasRole.HISTORICAL)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "64-17-5"), new CompoundExternalIdentifier(ExternalIdentifierScheme.PUBCHEM_CID, "702"))),
        new SeedCompoundSpec("COMP-DIMETHYL-ETHER", "Dimethyl ether", "CH3OCH3", List.of(new CompoundAlias("Methoxymethane", CompoundAliasRole.SYSTEMATIC), new CompoundAlias("Wood ether", CompoundAliasRole.HISTORICAL)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "115-10-6"), new CompoundExternalIdentifier(ExternalIdentifierScheme.PUBCHEM_CID, "8254"))),

        // Carbohydrates
        new SeedCompoundSpec("COMP-GLUCOSE", "Glucose", "C6H12O6", List.of(new CompoundAlias("D-Glucose", CompoundAliasRole.SYSTEMATIC), new CompoundAlias("Dextrose", CompoundAliasRole.COMMON), new CompoundAlias("Grape sugar", CompoundAliasRole.HISTORICAL)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "50-99-7"))),
        new SeedCompoundSpec("COMP-SUCROSE", "Sucrose", "C12H22O11", List.of(new CompoundAlias("Table sugar", CompoundAliasRole.COMMON), new CompoundAlias("Saccharose", CompoundAliasRole.HISTORICAL)), List.of(new CompoundExternalIdentifier(ExternalIdentifierScheme.CAS_REGISTRY_NUMBER, "57-50-1")))
    );

    private KnownCompoundRegistry() {}

    public static String buildHillFormula(CompoundComposition composition) {
        if (composition == null || composition.getElementCounts().isEmpty()) return "";
        List<CompoundElementCount> counts = new ArrayList<>(composition.getElementCounts());
        boolean hasCarbon = counts.stream().anyMatch(c -> c.getSymbol().equalsIgnoreCase("C"));
        counts.sort((a, b) -> {
            if (hasCarbon) {
                if (a.getSymbol().equalsIgnoreCase("C")) return -1;
                if (b.getSymbol().equalsIgnoreCase("C")) return 1;
                if (a.getSymbol().equalsIgnoreCase("H")) return -1;
                if (b.getSymbol().equalsIgnoreCase("H")) return 1;
            }
            return a.getSymbol().compareToIgnoreCase(b.getSymbol());
        });
        StringBuilder sb = new StringBuilder();
        for (CompoundElementCount c : counts) {
            sb.append(c.getSymbol());
            if (c.getAtomCount().compareTo(BigInteger.ONE) > 0) {
                sb.append(c.getAtomCount());
            }
        }
        return sb.toString();
    }

    public static List<Compound> buildAll55CoreCompounds(ElementMassProvider massProvider) {
        if (massProvider == null) {
            throw new CompoundException(CompoundErrorCode.ELEMENT_MASS_NOT_FOUND, "ElementMassProvider cannot be null");
        }
        List<Compound> compounds = new ArrayList<>();

        for (SeedCompoundSpec spec : SPECS) {
            ChemicalFormula parsed = PARSER.parse(spec.formulaStr);

            List<CompoundElementCount> counts = new ArrayList<>();
            parsed.getElementCounts().forEach((sym, count) -> {
                int z = com.ailab.chemistry.domain.element.KnownElementRegistry.getBySymbol(sym.getSymbol()).atomicNumber();
                counts.add(new CompoundElementCount(z, sym.getSymbol(), count));
            });
            CompoundComposition comp = new CompoundComposition(counts);
            MolarMass mass = CALCULATOR.calculate(comp, massProvider);
            CompoundFormula formula = new CompoundFormula(
                    spec.formulaStr,
                    parsed.getNormalizedFormula(),
                    buildHillFormula(comp),
                    comp,
                    new CompoundCharge(0),
                    hydrateInfo(spec.formulaStr)
            );

            Compound c = new Compound(
                    CompoundId.of(UUID.nameUUIDFromBytes(("compound-" + spec.codeStr).getBytes()).toString()),
                    new CompoundCode(spec.codeStr),
                    spec.nameStr,
                    spec.aliases,
                    formula,
                    comp,
                    new CompoundCharge(0),
                    mass,
                    spec.externalIdentifiers,
                    CATALOG_VERSION,
                    PROVENANCE
            );

            compounds.add(c);
        }

        return List.copyOf(compounds);
    }

    private static String hydrateInfo(String formula) {
        int separator = formula.indexOf('.');
        if (separator < 0 || separator == formula.length() - 1) {
            return null;
        }
        return formula.substring(separator + 1);
    }

    private record SeedCompoundSpec(
            String codeStr,
            String nameStr,
            String formulaStr,
            List<CompoundAlias> aliases,
            List<CompoundExternalIdentifier> externalIdentifiers
    ) {}
}
