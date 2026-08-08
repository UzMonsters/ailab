package com.ailab.chemistry.domain.formula.exception;

public class InvalidChemicalFormulaException extends RuntimeException {
    private final FormulaErrorCode errorCode;

    public InvalidChemicalFormulaException(String message, FormulaErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public FormulaErrorCode getErrorCode() {
        return errorCode;
    }
}
