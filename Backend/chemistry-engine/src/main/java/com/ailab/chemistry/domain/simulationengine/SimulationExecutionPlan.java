package com.ailab.chemistry.domain.simulationengine;

public record SimulationExecutionPlan(String processCode, int processVersion, String stepId) {
    public SimulationExecutionPlan {
        if (processCode == null || processCode.isBlank() || processVersion <= 0 || stepId == null || stepId.isBlank()) {
            throw new SimulationExecutionException(SimulationExecutionErrorCode.INVALID_COMMAND,
                    "Process code, version, and step id are required");
        }
    }
}
