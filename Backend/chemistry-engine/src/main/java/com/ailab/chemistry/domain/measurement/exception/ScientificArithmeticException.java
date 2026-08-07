package com.ailab.chemistry.domain.measurement.exception;

public class ScientificArithmeticException extends InvalidMeasurementException {
    public ScientificArithmeticException(String message) {
        super(message, MeasurementErrorCode.ARITHMETIC_ERROR);
    }
}
