package com.ailab.chemistry.domain.simulationstate;

public class SimulationStateException extends RuntimeException {
    private final SimulationStateErrorCode errorCode;

    public SimulationStateException(SimulationStateErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public SimulationStateErrorCode errorCode() {
        return errorCode;
    }
}
