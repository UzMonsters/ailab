package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.measurement.AmountOfSubstance;
import com.ailab.chemistry.domain.measurement.AmountOfSubstanceUnit;
import com.ailab.chemistry.domain.measurement.MolarConcentrationUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.measurement.Volume;
import com.ailab.chemistry.domain.measurement.VolumeUnit;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BufferCalculatorTest {

    private static final Temperature T25 = Temperature.of("25.0", TemperatureUnit.CELSIUS);
    private static final Volume ONE_LITER = Volume.of("1.0", VolumeUnit.LITER);
    private static final BufferCalculator CALCULATOR = new BufferCalculator();
    private static final BufferSystem ACETIC_ACETATE = new BufferSystem(
            "PAIR-CH3COOH-CH3COO",
            "SPEC-CH3COOH",
            "SPEC-CH3COO-MINUS",
            BufferSystemType.WEAK_ACID_CONJUGATE_BASE,
            new BigDecimal("1.75e-5"),
            new BigDecimal("5.71e-10"),
            new BigDecimal("1.00e-14"),
            T25,
            "COMP-H2O",
            List.of("CRC Handbook of Chemistry and Physics, 104th Ed.")
    );
    private static final BufferSystem AMMONIUM_AMMONIA = new BufferSystem(
            "PAIR-NH4-NH3",
            "SPEC-NH4-PLUS",
            "SPEC-NH3",
            BufferSystemType.WEAK_BASE_CONJUGATE_ACID,
            new BigDecimal("5.69e-10"),
            new BigDecimal("1.76e-5"),
            new BigDecimal("1.00e-14"),
            T25,
            "COMP-H2O",
            List.of("CRC Handbook of Chemistry and Physics, 104th Ed.")
    );

    @Test
    void weakAcidBufferUsesComponentMoleRatio() {
        BufferCalculationResult equal = CALCULATOR.calculate(BufferCalculationRequest.fromAmounts(
                ACETIC_ACETATE,
                amount("0.100"),
                amount("0.100"),
                ONE_LITER
        ));

        assertThat(equal.getStatus()).isEqualTo(BufferRegionStatus.VALID_BUFFER);
        assertThat(equal.getPh().getValue()).isCloseTo(new BigDecimal("4.7567"), org.assertj.core.data.Offset.offset(new BigDecimal("0.0005")));
        assertThat(equal.getCalculationMethod()).isEqualTo(BufferCalculationMethod.HENDERSON_HASSELBALCH);
        assertThat(equal.getComponentRatio()).isEqualByComparingTo("1");

        BufferCalculationResult tenToOne = CALCULATOR.calculate(BufferCalculationRequest.fromAmounts(
                ACETIC_ACETATE,
                amount("0.010"),
                amount("0.100"),
                ONE_LITER
        ));

        assertThat(tenToOne.getPh().getValue()).isCloseTo(new BigDecimal("5.7567"), org.assertj.core.data.Offset.offset(new BigDecimal("0.0005")));
    }

    @Test
    void weakBaseBufferUsesPohAndActivePkw() {
        BufferCalculationResult equal = CALCULATOR.calculate(BufferCalculationRequest.fromAmounts(
                AMMONIUM_AMMONIA,
                amount("0.100"),
                amount("0.100"),
                ONE_LITER
        ));

        assertThat(equal.getPoh().getValue()).isCloseTo(new BigDecimal("4.7545"), org.assertj.core.data.Offset.offset(new BigDecimal("0.0005")));
        assertThat(equal.getPh().getValue()).isCloseTo(new BigDecimal("9.2455"), org.assertj.core.data.Offset.offset(new BigDecimal("0.0005")));
    }

    @Test
    void preparationComputesComponentsFromTargetPhAndTotalConcentration() {
        BufferPreparationResult result = CALCULATOR.calculatePreparation(new BufferPreparationRequest(
                ACETIC_ACETATE,
                new BigDecimal("4.7567"),
                new BigDecimal("0.200"),
                ONE_LITER
        ));

        assertThat(result.getStatus()).isEqualTo(BufferRegionStatus.VALID_BUFFER);
        assertThat(result.getAcidComponentConcentration().in(MolarConcentrationUnit.MOL_PER_LITER))
                .isCloseTo(new BigDecimal("0.100"), org.assertj.core.data.Offset.offset(new BigDecimal("0.0001")));
        assertThat(result.getBaseComponentConcentration().in(MolarConcentrationUnit.MOL_PER_LITER))
                .isCloseTo(new BigDecimal("0.100"), org.assertj.core.data.Offset.offset(new BigDecimal("0.0001")));
        assertThat(result.getAcidComponentMoles().in(AmountOfSubstanceUnit.MOLE))
                .isCloseTo(new BigDecimal("0.100"), org.assertj.core.data.Offset.offset(new BigDecimal("0.0001")));
    }

    @Test
    void dilutionPreservesIdealPhAndMolesButLowersCapacity() {
        BufferCalculationRequest initial = BufferCalculationRequest.fromAmounts(
                ACETIC_ACETATE,
                amount("0.100"),
                amount("0.100"),
                ONE_LITER
        );

        BufferCalculationResult before = CALCULATOR.calculate(initial);
        BufferCalculationResult diluted = CALCULATOR.dilute(initial, Volume.of("2.0", VolumeUnit.LITER));

        assertThat(diluted.getStatus()).isEqualTo(BufferRegionStatus.VALID_BUFFER);
        assertThat(diluted.getPh().getValue()).isEqualByComparingTo(before.getPh().getValue());
        assertThat(diluted.getAcidComponent().getAmount().in(AmountOfSubstanceUnit.MOLE)).isEqualByComparingTo("0.100");
        assertThat(diluted.getTotalBufferConcentration().in(MolarConcentrationUnit.MOL_PER_LITER)).isEqualByComparingTo("0.100");
        assertThat(diluted.getCapacity().getApproximateCapacity()).isLessThan(before.getCapacity().getApproximateCapacity());
    }

    @Test
    void strongReagentsNeutralizeBeforeRecalculatingAndReportExhaustion() {
        BufferCalculationRequest initial = BufferCalculationRequest.fromAmounts(
                ACETIC_ACETATE,
                amount("0.100"),
                amount("0.100"),
                ONE_LITER
        );

        BufferPerturbationResult acidAdded = CALCULATOR.addStrongAcidOrBase(BufferPerturbationRequest.strongAcidNegligibleVolume(initial, amount("0.050")));
        assertThat(acidAdded.getStatus()).isEqualTo(BufferRegionStatus.VALID_BUFFER);
        assertThat(acidAdded.getResult().orElseThrow().getComponentRatio()).isEqualByComparingTo("0.3333333333333333333333333333333333");
        assertThat(acidAdded.getResult().orElseThrow().getPh().getValue()).isCloseTo(new BigDecimal("4.2796"), org.assertj.core.data.Offset.offset(new BigDecimal("0.001")));

        BufferPerturbationResult baseAdded = CALCULATOR.addStrongAcidOrBase(BufferPerturbationRequest.strongBaseNegligibleVolume(initial, amount("0.050")));
        assertThat(baseAdded.getStatus()).isEqualTo(BufferRegionStatus.VALID_BUFFER);
        assertThat(baseAdded.getResult().orElseThrow().getComponentRatio()).isEqualByComparingTo("3");

        BufferPerturbationResult exact = CALCULATOR.addStrongAcidOrBase(BufferPerturbationRequest.strongAcidNegligibleVolume(initial, amount("0.100")));
        assertThat(exact.getStatus()).isEqualTo(BufferRegionStatus.EXACT_EXHAUSTION);
        assertThat(exact.getResult()).isEmpty();

        BufferPerturbationResult excess = CALCULATOR.addStrongAcidOrBase(BufferPerturbationRequest.strongAcidNegligibleVolume(initial, amount("0.150")));
        assertThat(excess.getStatus()).isEqualTo(BufferRegionStatus.EXCESS_STRONG_ACID_UNSUPPORTED);
        assertThat(excess.getRemainingExcessAmount().orElseThrow().in(AmountOfSubstanceUnit.MOLE)).isEqualByComparingTo("0.050");
    }

    @Test
    void perturbationRequiresExplicitVolumePolicyAndUsesFinalVolumeForConcentrationsAndCapacity() {
        BufferCalculationRequest initial = BufferCalculationRequest.fromAmounts(
                ACETIC_ACETATE,
                amount("0.100"),
                amount("0.100"),
                ONE_LITER
        );

        BufferPerturbationResult unchangedVolume = CALCULATOR.addStrongAcidOrBase(
                BufferPerturbationRequest.strongAcidNegligibleVolume(initial, amount("0.050"))
        );
        BufferPerturbationResult explicitTwoLiters = CALCULATOR.addStrongAcidOrBase(
                BufferPerturbationRequest.strongAcidWithFinalVolume(initial, amount("0.050"), Volume.of("2.0", VolumeUnit.LITER))
        );

        BufferCalculationResult unchanged = unchangedVolume.getResult().orElseThrow();
        BufferCalculationResult diluted = explicitTwoLiters.getResult().orElseThrow();
        assertThat(unchanged.getAcidComponent().getConcentration().in(MolarConcentrationUnit.MOL_PER_LITER)).isEqualByComparingTo("0.150");
        assertThat(diluted.getAcidComponent().getConcentration().in(MolarConcentrationUnit.MOL_PER_LITER)).isEqualByComparingTo("0.075");
        assertThat(unchanged.getBaseComponent().getConcentration().in(MolarConcentrationUnit.MOL_PER_LITER)).isEqualByComparingTo("0.050");
        assertThat(diluted.getBaseComponent().getConcentration().in(MolarConcentrationUnit.MOL_PER_LITER)).isEqualByComparingTo("0.025");
        assertThat(diluted.getPh().getValue()).isEqualByComparingTo(unchanged.getPh().getValue());
        assertThat(diluted.getCapacity().getApproximateCapacity()).isLessThan(unchanged.getCapacity().getApproximateCapacity());

        assertThatThrownBy(() -> BufferPerturbationRequest.strongAcidWithFinalVolume(initial, amount("0.050"), Volume.of("0.5", VolumeUnit.LITER)))
                .isInstanceOf(BufferException.class)
                .extracting("errorCode")
                .isEqualTo(BufferErrorCode.INVALID_FINAL_VOLUME);
    }

    @Test
    void rejectsInvalidUnsafeOrUnsupportedRequests() {
        assertThatThrownBy(() -> BufferCalculationRequest.fromAmounts(ACETIC_ACETATE, amount("0"), amount("0.1"), ONE_LITER))
                .isInstanceOf(BufferException.class)
                .extracting("errorCode")
                .isEqualTo(BufferErrorCode.NON_POSITIVE_COMPONENT_AMOUNT);

        BufferSystem nonWater = new BufferSystem(
                "PAIR-CH3COOH-CH3COO",
                "SPEC-CH3COOH",
                "SPEC-CH3COO-MINUS",
                BufferSystemType.WEAK_ACID_CONJUGATE_BASE,
                new BigDecimal("1.75e-5"),
                new BigDecimal("5.71e-10"),
                new BigDecimal("1.00e-14"),
                T25,
                "COMP-METHANOL",
                List.of("test")
        );

        assertThatThrownBy(() -> CALCULATOR.calculate(BufferCalculationRequest.fromAmounts(nonWater, amount("0.1"), amount("0.1"), ONE_LITER)))
                .isInstanceOf(BufferException.class)
                .extracting("errorCode")
                .isEqualTo(BufferErrorCode.UNSUPPORTED_SOLVENT);
    }

    @Test
    void repeatedAndScaleEquivalentCalculationsAreDeterministic() {
        BufferCalculationRequest left = BufferCalculationRequest.fromAmounts(ACETIC_ACETATE, amount("0.1000"), amount("0.100"), ONE_LITER);
        BufferCalculationRequest right = BufferCalculationRequest.fromAmounts(ACETIC_ACETATE, amount("0.1"), amount("0.10"), ONE_LITER);

        assertThat(CALCULATOR.calculate(left)).isEqualTo(CALCULATOR.calculate(right));
    }

    private static AmountOfSubstance amount(String value) {
        return AmountOfSubstance.of(value, AmountOfSubstanceUnit.MOLE);
    }
}
