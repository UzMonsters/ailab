package com.ailab.chemistry.domain.labenvironment;

public class EnvironmentException extends RuntimeException {
    private final EnvironmentErrorCode errorCode;

    public EnvironmentException(EnvironmentErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public EnvironmentErrorCode errorCode() {
        return errorCode;
    }
}
