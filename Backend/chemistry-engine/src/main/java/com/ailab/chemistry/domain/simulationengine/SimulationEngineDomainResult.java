package com.ailab.chemistry.domain.simulationengine;

import com.ailab.chemistry.domain.laboratoryevent.ScientificOperationAppliedPayload;

public record SimulationEngineDomainResult(
        SimulationExecutionStatus status,
        ScientificOperationAppliedPayload payload
) {
}
