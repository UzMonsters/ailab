package com.ailab.chemistry.domain.labenvironment;

import java.util.List;

public record EnvironmentalRequirement(
        TemperatureRange acceptableTemperatureRange,
        PressureRange acceptablePressureRange,
        HumidityRange acceptableHumidityRange,
        VentilationMode requiredVentilationMode,
        boolean fumeHoodRequired,
        boolean isolatedEnclosureRequired,
        List<VentilationMode> prohibitedVentilationModes
) {
    public EnvironmentalRequirement {
        prohibitedVentilationModes = prohibitedVentilationModes == null ? List.of() : List.copyOf(prohibitedVentilationModes);
    }
}
