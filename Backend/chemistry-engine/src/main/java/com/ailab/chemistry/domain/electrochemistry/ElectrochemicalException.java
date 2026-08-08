package com.ailab.chemistry.domain.electrochemistry;

public class ElectrochemicalException extends RuntimeException {
    private final ElectrochemicalErrorCode errorCode;

    public ElectrochemicalException(ElectrochemicalErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ElectrochemicalErrorCode getErrorCode() {
        return errorCode;
    }
}
