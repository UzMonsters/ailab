package com.ailab.chemistry.infrastructure.persistence.simulation;

import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEventId;
import com.ailab.chemistry.domain.simulationengine.SimulationCalculationAudit;
import com.ailab.chemistry.domain.simulationengine.SimulationCalculationAuditRepository;
import com.ailab.chemistry.domain.simulationstate.SimulationSessionId;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Profile("!local & !prod & !migration-test")
public class UnavailableSimulationCalculationAuditRepository implements SimulationCalculationAuditRepository {
    @Override
    public SimulationCalculationAudit save(SimulationCalculationAudit audit) {
        throw unavailable();
    }

    @Override
    public Optional<SimulationCalculationAudit> find(SimulationSessionId sessionId, LaboratoryEventId eventId) {
        throw unavailable();
    }

    private IllegalStateException unavailable() {
        return new IllegalStateException("Simulation calculation audits require a PostgreSQL repository");
    }
}
