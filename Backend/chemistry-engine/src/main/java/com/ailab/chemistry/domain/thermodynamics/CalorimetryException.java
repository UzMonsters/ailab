package com.ailab.chemistry.domain.thermodynamics;

import java.util.Objects;

public class CalorimetryException extends RuntimeException {
    private final CalorimetryErrorCode errorCode;

    public CalorimetryException(CalorimetryErrorCode errorCode, String message) {
        super(message);
        this.errorCode = Objects.requireNonNull(errorCode, "errorCode must not be null");
    }

    public CalorimetryErrorCode getErrorCode() {
        return errorCode;
    }
}
