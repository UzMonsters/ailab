package com.ailab.chemistry.domain.equipment;

import java.math.BigDecimal;
import java.util.Objects;

public record AccuracySpecification(BigDecimal absoluteError, String unit) {
    public AccuracySpecification {
        Objects.requireNonNull(absoluteError, "absoluteError must not be null");
        Objects.requireNonNull(unit, "unit must not be null");
        if (absoluteError.compareTo(BigDecimal.ZERO) < 0) {
            throw new EquipmentException(EquipmentErrorCode.INVALID_PROFILE, "Accuracy absolute error cannot be negative");
        }
    }
}
