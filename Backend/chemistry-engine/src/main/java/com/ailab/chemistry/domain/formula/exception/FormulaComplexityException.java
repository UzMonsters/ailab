package com.ailab.chemistry.domain.formula.exception;

public class FormulaComplexityException extends InvalidChemicalFormulaException {
    public FormulaComplexityException(String message) {
        super(message, FormulaErrorCode.FORMULA_TOO_COMPLEX);
    }
}
