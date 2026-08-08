package com.ailab.chemistry.domain.simulationengine;

import java.util.List;
import java.util.Map;

public record ScientificModelSelection(
        String calculationMethod,
        String reactionOrProfileIdentifier,
        ScientificModelReference model,
        List<ScientificDatasetReference> datasets,
        Map<String, String> assumptions
) {
    public ScientificModelSelection {
        if (calculationMethod == null || calculationMethod.isBlank()
                || reactionOrProfileIdentifier == null || reactionOrProfileIdentifier.isBlank()
                || model == null) {
            throw new SimulationExecutionException(SimulationExecutionErrorCode.UNSUPPORTED_MODEL_SELECTION,
                    "Explicit method, reaction/profile identifier, and model are required");
        }
        datasets = List.copyOf(datasets == null ? List.of() : datasets);
        assumptions = Map.copyOf(assumptions == null ? Map.of() : assumptions);
    }
}
