package com.ailab.chemistry.domain.simulationengine;

import java.util.Map;

public record SimulationCalculationResult(Map<String, String> values) {
    public SimulationCalculationResult {
        values = Map.copyOf(values == null ? Map.of() : values);
    }
}
