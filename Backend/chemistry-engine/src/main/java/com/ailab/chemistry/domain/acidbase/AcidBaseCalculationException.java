package com.ailab.chemistry.domain.acidbase;

public class AcidBaseCalculationException extends RuntimeException {

    private final AcidBaseCalculationErrorCode errorCode;

    public AcidBaseCalculationException(AcidBaseCalculationErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public AcidBaseCalculationErrorCode getErrorCode() {
        return errorCode;
    }
}
