package com.ailab.chemistry.domain.hazard;

public class HazardException extends RuntimeException {
    private final HazardErrorCode errorCode;

    public HazardException(HazardErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public HazardException(HazardErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public HazardErrorCode getErrorCode() {
        return errorCode;
    }
}
