package com.ailab.chemistry.domain.container;

import com.ailab.chemistry.domain.measurement.Volume;
import com.ailab.chemistry.domain.measurement.VolumeUnit;

import java.math.BigDecimal;
import java.util.Objects;

public record MaximumWorkingVolume(Volume volume) {
    public MaximumWorkingVolume {
        Objects.requireNonNull(volume, "volume must not be null");
        if (volume.in(VolumeUnit.LITER).compareTo(BigDecimal.ZERO) <= 0) {
            throw new ContainerException(ContainerErrorCode.INVALID_PROFILE, "Maximum working volume must be positive");
        }
    }
}
