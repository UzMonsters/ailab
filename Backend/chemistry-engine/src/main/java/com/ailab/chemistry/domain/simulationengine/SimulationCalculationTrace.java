package com.ailab.chemistry.domain.simulationengine;

import java.util.List;
import java.util.Map;

public record SimulationCalculationTrace(
        String selectedHandler,
        SimulationCalculationInput input,
        SimulationCalculationResult result,
        String solverStatus,
        int iterationCount,
        List<String> intermediatePoints,
        Map<String, String> tolerances
) {
    public SimulationCalculationTrace {
        if (selectedHandler == null || selectedHandler.isBlank()) {
            throw new SimulationExecutionException(SimulationExecutionErrorCode.UNSUPPORTED_MODEL_SELECTION,
                    "Selected handler must be recorded");
        }
        input = input == null ? new SimulationCalculationInput(Map.of()) : input;
        result = result == null ? new SimulationCalculationResult(Map.of()) : result;
        solverStatus = solverStatus == null || solverStatus.isBlank() ? "SUCCESS" : solverStatus;
        intermediatePoints = List.copyOf(intermediatePoints == null ? List.of() : intermediatePoints);
        tolerances = Map.copyOf(tolerances == null ? Map.of() : tolerances);
    }
}
