package com.ailab.chemistry.infrastructure.persistence.simulation;

import com.ailab.chemistry.domain.simulationstate.SimulationSessionId;
import com.ailab.chemistry.domain.simulationstate.SimulationSnapshot;
import com.ailab.chemistry.domain.simulationstate.SimulationState;
import com.ailab.chemistry.domain.simulationstate.SimulationStateRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Profile("!local & !prod & !migration-test")
public class UnavailableSimulationStateRepository implements SimulationStateRepository {
    @Override
    public SimulationState create(SimulationState state, String processCode, int processVersion) {
        throw unavailable();
    }

    @Override
    public Optional<SimulationState> findCurrent(SimulationSessionId sessionId) {
        throw unavailable();
    }

    @Override
    public SimulationState lockCurrent(SimulationSessionId sessionId) {
        throw unavailable();
    }

    @Override
    public void updateCurrent(SimulationState state) {
        throw unavailable();
    }

    @Override
    public void saveSnapshot(SimulationState state, long eventSequence, String checksum) {
        throw unavailable();
    }

    @Override
    public Optional<SimulationSnapshot> latestSnapshot(SimulationSessionId sessionId) {
        throw unavailable();
    }

    private IllegalStateException unavailable() {
        return new IllegalStateException("Production simulation state repository is unavailable for this profile");
    }
}
