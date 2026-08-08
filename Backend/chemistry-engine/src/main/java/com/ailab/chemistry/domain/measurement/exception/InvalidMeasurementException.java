package com.ailab.chemistry.domain.measurement.exception;

public class InvalidMeasurementException extends RuntimeException {
    private final MeasurementErrorCode errorCode;

    public InvalidMeasurementException(String message, MeasurementErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public MeasurementErrorCode getErrorCode() {
        return errorCode;
    }
}
