package com.ailab.chemistry.domain.simulationengine;

public record ScientificOperationSpecification(
        SimulationOperationType operationType,
        ScientificModelSelection modelSelection
) {
    public ScientificOperationSpecification {
        if (operationType == null) {
            throw new SimulationExecutionException(SimulationExecutionErrorCode.EXPLICIT_OPERATION_REQUIRED,
                    "Each command must specify exactly one explicit operation type");
        }
        if (modelSelection == null) {
            throw new SimulationExecutionException(SimulationExecutionErrorCode.UNSUPPORTED_MODEL_SELECTION,
                    "Scientific model selection is required");
        }
    }
}
