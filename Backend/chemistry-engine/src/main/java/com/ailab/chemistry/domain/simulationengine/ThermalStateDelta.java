package com.ailab.chemistry.domain.simulationengine;

import java.math.BigDecimal;

public record ThermalStateDelta(String vesselId, BigDecimal finalTemperatureKelvin, BigDecimal energyResidualJ) {
}
