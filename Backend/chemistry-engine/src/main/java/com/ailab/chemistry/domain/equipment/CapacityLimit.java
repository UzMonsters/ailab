package com.ailab.chemistry.domain.equipment;

import java.math.BigDecimal;
import java.util.Objects;

public record CapacityLimit(String quantity, BigDecimal value, String unit) {
    public CapacityLimit {
        Objects.requireNonNull(quantity, "quantity must not be null");
        Objects.requireNonNull(value, "value must not be null");
        Objects.requireNonNull(unit, "unit must not be null");
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new EquipmentException(EquipmentErrorCode.INVALID_PROFILE, "Capacity must be positive");
        }
    }
}
