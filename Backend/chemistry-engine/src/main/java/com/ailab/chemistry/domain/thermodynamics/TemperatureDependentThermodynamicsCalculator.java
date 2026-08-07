package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.measurement.ScientificMath;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;

import java.math.BigDecimal;
import java.util.Objects;

public final class TemperatureDependentThermodynamicsCalculator {
    private static final BigDecimal THOUSAND = new BigDecimal("1000");
    private static final Temperature REFERENCE_TEMPERATURE = Temperature.of("298.15", TemperatureUnit.KELVIN);

    public TemperatureDependentPropertyResult calculateSpecies(
            HeatCapacityCorrelation correlation,
            Temperature targetTemperature,
            BigDecimal referenceEntropyJPerMolKelvin) {
        Objects.requireNonNull(correlation, "correlation must not be null");
        Objects.requireNonNull(targetTemperature, "targetTemperature must not be null");
        Objects.requireNonNull(referenceEntropyJPerMolKelvin, "referenceEntropyJPerMolKelvin must not be null");
        if (targetTemperature.in(TemperatureUnit.KELVIN).compareTo(BigDecimal.ZERO) <= 0) {
            throw new TemperatureCorrectionException(TemperatureCorrectionErrorCode.TEMPERATURE_OUT_OF_RANGE,
                    "Target temperature must be greater than 0 K");
        }
        if (!correlation.validityRange().contains(targetTemperature)) {
            throw new TemperatureCorrectionException(TemperatureCorrectionErrorCode.TEMPERATURE_OUT_OF_RANGE,
                    "Target temperature is outside the correlation validity range");
        }
        if (correlation.type() != HeatCapacityCorrelationType.SHOMATE) {
            throw new TemperatureCorrectionException(TemperatureCorrectionErrorCode.UNSUPPORTED_CORRELATION_TYPE,
                    "Unsupported temperature correlation " + correlation.type());
        }

        PolynomialCoefficientSet k = correlation.coefficients();
        double t = targetTemperature.in(TemperatureUnit.KELVIN).divide(THOUSAND, ScientificMath.CALCULATION_CONTEXT).doubleValue();
        double a = k.a().doubleValue();
        double b = k.b().doubleValue();
        double c = k.c().doubleValue();
        double d = k.d().doubleValue();
        double e = k.e().doubleValue();
        double f = k.f().doubleValue();
        double g = k.g().doubleValue();
        double h = k.h().doubleValue();

        double cp = a + b * t + c * Math.pow(t, 2) + d * Math.pow(t, 3) + e / Math.pow(t, 2);
        double enthalpyIncrement = a * t + b * Math.pow(t, 2) / 2.0 + c * Math.pow(t, 3) / 3.0
                + d * Math.pow(t, 4) / 4.0 - e / t + f - h;
        double entropy = a * Math.log(t) + b * t + c * Math.pow(t, 2) / 2.0 + d * Math.pow(t, 3) / 3.0
                - e / (2.0 * Math.pow(t, 2)) + g;

        ensureFinite(cp);
        ensureFinite(enthalpyIncrement);
        ensureFinite(entropy);

        BigDecimal cpValue = BigDecimal.valueOf(cp).round(ScientificMath.CALCULATION_CONTEXT).stripTrailingZeros();
        BigDecimal enthalpyValue = targetTemperature.equals(REFERENCE_TEMPERATURE)
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(enthalpyIncrement).round(ScientificMath.CALCULATION_CONTEXT).stripTrailingZeros();
        BigDecimal entropyValue = targetTemperature.equals(REFERENCE_TEMPERATURE)
                ? referenceEntropyJPerMolKelvin
                : BigDecimal.valueOf(entropy).round(ScientificMath.CALCULATION_CONTEXT).stripTrailingZeros();
        BigDecimal entropyIncrement = entropyValue.subtract(referenceEntropyJPerMolKelvin, ScientificMath.CALCULATION_CONTEXT)
                .stripTrailingZeros();

        return new TemperatureDependentPropertyResult(
                correlation.compoundCode(),
                correlation.state(),
                targetTemperature,
                TemperatureCorrectionStatus.CALCULABLE,
                correlation,
                cpValue,
                enthalpyValue,
                entropyValue,
                entropyIncrement,
                correlation.provenance(),
                TemperatureCorrectionMethod.SHOMATE_TEMPERATURE_CORRECTION,
                "Shomate correlation evaluated with t=T/1000; no extrapolation or phase substitution applied.");
    }

    private static void ensureFinite(double value) {
        if (!Double.isFinite(value)) {
            throw new TemperatureCorrectionException(TemperatureCorrectionErrorCode.INVALID_CORRELATION,
                    "Temperature correlation produced a non-finite value");
        }
    }
}
