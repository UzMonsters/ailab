package com.ailab.chemistry.domain.simulationengine;

public class SimulationExecutionException extends RuntimeException {
    private final SimulationExecutionErrorCode errorCode;

    public SimulationExecutionException(SimulationExecutionErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public SimulationExecutionErrorCode getErrorCode() {
        return errorCode;
    }
}
