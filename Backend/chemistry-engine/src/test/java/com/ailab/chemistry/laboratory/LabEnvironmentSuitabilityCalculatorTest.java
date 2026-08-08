package com.ailab.chemistry.laboratory;

import com.ailab.chemistry.domain.labenvironment.*;
import com.ailab.chemistry.domain.measurement.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LabEnvironmentSuitabilityCalculatorTest {
    private final EnvironmentSuitabilityCalculator calculator = new EnvironmentSuitabilityCalculator();

    @Test
    void humidityBoundariesAndInvalidValuesAreExplicit() {
        assertThat(RelativeHumidity.percent("0").valuePercent()).isEqualByComparingTo("0");
        assertThat(RelativeHumidity.percent("100").valuePercent()).isEqualByComparingTo("100");
        assertThatThrownBy(() -> RelativeHumidity.percent("100.1")).isInstanceOf(EnvironmentException.class);
        assertThatThrownBy(() -> RelativeHumidity.percent("-0.1")).isInstanceOf(EnvironmentException.class);
    }

    @Test
    void ambientConditionsVentilationAndFumeHoodStateAreValidated() {
        EnvironmentalRequirement requirement = new EnvironmentalRequirement(
                new TemperatureRange(Temperature.of("18", TemperatureUnit.CELSIUS), Temperature.of("25", TemperatureUnit.CELSIUS)),
                new PressureRange(Pressure.of("0.95", PressureUnit.BAR), Pressure.of("1.05", PressureUnit.BAR)),
                new HumidityRange(RelativeHumidity.percent("20"), RelativeHumidity.percent("60")),
                VentilationMode.FUME_HOOD, true, false, List.of(VentilationMode.NONE));

        EnvironmentSuitabilityResult accepted = calculator.evaluate(new EnvironmentSuitabilityRequest(
                snapshot(VentilationMode.FUME_HOOD, FumeHoodState.OPERATING), requirement));
        assertThat(accepted.status()).isEqualTo(EnvironmentSuitabilityStatus.SUITABLE);

        EnvironmentSuitabilityResult unavailable = calculator.evaluate(new EnvironmentSuitabilityRequest(
                snapshot(VentilationMode.GENERAL_VENTILATION, FumeHoodState.UNAVAILABLE), requirement));
        assertThat(unavailable.errorCodes()).contains(EnvironmentErrorCode.FUME_HOOD_NOT_OPERATING, EnvironmentErrorCode.VENTILATION_MODE_MISMATCH);

        EnvironmentSuitabilityResult availableButNotOperating = calculator.evaluate(new EnvironmentSuitabilityRequest(
                snapshot(VentilationMode.FUME_HOOD, FumeHoodState.AVAILABLE), requirement));
        assertThat(availableButNotOperating.errorCodes()).contains(EnvironmentErrorCode.FUME_HOOD_NOT_OPERATING);

        LaboratoryEnvironmentSnapshot missingHumidity = new LaboratoryEnvironmentSnapshot(
                Temperature.of("20", TemperatureUnit.CELSIUS), Pressure.of("1", PressureUnit.BAR), null,
                VentilationMode.FUME_HOOD, FumeHoodState.OPERATING, null, Instant.parse("2026-08-06T10:00:00Z"));
        EnvironmentSuitabilityResult missing = calculator.evaluate(new EnvironmentSuitabilityRequest(missingHumidity, requirement));
        assertThat(missing.errorCodes()).contains(EnvironmentErrorCode.MISSING_REQUIRED_ENVIRONMENT_VALUE);
    }

    private static LaboratoryEnvironmentSnapshot snapshot(VentilationMode mode, FumeHoodState hood) {
        return new LaboratoryEnvironmentSnapshot(
                Temperature.of("20", TemperatureUnit.CELSIUS),
                Pressure.of("1", PressureUnit.BAR),
                RelativeHumidity.percent("45"),
                mode,
                hood,
                null,
                Instant.parse("2026-08-06T10:00:00Z"));
    }
}
