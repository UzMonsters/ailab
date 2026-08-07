package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.measurement.Volume;

import java.util.Objects;

public record PolyproticEquivalencePoint(
        int protonationStep,
        Volume volume,
        PolyproticTitrationRegion region
) {
    public PolyproticEquivalencePoint {
        if (protonationStep < 1 || protonationStep > 2) {
            throw new PolyproticTitrationException(PolyproticTitrationErrorCode.INVALID_SYSTEM_TYPE, "Diprotic equivalence step must be 1 or 2");
        }
        Objects.requireNonNull(volume, "volume must not be null");
        Objects.requireNonNull(region, "region must not be null");
    }
}
