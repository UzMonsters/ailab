package com.ailab.chemistry.domain.container;

import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.PressureUnit;

import java.math.BigDecimal;
import java.util.Objects;

public record ContainerPressureLimit(Pressure maximum) {
    public ContainerPressureLimit {
        Objects.requireNonNull(maximum, "maximum must not be null");
        if (maximum.in(PressureUnit.PASCAL).compareTo(BigDecimal.ZERO) <= 0) {
            throw new ContainerException(ContainerErrorCode.INVALID_PROFILE, "Pressure limit must be positive");
        }
    }
}
