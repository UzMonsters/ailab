package com.ailab.chemistry.domain.simulationengine;

public record SimulationCommandId(String value) {
    public SimulationCommandId {
        if (value == null || value.isBlank()) {
            throw new SimulationExecutionException(SimulationExecutionErrorCode.INVALID_COMMAND,
                    "Simulation command id is required");
        }
    }
}
