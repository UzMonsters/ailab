package com.ailab.chemistry.domain.measurement.exception;

public class BelowAbsoluteZeroException extends InvalidMeasurementException {
    public BelowAbsoluteZeroException(String message) {
        super(message, MeasurementErrorCode.BELOW_ABSOLUTE_ZERO);
    }
}
