package com.ailab.chemistry.domain.phasebehavior;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.AmountOfSubstance;
import com.ailab.chemistry.domain.measurement.AmountOfSubstanceUnit;
import com.ailab.chemistry.domain.measurement.EnergyUnit;
import com.ailab.chemistry.domain.measurement.MolarHeatCapacity;
import com.ailab.chemistry.domain.measurement.MolarHeatCapacityUnit;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.PressureUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.infrastructure.persistence.phasebehavior.InMemoryPhaseBehaviorRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.Offset.offset;

class PhaseBehaviorCalculatorTest {

    private final PhaseBehaviorCalculator calculator = new PhaseBehaviorCalculator();
    private final PhaseBehaviorRepository repository = InMemoryPhaseBehaviorRepository.reference();

    @Test
    void waterForwardAndReverseTransitionsHaveEqualMagnitudeAndOppositeSigns() {
        PhaseTransitionResult fusion = calculator.calculateTransition(new PhaseTransitionRequest(
                "COMP-H2O", PhaseTransitionType.FUSION, MatterState.SOLID, MatterState.LIQUID,
                AmountOfSubstance.of("2", AmountOfSubstanceUnit.MOLE),
                Temperature.of("273.15", TemperatureUnit.KELVIN),
                Pressure.of("1", PressureUnit.ATMOSPHERE)), repository);
        PhaseTransitionResult freezing = calculator.calculateTransition(new PhaseTransitionRequest(
                "COMP-H2O", PhaseTransitionType.FREEZING, MatterState.LIQUID, MatterState.SOLID,
                AmountOfSubstance.of("2", AmountOfSubstanceUnit.MOLE),
                Temperature.of("273.15", TemperatureUnit.KELVIN),
                Pressure.of("1", PressureUnit.ATMOSPHERE)), repository);
        PhaseTransitionResult vaporization = calculator.calculateTransition(new PhaseTransitionRequest(
                "COMP-H2O", PhaseTransitionType.VAPORIZATION, MatterState.LIQUID, MatterState.GAS,
                AmountOfSubstance.of("1", AmountOfSubstanceUnit.MOLE),
                Temperature.of("373.15", TemperatureUnit.KELVIN),
                Pressure.of("1", PressureUnit.ATMOSPHERE)), repository);
        PhaseTransitionResult condensation = calculator.calculateTransition(new PhaseTransitionRequest(
                "COMP-H2O", PhaseTransitionType.CONDENSATION, MatterState.GAS, MatterState.LIQUID,
                AmountOfSubstance.of("1", AmountOfSubstanceUnit.MOLE),
                Temperature.of("373.15", TemperatureUnit.KELVIN),
                Pressure.of("1", PressureUnit.ATMOSPHERE)), repository);

        assertThat(fusion.heat().in(EnergyUnit.JOULE)).isCloseTo(new BigDecimal("12022"), offset(new BigDecimal("0.1")));
        assertThat(freezing.heat().in(EnergyUnit.JOULE)).isEqualByComparingTo(fusion.heat().in(EnergyUnit.JOULE).negate());
        assertThat(vaporization.heat().in(EnergyUnit.JOULE)).isCloseTo(new BigDecimal("40650"), offset(new BigDecimal("1")));
        assertThat(condensation.heat().in(EnergyUnit.JOULE)).isEqualByComparingTo(vaporization.heat().in(EnergyUnit.JOULE).negate());
    }

    @Test
    void sourcedAntoineSaturationPressureAndNormalBoilingPointRespectValidity() {
        SaturationPressureResult at100c = calculator.calculateSaturationPressure(new SaturationPressureRequest(
                "COMP-H2O", MatterState.LIQUID, MatterState.GAS,
                Temperature.of("373.15", TemperatureUnit.KELVIN)), repository);

        assertThat(at100c.status()).isEqualTo(PhaseBehaviorStatus.SUCCESS);
        assertThat(at100c.pressure().in(PressureUnit.ATMOSPHERE)).isCloseTo(BigDecimal.ONE, offset(new BigDecimal("0.001")));

        BoilingPointResult boiling = calculator.calculateBoilingPoint(new BoilingPointRequest(
                "COMP-H2O", MatterState.LIQUID, MatterState.GAS, Pressure.of("1", PressureUnit.ATMOSPHERE)), repository);
        assertThat(boiling.status()).isEqualTo(PhaseBehaviorStatus.CONVERGED);
        assertThat(boiling.temperature().in(TemperatureUnit.CELSIUS)).isCloseTo(new BigDecimal("100"), offset(new BigDecimal("0.02")));

        assertThat(calculator.calculateSaturationPressure(new SaturationPressureRequest(
                "COMP-H2O", MatterState.LIQUID, MatterState.GAS,
                Temperature.of("274.15", TemperatureUnit.KELVIN)), repository).status())
                .isEqualTo(PhaseBehaviorStatus.SUCCESS);
        assertThatThrownBy(() -> calculator.calculateSaturationPressure(new SaturationPressureRequest(
                "COMP-H2O", MatterState.LIQUID, MatterState.GAS,
                Temperature.of("273.14", TemperatureUnit.KELVIN)), repository))
                .isInstanceOf(PhaseBehaviorException.class)
                .extracting("errorCode")
                .isEqualTo(PhaseBehaviorErrorCode.CORRELATION_OUT_OF_RANGE);
    }

