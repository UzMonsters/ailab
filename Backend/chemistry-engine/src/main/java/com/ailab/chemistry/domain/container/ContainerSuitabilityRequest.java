package com.ailab.chemistry.domain.container;

import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.Volume;

import java.util.Objects;

public record ContainerSuitabilityRequest(
        ContainerProfile profile,
        Volume actualContentVolume,
        boolean sealedOperation,
        Temperature operatingTemperature,
        Pressure operatingPressure,
        Volume requiredHeadspace,
        String compoundOrFamily,
        String physicalState,
        String concentration,
        com.ailab.chemistry.domain.measurement.Duration contactDuration
) {
    public ContainerSuitabilityRequest {
        Objects.requireNonNull(profile, "profile must not be null");
        Objects.requireNonNull(actualContentVolume, "actualContentVolume must not be null");
    }
}
