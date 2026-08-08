package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.compound.MolarMass;

public interface CompoundCalculationService {
    MolarMass calculateMolarMass(String formulaStr);
}
