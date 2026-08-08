package com.ailab.chemistry.domain.gas;

public class GasLawException extends RuntimeException {
    private final GasLawErrorCode errorCode;

    public GasLawException(GasLawErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public GasLawErrorCode getErrorCode() {
        return errorCode;
    }
}
