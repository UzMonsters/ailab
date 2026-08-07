package com.ailab.chemistry.domain.equipment;

import java.math.BigDecimal;
import java.util.Objects;

public record OperatingRange(BigDecimal minimum, BigDecimal maximum, String unit) {
    public OperatingRange {
        Objects.requireNonNull(minimum, "minimum must not be null");
        Objects.requireNonNull(maximum, "maximum must not be null");
        Objects.requireNonNull(unit, "unit must not be null");
        if (minimum.compareTo(maximum) > 0) {
            throw new EquipmentException(EquipmentErrorCode.INVALID_PROFILE, "Operating range minimum cannot exceed maximum");
        }
    }

    public boolean contains(BigDecimal value, String requestedUnit) {
        return unit.equals(requestedUnit) && value.compareTo(minimum) >= 0 && value.compareTo(maximum) <= 0;
    }
}
