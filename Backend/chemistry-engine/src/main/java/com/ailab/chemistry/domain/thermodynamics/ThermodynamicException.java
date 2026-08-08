package com.ailab.chemistry.domain.thermodynamics;

public class ThermodynamicException extends RuntimeException {
    private final ThermodynamicErrorCode errorCode;

    public ThermodynamicException(ThermodynamicErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ThermodynamicErrorCode getErrorCode() {
        return errorCode;
    }
}
