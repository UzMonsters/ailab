package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.laboratoryevent.IdempotencyKey;
import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEventPayload;
import com.ailab.chemistry.domain.simulationstate.CreateSimulationSessionRequest;
import com.ailab.chemistry.domain.simulationstate.SimulationSessionId;
import com.ailab.chemistry.domain.simulationstate.SimulationState;

public interface SimulationSessionService {
    SimulationState createSession(CreateSimulationSessionRequest request);

    SimulationState appendEvent(SimulationSessionId sessionId, long expectedVersion, IdempotencyKey idempotencyKey,
                                LaboratoryEventPayload payload);

    SimulationState getCurrentState(SimulationSessionId sessionId);

    SimulationState replay(SimulationSessionId sessionId);

    SimulationState replayFromLatestSnapshot(SimulationSessionId sessionId);
}
