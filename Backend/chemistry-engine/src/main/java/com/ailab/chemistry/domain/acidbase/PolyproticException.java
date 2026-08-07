package com.ailab.chemistry.domain.acidbase;

public class PolyproticException extends RuntimeException {
    private final PolyproticErrorCode errorCode;

    public PolyproticException(PolyproticErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public PolyproticErrorCode getErrorCode() {
        return errorCode;
    }
}
