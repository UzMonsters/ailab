package com.ailab.chemistry.compound;

import com.ailab.chemistry.domain.compound.*;
import com.ailab.chemistry.infrastructure.persistence.compound.InMemoryCompoundRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CompoundDomainTests {

    private final TestElementMassProvider testMassProvider = new TestElementMassProvider();
    private final CompoundRepository repository = new InMemoryCompoundRepository(testMassProvider);
    private final MolarMassCalculator calculator = new MolarMassCalculatorImpl();

    @Test
    void testRegistryContains55CoreCompounds() {
        List<Compound> compounds = KnownCompoundRegistry.buildAll55CoreCompounds(testMassProvider);
        assertThat(compounds).hasSize(55);
    }

    @Test
    void testFormulaRepresentationsSeparation() {
        // Ethanol
        Compound ethanol = repository.findByCode(new CompoundCode("COMP-ETHANOL")).orElseThrow();
        assertThat(ethanol.getFormula().getOriginalFormula()).isEqualTo("C2H5OH");
        assertThat(ethanol.getFormula().getNormalizedFormula()).isEqualTo("C2H5OH");
        assertThat(ethanol.getFormula().getCompositionFormula()).isEqualTo("C2H6O");

        // Dimethyl ether
        Compound dme = repository.findByCode(new CompoundCode("COMP-DIMETHYL-ETHER")).orElseThrow();
        assertThat(dme.getFormula().getOriginalFormula()).isEqualTo("CH3OCH3");
        assertThat(dme.getFormula().getNormalizedFormula()).isEqualTo("CH3OCH3");
        assertThat(dme.getFormula().getCompositionFormula()).isEqualTo("C2H6O");

        // Copper sulfate pentahydrate
        Compound hydrate = repository.findByCode(new CompoundCode("COMP-CUSO4-5H2O")).orElseThrow();
        assertThat(hydrate.getFormula().getOriginalFormula()).isEqualTo("CuSO4.5H2O");
        assertThat(hydrate.getFormula().getNormalizedFormula()).isEqualTo("CuSO4·5H2O");
        assertThat(hydrate.getFormula().getCompositionFormula()).isEqualTo("CuH10O9S");
    }

    @Test
    void testIsomerSearchByCompositionVsNormalizedFormula() {
        // Search by composition formula "C2H6O" returns BOTH isomers
        List<Compound> isomers = repository.findByCompositionFormula("C2H6O");
        assertThat(isomers).hasSize(2);
        List<String> names = isomers.stream().map(Compound::getPrimaryName).toList();
        assertThat(names).containsExactlyInAnyOrder("Ethanol", "Dimethyl ether");

        // Exact search by normalized formula returns single specific compound
        List<Compound> ethanolList = repository.findByNormalizedFormula("C2H5OH");
        assertThat(ethanolList).hasSize(1);
        assertThat(ethanolList.get(0).getPrimaryName()).isEqualTo("Ethanol");

        List<Compound> dmeList = repository.findByNormalizedFormula("CH3OCH3");
        assertThat(dmeList).hasSize(1);
        assertThat(dmeList.get(0).getPrimaryName()).isEqualTo("Dimethyl ether");
    }

    @Test
    void testMolarMassChangesWhenProviderMassChanges() {
        // Dynamic provider check: prove molar mass is NOT statically hardcoded
        TestElementMassProvider dynamicProvider = new TestElementMassProvider();
        // Custom H mass = 2.000 g/mol
        dynamicProvider.setCustomMass(1, new BigDecimal("2.000"));

        Compound waterStandard = KnownCompoundRegistry.buildAll55CoreCompounds(testMassProvider).stream()
                .filter(c -> c.getCode().getValue().equals("COMP-H2O")).findFirst().orElseThrow();
        Compound waterCustom = KnownCompoundRegistry.buildAll55CoreCompounds(dynamicProvider).stream()
                .filter(c -> c.getCode().getValue().equals("COMP-H2O")).findFirst().orElseThrow();

        // Standard H2O mass ~18.015, Custom H2O mass = 2*2.000 + 15.999 = 19.999
        assertThat(waterCustom.getMolarMass().getRepresentativeValue())
                .isNotEqualTo(waterStandard.getMolarMass().getRepresentativeValue());
        assertThat(waterCustom.getMolarMass().getRepresentativeValue()).isEqualTo(new BigDecimal("19.999"));
    }

    @Test
    void testRegressionMolarMassCalculations9RequiredCompounds() {
        // 1. H2O
        Compound h2o = repository.findByCode(new CompoundCode("COMP-H2O")).orElseThrow();
        assertThat(h2o.getMolarMass().getRepresentativeValue()).isEqualTo(new BigDecimal("18.015"));
        assertThat(h2o.getMolarMass().getLowerBound()).isEqualTo(new BigDecimal("18.01059"));
        assertThat(h2o.getMolarMass().getUpperBound()).isEqualTo(new BigDecimal("18.01599"));
        assertThat(h2o.getMolarMass().getKind()).isEqualTo(MolarMassKind.INTERVAL);
        assertThat(h2o.getMolarMass().getCalculationBasis().getElementDatasetVersion()).isEqualTo("v1.1.0");

        // 2. CO2
        Compound co2 = repository.findByCode(new CompoundCode("COMP-CO2")).orElseThrow();
        assertThat(co2.getMolarMass().getRepresentativeValue()).isEqualTo(new BigDecimal("44.009"));
        assertThat(co2.getMolarMass().getKind()).isEqualTo(MolarMassKind.INTERVAL);

        // 3. NaCl
        Compound nacl = repository.findByCode(new CompoundCode("COMP-NACL")).orElseThrow();
        assertThat(nacl.getMolarMass().getRepresentativeValue()).isEqualTo(new BigDecimal("58.44"));
        assertThat(nacl.getMolarMass().getKind()).isEqualTo(MolarMassKind.INTERVAL); // Cl is interval

        // 4. H2SO4
        Compound h2so4 = repository.findByCode(new CompoundCode("COMP-H2SO4")).orElseThrow();
        assertThat(h2so4.getMolarMass().getRepresentativeValue()).isEqualTo(new BigDecimal("98.072"));
        assertThat(h2so4.getMolarMass().getKind()).isEqualTo(MolarMassKind.INTERVAL);

        // 5. CaCO3
        Compound caco3 = repository.findByCode(new CompoundCode("COMP-CACO3")).orElseThrow();
        assertThat(caco3.getMolarMass().getRepresentativeValue()).isEqualTo(new BigDecimal("100.086"));
        assertThat(caco3.getMolarMass().getKind()).isEqualTo(MolarMassKind.INTERVAL);

        // 6. C6H12O6 (Glucose)
        Compound glucose = repository.findByCode(new CompoundCode("COMP-GLUCOSE")).orElseThrow();
        assertThat(glucose.getMolarMass().getRepresentativeValue()).isEqualTo(new BigDecimal("180.156"));
        assertThat(glucose.getMolarMass().getKind()).isEqualTo(MolarMassKind.INTERVAL);

        // 7. CuSO4
        Compound cuso4 = repository.findByCode(new CompoundCode("COMP-CUSO4")).orElseThrow();
        assertThat(cuso4.getMolarMass().getRepresentativeValue()).isEqualTo(new BigDecimal("159.602"));
        assertThat(cuso4.getMolarMass().getKind()).isEqualTo(MolarMassKind.INTERVAL);

        // 8. CuSO4·5H2O
        Compound hydrate = repository.findByCode(new CompoundCode("COMP-CUSO4-5H2O")).orElseThrow();
        assertThat(hydrate.getMolarMass().getRepresentativeValue()).isEqualTo(new BigDecimal("249.677"));
        assertThat(hydrate.getMolarMass().getKind()).isEqualTo(MolarMassKind.INTERVAL);

        // 9. KMnO4
        Compound kmno4 = repository.findByCode(new CompoundCode("COMP-KMNO4")).orElseThrow();
        assertThat(kmno4.getMolarMass().getRepresentativeValue()).isEqualTo(new BigDecimal("158.032"));
        assertThat(kmno4.getMolarMass().getKind()).isEqualTo(MolarMassKind.INTERVAL);
    }
}
