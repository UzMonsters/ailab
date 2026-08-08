package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.element.MatterState;

import java.util.Map;
import java.util.Objects;

public record ReactionThermodynamicRecordSet(
        String compoundCode,
        MatterState state,
        ThermodynamicReferenceConditions conditions,
        ReactionThermodynamicSourceProperty enthalpyOfFormation,
        ReactionThermodynamicSourceProperty gibbsEnergyOfFormation,
        ReactionThermodynamicSourceProperty standardMolarEntropy,
        ReactionThermodynamicSourceProperty molarHeatCapacity) {

    public ReactionThermodynamicRecordSet {
        Objects.requireNonNull(compoundCode, "compoundCode must not be null");
        Objects.requireNonNull(state, "state must not be null");
        Objects.requireNonNull(conditions, "conditions must not be null");
        Objects.requireNonNull(enthalpyOfFormation, "enthalpyOfFormation must not be null");
        Objects.requireNonNull(gibbsEnergyOfFormation, "gibbsEnergyOfFormation must not be null");
        Objects.requireNonNull(standardMolarEntropy, "standardMolarEntropy must not be null");
        Objects.requireNonNull(molarHeatCapacity, "molarHeatCapacity must not be null");
    }

    public Map<ReactionThermodynamicProperty, ReactionThermodynamicSourceProperty> sourceProperties() {
        return Map.of(
                ReactionThermodynamicProperty.STANDARD_REACTION_ENTHALPY, enthalpyOfFormation,
                ReactionThermodynamicProperty.STANDARD_REACTION_GIBBS_ENERGY, gibbsEnergyOfFormation,
                ReactionThermodynamicProperty.STANDARD_REACTION_ENTROPY, standardMolarEntropy,
                ReactionThermodynamicProperty.STANDARD_REACTION_HEAT_CAPACITY, molarHeatCapacity);
    }
}
