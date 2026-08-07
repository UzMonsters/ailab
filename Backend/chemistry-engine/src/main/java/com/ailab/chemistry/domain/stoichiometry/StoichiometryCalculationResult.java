package com.ailab.chemistry.domain.stoichiometry;

import com.ailab.chemistry.domain.measurement.AmountOfSubstance;
import com.ailab.chemistry.domain.measurement.Mass;

import java.util.*;

public final class StoichiometryCalculationResult {

    private final String reactionCode;
    private final String sourceReactantCode;
    private final StoichiometricQuantity sourceQuantity;
    private final AmountOfSubstance pureSourceMoles;
    private final ReactionExtent reactionExtent;
    private final Map<String, AmountOfSubstance> requiredReactantMoles;
    private final Map<String, Mass> requiredReactantMasses;
    private final Map<String, AmountOfSubstance> expectedProductMoles;
    private final Map<String, Mass> expectedProductMasses;

    public StoichiometryCalculationResult(
            String reactionCode,
            String sourceReactantCode,
            StoichiometricQuantity sourceQuantity,
            AmountOfSubstance pureSourceMoles,
            ReactionExtent reactionExtent,
            Map<String, AmountOfSubstance> requiredReactantMoles,
            Map<String, Mass> requiredReactantMasses,
            Map<String, AmountOfSubstance> expectedProductMoles,
            Map<String, Mass> expectedProductMasses) {
        this.reactionCode = Objects.requireNonNull(reactionCode, "Reaction code must not be null");
        this.sourceReactantCode = Objects.requireNonNull(sourceReactantCode, "Source reactant code must not be null");
        this.sourceQuantity = Objects.requireNonNull(sourceQuantity, "Source quantity must not be null");
        this.pureSourceMoles = Objects.requireNonNull(pureSourceMoles, "Pure source moles must not be null");
        this.reactionExtent = Objects.requireNonNull(reactionExtent, "Reaction extent must not be null");
        this.requiredReactantMoles = Collections.unmodifiableMap(new HashMap<>(requiredReactantMoles));
        this.requiredReactantMasses = Collections.unmodifiableMap(new HashMap<>(requiredReactantMasses));
        this.expectedProductMoles = Collections.unmodifiableMap(new HashMap<>(expectedProductMoles));
        this.expectedProductMasses = Collections.unmodifiableMap(new HashMap<>(expectedProductMasses));
    }

    public String getReactionCode() {
        return reactionCode;
    }

    public String getSourceReactantCode() {
        return sourceReactantCode;
    }

    public StoichiometricQuantity getSourceQuantity() {
        return sourceQuantity;
    }

    public AmountOfSubstance getPureSourceMoles() {
        return pureSourceMoles;
    }

    public ReactionExtent getReactionExtent() {
        return reactionExtent;
    }

    public Map<String, AmountOfSubstance> getRequiredReactantMoles() {
        return requiredReactantMoles;
    }

    public Map<String, Mass> getRequiredReactantMasses() {
        return requiredReactantMasses;
    }

    public Map<String, AmountOfSubstance> getExpectedProductMoles() {
        return expectedProductMoles;
    }

    public Map<String, Mass> getExpectedProductMasses() {
        return expectedProductMasses;
    }

    @Override
    public String toString() {
        return "StoichiometryCalculationResult{rxn=" + reactionCode + ", source=" + sourceReactantCode + ", extent=" + reactionExtent + '}';
    }
}
