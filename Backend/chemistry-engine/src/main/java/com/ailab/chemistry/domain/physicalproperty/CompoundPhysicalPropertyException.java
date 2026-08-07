package com.ailab.chemistry.domain.physicalproperty;

public class CompoundPhysicalPropertyException extends RuntimeException {

    private final CompoundPhysicalPropertyErrorCode errorCode;

    public CompoundPhysicalPropertyException(CompoundPhysicalPropertyErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public CompoundPhysicalPropertyException(CompoundPhysicalPropertyErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public CompoundPhysicalPropertyErrorCode getErrorCode() {
        return errorCode;
    }
}
