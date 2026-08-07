package com.ailab.chemistry.domain.simulationengine;

public record ScientificModelReference(String identifier, String version) {
    public ScientificModelReference {
        if (identifier == null || identifier.isBlank() || version == null || version.isBlank()) {
            throw new SimulationExecutionException(SimulationExecutionErrorCode.UNSUPPORTED_MODEL_SELECTION,
                    "Scientific model identifier and version are required");
        }
    }
}
