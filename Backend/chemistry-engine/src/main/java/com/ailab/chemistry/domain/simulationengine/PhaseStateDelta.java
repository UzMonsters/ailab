package com.ailab.chemistry.domain.simulationengine;

import java.math.BigDecimal;

public record PhaseStateDelta(String vesselId, String compoundCode, String initialPhase, String finalPhase, BigDecimal amount) {
}
