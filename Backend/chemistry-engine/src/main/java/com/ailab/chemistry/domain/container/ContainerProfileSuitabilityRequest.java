package com.ailab.chemistry.domain.container;

import com.ailab.chemistry.domain.measurement.Duration;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.Volume;

import java.util.Objects;

public record ContainerProfileSuitabilityRequest(
        String profileId,
        Volume actualContentVolume,
        boolean sealedOperation,
        Temperature operatingTemperature,
        Pressure operatingPressure,
        Volume requiredHeadspace,
        String compoundOrFamily,
        String physicalState,
        String concentration,
        Duration contactDuration
) {
    public ContainerProfileSuitabilityRequest {
        Objects.requireNonNull(profileId, "profileId must not be null");
        Objects.requireNonNull(actualContentVolume, "actualContentVolume must not be null");
    }
}
