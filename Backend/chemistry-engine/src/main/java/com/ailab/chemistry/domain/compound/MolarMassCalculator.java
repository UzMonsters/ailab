package com.ailab.chemistry.domain.compound;

public interface MolarMassCalculator {
    MolarMass calculate(CompoundComposition composition, ElementMassProvider elementMassProvider);
}
