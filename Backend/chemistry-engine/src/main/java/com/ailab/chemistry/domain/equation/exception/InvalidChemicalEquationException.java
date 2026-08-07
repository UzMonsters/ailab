package com.ailab.chemistry.domain.equation.exception;

public class InvalidChemicalEquationException extends RuntimeException {
    private final EquationErrorCode errorCode;

    public InvalidChemicalEquationException(String message, EquationErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public EquationErrorCode getErrorCode() {
        return errorCode;
    }
}
