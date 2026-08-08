package com.ailab.chemistry.domain.simulationstate;

import java.time.Instant;

public record CreateSimulationSessionRequest(
        SimulationSessionId sessionId,
        String processCode,
        int processVersion,
        Instant requestedAt
) {
    public CreateSimulationSessionRequest {
        if (sessionId == null || processCode == null || processCode.isBlank() || processVersion < 1 || requestedAt == null) {
            throw new IllegalArgumentException("Session id, process, version, and timestamp are required");
        }
    }
}
