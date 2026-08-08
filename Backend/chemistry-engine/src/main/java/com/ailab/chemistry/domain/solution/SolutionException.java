package com.ailab.chemistry.domain.solution;

import java.util.Objects;

public class SolutionException extends RuntimeException {
    private final SolutionErrorCode errorCode;

    public SolutionException(SolutionErrorCode errorCode, String message) {
        super(message);
        this.errorCode = Objects.requireNonNull(errorCode, "ErrorCode must not be null");
    }

    public SolutionException(SolutionErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = Objects.requireNonNull(errorCode, "ErrorCode must not be null");
    }

    public SolutionErrorCode getErrorCode() {
        return errorCode;
    }
}
