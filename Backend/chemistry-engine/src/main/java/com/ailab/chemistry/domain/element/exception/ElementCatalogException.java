package com.ailab.chemistry.domain.element.exception;

public class ElementCatalogException extends RuntimeException {
    private final ElementCatalogErrorCode errorCode;

    public ElementCatalogException(String message, ElementCatalogErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ElementCatalogErrorCode getErrorCode() {
        return errorCode;
    }
}
