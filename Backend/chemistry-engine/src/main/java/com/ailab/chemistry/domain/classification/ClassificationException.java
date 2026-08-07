package com.ailab.chemistry.domain.classification;

public class ClassificationException extends RuntimeException {

    private final ClassificationErrorCode errorCode;

    public ClassificationException(ClassificationErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ClassificationException(ClassificationErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ClassificationErrorCode getErrorCode() {
        return errorCode;
    }
}
