package com.ailab.chemistry.domain.gas;

import com.ailab.chemistry.domain.measurement.AmountOfSubstance;
import com.ailab.chemistry.domain.measurement.AmountOfSubstanceUnit;
import com.ailab.chemistry.domain.measurement.Density;
import com.ailab.chemistry.domain.measurement.DensityUnit;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.PressureUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.measurement.Volume;
import com.ailab.chemistry.domain.measurement.VolumeUnit;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.Offset.offset;

class GasLawCalculatorTest {

    private final GasLawCalculator calculator = new GasLawCalculator();

    @Test
    void oneMoleIdealGasAtExplicitStandardPressureAndTemperatureHasExpectedMolarVolume() {
        GasStateResult result = calculator.calculateState(GasStateRequest.solveVolume(
                GasEquationModel.IDEAL_GAS,
                Pressure.of("101325", PressureUnit.PASCAL),
                AmountOfSubstance.of("1", AmountOfSubstanceUnit.MOLE),
                Temperature.of("273.15", TemperatureUnit.KELVIN),
                null));

        assertThat(result.status()).isEqualTo(GasCalculationStatus.SUCCESS);
        assertThat(result.state().volume().in(VolumeUnit.LITER))
                .isCloseTo(new BigDecimal("22.41396954"), offset(new BigDecimal("0.00001")));
    }

    @Test
    void solvesEachGasStateUnknownAndRejectsInconsistentOverdeterminedState() {
        GasState known = new GasState(
                Pressure.of("200000", PressureUnit.PASCAL),
                Volume.of("10", VolumeUnit.LITER),
                AmountOfSubstance.of("0.801815700284840276", AmountOfSubstanceUnit.MOLE),
                Temperature.of("300", TemperatureUnit.KELVIN),
                CompressibilityFactor.ideal());

        assertThat(calculator.calculateState(GasStateRequest.solvePressure(
                GasEquationModel.IDEAL_GAS, known.volume(), known.amount(), known.temperature(), null))
                .state().pressure().in(PressureUnit.PASCAL))
                .isCloseTo(new BigDecimal("200000"), offset(new BigDecimal("0.2")));
        assertThat(calculator.calculateState(GasStateRequest.solveAmount(
                GasEquationModel.IDEAL_GAS, known.pressure(), known.volume(), known.temperature(), null))
                .state().amount().in(AmountOfSubstanceUnit.MOLE))
                .isCloseTo(new BigDecimal("0.8018157"), offset(new BigDecimal("0.000001")));
        assertThat(calculator.calculateState(GasStateRequest.solveTemperature(
                GasEquationModel.IDEAL_GAS, known.pressure(), known.volume(), known.amount(), null))
                .state().temperature().in(TemperatureUnit.KELVIN))
                .isCloseTo(new BigDecimal("300"), offset(new BigDecimal("0.001")));

        GasStateResult consistent = calculator.calculateState(GasStateRequest.validate(
                GasEquationModel.IDEAL_GAS, known.pressure(), known.volume(), known.amount(), known.temperature(), null));
        assertThat(consistent.status()).isEqualTo(GasCalculationStatus.SUCCESS);
        assertThat(consistent.residual().abs()).isLessThan(new BigDecimal("0.001"));

        GasStateResult inconsistent = calculator.calculateState(GasStateRequest.validate(
                GasEquationModel.IDEAL_GAS, Pressure.of("210000", PressureUnit.PASCAL),
                known.volume(), known.amount(), known.temperature(), null));
        assertThat(inconsistent.status()).isEqualTo(GasCalculationStatus.RESIDUAL_EXCEEDS_TOLERANCE);
    }

    @Test
    void mixturesReconcileMoleFractionsAndPartialPressures() {
        GasMixtureResult result = calculator.calculateMixture(new GasMixture(
                Pressure.of("2", PressureUnit.BAR),
                List.of(
                        new GasMixtureComponent("COMP-N2", AmountOfSubstance.of("1.0", AmountOfSubstanceUnit.MOLE)),
                        new GasMixtureComponent("COMP-O2", AmountOfSubstance.of("3.0", AmountOfSubstanceUnit.MOLE))
                )));

        assertThat(result.status()).isEqualTo(GasCalculationStatus.SUCCESS);
        assertThat(result.moleFractions().get(0).value()).isCloseTo(new BigDecimal("0.25"), offset(new BigDecimal("0.000001")));
        assertThat(result.partialPressures().get(1).pressure().in(PressureUnit.BAR)).isCloseTo(new BigDecimal("1.5"), offset(new BigDecimal("0.000001")));
        assertThat(result.partialPressureSum().in(PressureUnit.BAR)).isCloseTo(new BigDecimal("2.0"), offset(new BigDecimal("0.000001")));
    }

