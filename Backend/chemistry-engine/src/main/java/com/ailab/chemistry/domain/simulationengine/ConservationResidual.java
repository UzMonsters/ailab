package com.ailab.chemistry.domain.simulationengine;

import java.math.BigDecimal;

public record ConservationResidual(
        ConservationStatus status,
        BigDecimal residual,
        BigDecimal tolerance,
        String unit
) {
    public ConservationResidual {
        if (status == null) {
            throw new SimulationExecutionException(SimulationExecutionErrorCode.STATE_DELTA_INVARIANT_FAILED,
                    "Conservation status is required");
        }
        residual = residual == null ? BigDecimal.ZERO : residual;
        tolerance = tolerance == null ? BigDecimal.ZERO : tolerance;
        unit = unit == null ? "" : unit;
    }
}
