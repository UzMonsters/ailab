package com.ailab.chemistry.domain.measurement.exception;

public class IncompatibleUnitException extends InvalidMeasurementException {
    public IncompatibleUnitException(String message) {
        super(message, MeasurementErrorCode.INCOMPATIBLE_UNIT);
    }
}
