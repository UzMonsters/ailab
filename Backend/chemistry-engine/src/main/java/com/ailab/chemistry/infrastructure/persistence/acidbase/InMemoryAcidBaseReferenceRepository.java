package com.ailab.chemistry.infrastructure.persistence.acidbase;

import com.ailab.chemistry.domain.acidbase.*;
import com.ailab.chemistry.domain.measurement.Temperature;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Primary
@Profile({"test", "standalone-engine"})
public class InMemoryAcidBaseReferenceRepository implements AcidBaseReferenceRepository {

    private final Map<String, ChemicalSpecies> speciesMap = new LinkedHashMap<>();
    private final List<ConjugatePair> conjugatePairs = new ArrayList<>();
    private final List<DissociationStep> dissociationSteps = new ArrayList<>();
    private final List<EquilibriumConstant> equilibriumConstants = new ArrayList<>();

    public InMemoryAcidBaseReferenceRepository() {
        seedReferenceData();
    }

    private void seedReferenceData() {
        // 1. Species
        addSpecies(new ChemicalSpecies(new ChemicalSpeciesCode("SPEC-H2O"), "Water", "H2O", SpeciesKind.SOLVENT, SpeciesCharge.ZERO, AcidBaseRole.AMPHIPROTIC, DissociationBehavior.AUTOIONIZING_SOLVENT, "COMP-H2O"));
        addSpecies(new ChemicalSpecies(new ChemicalSpeciesCode("SPEC-H3O-PLUS"), "Hydronium", "H3O+", SpeciesKind.CATION, SpeciesCharge.PLUS_ONE, AcidBaseRole.ACID, DissociationBehavior.NOT_APPLICABLE, null));
        addSpecies(new ChemicalSpecies(new ChemicalSpeciesCode("SPEC-OH-MINUS"), "Hydroxide", "OH-", SpeciesKind.ANION, SpeciesCharge.MINUS_ONE, AcidBaseRole.BASE, DissociationBehavior.NOT_APPLICABLE, null));
        addSpecies(new ChemicalSpecies(new ChemicalSpeciesCode("SPEC-H-PLUS"), "Hydrogen Ion", "H+", SpeciesKind.CATION, SpeciesCharge.PLUS_ONE, AcidBaseRole.ACID, DissociationBehavior.NOT_APPLICABLE, null));
        addSpecies(new ChemicalSpecies(new ChemicalSpeciesCode("SPEC-NA-PLUS"), "Sodium Ion", "Na+", SpeciesKind.CATION, SpeciesCharge.PLUS_ONE, AcidBaseRole.NEUTRAL, DissociationBehavior.NOT_APPLICABLE, null));
        addSpecies(new ChemicalSpecies(new ChemicalSpeciesCode("SPEC-CL-MINUS"), "Chloride Ion", "Cl-", SpeciesKind.ANION, SpeciesCharge.MINUS_ONE, AcidBaseRole.NEUTRAL, DissociationBehavior.NOT_APPLICABLE, null));
        addSpecies(new ChemicalSpecies(new ChemicalSpeciesCode("SPEC-CA-2PLUS"), "Calcium Ion", "Ca^2+", SpeciesKind.CATION, SpeciesCharge.of(2), AcidBaseRole.NEUTRAL, DissociationBehavior.NOT_APPLICABLE, null));
        addSpecies(new ChemicalSpecies(new ChemicalSpeciesCode("SPEC-MG-2PLUS"), "Magnesium Ion", "Mg^2+", SpeciesKind.CATION, SpeciesCharge.of(2), AcidBaseRole.NEUTRAL, DissociationBehavior.NOT_APPLICABLE, null));
        addSpecies(new ChemicalSpecies(new ChemicalSpeciesCode("SPEC-AL-3PLUS"), "Aluminium Ion", "Al^3+", SpeciesKind.CATION, SpeciesCharge.of(3), AcidBaseRole.NEUTRAL, DissociationBehavior.NOT_APPLICABLE, null));
        addSpecies(new ChemicalSpecies(new ChemicalSpeciesCode("SPEC-NH4-PLUS"), "Ammonium", "NH4+", SpeciesKind.CATION, SpeciesCharge.PLUS_ONE, AcidBaseRole.ACID, DissociationBehavior.NOT_APPLICABLE, null));
        addSpecies(new ChemicalSpecies(new ChemicalSpeciesCode("SPEC-NH3"), "Ammonia", "NH3", SpeciesKind.NEUTRAL_COMPOUND, SpeciesCharge.ZERO, AcidBaseRole.BASE, DissociationBehavior.WEAK_ELECTROLYTE, "COMP-NH3"));
        addSpecies(new ChemicalSpecies(new ChemicalSpeciesCode("SPEC-HCL"), "Hydrochloric Acid", "HCl", SpeciesKind.NEUTRAL_COMPOUND, SpeciesCharge.ZERO, AcidBaseRole.ACID, DissociationBehavior.STRONG_ELECTROLYTE, "COMP-HCL"));
        addSpecies(new ChemicalSpecies(new ChemicalSpeciesCode("SPEC-HNO3"), "Nitric Acid", "HNO3", SpeciesKind.NEUTRAL_COMPOUND, SpeciesCharge.ZERO, AcidBaseRole.ACID, DissociationBehavior.STRONG_ELECTROLYTE, "COMP-HNO3"));
        addSpecies(new ChemicalSpecies(new ChemicalSpeciesCode("SPEC-H2SO4"), "Sulfuric Acid", "H2SO4", SpeciesKind.NEUTRAL_COMPOUND, SpeciesCharge.ZERO, AcidBaseRole.ACID, DissociationBehavior.STRONG_ELECTROLYTE, "COMP-H2SO4"));
        addSpecies(new ChemicalSpecies(new ChemicalSpeciesCode("SPEC-NAOH"), "Sodium Hydroxide", "NaOH", SpeciesKind.NEUTRAL_COMPOUND, SpeciesCharge.ZERO, AcidBaseRole.BASE, DissociationBehavior.STRONG_ELECTROLYTE, "COMP-NAOH"));
        addSpecies(new ChemicalSpecies(new ChemicalSpeciesCode("SPEC-HSO4-MINUS"), "Hydrogen Sulfate", "HSO4-", SpeciesKind.ANION, SpeciesCharge.MINUS_ONE, AcidBaseRole.AMPHIPROTIC, DissociationBehavior.NOT_APPLICABLE, null));
        addSpecies(new ChemicalSpecies(new ChemicalSpeciesCode("SPEC-SO4-2MINUS"), "Sulfate", "SO4^2-", SpeciesKind.ANION, SpeciesCharge.of(-2), AcidBaseRole.BASE, DissociationBehavior.NOT_APPLICABLE, null));
        addSpecies(new ChemicalSpecies(new ChemicalSpeciesCode("SPEC-CH3COOH"), "Acetic Acid", "CH3COOH", SpeciesKind.NEUTRAL_COMPOUND, SpeciesCharge.ZERO, AcidBaseRole.ACID, DissociationBehavior.WEAK_ELECTROLYTE, "COMP-CH3COOH"));
        addSpecies(new ChemicalSpecies(new ChemicalSpeciesCode("SPEC-CH3COO-MINUS"), "Acetate", "CH3COO-", SpeciesKind.ANION, SpeciesCharge.MINUS_ONE, AcidBaseRole.BASE, DissociationBehavior.NOT_APPLICABLE, null));
        addSpecies(new ChemicalSpecies(new ChemicalSpeciesCode("SPEC-H2CO3"), "Carbonic Acid", "H2CO3", SpeciesKind.NEUTRAL_COMPOUND, SpeciesCharge.ZERO, AcidBaseRole.ACID, DissociationBehavior.WEAK_ELECTROLYTE, "COMP-H2CO3"));
        addSpecies(new ChemicalSpecies(new ChemicalSpeciesCode("SPEC-HCO3-MINUS"), "Bicarbonate", "HCO3-", SpeciesKind.ANION, SpeciesCharge.MINUS_ONE, AcidBaseRole.AMPHIPROTIC, DissociationBehavior.NOT_APPLICABLE, null));
        addSpecies(new ChemicalSpecies(new ChemicalSpeciesCode("SPEC-CO3-2MINUS"), "Carbonate", "CO3^2-", SpeciesKind.ANION, SpeciesCharge.of(-2), AcidBaseRole.BASE, DissociationBehavior.NOT_APPLICABLE, null));

        // 2. Conjugate Pairs
        conjugatePairs.add(new ConjugatePair("PAIR-H2O-OH", "SPEC-H2O", "SPEC-OH-MINUS"));
        conjugatePairs.add(new ConjugatePair("PAIR-H3O-H2O", "SPEC-H3O-PLUS", "SPEC-H2O"));
        conjugatePairs.add(new ConjugatePair("PAIR-NH4-NH3", "SPEC-NH4-PLUS", "SPEC-NH3"));
        conjugatePairs.add(new ConjugatePair("PAIR-CH3COOH-CH3COO", "SPEC-CH3COOH", "SPEC-CH3COO-MINUS"));
        conjugatePairs.add(new ConjugatePair("PAIR-H2CO3-HCO3", "SPEC-H2CO3", "SPEC-HCO3-MINUS"));
        conjugatePairs.add(new ConjugatePair("PAIR-HCO3-CO3", "SPEC-HCO3-MINUS", "SPEC-CO3-2MINUS"));
        conjugatePairs.add(new ConjugatePair("PAIR-H2SO4-HSO4", "SPEC-H2SO4", "SPEC-HSO4-MINUS"));
        conjugatePairs.add(new ConjugatePair("PAIR-HSO4-SO4", "SPEC-HSO4-MINUS", "SPEC-SO4-2MINUS"));

        // 3. Equilibrium Constants (Weak species only)
        EquilibriumReferenceConditions cond = EquilibriumReferenceConditions.STANDARD_WATER_25C;
        EquilibriumConstant kw = EquilibriumConstant.weak("SPEC-H2O", EquilibriumConstantType.KW, 1, new BigDecimal("1.00e-14"), cond);
        EquilibriumConstant kaAcetic = EquilibriumConstant.weak("SPEC-CH3COOH", EquilibriumConstantType.KA, 1, new BigDecimal("1.75e-5"), cond);
        EquilibriumConstant kbAcetate = EquilibriumConstant.weak("SPEC-CH3COO-MINUS", EquilibriumConstantType.KB, 1, new BigDecimal("5.71e-10"), cond);
        EquilibriumConstant ka1Carbonic = EquilibriumConstant.weak("SPEC-H2CO3", EquilibriumConstantType.KA, 1, new BigDecimal("4.45e-7"), cond);
        EquilibriumConstant ka2Bicarbonate = EquilibriumConstant.weak("SPEC-HCO3-MINUS", EquilibriumConstantType.KA, 2, new BigDecimal("4.69e-11"), cond);
        EquilibriumConstant kb1Bicarbonate = EquilibriumConstant.weak("SPEC-HCO3-MINUS", EquilibriumConstantType.KB, 1, new BigDecimal("2.25e-8"), cond);
        EquilibriumConstant kb1Carbonate = EquilibriumConstant.weak("SPEC-CO3-2MINUS", EquilibriumConstantType.KB, 1, new BigDecimal("2.13e-4"), cond);
        EquilibriumConstant kaAmmonium = EquilibriumConstant.weak("SPEC-NH4-PLUS", EquilibriumConstantType.KA, 1, new BigDecimal("5.69e-10"), cond);
        EquilibriumConstant kbAmmonia = EquilibriumConstant.weak("SPEC-NH3", EquilibriumConstantType.KB, 1, new BigDecimal("1.76e-5"), cond);
        EquilibriumConstant ka2HSO4 = EquilibriumConstant.weak("SPEC-HSO4-MINUS", EquilibriumConstantType.KA, 2, new BigDecimal("1.02e-2"), cond);

        equilibriumConstants.addAll(List.of(kw, kaAcetic, kbAcetate, ka1Carbonic, ka2Bicarbonate, kb1Bicarbonate, kb1Carbonate, kaAmmonium, kbAmmonia, ka2HSO4));

        // 4. Dissociation Steps
        dissociationSteps.add(new DissociationStep("SPEC-H2O", "SPEC-OH-MINUS", 1, kw));
        dissociationSteps.add(new DissociationStep("SPEC-CH3COOH", "SPEC-CH3COO-MINUS", 1, kaAcetic));
        dissociationSteps.add(new DissociationStep("SPEC-H2CO3", "SPEC-HCO3-MINUS", 1, ka1Carbonic));
        dissociationSteps.add(new DissociationStep("SPEC-HCO3-MINUS", "SPEC-CO3-2MINUS", 2, ka2Bicarbonate));
        dissociationSteps.add(new DissociationStep("SPEC-H2SO4", "SPEC-HSO4-MINUS", 1, null)); // Strong electrolyte step 1
        dissociationSteps.add(new DissociationStep("SPEC-HSO4-MINUS", "SPEC-SO4-2MINUS", 2, ka2HSO4));
        dissociationSteps.add(new DissociationStep("SPEC-NH4-PLUS", "SPEC-NH3", 1, kaAmmonium));
    }

