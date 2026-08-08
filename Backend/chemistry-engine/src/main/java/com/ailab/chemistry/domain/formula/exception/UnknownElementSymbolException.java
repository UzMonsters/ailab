package com.ailab.chemistry.domain.formula.exception;

public class UnknownElementSymbolException extends InvalidChemicalFormulaException {
    public UnknownElementSymbolException(String message) {
        super(message, FormulaErrorCode.UNKNOWN_ELEMENT_SYMBOL);
    }
}
