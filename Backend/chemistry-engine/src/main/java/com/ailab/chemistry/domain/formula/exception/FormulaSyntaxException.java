package com.ailab.chemistry.domain.formula.exception;

public class FormulaSyntaxException extends InvalidChemicalFormulaException {
    public FormulaSyntaxException(String message, FormulaErrorCode errorCode) {
        super(message, errorCode);
    }
}
