package com.ailab.chemistry.domain.acidbase;

public class BufferException extends RuntimeException {
    private final BufferErrorCode errorCode;

    public BufferException(BufferErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BufferErrorCode getErrorCode() {
        return errorCode;
    }
}
