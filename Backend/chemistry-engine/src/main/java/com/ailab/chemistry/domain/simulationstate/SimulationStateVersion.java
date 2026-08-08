package com.ailab.chemistry.domain.simulationstate;

public record SimulationStateVersion(long value) {
    public SimulationStateVersion {
        if (value < 0) {
            throw new IllegalArgumentException("State version must be non-negative");
        }
    }
}
