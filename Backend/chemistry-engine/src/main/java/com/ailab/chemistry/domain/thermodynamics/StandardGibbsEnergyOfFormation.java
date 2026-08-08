package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.measurement.MolarEnergy;

public record StandardGibbsEnergyOfFormation(MolarEnergy value, ThermodynamicReferenceConditions conditions) {
}
