package com.ailab.chemistry.domain.simulationstate;

public enum SimulationSessionStatus {
    CREATED,
    READY,
    RUNNING,
    PAUSED,
    COMPLETED,
    CANCELLED,
    FAILED;

    public boolean terminal() {
        return this == COMPLETED || this == CANCELLED || this == FAILED;
    }
}
