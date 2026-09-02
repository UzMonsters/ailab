package com.ailab.learning.exception;

public class PrerequisiteNotMetException extends RuntimeException {
    private final String requiredLevelId;

    public PrerequisiteNotMetException(String message, String requiredLevelId) {
        super(message);
        this.requiredLevelId = requiredLevelId;
    }

    public String getRequiredLevelId() {
        return requiredLevelId;
    }
}
