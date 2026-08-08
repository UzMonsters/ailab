package com.ailab.chemistry.domain.thermodynamics;

import java.util.Objects;

public class EquilibriumCompositionException extends RuntimeException {
    private final EquilibriumCompositionErrorCode errorCode;

    public EquilibriumCompositionException(EquilibriumCompositionErrorCode errorCode, String message) {
        super(message);
        this.errorCode = Objects.requireNonNull(errorCode, "errorCode must not be null");
    }

    public EquilibriumCompositionErrorCode getErrorCode() {
        return errorCode;
    }
}
