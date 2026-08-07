package com.ailab.chemistry.domain.simulationstate;

public record SimulationSnapshot(SimulationState state, long eventSequence, String checksum) {
}
