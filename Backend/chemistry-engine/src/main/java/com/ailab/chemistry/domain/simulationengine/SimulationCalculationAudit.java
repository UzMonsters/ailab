package com.ailab.chemistry.domain.simulationengine;

import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEventId;
import com.ailab.chemistry.domain.simulationstate.SimulationSessionId;

import java.time.Instant;
import java.util.List;

public record SimulationCalculationAudit(
        LaboratoryEventId eventId,
        SimulationSessionId sessionId,
        String commandId,
        SimulationOperationType operationType,
        ScientificModelReference model,
        List<ScientificDatasetReference> datasetVersions,
        String inputHash,
        String resultHash,
        SimulationCalculationTrace calculationTrace,
        ConservationLedger conservationLedger,
        Instant createdAt
) {
    public SimulationCalculationAudit {
        datasetVersions = List.copyOf(datasetVersions == null ? List.of() : datasetVersions);
    }
}
