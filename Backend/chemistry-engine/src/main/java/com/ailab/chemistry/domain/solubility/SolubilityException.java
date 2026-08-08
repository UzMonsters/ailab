package com.ailab.chemistry.domain.solubility;

public class SolubilityException extends RuntimeException {
    private final SolubilityErrorCode errorCode;

    public SolubilityException(SolubilityErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public SolubilityErrorCode getErrorCode() {
        return errorCode;
    }
}
