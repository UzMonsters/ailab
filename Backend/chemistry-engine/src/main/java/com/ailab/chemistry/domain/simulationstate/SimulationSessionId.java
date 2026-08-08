package com.ailab.chemistry.domain.simulationstate;

public record SimulationSessionId(String value) {
    public SimulationSessionId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Simulation session id is required");
        }
    }
}
