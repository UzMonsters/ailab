package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.measurement.Volume;

import java.util.Objects;

public record EquivalencePoint(
        Volume volume,
        TitrationSystemType systemType
) {
    public EquivalencePoint {
        Objects.requireNonNull(volume, "volume must not be null");
        Objects.requireNonNull(systemType, "systemType must not be null");
    }

    public Volume getVolume() {
        return volume;
    }
}
