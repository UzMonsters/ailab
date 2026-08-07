package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.equation.RationalNumber;

import java.util.List;
import java.util.Map;

public record ReactionThermodynamicTerm(
        String compoundCode,
        String formula,
        MatterState state,
        RationalNumber signedCoefficient,
        Map<ReactionThermodynamicProperty, ReactionThermodynamicSourceProperty> sourceProperties,
        Map<ReactionThermodynamicProperty, ReactionThermodynamicResultProperty> contributions,
        List<ThermodynamicProvenance> provenances) {

    public ReactionThermodynamicTerm {
        sourceProperties = Map.copyOf(sourceProperties);
        contributions = Map.copyOf(contributions);
        provenances = List.copyOf(provenances);
    }
}
