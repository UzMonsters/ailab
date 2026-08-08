package com.ailab.chemistry.domain.element.property;

public class ElementPropertyException extends RuntimeException {
    private final ElementPropertyErrorCode errorCode;

    public ElementPropertyException(ElementPropertyErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ElementPropertyErrorCode getErrorCode() {
        return errorCode;
    }
}
