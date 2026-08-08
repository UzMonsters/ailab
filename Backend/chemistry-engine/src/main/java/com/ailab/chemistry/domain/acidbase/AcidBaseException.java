package com.ailab.chemistry.domain.acidbase;

import java.util.Objects;

public class AcidBaseException extends RuntimeException {
    private final AcidBaseErrorCode errorCode;

    public AcidBaseException(AcidBaseErrorCode errorCode, String message) {
        super(message);
        this.errorCode = Objects.requireNonNull(errorCode, "ErrorCode must not be null");
    }

    public AcidBaseException(AcidBaseErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = Objects.requireNonNull(errorCode, "ErrorCode must not be null");
    }

    public AcidBaseErrorCode getErrorCode() {
        return errorCode;
    }
}
