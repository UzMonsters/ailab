package com.ailab.chemistry.domain.simulationengine;

public record SimulationExecutionContext(SimulationExecutionPlan plan, SimulationCommand command) {
    public SimulationExecutionContext {
        if (plan == null || command == null) {
            throw new SimulationExecutionException(SimulationExecutionErrorCode.INVALID_COMMAND,
                    "Execution plan and command are required");
        }
    }
}
