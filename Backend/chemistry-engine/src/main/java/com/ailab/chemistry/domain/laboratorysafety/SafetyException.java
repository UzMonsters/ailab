package com.ailab.chemistry.domain.laboratorysafety;

import java.util.Objects;

public class SafetyException extends RuntimeException {
    private final SafetyErrorCode errorCode;
    private final LaboratorySafetyEvaluationResult evaluationResult;

    public SafetyException(SafetyErrorCode errorCode, String message, LaboratorySafetyEvaluationResult evaluationResult) {
        super(message);
        this.errorCode = Objects.requireNonNull(errorCode, "errorCode must not be null");
        this.evaluationResult = evaluationResult;
    }

    public SafetyErrorCode getErrorCode() {
        return errorCode;
    }

    public LaboratorySafetyEvaluationResult getEvaluationResult() {
        return evaluationResult;
    }
}
