package com.ailab.chemistry.domain.measurement;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashSet;
import java.util.Set;

import com.ailab.chemistry.domain.measurement.exception.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MeasurementTests {

    // --- 1. Mass Tests ---
    @Test
    void testMassConversionsAndArithmetic() {
        Mass m1 = Mass.of("1000", MassUnit.MILLIGRAM);
        Mass m2 = Mass.of("1", MassUnit.GRAM);
        Mass m3 = Mass.of("0.001", MassUnit.KILOGRAM);

        // Conversion equality check
        assertThat(m1).isEqualTo(m2);
        assertThat(m2).isEqualTo(m3);
        assertThat(m1.in(MassUnit.GRAM)).isEqualByComparingTo("1");
        assertThat(m3.in(MassUnit.MILLIGRAM)).isEqualByComparingTo("1000");

        // Arithmetic
        Mass added = m1.add(m2);
        assertThat(added.in(MassUnit.GRAM)).isEqualByComparingTo("2");

        Mass subtracted = added.subtract(m2);
        assertThat(subtracted).isEqualTo(m1);

        // Multiplying and dividing
        Mass multiplied = m2.multiply(new BigDecimal("3"));
        assertThat(multiplied.in(MassUnit.GRAM)).isEqualByComparingTo("3");

        Mass divided = multiplied.divide(new BigDecimal("2"));
        assertThat(divided.in(MassUnit.GRAM)).isEqualByComparingTo("1.5");

        // Rejections
        assertThatThrownBy(() -> Mass.of("-1", MassUnit.GRAM))
                .isInstanceOf(NegativeQuantityException.class);
        assertThatThrownBy(() -> m1.subtract(added))
                .isInstanceOf(NegativeQuantityException.class);
        assertThatThrownBy(() -> m1.multiply(new BigDecimal("-2")))
                .isInstanceOf(NegativeQuantityException.class);
        assertThatThrownBy(() -> m1.divide(new BigDecimal("-2")))
                .isInstanceOf(NegativeQuantityException.class);
        assertThatThrownBy(() -> m1.divide(BigDecimal.ZERO))
                .isInstanceOf(ScientificArithmeticException.class);
    }

    // --- 2. Volume Tests ---
    @Test
    void testVolumeConversionsAndArithmetic() {
        Volume v1 = Volume.of("1000", VolumeUnit.MICROLITER);
        Volume v2 = Volume.of("1", VolumeUnit.MILLILITER);
        Volume v3 = Volume.of("0.001", VolumeUnit.LITER);

        assertThat(v1).isEqualTo(v2);
        assertThat(v2).isEqualTo(v3);

        // Arithmetic
        Volume added = v1.add(v3);
        assertThat(added.in(VolumeUnit.MILLILITER)).isEqualByComparingTo("2");

        Volume subtracted = added.subtract(v2);
        assertThat(subtracted).isEqualTo(v3);

        // Rejections
        assertThatThrownBy(() -> Volume.of("-5", VolumeUnit.LITER))
                .isInstanceOf(NegativeQuantityException.class);
        assertThatThrownBy(() -> v1.subtract(added))
                .isInstanceOf(NegativeQuantityException.class);
    }

    // --- 3. Amount of Substance Tests ---
    @Test
    void testAmountConversionsAndArithmetic() {
        AmountOfSubstance a1 = AmountOfSubstance.of("1000", AmountOfSubstanceUnit.MILLIMOLE);
        AmountOfSubstance a2 = AmountOfSubstance.of("1", AmountOfSubstanceUnit.MOLE);

        assertThat(a1).isEqualTo(a2);

        AmountOfSubstance added = a1.add(a2);
        assertThat(added.in(AmountOfSubstanceUnit.MOLE)).isEqualByComparingTo("2");

        AmountOfSubstance subtracted = added.subtract(a2);
        assertThat(subtracted).isEqualTo(a1);

        assertThatThrownBy(() -> AmountOfSubstance.of("-1", AmountOfSubstanceUnit.MOLE))
                .isInstanceOf(NegativeQuantityException.class);
        assertThatThrownBy(() -> a1.subtract(added))
                .isInstanceOf(NegativeQuantityException.class);
    }

    // --- 4. Temperature Tests ---
    @Test
    void testTemperatureCelsiusKelvinConversionsAndOffsets() {
        // 0 °C = 273.15 K
        Temperature t0C = Temperature.of("0", TemperatureUnit.CELSIUS);
        Temperature t273K = Temperature.of("273.15", TemperatureUnit.KELVIN);
        assertThat(t0C).isEqualTo(t273K);
        assertThat(t0C.in(TemperatureUnit.KELVIN)).isEqualByComparingTo("273.15");

        // 100 °C = 373.15 K
        Temperature t100C = Temperature.of("100", TemperatureUnit.CELSIUS);
        Temperature t373K = Temperature.of("373.15", TemperatureUnit.KELVIN);
        assertThat(t100C).isEqualTo(t373K);

        // -273.15 °C = 0 K
        Temperature tAbsZero = Temperature.of("-273.15", TemperatureUnit.CELSIUS);
        assertThat(tAbsZero.in(TemperatureUnit.KELVIN)).isEqualByComparingTo("0");

        // Rejection below absolute zero
        assertThatThrownBy(() -> Temperature.of("-273.16", TemperatureUnit.CELSIUS))
                .isInstanceOf(BelowAbsoluteZeroException.class);
        assertThatThrownBy(() -> Temperature.of("-0.01", TemperatureUnit.KELVIN))
                .isInstanceOf(BelowAbsoluteZeroException.class);
    }

    @Test
    void testTemperatureDeltaArithmetic() {
        Temperature t1 = Temperature.of("20", TemperatureUnit.CELSIUS); // 293.15 K
        TemperatureDelta delta = TemperatureDelta.celsius(new BigDecimal("10")); // magnitude of 10 K delta

        // Add delta: 20 °C + 10 °C delta = 30 °C
        Temperature tAdded = t1.add(delta);
        assertThat(tAdded.in(TemperatureUnit.CELSIUS)).isEqualByComparingTo("30");
        assertThat(tAdded.in(TemperatureUnit.KELVIN)).isEqualByComparingTo("303.15");

        // Subtract delta
        Temperature tSubtracted = tAdded.subtract(delta);
        assertThat(tSubtracted).isEqualTo(t1);

        // Subtract two absolute temperatures to produce a TemperatureDelta
        Temperature t2 = Temperature.of("30", TemperatureUnit.CELSIUS);
        TemperatureDelta tDiff = t2.subtract(t1);
        assertThat(tDiff.in(TemperatureUnit.KELVIN)).isEqualByComparingTo("10");

        // Kelvins magnitude delta and Celsius magnitude delta are equal
        TemperatureDelta kDelta = TemperatureDelta.kelvin(new BigDecimal("10"));
        assertThat(tDiff).isEqualTo(kDelta);

        // Celsius delta magnitude doesn't apply the absolute temperature offset 273.15
        assertThat(delta.in(TemperatureUnit.CELSIUS)).isEqualByComparingTo("10");

        // Delta supports negative value (since delta can be negative)
        TemperatureDelta negDelta = TemperatureDelta.kelvin(new BigDecimal("-10"));
        assertThat(negDelta.in(TemperatureUnit.KELVIN)).isEqualByComparingTo("-10");
        Temperature tNegAdded = t2.add(negDelta);
        assertThat(tNegAdded).isEqualTo(t1);
    }

    // --- 5. Absolute Pressure Tests ---
    @Test
    void testPressureConversions() {
        Pressure p1 = Pressure.of("1", PressureUnit.ATMOSPHERE);
        Pressure p2 = Pressure.of("101325", PressureUnit.PASCAL);
        Pressure p3 = Pressure.of("101.325", PressureUnit.KILOPASCAL);

        assertThat(p1).isEqualTo(p2);
        assertThat(p2).isEqualTo(p3);

        Pressure pBar = Pressure.of("1", PressureUnit.BAR);
        Pressure pPa = Pressure.of("100000", PressureUnit.PASCAL);
        assertThat(pBar).isEqualTo(pPa);

        assertThatThrownBy(() -> Pressure.of("-0.001", PressureUnit.PASCAL))
                .isInstanceOf(NegativeQuantityException.class);
    }

    // --- 6. Concentration Tests ---
    @Test
    void testConcentrationConversionsAndValidation() {
        MolarConcentration mc1 = MolarConcentration.of("1", MolarConcentrationUnit.MOL_PER_LITER);
        MolarConcentration mc2 = MolarConcentration.of("1000", MolarConcentrationUnit.MILLIMOL_PER_LITER);
        assertThat(mc1).isEqualTo(mc2);

        MassConcentration mac1 = MassConcentration.of("1", MassConcentrationUnit.GRAM_PER_LITER);
        MassConcentration mac2 = MassConcentration.of("1000", MassConcentrationUnit.MILLIGRAM_PER_LITER);
        assertThat(mac1).isEqualTo(mac2);

        assertThatThrownBy(() -> MolarConcentration.of("-0.1", MolarConcentrationUnit.MOL_PER_LITER))
                .isInstanceOf(NegativeQuantityException.class);
        assertThatThrownBy(() -> MassConcentration.of("-5", MassConcentrationUnit.GRAM_PER_LITER))
                .isInstanceOf(NegativeQuantityException.class);
    }

    @Test
    void testPercentageConcentrationBasesAndRanges() {
        PercentageConcentration pc1 = PercentageConcentration.of("5", ConcentrationBasis.MASS_PER_VOLUME);
        PercentageConcentration pc2 = PercentageConcentration.of("5", ConcentrationBasis.MASS_PER_MASS);

        // Different bases must not be equal
        assertThat(pc1).isNotEqualTo(pc2);

        // Ordering comparison across bases must throw IncompatibleUnitException
        assertThatThrownBy(() -> pc1.compareTo(pc2))
                .isInstanceOf(IncompatibleUnitException.class);

        // Adding or subtracting across bases must throw IncompatibleUnitException
        assertThatThrownBy(() -> pc1.add(pc2))
                .isInstanceOf(IncompatibleUnitException.class);

        // Percentage range [0, 100] validation
        assertThatThrownBy(() -> PercentageConcentration.of("-0.01", ConcentrationBasis.VOLUME_PER_VOLUME))
                .isInstanceOf(InvalidPercentageConcentrationException.class);
        assertThatThrownBy(() -> PercentageConcentration.of("100.01", ConcentrationBasis.VOLUME_PER_VOLUME))
                .isInstanceOf(InvalidPercentageConcentrationException.class);
    }

    // --- 7. Energy Tests ---
    @Test
    void testEnergySignedPreservation() {
        Energy e1 = Energy.of("1", EnergyUnit.KILOJOULE);
        Energy e2 = Energy.of("1000", EnergyUnit.JOULE);
        assertThat(e1).isEqualTo(e2);

        // Energy supports negative / signed values
        Energy negEnergy = Energy.of("-500", EnergyUnit.JOULE);
        assertThat(negEnergy.in(EnergyUnit.JOULE)).isEqualByComparingTo("-500");

        Energy added = e2.add(negEnergy);
        assertThat(added.in(EnergyUnit.JOULE)).isEqualByComparingTo("500");

        // Signed energy allows negative scalar
        Energy negMultiplied = added.multiply(new BigDecimal("-2"));
        assertThat(negMultiplied.in(EnergyUnit.JOULE)).isEqualByComparingTo("-1000");
    }

    // --- 8. Duration Tests ---
    @Test
    void testDurationConversions() {
        Duration d1 = Duration.of("3600", DurationUnit.SECOND);
        Duration d2 = Duration.of("1", DurationUnit.HOUR);
        Duration d3 = Duration.of("60", DurationUnit.MINUTE);

        assertThat(d1).isEqualTo(d2);
        assertThat(d2).isEqualTo(d3);

        Duration ms = Duration.of("1000", DurationUnit.MILLISECOND);
        assertThat(ms.in(DurationUnit.SECOND)).isEqualByComparingTo("1");

        assertThatThrownBy(() -> Duration.of("-1", DurationUnit.SECOND))
                .isInstanceOf(NegativeQuantityException.class);
    }

    // --- 9. Precision & Approximations ---
    @Test
    void testScaleIndependentEqualityAndHashing() {
        BigDecimal val1 = new BigDecimal("1.0");
        BigDecimal val2 = new BigDecimal("1.00");
        assertThat(val1).isNotEqualTo(val2); // Standard BigDecimal equals is scale dependent

        Mass m1 = Mass.of(val1, MassUnit.GRAM);
        Mass m2 = Mass.of(val2, MassUnit.GRAM);

        // But Mass exact equality must be scale-independent!
        assertThat(m1).isEqualTo(m2);
        assertThat(m1.hashCode()).isEqualTo(m2.hashCode());
    }

    @Test
    void testApproximateComparisonTolerances() {
        BigDecimal first = new BigDecimal("100.005");
        BigDecimal second = new BigDecimal("100.000");

        // Absolute diff: 0.005. Should match absolute tolerance 0.01
        boolean absoluteMatch = ScientificMath.isApproximatelyEqual(
                first, second, new BigDecimal("0.01"), new BigDecimal("0.000001"));
        assertThat(absoluteMatch).isTrue();

        // Should not match absolute tolerance 0.001 but match relative tolerance 0.0001 (diff / max = 0.005 / 100.005 = ~0.00005)
        boolean relativeMatch = ScientificMath.isApproximatelyEqual(
                first, second, new BigDecimal("0.001"), new BigDecimal("0.0001"));
        assertThat(relativeMatch).isTrue();

        // Reject negative tolerances
        assertThatThrownBy(() -> ScientificMath.isApproximatelyEqual(
                first, second, new BigDecimal("-0.01"), new BigDecimal("0.01")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testNoPrecisionDriftRoundTrip() {
        // 1/3 liters in microliter: (1/3) * 1000000
        BigDecimal oneThird = BigDecimal.ONE.divide(new BigDecimal("3"), ScientificMath.CALCULATION_CONTEXT);
        Volume vStart = Volume.of(oneThird, VolumeUnit.LITER);

        BigDecimal microliters = vStart.in(VolumeUnit.MICROLITER);
        Volume vRoundTrip = Volume.of(microliters, VolumeUnit.MICROLITER);

        // Verify no drift (exact equality is maintained)
        assertThat(vStart).isEqualTo(vRoundTrip);
    }

    // --- 10. Hash Collection Consistency Tests ---
    @Test
    void testHashSetBucketConsistency() {
        Set<Mass> masses = new HashSet<>();
        masses.add(Mass.of("1.0", MassUnit.GRAM));
        masses.add(Mass.of("1000", MassUnit.MILLIGRAM));
        masses.add(Mass.of("0.001", MassUnit.KILOGRAM));

        // Since they represent the same canonical quantity, they are equal and must hash to the same bucket.
        // Therefore, the set size must be exactly 1!
        assertThat(masses).hasSize(1);
    }

    // --- 11. Unit Symbol Parsing Tests ---
    @Test
    void testUnitSymbolParsing() {
        assertThat(MassUnit.fromSymbol("mg")).isEqualTo(MassUnit.MILLIGRAM);
        assertThat(MassUnit.fromSymbol("G")).isEqualTo(MassUnit.GRAM);
        assertThat(MassUnit.fromSymbol("kg")).isEqualTo(MassUnit.KILOGRAM);
        assertThatThrownBy(() -> MassUnit.fromSymbol("unknown"))
                .isInstanceOf(IncompatibleUnitException.class);

        assertThat(VolumeUnit.fromSymbol("µL")).isEqualTo(VolumeUnit.MICROLITER);
        assertThat(VolumeUnit.fromSymbol("μL")).isEqualTo(VolumeUnit.MICROLITER);
        assertThat(VolumeUnit.fromSymbol("uL")).isEqualTo(VolumeUnit.MICROLITER);
        assertThat(VolumeUnit.fromSymbol("mL")).isEqualTo(VolumeUnit.MILLILITER);
        assertThat(VolumeUnit.fromSymbol("L")).isEqualTo(VolumeUnit.LITER);

        assertThat(AmountOfSubstanceUnit.fromSymbol("mmol")).isEqualTo(AmountOfSubstanceUnit.MILLIMOLE);
        assertThat(AmountOfSubstanceUnit.fromSymbol("mol")).isEqualTo(AmountOfSubstanceUnit.MOLE);

        assertThat(TemperatureUnit.fromSymbol("K")).isEqualTo(TemperatureUnit.KELVIN);
        assertThat(TemperatureUnit.fromSymbol("°C")).isEqualTo(TemperatureUnit.CELSIUS);
        assertThat(TemperatureUnit.fromSymbol("c")).isEqualTo(TemperatureUnit.CELSIUS);

        assertThat(PressureUnit.fromSymbol("Pa")).isEqualTo(PressureUnit.PASCAL);
        assertThat(PressureUnit.fromSymbol("kPa")).isEqualTo(PressureUnit.KILOPASCAL);
        assertThat(PressureUnit.fromSymbol("atm")).isEqualTo(PressureUnit.ATMOSPHERE);
        assertThat(PressureUnit.fromSymbol("bar")).isEqualTo(PressureUnit.BAR);

        assertThat(MolarConcentrationUnit.fromSymbol("mol/L")).isEqualTo(MolarConcentrationUnit.MOL_PER_LITER);
        assertThat(MolarConcentrationUnit.fromSymbol("mmol/L")).isEqualTo(MolarConcentrationUnit.MILLIMOL_PER_LITER);

        assertThat(MassConcentrationUnit.fromSymbol("g/L")).isEqualTo(MassConcentrationUnit.GRAM_PER_LITER);
        assertThat(MassConcentrationUnit.fromSymbol("mg/L")).isEqualTo(MassConcentrationUnit.MILLIGRAM_PER_LITER);

        assertThat(ConcentrationBasis.fromSymbol("w/w")).isEqualTo(ConcentrationBasis.MASS_PER_MASS);
        assertThat(ConcentrationBasis.fromSymbol("w/v")).isEqualTo(ConcentrationBasis.MASS_PER_VOLUME);
        assertThat(ConcentrationBasis.fromSymbol("v/v")).isEqualTo(ConcentrationBasis.VOLUME_PER_VOLUME);

        assertThat(EnergyUnit.fromSymbol("J")).isEqualTo(EnergyUnit.JOULE);
        assertThat(EnergyUnit.fromSymbol("kJ")).isEqualTo(EnergyUnit.KILOJOULE);

        assertThat(DurationUnit.fromSymbol("ms")).isEqualTo(DurationUnit.MILLISECOND);
        assertThat(DurationUnit.fromSymbol("s")).isEqualTo(DurationUnit.SECOND);
        assertThat(DurationUnit.fromSymbol("min")).isEqualTo(DurationUnit.MINUTE);
        assertThat(DurationUnit.fromSymbol("h")).isEqualTo(DurationUnit.HOUR);
    }
}
