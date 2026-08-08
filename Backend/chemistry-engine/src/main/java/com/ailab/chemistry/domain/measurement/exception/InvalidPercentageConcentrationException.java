package com.ailab.chemistry.domain.measurement.exception;

public class InvalidPercentageConcentrationException extends InvalidMeasurementException {
    public InvalidPercentageConcentrationException(String message) {
        super(message, MeasurementErrorCode.INVALID_PERCENTAGE_RANGE);
    }
}
