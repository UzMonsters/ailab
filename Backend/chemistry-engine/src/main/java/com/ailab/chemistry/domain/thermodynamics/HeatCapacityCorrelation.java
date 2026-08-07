package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.element.MatterState;

import java.util.Objects;

public record HeatCapacityCorrelation(
        String compoundCode,
        MatterState state,
        HeatCapacityCorrelationType type,
        PolynomialCoefficientSet coefficients,
        TemperatureValidityRange validityRange,
        String heatCapacityUnit,
        String scalingConvention,
        ThermodynamicProvenance provenance) {

    public HeatCapacityCorrelation {
        if (compoundCode == null || compoundCode.isBlank()) {
            throw new TemperatureCorrectionException(TemperatureCorrectionErrorCode.INVALID_CORRELATION,
                    "Compound code must be present");
        }
        Objects.requireNonNull(state, "state must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(coefficients, "coefficients must not be null");
        Objects.requireNonNull(validityRange, "validityRange must not be null");
        if (state == MatterState.UNKNOWN || state == MatterState.MIXED || state == MatterState.SUPERCRITICAL) {
            throw new TemperatureCorrectionException(TemperatureCorrectionErrorCode.INVALID_CORRELATION,
                    "Temperature correlations must be phase-specific gas, liquid, or solid records");
        }
        if (type != HeatCapacityCorrelationType.SHOMATE) {
            throw new TemperatureCorrectionException(TemperatureCorrectionErrorCode.UNSUPPORTED_CORRELATION_TYPE,
                    "Only explicit Shomate correlations are enabled for Phase 8C");
        }
        if (!"J/(mol*K)".equals(heatCapacityUnit)) {
            throw new TemperatureCorrectionException(TemperatureCorrectionErrorCode.INVALID_CORRELATION,
                    "Heat capacity unit must be J/(mol*K)");
        }
        if (scalingConvention == null || scalingConvention.isBlank()) {
            throw new TemperatureCorrectionException(TemperatureCorrectionErrorCode.INVALID_CORRELATION,
                    "Scaling convention must be present");
        }
        if (provenance == null) {
            throw new TemperatureCorrectionException(TemperatureCorrectionErrorCode.INVALID_CORRELATION,
                    "Correlation provenance must be present");
        }
    }
}
