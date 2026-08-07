package com.ailab.chemistry.domain.acidbase;

public final class PolyproticTitrationException extends RuntimeException {
    private final PolyproticTitrationErrorCode errorCode;

    public PolyproticTitrationException(PolyproticTitrationErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public PolyproticTitrationErrorCode getErrorCode() {
        return errorCode;
    }
}