    @Test
    void tripleAndCriticalBoundariesPreventFabricatedLiquidVaporPaths() {
        assertThatThrownBy(() -> calculator.calculateTransition(new PhaseTransitionRequest(
                "COMP-CO2", PhaseTransitionType.VAPORIZATION, MatterState.LIQUID, MatterState.GAS,
                AmountOfSubstance.of("1", AmountOfSubstanceUnit.MOLE),
                Temperature.of("250", TemperatureUnit.KELVIN),
                Pressure.of("1", PressureUnit.ATMOSPHERE)), repository))
                .isInstanceOf(PhaseBehaviorException.class)
                .extracting("errorCode")
                .isEqualTo(PhaseBehaviorErrorCode.BELOW_TRIPLE_POINT_PRESSURE);

        assertThatThrownBy(() -> calculator.calculateSaturationPressure(new SaturationPressureRequest(
                "COMP-H2O", MatterState.LIQUID, MatterState.GAS,
                Temperature.of("650", TemperatureUnit.KELVIN)), repository))
                .isInstanceOf(PhaseBehaviorException.class)
                .extracting("errorCode")
                .isEqualTo(PhaseBehaviorErrorCode.ABOVE_CRITICAL_POINT);
    }

    @Test
    void heatingPathSegmentsAreContinuousAndReverseToOppositeTotalHeat() {
        HeatingPathResult heating = calculator.calculateHeatingPath(new HeatingPathRequest(
                "COMP-H2O",
                AmountOfSubstance.of("1", AmountOfSubstanceUnit.MOLE),
                MatterState.LIQUID,
                MatterState.GAS,
                Temperature.of("298.15", TemperatureUnit.KELVIN),
                Temperature.of("400.15", TemperatureUnit.KELVIN),
                Pressure.of("1", PressureUnit.ATMOSPHERE),
                List.of(
                        new SensiblePhaseSegmentSpec(MatterState.LIQUID, Temperature.of("298.15", TemperatureUnit.KELVIN), Temperature.of("373.15", TemperatureUnit.KELVIN), MolarHeatCapacity.of("75.3", MolarHeatCapacityUnit.JOULE_PER_MOLE_KELVIN)),
                        new TransitionSegmentSpec(PhaseTransitionType.VAPORIZATION, MatterState.LIQUID, MatterState.GAS, Temperature.of("373.15", TemperatureUnit.KELVIN)),
                        new SensiblePhaseSegmentSpec(MatterState.GAS, Temperature.of("373.15", TemperatureUnit.KELVIN), Temperature.of("400.15", TemperatureUnit.KELVIN), MolarHeatCapacity.of("33.6", MolarHeatCapacityUnit.JOULE_PER_MOLE_KELVIN))
                )), repository);

        assertThat(heating.status()).isEqualTo(PhaseBehaviorStatus.SUCCESS);
        assertThat(heating.segments()).hasSize(3);
        assertThat(heating.totalHeat().in(EnergyUnit.JOULE)).isCloseTo(new BigDecimal("47204.7"), offset(new BigDecimal("0.5")));
        assertThat(heating.segmentHeatSum().in(EnergyUnit.JOULE)).isEqualByComparingTo(heating.totalHeat().in(EnergyUnit.JOULE));

        HeatingPathResult cooling = calculator.calculateHeatingPath(new HeatingPathRequest(
                "COMP-H2O",
                AmountOfSubstance.of("1", AmountOfSubstanceUnit.MOLE),
                MatterState.GAS,
                MatterState.LIQUID,
                Temperature.of("400.15", TemperatureUnit.KELVIN),
                Temperature.of("298.15", TemperatureUnit.KELVIN),
                Pressure.of("1", PressureUnit.ATMOSPHERE),
                List.of(
                        new SensiblePhaseSegmentSpec(MatterState.GAS, Temperature.of("400.15", TemperatureUnit.KELVIN), Temperature.of("373.15", TemperatureUnit.KELVIN), MolarHeatCapacity.of("33.6", MolarHeatCapacityUnit.JOULE_PER_MOLE_KELVIN)),
                        new TransitionSegmentSpec(PhaseTransitionType.CONDENSATION, MatterState.GAS, MatterState.LIQUID, Temperature.of("373.15", TemperatureUnit.KELVIN)),
                        new SensiblePhaseSegmentSpec(MatterState.LIQUID, Temperature.of("373.15", TemperatureUnit.KELVIN), Temperature.of("298.15", TemperatureUnit.KELVIN), MolarHeatCapacity.of("75.3", MolarHeatCapacityUnit.JOULE_PER_MOLE_KELVIN))
                )), repository);

        assertThat(cooling.totalHeat().in(EnergyUnit.JOULE)).isEqualByComparingTo(heating.totalHeat().in(EnergyUnit.JOULE).negate());
    }

