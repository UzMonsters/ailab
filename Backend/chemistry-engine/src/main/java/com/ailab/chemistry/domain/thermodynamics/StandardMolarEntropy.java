package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.measurement.MolarEntropy;

public record StandardMolarEntropy(MolarEntropy value, ThermodynamicReferenceConditions conditions) {
}
