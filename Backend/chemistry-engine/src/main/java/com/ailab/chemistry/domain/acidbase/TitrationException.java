package com.ailab.chemistry.domain.acidbase;

public class TitrationException extends RuntimeException {
    private final TitrationErrorCode errorCode;

    public TitrationException(TitrationErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public TitrationErrorCode getErrorCode() {
        return errorCode;
    }
}