    @Test
    void missingDataPhaseMismatchUnsupportedPressureAndSkippedTransitionAreRejected() {
        assertThatThrownBy(() -> calculator.calculateTransition(new PhaseTransitionRequest(
                "COMP-ETHANOL", PhaseTransitionType.FUSION, MatterState.SOLID, MatterState.LIQUID,
                AmountOfSubstance.of("1", AmountOfSubstanceUnit.MOLE),
                Temperature.of("159", TemperatureUnit.KELVIN),
                Pressure.of("1", PressureUnit.ATMOSPHERE)), repository))
                .isInstanceOf(PhaseBehaviorException.class)
                .extracting("errorCode")
                .isEqualTo(PhaseBehaviorErrorCode.MISSING_TRANSITION_RECORD);
        assertThatThrownBy(() -> calculator.calculateTransition(new PhaseTransitionRequest(
                "COMP-H2O", PhaseTransitionType.VAPORIZATION, MatterState.SOLID, MatterState.GAS,
                AmountOfSubstance.of("1", AmountOfSubstanceUnit.MOLE),
                Temperature.of("373.15", TemperatureUnit.KELVIN),
                Pressure.of("1", PressureUnit.ATMOSPHERE)), repository))
                .isInstanceOf(PhaseBehaviorException.class)
                .extracting("errorCode")
                .isEqualTo(PhaseBehaviorErrorCode.PHASE_MISMATCH);
        assertThatThrownBy(() -> calculator.calculateTransition(new PhaseTransitionRequest(
                "COMP-H2O", PhaseTransitionType.VAPORIZATION, MatterState.LIQUID, MatterState.GAS,
                AmountOfSubstance.of("1", AmountOfSubstanceUnit.MOLE),
                Temperature.of("373.15", TemperatureUnit.KELVIN),
                Pressure.of("2", PressureUnit.ATMOSPHERE)), repository))
                .isInstanceOf(PhaseBehaviorException.class)
                .extracting("errorCode")
                .isEqualTo(PhaseBehaviorErrorCode.UNSUPPORTED_PRESSURE);
        HeatingPathRequest skippedTransition = new HeatingPathRequest(
                "COMP-H2O",
                AmountOfSubstance.of("1", AmountOfSubstanceUnit.MOLE),
                MatterState.LIQUID,
                MatterState.GAS,
                Temperature.of("298.15", TemperatureUnit.KELVIN),
                Temperature.of("400.15", TemperatureUnit.KELVIN),
                Pressure.of("1", PressureUnit.ATMOSPHERE),
                List.of(new SensiblePhaseSegmentSpec(
                        MatterState.LIQUID,
                        Temperature.of("298.15", TemperatureUnit.KELVIN),
                        Temperature.of("400.15", TemperatureUnit.KELVIN),
                        MolarHeatCapacity.of("75.3", MolarHeatCapacityUnit.JOULE_PER_MOLE_KELVIN))));

        assertThatThrownBy(() -> calculator.calculateHeatingPath(skippedTransition, repository))
                .isInstanceOf(PhaseBehaviorException.class)
                .extracting("errorCode")
                .isEqualTo(PhaseBehaviorErrorCode.SKIPPED_KNOWN_TRANSITION);
    }
}
