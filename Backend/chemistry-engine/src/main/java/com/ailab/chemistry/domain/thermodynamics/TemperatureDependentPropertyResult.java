package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.Temperature;

import java.math.BigDecimal;

public record TemperatureDependentPropertyResult(
        String compoundCode,
        MatterState state,
        Temperature temperature,
        TemperatureCorrectionStatus status,
        HeatCapacityCorrelation correlation,
        BigDecimal heatCapacityJPerMolKelvin,
        BigDecimal enthalpyIncrementKjPerMol,
        BigDecimal entropyAtTemperatureJPerMolKelvin,
        BigDecimal entropyIncrementJPerMolKelvin,
        ThermodynamicProvenance provenance,
        TemperatureCorrectionMethod method,
        String explanation) {
}
