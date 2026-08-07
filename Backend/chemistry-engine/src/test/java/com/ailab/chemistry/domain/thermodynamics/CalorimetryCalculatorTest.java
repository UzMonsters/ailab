package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.AmountOfSubstance;
import com.ailab.chemistry.domain.measurement.AmountOfSubstanceUnit;
import com.ailab.chemistry.domain.measurement.EnergyUnit;
import com.ailab.chemistry.domain.measurement.Mass;
import com.ailab.chemistry.domain.measurement.MassUnit;
import com.ailab.chemistry.domain.measurement.MolarHeatCapacity;
import com.ailab.chemistry.domain.measurement.MolarHeatCapacityUnit;
import com.ailab.chemistry.domain.measurement.SpecificHeatCapacity;
import com.ailab.chemistry.domain.measurement.SpecificHeatCapacityUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.measurement.exception.BelowAbsoluteZeroException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CalorimetryCalculatorTest {

    private final CalorimetryCalculator calculator = new CalorimetryCalculator();

    @Test
    void sensibleHeatMassAndMolarBasesAndHeatingCoolingSigns() {
        // Mass basis: 2 kg water (4184 J/kg*K), 293.15 K -> 313.15 K (+20 K)
        ThermalSample sampleMassHeating = new ThermalSample(
                "COMP-H2O", MatterState.LIQUID,
                Mass.of("2.0", MassUnit.KILOGRAM), null,
                SpecificHeatCapacity.of("4184", SpecificHeatCapacityUnit.JOULE_PER_KILOGRAM_KELVIN), null,
                Temperature.of("293.15", TemperatureUnit.KELVIN)
        );

        SensibleHeatResult resHeating = calculator.calculateSensibleHeat(
                new SensibleHeatRequest(sampleMassHeating, Temperature.of("313.15", TemperatureUnit.KELVIN), CalorimetryMethod.CONSTANT_SPECIFIC_HEAT_CAPACITY), null);

        assertThat(resHeating.heatTransferredJoules().in(EnergyUnit.JOULE)).isCloseTo(new BigDecimal("167360"), org.assertj.core.data.Offset.offset(new BigDecimal("1.0")));
        assertThat(resHeating.heatTransferredJoules().in(EnergyUnit.JOULE)).isGreaterThan(BigDecimal.ZERO);

        // Cooling sign: initial 313.15 K -> final 293.15 K (-20 K)
        ThermalSample sampleMassCooling = new ThermalSample(
                "COMP-H2O", MatterState.LIQUID,
                Mass.of("2.0", MassUnit.KILOGRAM), null,
                SpecificHeatCapacity.of("4184", SpecificHeatCapacityUnit.JOULE_PER_KILOGRAM_KELVIN), null,
                Temperature.of("313.15", TemperatureUnit.KELVIN)
        );

        SensibleHeatResult resCooling = calculator.calculateSensibleHeat(
                new SensibleHeatRequest(sampleMassCooling, Temperature.of("293.15", TemperatureUnit.KELVIN), CalorimetryMethod.CONSTANT_SPECIFIC_HEAT_CAPACITY), null);

        assertThat(resCooling.heatTransferredJoules().in(EnergyUnit.JOULE)).isLessThan(BigDecimal.ZERO);
        assertThat(resCooling.heatTransferredJoules().in(EnergyUnit.JOULE)).isEqualTo(resHeating.heatTransferredJoules().in(EnergyUnit.JOULE).negate());

        // Molar basis: 10 mol gas, Cp,m = 29.1 J/mol*K, 300 K -> 400 K (+100 K)
        ThermalSample sampleMolar = new ThermalSample(
                "COMP-O2", MatterState.GAS,
                null, AmountOfSubstance.of("10.0", AmountOfSubstanceUnit.MOLE),
                null, MolarHeatCapacity.of("29.1", MolarHeatCapacityUnit.JOULE_PER_MOLE_KELVIN),
                Temperature.of("300.0", TemperatureUnit.KELVIN)
        );

        SensibleHeatResult resMolar = calculator.calculateSensibleHeat(
                new SensibleHeatRequest(sampleMolar, Temperature.of("400.0", TemperatureUnit.KELVIN), CalorimetryMethod.CONSTANT_MOLAR_HEAT_CAPACITY), null);

        assertThat(resMolar.heatTransferredJoules().in(EnergyUnit.JOULE)).isCloseTo(new BigDecimal("29100"), org.assertj.core.data.Offset.offset(new BigDecimal("0.1")));
    }

    @Test
    void thermalMixingEqualMassesSameSubstance20CAnd80CProduces50C() {
        ThermalSample s1 = new ThermalSample(
                "COMP-H2O", MatterState.LIQUID,
                Mass.of("1.0", MassUnit.KILOGRAM), null,
                SpecificHeatCapacity.of("4184", SpecificHeatCapacityUnit.JOULE_PER_KILOGRAM_KELVIN), null,
                Temperature.of("293.15", TemperatureUnit.KELVIN) // 20 °C
        );

        ThermalSample s2 = new ThermalSample(
                "COMP-H2O", MatterState.LIQUID,
                Mass.of("1.0", MassUnit.KILOGRAM), null,
                SpecificHeatCapacity.of("4184", SpecificHeatCapacityUnit.JOULE_PER_KILOGRAM_KELVIN), null,
                Temperature.of("353.15", TemperatureUnit.KELVIN) // 80 °C
        );

        ThermalMixingResult result = calculator.calculateFinalTemperature(
                new ThermalMixingRequest(List.of(s1, s2), null, CalorimetryMethod.CONSTANT_SPECIFIC_HEAT_CAPACITY), null);

        assertThat(result.status()).isEqualTo(CalorimetryStatus.CONVERGED);
        assertThat(result.finalTemperature().in(TemperatureUnit.CELSIUS)).isCloseTo(new BigDecimal("50.0"), org.assertj.core.data.Offset.offset(new BigDecimal("0.001")));
        assertThat(result.energyBalance().isBalanced()).isTrue();
    }

    @Test
    void thermalMixingCalorimeterCapacityShiftsFinalTemperature() {
        ThermalSample hot = new ThermalSample(
                "COMP-H2O", MatterState.LIQUID,
                Mass.of("1.0", MassUnit.KILOGRAM), null,
                SpecificHeatCapacity.of("4184", SpecificHeatCapacityUnit.JOULE_PER_KILOGRAM_KELVIN), null,
                Temperature.of("353.15", TemperatureUnit.KELVIN) // 80 °C
        );

        ThermalSample cold = new ThermalSample(
                "COMP-H2O", MatterState.LIQUID,
                Mass.of("1.0", MassUnit.KILOGRAM), null,
                SpecificHeatCapacity.of("4184", SpecificHeatCapacityUnit.JOULE_PER_KILOGRAM_KELVIN), null,
                Temperature.of("293.15", TemperatureUnit.KELVIN) // 20 °C
        );

        // Calorimeter Ccal = 1000 J/K initially at 20 °C
        Calorimeter cal = new Calorimeter(HeatCapacity.ofJoulesPerKelvin("1000"), Temperature.of("293.15", TemperatureUnit.KELVIN));

        ThermalMixingResult result = calculator.calculateFinalTemperature(
                new ThermalMixingRequest(List.of(hot, cold), cal, CalorimetryMethod.CONSTANT_SPECIFIC_HEAT_CAPACITY), null);

        // Final temperature shifted below 50 °C due to calorimeter heat absorption
        assertThat(result.finalTemperature().in(TemperatureUnit.CELSIUS)).isLessThan(new BigDecimal("50.0"));
        assertThat(result.energyBalance().isBalanced()).isTrue();
    }

    @Test
    void negativeMassOrTemperatureThrowsException() {
        assertThatThrownBy(() -> Temperature.of("-10.0", TemperatureUnit.KELVIN))
                .isInstanceOf(BelowAbsoluteZeroException.class);

        assertThatThrownBy(() -> new HeatCapacity(new BigDecimal("-5.0"), "J/K"))
                .isInstanceOf(CalorimetryException.class)
                .extracting("errorCode")
                .isEqualTo(CalorimetryErrorCode.INVALID_HEAT_CAPACITY);
    }
}
