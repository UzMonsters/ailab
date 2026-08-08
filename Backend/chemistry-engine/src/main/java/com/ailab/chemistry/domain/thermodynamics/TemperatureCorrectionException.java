package com.ailab.chemistry.domain.thermodynamics;

public class TemperatureCorrectionException extends RuntimeException {
    private final TemperatureCorrectionErrorCode errorCode;

    public TemperatureCorrectionException(TemperatureCorrectionErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public TemperatureCorrectionErrorCode getErrorCode() {
        return errorCode;
    }
}
