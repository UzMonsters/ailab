package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.laboratoryevent.IdempotencyKey;
import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEventId;
import com.ailab.chemistry.domain.simulationengine.SimulationCalculationAudit;
import com.ailab.chemistry.domain.simulationengine.SimulationCommand;
import com.ailab.chemistry.domain.simulationengine.SimulationExecutionResult;
import com.ailab.chemistry.domain.simulationstate.SimulationSessionId;

public interface SimulationEngineService {
    SimulationExecutionResult execute(
            SimulationSessionId sessionId,
            long expectedStateVersion,
            IdempotencyKey idempotencyKey,
            SimulationCommand command
    );

    SimulationCalculationAudit audit(
            SimulationSessionId sessionId,
            LaboratoryEventId eventId
    );
}