    @Test
    void explicitCompressibilityFactorAndDensityMolarMassAreDeterministic() {
        GasStateResult ideal = calculator.calculateState(GasStateRequest.solveVolume(
                GasEquationModel.IDEAL_GAS,
                Pressure.of("10", PressureUnit.BAR),
                AmountOfSubstance.of("2", AmountOfSubstanceUnit.MOLE),
                Temperature.of("350", TemperatureUnit.KELVIN),
                null));
        GasStateResult zGas = calculator.calculateState(GasStateRequest.solveVolume(
                GasEquationModel.EXPLICIT_COMPRESSIBILITY_FACTOR,
                Pressure.of("10", PressureUnit.BAR),
                AmountOfSubstance.of("2", AmountOfSubstanceUnit.MOLE),
                Temperature.of("350", TemperatureUnit.KELVIN),
                CompressibilityFactor.of("0.85")));

        assertThat(zGas.state().volume().in(VolumeUnit.LITER))
                .isCloseTo(ideal.state().volume().in(VolumeUnit.LITER).multiply(new BigDecimal("0.85")), offset(new BigDecimal("0.000001")));
        assertThat(calculator.calculateDensity(
                Pressure.of("101325", PressureUnit.PASCAL),
                new BigDecimal("0.0280134"),
                Temperature.of("273.15", TemperatureUnit.KELVIN),
                CompressibilityFactor.ideal()).in(DensityUnit.KILOGRAM_PER_CUBIC_METER))
                .isCloseTo(new BigDecimal("1.24982"), offset(new BigDecimal("0.0001")));
        assertThat(calculator.calculateMolarMass(
                Density.of("1.24982", DensityUnit.KILOGRAM_PER_CUBIC_METER),
                Pressure.of("101325", PressureUnit.PASCAL),
                Temperature.of("273.15", TemperatureUnit.KELVIN),
                CompressibilityFactor.ideal()))
                .isCloseTo(new BigDecimal("0.028013"), offset(new BigDecimal("0.00001")));
        assertThat(zGas.state().volume()).isEqualTo(calculator.calculateState(GasStateRequest.solveVolume(
                GasEquationModel.EXPLICIT_COMPRESSIBILITY_FACTOR,
                Pressure.of("10", PressureUnit.BAR),
                AmountOfSubstance.of("2", AmountOfSubstanceUnit.MOLE),
                Temperature.of("350", TemperatureUnit.KELVIN),
                CompressibilityFactor.of("0.85"))).state().volume());
    }

    @Test
    void explicitTransformationConstraintsAreRequiredAndApplied() {
        GasStateTransformation boyle = GasStateTransformation.solveFinalVolume(
                GasTransformationConstraint.CONSTANT_TEMPERATURE,
                Pressure.of("1", PressureUnit.BAR), Volume.of("10", VolumeUnit.LITER), Temperature.of("300", TemperatureUnit.KELVIN),
                Pressure.of("2", PressureUnit.BAR), null, Temperature.of("300", TemperatureUnit.KELVIN));
        assertThat(calculator.calculateTransformation(boyle).state().volume().in(VolumeUnit.LITER))
                .isCloseTo(new BigDecimal("5"), offset(new BigDecimal("0.000001")));

        GasStateTransformation charles = GasStateTransformation.solveFinalVolume(
                GasTransformationConstraint.CONSTANT_PRESSURE,
                Pressure.of("1", PressureUnit.BAR), Volume.of("10", VolumeUnit.LITER), Temperature.of("300", TemperatureUnit.KELVIN),
                Pressure.of("1", PressureUnit.BAR), null, Temperature.of("600", TemperatureUnit.KELVIN));
        assertThat(calculator.calculateTransformation(charles).state().volume().in(VolumeUnit.LITER))
                .isCloseTo(new BigDecimal("20"), offset(new BigDecimal("0.000001")));

        GasStateTransformation combined = GasStateTransformation.solveFinalPressure(
                GasTransformationConstraint.CONSTANT_AMOUNT,
                Pressure.of("1", PressureUnit.BAR), Volume.of("10", VolumeUnit.LITER), Temperature.of("300", TemperatureUnit.KELVIN),
                null, Volume.of("5", VolumeUnit.LITER), Temperature.of("600", TemperatureUnit.KELVIN));
        assertThat(calculator.calculateTransformation(combined).state().pressure().in(PressureUnit.BAR))
                .isCloseTo(new BigDecimal("4"), offset(new BigDecimal("0.000001")));

        assertThatThrownBy(() -> GasStateTransformation.solveFinalVolume(
                null,
                Pressure.of("1", PressureUnit.BAR), Volume.of("10", VolumeUnit.LITER), Temperature.of("300", TemperatureUnit.KELVIN),
                Pressure.of("2", PressureUnit.BAR), null, Temperature.of("300", TemperatureUnit.KELVIN)))
                .isInstanceOf(GasLawException.class)
                .extracting("errorCode")
                .isEqualTo(GasLawErrorCode.MISSING_PROCESS_CONSTRAINT);
    }

    @Test
    void invalidPositiveQuantitiesAndUnsupportedModelsAreRejected() {
        assertThatThrownBy(() -> CompressibilityFactor.of("0"))
                .isInstanceOf(GasLawException.class)
                .extracting("errorCode")
                .isEqualTo(GasLawErrorCode.INVALID_COMPRESSIBILITY_FACTOR);
        assertThatThrownBy(() -> calculator.calculateState(GasStateRequest.solveVolume(
                GasEquationModel.EXPLICIT_COMPRESSIBILITY_FACTOR,
                Pressure.of("1", PressureUnit.BAR),
                AmountOfSubstance.of("1", AmountOfSubstanceUnit.MOLE),
                Temperature.of("300", TemperatureUnit.KELVIN),
                null)))
                .isInstanceOf(GasLawException.class)
                .extracting("errorCode")
                .isEqualTo(GasLawErrorCode.MISSING_COMPRESSIBILITY_FACTOR);
        assertThatThrownBy(() -> calculator.calculateState(GasStateRequest.solveTemperature(
                GasEquationModel.IDEAL_GAS,
                Pressure.of("1", PressureUnit.BAR),
                Volume.of("1", VolumeUnit.LITER),
                AmountOfSubstance.of("0", AmountOfSubstanceUnit.MOLE),
                null)))
                .isInstanceOf(GasLawException.class)
                .extracting("errorCode")
                .isEqualTo(GasLawErrorCode.INVALID_AMOUNT);
    }
}
