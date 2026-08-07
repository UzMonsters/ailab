package com.ailab.chemistry.domain.kinetics;

import java.util.Objects;

public class KineticException extends RuntimeException {
    private final KineticErrorCode errorCode;

    public KineticException(KineticErrorCode errorCode, String message) {
        super(message);
        this.errorCode = Objects.requireNonNull(errorCode, "errorCode must not be null");
    }

    public KineticErrorCode getErrorCode() {
        return errorCode;
    }
}
