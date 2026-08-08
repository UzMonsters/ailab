package com.ailab.chemistry.domain.simulationengine;

import java.util.Map;

public record SimulationCalculationInput(Map<String, String> values) {
    public SimulationCalculationInput {
        values = Map.copyOf(values == null ? Map.of() : values);
    }
}