    private void addSpecies(ChemicalSpecies s) {
        speciesMap.put(s.getCode().getValue().toUpperCase(), s);
    }

    @Override
    public Optional<ChemicalSpecies> findSpeciesByCode(ChemicalSpeciesCode code) {
        if (code == null) return Optional.empty();
        return Optional.ofNullable(speciesMap.get(code.getValue().toUpperCase()));
    }

    @Override
    public List<ChemicalSpecies> findAllSpecies() {
        return new ArrayList<>(speciesMap.values());
    }

    @Override
    public List<DissociationStep> findDissociationStepsForSpecies(ChemicalSpeciesCode code) {
        if (code == null) return Collections.emptyList();
        return dissociationSteps.stream()
                .filter(step -> step.getAcidSpeciesCode().equalsIgnoreCase(code.getValue()))
                .sorted(Comparator.comparingInt(DissociationStep::getStepNumber))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<EquilibriumConstant> findKa(ChemicalSpeciesCode code, Temperature temperature, String solventCode) {
        if (code == null) return Optional.empty();
        return equilibriumConstants.stream()
                .filter(c -> c.getSpeciesCode().equalsIgnoreCase(code.getValue()) && (c.getType() == EquilibriumConstantType.KA || c.getType() == EquilibriumConstantType.KW))
                .filter(c -> matchesConditions(c, temperature, solventCode))
                .findFirst();
    }

    @Override
    public Optional<EquilibriumConstant> findKb(ChemicalSpeciesCode code, Temperature temperature, String solventCode) {
        if (code == null) return Optional.empty();
        return equilibriumConstants.stream()
                .filter(c -> c.getSpeciesCode().equalsIgnoreCase(code.getValue()) && c.getType() == EquilibriumConstantType.KB)
                .filter(c -> matchesConditions(c, temperature, solventCode))
                .findFirst();
    }

    @Override
    public Optional<ConjugatePair> findConjugatePair(ChemicalSpeciesCode code) {
        if (code == null) return Optional.empty();
        return conjugatePairs.stream()
                .filter(pair -> pair.getAcidSpeciesCode().equalsIgnoreCase(code.getValue()) || pair.getBaseSpeciesCode().equalsIgnoreCase(code.getValue()))
                .findFirst();
    }

    private boolean matchesConditions(EquilibriumConstant constant, Temperature temperature, String solventCode) {
        if (temperature == null || solventCode == null || solventCode.isBlank()) {
            return false;
        }
        return constant.getConditions().getTemperature().equals(temperature)
                && constant.getConditions().getSolventCompoundCode().equalsIgnoreCase(solventCode);
    }
}
