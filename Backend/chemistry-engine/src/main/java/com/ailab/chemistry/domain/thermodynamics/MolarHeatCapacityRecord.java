package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.measurement.MolarHeatCapacity;

public record MolarHeatCapacityRecord(MolarHeatCapacity value, ThermodynamicReferenceConditions conditions) {
}
