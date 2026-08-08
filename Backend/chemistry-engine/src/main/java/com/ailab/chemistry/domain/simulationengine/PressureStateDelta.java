package com.ailab.chemistry.domain.simulationengine;

import java.math.BigDecimal;

public record PressureStateDelta(String vesselId, BigDecimal finalPressureKpa, BigDecimal finalVolumeMl) {
}
