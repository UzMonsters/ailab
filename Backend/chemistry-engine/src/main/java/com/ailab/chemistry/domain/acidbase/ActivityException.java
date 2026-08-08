package com.ailab.chemistry.domain.acidbase;

public final class ActivityException extends RuntimeException {
    private final ActivityErrorCode errorCode;

    public ActivityException(ActivityErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ActivityErrorCode getErrorCode() {
        return errorCode;
    }
}
