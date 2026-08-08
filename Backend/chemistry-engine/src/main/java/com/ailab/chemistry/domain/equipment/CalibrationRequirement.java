package com.ailab.chemistry.domain.equipment;

import com.ailab.chemistry.domain.measurement.Duration;
import com.ailab.chemistry.domain.measurement.DurationUnit;

import java.math.BigDecimal;
import java.util.Objects;

public record CalibrationRequirement(boolean required, Duration interval, Duration dueSoonWindow, String provenance) {
    public CalibrationRequirement {
        if (required) {
            Objects.requireNonNull(interval, "interval must not be null when calibration is required");
            if (interval.in(DurationUnit.SECOND).compareTo(BigDecimal.ZERO) <= 0) {
                throw new EquipmentException(EquipmentErrorCode.INVALID_PROFILE, "Calibration interval must be positive");
            }
        }
    }

    public static CalibrationRequirement notRequired() {
        return new CalibrationRequirement(false, null, null, "not required");
    }

    public static CalibrationRequirement required(Duration interval, Duration dueSoonWindow, String provenance) {
        return new CalibrationRequirement(true, interval, dueSoonWindow, provenance);
    }
}
