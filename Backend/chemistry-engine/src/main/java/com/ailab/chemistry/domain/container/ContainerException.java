package com.ailab.chemistry.domain.container;

public class ContainerException extends RuntimeException {
    private final ContainerErrorCode errorCode;

    public ContainerException(ContainerErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ContainerErrorCode errorCode() {
        return errorCode;
    }
}
