package com.ailab.chemistry.domain.thermodynamics;

public class EquilibriumException extends RuntimeException {
    private final EquilibriumErrorCode errorCode;

    public EquilibriumException(EquilibriumErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public EquilibriumErrorCode getErrorCode() {
        return errorCode;
    }
}
