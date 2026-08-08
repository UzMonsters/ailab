package com.ailab.chemistry.domain.laboratoryevent;

import com.ailab.chemistry.domain.simulationstate.SimulationSessionId;

import java.util.List;
import java.util.Optional;

public interface LaboratoryEventStore {
    Optional<LaboratoryEvent> findByIdempotencyKey(SimulationSessionId sessionId, IdempotencyKey idempotencyKey);

    LaboratoryEvent append(LaboratoryEvent event);

    List<LaboratoryEvent> eventsForSession(SimulationSessionId sessionId);
}
