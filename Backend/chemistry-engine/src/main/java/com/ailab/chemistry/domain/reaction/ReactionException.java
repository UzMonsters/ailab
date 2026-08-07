package com.ailab.chemistry.domain.reaction;

public class ReactionException extends RuntimeException {
    private final ReactionErrorCode errorCode;

    public ReactionException(ReactionErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ReactionException(ReactionErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ReactionErrorCode getErrorCode() {
        return errorCode;
    }
}
