package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.Temperature;

import java.math.BigDecimal;

public record EquilibriumConstantResult(
        String reactionCode,
        Temperature temperature,
        Pressure standardPressure,
        EquilibriumCalculationStatus status,
        StandardEquilibriumConstant standardConstant,
        BigDecimal deltaGibbsStandardKjPerMol,
        ReactionThermodynamicVector reactionVector,
        TemperatureCorrectionCoverage coverage,
        PhaseStabilityStatus phaseStabilityStatus,
        EquilibriumCalculationMethod method,
        String explanation) {
}
