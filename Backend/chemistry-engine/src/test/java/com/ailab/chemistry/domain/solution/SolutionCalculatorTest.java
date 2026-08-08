package com.ailab.chemistry.domain.solution;

import com.ailab.chemistry.domain.compound.MolarMass;
import com.ailab.chemistry.domain.compound.MolarMassCalculationBasis;
import com.ailab.chemistry.domain.measurement.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolutionCalculatorTest {

    private SolutionCalculator calculator;
    private MolarMassCalculationBasis basis;
    private MolarMass naClMass;

    @BeforeEach
    void setUp() {
        calculator = new SolutionCalculator();
        basis = new MolarMassCalculationBasis("IUPAC-2021", "1.0");
        naClMass = MolarMass.exact(new BigDecimal("58.443"), basis);
    }

    @Test
    @DisplayName("NaCl: 1 mol in 1 L gives 1 mol/L; catalogue mass in 1 L converts to ~1 mol/L")
    void testNaClMolarityAndConversion() {
        AmountOfSubstance n = AmountOfSubstance.of("1.0", AmountOfSubstanceUnit.MOLE);
        Volume v = Volume.of("1.0", VolumeUnit.LITER);

        MolarConcentration molarity = calculator.calculateMolarity(n, v);
        assertThat(molarity.in(MolarConcentrationUnit.MOL_PER_LITER)).isEqualByComparingTo(BigDecimal.ONE);

        MassConcentration massConc = MassConcentration.of("58.443", MassConcentrationUnit.GRAM_PER_LITER);
        ConcentrationConversionResult conv = calculator.convertMassConcentrationToMolarity("COMP-NACL", massConc, naClMass);
        assertThat(conv.getMolarity().orElseThrow().in(MolarConcentrationUnit.MOL_PER_LITER)).isCloseTo(BigDecimal.ONE, org.assertj.core.data.Offset.offset(new BigDecimal("0.001")));
    }

    @Test
    @DisplayName("Interval molar mass propagates lower and upper bounds in mass concentration to molarity conversion")
    void testIntervalBoundsPropagation() {
        MolarMass intervalMass = MolarMass.interval(new BigDecimal("100.0"), new BigDecimal("98.0"), new BigDecimal("102.0"), basis);
        MassConcentration massConc = MassConcentration.of("100.0", MassConcentrationUnit.GRAM_PER_LITER);

        ConcentrationConversionResult conv = calculator.convertMassConcentrationToMolarity("COMP-TEST", massConc, intervalMass);
        MolarConcentration rep = conv.getMolarity().orElseThrow();
        MolarConcentration low = conv.getMolarityLowerBound().orElseThrow();
        MolarConcentration upp = conv.getMolarityUpperBound().orElseThrow();

        assertThat(rep.in(MolarConcentrationUnit.MOL_PER_LITER)).isEqualByComparingTo(BigDecimal.ONE);
        assertThat(low.in(MolarConcentrationUnit.MOL_PER_LITER)).isLessThan(rep.in(MolarConcentrationUnit.MOL_PER_LITER));
        assertThat(upp.in(MolarConcentrationUnit.MOL_PER_LITER)).isGreaterThan(rep.in(MolarConcentrationUnit.MOL_PER_LITER));
    }

    @Test
    @DisplayName("Dilution: 250 mL of 2 mol/L diluted to 0.5 mol/L gives 1 L target volume and 750 mL added solvent")
    void testDilution() {
        DilutionRequest request = DilutionRequest.fromInitialToTargetConcentration(
                "COMP-NACL",
                MolarConcentration.of("2.0", MolarConcentrationUnit.MOL_PER_LITER),
                Volume.of("250.0", VolumeUnit.MILLILITER),
                MolarConcentration.of("0.5", MolarConcentrationUnit.MOL_PER_LITER)
        );

        DilutionResult result = calculator.calculateDilution(request);
        assertThat(result.getTargetVolume().in(VolumeUnit.LITER)).isEqualByComparingTo(BigDecimal.ONE);
        assertThat(result.getRequiredAddedSolventVolume().in(VolumeUnit.MILLILITER)).isEqualByComparingTo(new BigDecimal("750.0"));
    }

    @Test
    @DisplayName("Mixing: 500 mL of 1 mol/L and 500 mL of 2 mol/L same-solute solution gives 1.5 mol/L")
    void testSameSoluteMixing() {
        // Sol1: 500 mL of 1 mol/L = 0.5 mol NaCl, 500 mL water (~500g water)
        SolutionComposition sol1 = new SolutionComposition(
                "COMP-NACL", "COMP-H2O",
                Mass.of("29.2215", MassUnit.GRAM), AmountOfSubstance.of("0.5", AmountOfSubstanceUnit.MOLE),
                Mass.of("500.0", MassUnit.GRAM), AmountOfSubstance.of("27.75", AmountOfSubstanceUnit.MOLE),
                Volume.of("500.0", VolumeUnit.MILLILITER), Volume.of("500.0", VolumeUnit.MILLILITER), null
        );

        // Sol2: 500 mL of 2 mol/L = 1.0 mol NaCl, 500 mL water (~500g water)
        SolutionComposition sol2 = new SolutionComposition(
                "COMP-NACL", "COMP-H2O",
                Mass.of("58.443", MassUnit.GRAM), AmountOfSubstance.of("1.0", AmountOfSubstanceUnit.MOLE),
                Mass.of("500.0", MassUnit.GRAM), AmountOfSubstance.of("27.75", AmountOfSubstanceUnit.MOLE),
                Volume.of("500.0", VolumeUnit.MILLILITER), Volume.of("500.0", VolumeUnit.MILLILITER), null
        );

        SolutionMixingResult mixing = calculator.mixSameSoluteSolutions(List.of(sol1, sol2), SolutionVolumeAssumption.ADDITIVE_VOLUMES);
        assertThat(mixing.getFinalMolarity().in(MolarConcentrationUnit.MOL_PER_LITER)).isEqualByComparingTo(new BigDecimal("1.5"));
        assertThat(mixing.getFinalSolutionVolume().in(VolumeUnit.LITER)).isEqualByComparingTo(BigDecimal.ONE);
    }

    @Test
    @DisplayName("Molality: 1 mol solute in 1 kg solvent gives 1 mol/kg")
    void testMolality() {
        AmountOfSubstance n = AmountOfSubstance.of("1.0", AmountOfSubstanceUnit.MOLE);
        Mass m = Mass.of("1.0", MassUnit.KILOGRAM);

        Molality molality = calculator.calculateMolality(n, m);
        assertThat(molality.getValueInMolPerKg()).isEqualByComparingTo(BigDecimal.ONE);
    }

    @Test
    @DisplayName("Mass percentage: 10 g solute with 90 g solvent gives 10% mass fraction")
    void testMassPercentage() {
        Mass solute = Mass.of("10.0", MassUnit.GRAM);
        Mass total = Mass.of("100.0", MassUnit.GRAM);

        MassFraction frac = calculator.calculateMassFraction(solute, total);
        assertThat(frac.getValue()).isEqualByComparingTo(new BigDecimal("0.1"));
        assertThat(frac.getMassPercentage()).isEqualByComparingTo(new BigDecimal("10.0"));
    }

    @Test
    @DisplayName("Mole fraction: 1 mol ethanol and 3 mol water gives ethanol mole fraction 0.25")
    void testMoleFraction() {
        AmountOfSubstance ethanol = AmountOfSubstance.of("1.0", AmountOfSubstanceUnit.MOLE);
        AmountOfSubstance total = AmountOfSubstance.of("4.0", AmountOfSubstanceUnit.MOLE);

        MoleFraction frac = calculator.calculateMoleFraction(ethanol, total);
        assertThat(frac.getValue()).isEqualByComparingTo(new BigDecimal("0.25"));
    }

    @Test
    @DisplayName("Validation: Solute equals solvent throws SOLUTE_EQUALS_SOLVENT")
    void testSoluteEqualsSolventRejection() {
        assertThatThrownBy(() -> new SolutionComposition(
                "COMP-H2O", "COMP-H2O",
                Mass.of("10.0", MassUnit.GRAM), AmountOfSubstance.of("0.5", AmountOfSubstanceUnit.MOLE),
                Mass.of("90.0", MassUnit.GRAM), AmountOfSubstance.of("5.0", AmountOfSubstanceUnit.MOLE),
                null, null, null
        )).isInstanceOf(SolutionException.class)
                .extracting("errorCode").isEqualTo(SolutionErrorCode.SOLUTE_EQUALS_SOLVENT);
    }

    @Test
    @DisplayName("Validation: Incompatible solutes during mixing throws INCOMPATIBLE_SOLUTES")
    void testIncompatibleSolutesRejection() {
        SolutionComposition sol1 = new SolutionComposition(
                "COMP-NACL", "COMP-H2O",
                Mass.of("10.0", MassUnit.GRAM), AmountOfSubstance.of("0.1", AmountOfSubstanceUnit.MOLE),
                Mass.of("90.0", MassUnit.GRAM), AmountOfSubstance.of("5.0", AmountOfSubstanceUnit.MOLE),
                Volume.of("100.0", VolumeUnit.MILLILITER), Volume.of("100.0", VolumeUnit.MILLILITER), null
        );

        SolutionComposition sol2 = new SolutionComposition(
                "COMP-KCL", "COMP-H2O",
                Mass.of("10.0", MassUnit.GRAM), AmountOfSubstance.of("0.1", AmountOfSubstanceUnit.MOLE),
                Mass.of("90.0", MassUnit.GRAM), AmountOfSubstance.of("5.0", AmountOfSubstanceUnit.MOLE),
                Volume.of("100.0", VolumeUnit.MILLILITER), Volume.of("100.0", VolumeUnit.MILLILITER), null
        );

        assertThatThrownBy(() -> calculator.mixSameSoluteSolutions(List.of(sol1, sol2), SolutionVolumeAssumption.ADDITIVE_VOLUMES))
                .isInstanceOf(SolutionException.class)
                .extracting("errorCode").isEqualTo(SolutionErrorCode.INCOMPATIBLE_SOLUTES);
    }

    @Test
    @DisplayName("Validation: Non-additive volume mode without density throws NON_ADDITIVE_VOLUME_DENSITY_REQUIRED")
    void testNonAdditiveVolumeModeWithoutDensityRejection() {
        SolutionComposition sol1 = new SolutionComposition(
                "COMP-NACL", "COMP-H2O",
                Mass.of("10.0", MassUnit.GRAM), AmountOfSubstance.of("0.1", AmountOfSubstanceUnit.MOLE),
                Mass.of("90.0", MassUnit.GRAM), AmountOfSubstance.of("5.0", AmountOfSubstanceUnit.MOLE),
                Volume.of("100.0", VolumeUnit.MILLILITER), Volume.of("100.0", VolumeUnit.MILLILITER), null // No density
        );

        assertThatThrownBy(() -> calculator.mixSameSoluteSolutions(List.of(sol1), SolutionVolumeAssumption.NON_ADDITIVE_DENSITY_REQUIRED))
                .isInstanceOf(SolutionException.class)
                .extracting("errorCode").isEqualTo(SolutionErrorCode.NON_ADDITIVE_VOLUME_DENSITY_REQUIRED);
    }

    @Test
    @DisplayName("Regression: Non-additive volume mixing uses final volume = total mixture mass / mixture density")
    void testNonAdditiveVolumeMixingWithDensity() {
        // 10g NaCl + 90g H2O = 100g total mass. Density = 1.0 g/cm3 (1000 kg/m3).
        // Final volume = 100g / (1000 g/L) = 0.1 L = 100 mL.
        SolutionComposition sol1 = new SolutionComposition(
                "COMP-NACL", "COMP-H2O",
                Mass.of("10.0", MassUnit.GRAM), AmountOfSubstance.of("0.171", AmountOfSubstanceUnit.MOLE),
                Mass.of("90.0", MassUnit.GRAM), AmountOfSubstance.of("5.0", AmountOfSubstanceUnit.MOLE),
                null, null, Density.of("1.0", DensityUnit.GRAM_PER_CUBIC_CENTIMETER)
        );

        SolutionMixingResult result = calculator.mixSameSoluteSolutions(List.of(sol1), SolutionVolumeAssumption.NON_ADDITIVE_DENSITY_REQUIRED);
        assertThat(result.getFinalSolutionVolume().in(VolumeUnit.MILLILITER)).isCloseTo(new BigDecimal("100.0"), org.assertj.core.data.Offset.offset(new BigDecimal("0.01")));
    }
}

