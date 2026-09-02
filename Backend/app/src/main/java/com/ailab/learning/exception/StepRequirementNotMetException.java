package com.ailab.learning.exception;

public class StepRequirementNotMetException extends RuntimeException {
    private final String reason;
    private final String hint;

    public StepRequirementNotMetException(String message, String reason, String hint) {
        super(message);
        this.reason = reason;
        this.hint = hint;
    }

    public String getReason() {
        return reason;
    }

    public String getHint() {
        return hint;
    }
}
