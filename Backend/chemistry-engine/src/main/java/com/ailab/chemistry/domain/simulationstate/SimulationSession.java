package com.ailab.chemistry.domain.simulationstate;

public record SimulationSession(
        SimulationSessionId sessionId,
        String processCode,
        int processVersion,
        SimulationSessionStatus status,
        SimulationStateVersion currentVersion
) {
    public SimulationSession {
        if (sessionId == null || processCode == null || processCode.isBlank()
                || processVersion < 1 || status == null || currentVersion == null) {
            throw new IllegalArgumentException("Simulation session identity, process, status, and version are required");
        }
    }
}
