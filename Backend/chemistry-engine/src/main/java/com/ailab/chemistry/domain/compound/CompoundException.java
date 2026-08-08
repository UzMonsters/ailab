package com.ailab.chemistry.domain.compound;

public class CompoundException extends RuntimeException {
    private final CompoundErrorCode errorCode;

    public CompoundException(CompoundErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public CompoundErrorCode getErrorCode() {
        return errorCode;
    }
}
