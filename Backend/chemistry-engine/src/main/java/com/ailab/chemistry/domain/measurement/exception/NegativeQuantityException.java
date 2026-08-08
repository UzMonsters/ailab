package com.ailab.chemistry.domain.measurement.exception;

public class NegativeQuantityException extends InvalidMeasurementException {
    public NegativeQuantityException(String message) {
        super(message, MeasurementErrorCode.NEGATIVE_QUANTITY);
    }
}
