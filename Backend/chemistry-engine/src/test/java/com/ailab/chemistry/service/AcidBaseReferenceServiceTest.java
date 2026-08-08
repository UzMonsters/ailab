package com.ailab.chemistry.service;

import com.ailab.chemistry.api.*;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.infrastructure.persistence.acidbase.InMemoryAcidBaseReferenceRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AcidBaseReferenceServiceTest {

    private AcidBaseReferenceService service;

    @BeforeEach
    void setUp() {
        InMemoryAcidBaseReferenceRepository repository = new InMemoryAcidBaseReferenceRepository();
        service = new AcidBaseReferenceServiceImpl(repository);
    }

    @Test
    @DisplayName("Retrieve chemical species by species code")
    void testGetSpecies() {
        ChemicalSpeciesDetails species = service.getSpecies("SPEC-CH3COOH");
        assertThat(species.getSpeciesCode()).isEqualTo("SPEC-CH3COOH");
        assertThat(species.getName()).isEqualTo("Acetic Acid");
        assertThat(species.getFormula()).isEqualTo("CH3COOH");
        assertThat(species.getKind()).isEqualTo("NEUTRAL_COMPOUND");
        assertThat(species.getCharge()).isEqualTo(0);
        assertThat(species.getPrimaryRole()).isEqualTo("ACID");
        assertThat(species.getDissociationBehavior()).isEqualTo("WEAK_ELECTROLYTE");
        assertThat(species.getAssociatedCompoundCode()).isEqualTo("COMP-CH3COOH");

        assertThat(service.getSpecies("SPEC-H2O").getDissociationBehavior()).isEqualTo("AUTOIONIZING_SOLVENT");
        assertThat(service.getSpecies("SPEC-H3O-PLUS").getDissociationBehavior()).isEqualTo("NOT_APPLICABLE");
    }

    @Test
    @DisplayName("Retrieve dissociation steps for monoprotic and polyprotic acids")
    void testGetDissociationSteps() {
        List<DissociationStepDetails> step1 = service.getDissociationSteps("SPEC-CH3COOH");
        assertThat(step1).hasSize(1);
        assertThat(step1.get(0).getStepNumber()).isEqualTo(1);
        assertThat(step1.get(0).getDeprotonatedSpeciesCode()).isEqualTo("SPEC-CH3COO-MINUS");

        List<DissociationStepDetails> step2 = service.getDissociationSteps("SPEC-HCO3-MINUS");
        assertThat(step2).hasSize(1);
        assertThat(step2.get(0).getStepNumber()).isEqualTo(2);
        assertThat(step2.get(0).getDeprotonatedSpeciesCode()).isEqualTo("SPEC-CO3-2MINUS");
    }

    @Test
    @DisplayName("Find Ka, Kb, and Kw equilibrium constants")
    void testFindEquilibriumConstants() {
        Temperature t25 = Temperature.of("25.0", TemperatureUnit.CELSIUS);

        EquilibriumConstantDetails ka = service.findKa("SPEC-CH3COOH", t25, "COMP-H2O").orElseThrow();
        assertThat(ka.getValue()).isEqualByComparingTo(new BigDecimal("1.75e-5"));
        assertThat(ka.getPValue()).isCloseTo(new BigDecimal("4.7567"), org.assertj.core.data.Offset.offset(new BigDecimal("0.001")));

        EquilibriumConstantDetails kb = service.findKb("SPEC-NH3", t25, "COMP-H2O").orElseThrow();
        assertThat(kb.getValue()).isEqualByComparingTo(new BigDecimal("1.76e-5"));

        // Strong acid (HCl) has role ACID and dissociationBehavior STRONG_ELECTROLYTE with no fake K record
        ChemicalSpeciesDetails hcl = service.getSpecies("SPEC-HCL");
        assertThat(hcl.getPrimaryRole()).isEqualTo("ACID");
        assertThat(hcl.getDissociationBehavior()).isEqualTo("STRONG_ELECTROLYTE");
        Optional<EquilibriumConstantDetails> hclKa = service.findKa("SPEC-HCL", t25, "COMP-H2O");
        assertThat(hclKa).isEmpty();
    }

    @Test
    @DisplayName("Retrieve conjugate pair details")
    void testGetConjugatePair() {
        ConjugatePairDetails pair = service.getConjugatePair("SPEC-CH3COOH");
        assertThat(pair.getAcidSpeciesCode()).isEqualTo("SPEC-CH3COOH");
        assertThat(pair.getBaseSpeciesCode()).isEqualTo("SPEC-CH3COO-MINUS");
    }
}
