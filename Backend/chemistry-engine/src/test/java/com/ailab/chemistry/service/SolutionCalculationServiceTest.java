package com.ailab.chemistry.service;

import com.ailab.chemistry.api.*;
import com.ailab.chemistry.compound.TestElementMassProvider;
import com.ailab.chemistry.domain.measurement.*;
import com.ailab.chemistry.domain.solution.*;
import com.ailab.chemistry.infrastructure.persistence.compound.InMemoryCompoundRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolutionCalculationServiceTest {

    private SolutionCalculationService solutionService;

    @BeforeEach
    void setUp() {
        InMemoryCompoundRepository compoundRepository = new InMemoryCompoundRepository(new TestElementMassProvider());
        CompoundCatalogService compoundCatalogService = new CompoundCatalogServiceImpl(compoundRepository);
        solutionService = new SolutionCalculationServiceImpl(compoundCatalogService);
    }

    @Test
    @DisplayName("Service calculate Molarity and Molality for NaCl")
    void testCalculateMolarityAndMolality() {
        AmountOfSubstance solute = AmountOfSubstance.of("1.0", AmountOfSubstanceUnit.MOLE);
        Volume vol = Volume.of("1.0", VolumeUnit.LITER);
        Mass solventMass = Mass.of("1.0", MassUnit.KILOGRAM);

        MolarConcentration molarity = solutionService.calculateMolarity("COMP-NACL", solute, vol);
        assertThat(molarity.in(MolarConcentrationUnit.MOL_PER_LITER)).isEqualByComparingTo(BigDecimal.ONE);

        Molality molality = solutionService.calculateMolality("COMP-NACL", solute, solventMass);
        assertThat(molality.getValueInMolPerKg()).isEqualByComparingTo(BigDecimal.ONE);
    }

    @Test
    @DisplayName("Service calculate Preparation of 1 L of 1 mol/L NaCl solution")
    void testCalculatePreparation() {
        MolarConcentration conc = MolarConcentration.of("1.0", MolarConcentrationUnit.MOL_PER_LITER);
        Volume vol = Volume.of("1.0", VolumeUnit.LITER);

        SolutionPreparationResult prep = solutionService.calculatePreparation("COMP-NACL", conc, vol);
        assertThat(prep.getRequiredSoluteAmount().in(AmountOfSubstanceUnit.MOLE)).isEqualByComparingTo(BigDecimal.ONE);
        assertThat(prep.getRequiredSoluteMass().in(MassUnit.GRAM)).isCloseTo(new BigDecimal("58.443"), org.assertj.core.data.Offset.offset(new BigDecimal("0.1")));
    }

    @Test
    @DisplayName("Service Dilution calculation")
    void testCalculateDilution() {
        DilutionRequest request = DilutionRequest.fromInitialToTargetConcentration(
                "COMP-NACL",
                MolarConcentration.of("2.0", MolarConcentrationUnit.MOL_PER_LITER),
                Volume.of("250.0", VolumeUnit.MILLILITER),
                MolarConcentration.of("0.5", MolarConcentrationUnit.MOL_PER_LITER)
        );

        DilutionResult dilution = solutionService.calculateDilution(request);
        assertThat(dilution.getTargetVolume().in(VolumeUnit.LITER)).isEqualByComparingTo(BigDecimal.ONE);
        assertThat(dilution.getRequiredAddedSolventVolume().in(VolumeUnit.MILLILITER)).isEqualByComparingTo(new BigDecimal("750.0"));
    }

    @Test
    @DisplayName("Service rejects unknown compound code")
    void testUnknownCompoundRejection() {
        assertThatThrownBy(() -> solutionService.calculateMolarity("COMP-UNKNOWN-999", AmountOfSubstance.of("1.0", AmountOfSubstanceUnit.MOLE), Volume.of("1.0", VolumeUnit.LITER)))
                .isInstanceOf(com.ailab.chemistry.domain.compound.CompoundException.class);
    }
}
