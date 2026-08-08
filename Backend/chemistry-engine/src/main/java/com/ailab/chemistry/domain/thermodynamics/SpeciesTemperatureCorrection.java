package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.equation.RationalNumber;

import java.math.BigDecimal;

public record SpeciesTemperatureCorrection(
        String compoundCode,
        MatterState state,
        RationalNumber signedCoefficient,
        TemperatureDependentPropertyResult propertyResult,
        BigDecimal enthalpyContributionKjPerMol,
        BigDecimal entropyContributionJPerMolKelvin,
        BigDecimal heatCapacityContributionJPerMolKelvin,
        ThermodynamicProvenance provenance) {
}
