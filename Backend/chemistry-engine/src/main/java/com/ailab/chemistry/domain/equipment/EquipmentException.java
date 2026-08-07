package com.ailab.chemistry.domain.equipment;

public class EquipmentException extends RuntimeException {
    private final EquipmentErrorCode errorCode;

    public EquipmentException(EquipmentErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public EquipmentErrorCode errorCode() {
        return errorCode;
    }
}
