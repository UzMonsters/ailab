package com.ailab.chemistry.domain.phasebehavior;

import com.ailab.chemistry.domain.measurement.Temperature;

import java.math.BigDecimal;

public record BoilingPointResult(
        PhaseBehaviorStatus status,
        AntoineCoefficientSet correlation,
        Temperature temperature,
        BigDecimal residualPascal
) {
}
