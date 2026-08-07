package com.ailab.chemistry.domain.simulationengine;

import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEventId;
import com.ailab.chemistry.domain.laboratoryevent.ScientificOperationAppliedPayload;
import com.ailab.chemistry.domain.simulationstate.SimulationState;

public record SimulationExecutionResult(
        SimulationExecutionStatus status,
        LaboratoryEventId eventId,
        ScientificOperationAppliedPayload payload,
        SimulationState state,
        SimulationCalculationAudit audit
) {
}
