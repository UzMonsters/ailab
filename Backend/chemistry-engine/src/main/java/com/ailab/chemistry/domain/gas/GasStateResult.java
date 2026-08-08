package com.ailab.chemistry.domain.gas;

import java.math.BigDecimal;

public record GasStateResult(
        GasState state,
        GasEquationModel model,
        GasCalculationMethod method,
        GasCalculationStatus status,
        BigDecimal residual
) {
}
