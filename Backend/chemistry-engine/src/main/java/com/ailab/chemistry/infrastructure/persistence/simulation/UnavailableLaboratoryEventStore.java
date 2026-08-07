package com.ailab.chemistry.infrastructure.persistence.simulation;

import com.ailab.chemistry.domain.laboratoryevent.IdempotencyKey;
import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEvent;
import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEventStore;
import com.ailab.chemistry.domain.simulationstate.SimulationSessionId;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("!local & !prod & !migration-test")
public class UnavailableLaboratoryEventStore implements LaboratoryEventStore {
    @Override
    public Optional<LaboratoryEvent> findByIdempotencyKey(SimulationSessionId sessionId, IdempotencyKey idempotencyKey) {
        throw unavailable();
    }

    @Override
    public LaboratoryEvent append(LaboratoryEvent event) {
        throw unavailable();
    }

    @Override
    public List<LaboratoryEvent> eventsForSession(SimulationSessionId sessionId) {
        throw unavailable();
    }

    private IllegalStateException unavailable() {
        return new IllegalStateException("Production laboratory event store is unavailable for this profile");
    }
}
