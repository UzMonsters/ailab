package com.ailab.chemistry.domain.simulationstate;

import java.time.Instant;

public record SimulationClock(Instant currentTime) {
    public SimulationClock {
        if (currentTime == null) {
            throw new IllegalArgumentException("Simulation clock timestamp is required");
        }
    }
}
