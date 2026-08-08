package com.ailab.chemistry.domain.simulationstate;

import java.util.Optional;

public interface SimulationStateRepository {
    SimulationState create(SimulationState state, String processCode, int processVersion);

    Optional<SimulationState> findCurrent(SimulationSessionId sessionId);

    SimulationState lockCurrent(SimulationSessionId sessionId);

    void updateCurrent(SimulationState state);

    void saveSnapshot(SimulationState state, long eventSequence, String checksum);

    Optional<SimulationSnapshot> latestSnapshot(SimulationSessionId sessionId);
}
