package com.ailab.chemistry.domain.stoichiometry;

import java.util.Objects;

public class StoichiometryException extends RuntimeException {
    private final StoichiometryErrorCode errorCode;

    public StoichiometryException(StoichiometryErrorCode errorCode, String message) {
        super(message);
        this.errorCode = Objects.requireNonNull(errorCode, "ErrorCode must not be null");
    }

    public StoichiometryException(StoichiometryErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = Objects.requireNonNull(errorCode, "ErrorCode must not be null");
    }

    public StoichiometryErrorCode getErrorCode() {
        return errorCode;
    }
}
