package com.ailab.chemistry.domain.laboratoryevent;

import com.ailab.chemistry.domain.simulationengine.ConservationLedger;
import com.ailab.chemistry.domain.simulationengine.ScientificDatasetReference;
import com.ailab.chemistry.domain.simulationengine.ScientificModelReference;
import com.ailab.chemistry.domain.simulationengine.SimulationCalculationTrace;
import com.ailab.chemistry.domain.simulationengine.SimulationStateDelta;

import java.util.List;
import java.util.Map;

public record ScientificOperationAppliedPayload(
        LaboratoryEventType eventType,
        String operationType,
        String commandId,
        String commandFingerprint,
        String processCode,
        int processVersion,
        String stepId,
        String calculationMethod,
        String reactionOrProfileIdentifier,
        ScientificModelReference model,
        List<ScientificDatasetReference> datasetVersions,
        Map<String, String> assumptions,
        SimulationCalculationTrace calculationTrace,
        SimulationStateDelta stateDelta,
        ConservationLedger conservationLedger,
        String inputHash,
        String resultHash,
        int eventSchemaVersion,
        int engineCalculationSchemaVersion
) implements LaboratoryEventPayload {
    public ScientificOperationAppliedPayload {
        if (eventType == null || operationType == null || operationType.isBlank()
                || commandId == null || commandId.isBlank()
                || commandFingerprint == null || commandFingerprint.isBlank()
                || processCode == null || processCode.isBlank()
                || processVersion <= 0 || stepId == null || stepId.isBlank()
                || calculationMethod == null || calculationMethod.isBlank()
                || reactionOrProfileIdentifier == null || reactionOrProfileIdentifier.isBlank()
                || model == null || calculationTrace == null || stateDelta == null
                || conservationLedger == null || inputHash == null || inputHash.isBlank()
                || resultHash == null || resultHash.isBlank()) {
            throw new IllegalArgumentException("Scientific operation payload fields are required");
        }
        datasetVersions = List.copyOf(datasetVersions == null ? List.of() : datasetVersions);
        assumptions = Map.copyOf(assumptions == null ? Map.of() : assumptions);
    }
}
