package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.measurement.Volume;
import com.ailab.chemistry.domain.measurement.VolumeUnit;

import java.math.BigDecimal;
import java.util.Objects;

public record BufferPreparationRequest(
        BufferSystem system,
        BigDecimal targetPh,
        BigDecimal totalBufferConcentration,
        Volume finalVolume
) {
    public BufferPreparationRequest {
        Objects.requireNonNull(system, "system must not be null");
        Objects.requireNonNull(targetPh, "targetPh must not be null");
        if (totalBufferConcentration == null || totalBufferConcentration.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BufferException(BufferErrorCode.NON_POSITIVE_TOTAL_CONCENTRATION, "Total buffer concentration must be positive");
        }
        Objects.requireNonNull(finalVolume, "finalVolume must not be null");
        if (finalVolume.in(VolumeUnit.LITER).compareTo(BigDecimal.ZERO) <= 0) {
            throw new BufferException(BufferErrorCode.NON_POSITIVE_VOLUME, "Final volume must be positive");
        }
    }
}
