package com.ailab.chemistry.domain.simulationengine;

import java.util.List;
import java.util.Map;

public record SimulationCommand(
        SimulationCommandId commandId,
        String stepId,
        String targetVesselId,
        ScientificOperationSpecification operation,
        Map<String, String> inputs,
        List<MaterialStateDelta> materialDeltas
) {
    public SimulationCommand {
        if (commandId == null || stepId == null || stepId.isBlank()
                || targetVesselId == null || targetVesselId.isBlank() || operation == null) {
            throw new SimulationExecutionException(SimulationExecutionErrorCode.INVALID_COMMAND,
                    "Command id, step id, target vessel id, and operation are required");
        }
        inputs = Map.copyOf(inputs == null ? Map.of() : inputs);
        materialDeltas = List.copyOf(materialDeltas == null ? List.of() : materialDeltas);
    }
}
