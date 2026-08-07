package com.ailab.chemistry.domain.gas;

import com.ailab.chemistry.domain.measurement.AmountOfSubstance;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.Volume;

import java.util.Objects;

public record GasState(
        Pressure pressure,
        Volume volume,
        AmountOfSubstance amount,
        Temperature temperature,
        CompressibilityFactor compressibilityFactor
) {
    public GasState {
        Objects.requireNonNull(pressure, "pressure must not be null");
        Objects.requireNonNull(volume, "volume must not be null");
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(temperature, "temperature must not be null");
        compressibilityFactor = compressibilityFactor == null ? CompressibilityFactor.ideal() : compressibilityFactor;
    }
}
