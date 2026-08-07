package com.ailab.chemistry.domain.phasebehavior;

public class PhaseBehaviorException extends RuntimeException {
    private final PhaseBehaviorErrorCode errorCode;

    public PhaseBehaviorException(PhaseBehaviorErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public PhaseBehaviorErrorCode getErrorCode() {
        return errorCode;
    }
}
