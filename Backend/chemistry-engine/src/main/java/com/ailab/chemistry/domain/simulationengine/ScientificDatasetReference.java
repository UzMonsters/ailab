package com.ailab.chemistry.domain.simulationengine;

public record ScientificDatasetReference(String name, String version) {
    public ScientificDatasetReference {
        if (name == null || name.isBlank() || version == null || version.isBlank()) {
            throw new SimulationExecutionException(SimulationExecutionErrorCode.UNSUPPORTED_MODEL_SELECTION,
                    "Dataset name and version are required");
        }
    }
}
