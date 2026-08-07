package com.ailab.chemistry.domain.simulationengine;

import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEventId;
import com.ailab.chemistry.domain.simulationstate.SimulationSessionId;

import java.util.Optional;

public interface SimulationCalculationAuditRepository {
    SimulationCalculationAudit save(SimulationCalculationAudit audit);

    Optional<SimulationCalculationAudit> find(SimulationSessionId sessionId, LaboratoryEventId eventId);
}
