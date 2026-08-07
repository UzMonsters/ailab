package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TemperatureDependentThermodynamicsCalculatorTest {

    private final TemperatureDependentThermodynamicsCalculator calculator = new TemperatureDependentThermodynamicsCalculator();

    @Test
    void shomateLiquidWaterAt400KMatchesPublishedTableValues() {
        var correlation = liquidWater();

        var result = calculator.calculateSpecies(correlation, Temperature.of("400.0", TemperatureUnit.KELVIN),
                new BigDecimal("69.91"));

        assertThat(result.status()).isEqualTo(TemperatureCorrectionStatus.CALCULABLE);
        assertThat(result.heatCapacityJPerMolKelvin()).isCloseTo(new BigDecimal("76.74"), offset("0.02"));
        assertThat(result.enthalpyIncrementKjPerMol()).isCloseTo(new BigDecimal("7.71"), offset("0.02"));
        assertThat(result.entropyAtTemperatureJPerMolKelvin()).isCloseTo(new BigDecimal("92.19"), offset("0.03"));
        assertThat(result.entropyIncrementJPerMolKelvin()).isCloseTo(new BigDecimal("22.28"), offset("0.04"));
    }

    @Test
    void shomateIncrementIsZeroAtReferenceTemperatureForSupportedReferenceRange() {
        var result = calculator.calculateSpecies(liquidWater(), Temperature.of("298.15", TemperatureUnit.KELVIN),
                new BigDecimal("69.91"));

        assertThat(result.enthalpyIncrementKjPerMol()).isCloseTo(BigDecimal.ZERO, offset("0.001"));
        assertThat(result.entropyIncrementJPerMolKelvin()).isCloseTo(BigDecimal.ZERO, offset("0.05"));
    }

    @Test
    void targetTemperatureMustStayInsideValidityRange() {
        assertThatThrownBy(() -> calculator.calculateSpecies(liquidWater(), Temperature.of("501.0", TemperatureUnit.KELVIN),
                new BigDecimal("69.91")))
                .isInstanceOf(TemperatureCorrectionException.class)
                .extracting("errorCode")
                .isEqualTo(TemperatureCorrectionErrorCode.TEMPERATURE_OUT_OF_RANGE);
    }

    @Test
    void validityBoundariesAreInclusive() {
        var lower = calculator.calculateSpecies(liquidWater(), Temperature.of("298.0", TemperatureUnit.KELVIN),
                new BigDecimal("69.91"));
        var upper = calculator.calculateSpecies(liquidWater(), Temperature.of("500.0", TemperatureUnit.KELVIN),
                new BigDecimal("69.91"));

        assertThat(lower.status()).isEqualTo(TemperatureCorrectionStatus.CALCULABLE);
        assertThat(upper.status()).isEqualTo(TemperatureCorrectionStatus.CALCULABLE);
        assertThat(upper.heatCapacityJPerMolKelvin()).isCloseTo(new BigDecimal("83.66"), offset("0.02"));
    }

    @Test
    void unknownOrMixedPhaseCorrelationIsRejected() {
        assertThatThrownBy(() -> new HeatCapacityCorrelation(
                "COMP-H2O",
                MatterState.UNKNOWN,
                HeatCapacityCorrelationType.SHOMATE,
                new PolynomialCoefficientSet("1", "1", "1", "1", "1", "1", "1", "1"),
                new TemperatureValidityRange(Temperature.of("298", TemperatureUnit.KELVIN), Temperature.of("500", TemperatureUnit.KELVIN)),
                "J/(mol*K)",
                "t=T/1000; Cp J/(mol*K); H kJ/mol; S J/(mol*K)",
                new ThermodynamicProvenance("TEST", "Test", "Test"))).isInstanceOf(TemperatureCorrectionException.class)
                .extracting("errorCode")
                .isEqualTo(TemperatureCorrectionErrorCode.INVALID_CORRELATION);
    }

    @Test
    void correlationRejectsMissingProvenanceAndInvalidRange() {
        assertThatThrownBy(() -> new HeatCapacityCorrelation(
                "COMP-H2O",
                MatterState.LIQUID,
                HeatCapacityCorrelationType.SHOMATE,
                new PolynomialCoefficientSet("1", "1", "1", "1", "1", "1", "1", "1"),
                new TemperatureValidityRange(Temperature.of("500", TemperatureUnit.KELVIN), Temperature.of("298", TemperatureUnit.KELVIN)),
                "J/(mol*K)",
                "t=T/1000; H in kJ/mol; S in J/(mol*K)",
                null))
                .isInstanceOf(TemperatureCorrectionException.class)
                .extracting("errorCode")
                .isEqualTo(TemperatureCorrectionErrorCode.INVALID_VALIDITY_RANGE);
    }

    private static HeatCapacityCorrelation liquidWater() {
        return new HeatCapacityCorrelation(
                "COMP-H2O",
                MatterState.LIQUID,
                HeatCapacityCorrelationType.SHOMATE,
                new PolynomialCoefficientSet("-203.6060", "1523.290", "-3196.413", "2474.455",
                        "3.855326", "-256.5478", "-488.7163", "-285.8304"),
                new TemperatureValidityRange(Temperature.of("298.0", TemperatureUnit.KELVIN),
                        Temperature.of("500.0", TemperatureUnit.KELVIN)),
                "J/(mol*K)",
                "t=T/1000; Cp J/(mol*K); H kJ/mol; S J/(mol*K)",
                new ThermodynamicProvenance("NIST-WEBBOOK",
                        "NIST Chemistry WebBook liquid water Shomate table; Chase 1998",
                        "NIST SRD 69 copyright applies."));
    }

    private static org.assertj.core.data.Offset<BigDecimal> offset(String value) {
        return org.assertj.core.data.Offset.offset(new BigDecimal(value));
    }
}
