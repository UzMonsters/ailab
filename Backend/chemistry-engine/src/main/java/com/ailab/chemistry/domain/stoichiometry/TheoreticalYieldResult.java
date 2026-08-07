package com.ailab.chemistry.domain.stoichiometry;

import com.ailab.chemistry.domain.measurement.AmountOfSubstance;
import com.ailab.chemistry.domain.measurement.Mass;

import java.util.*;

public final class TheoreticalYieldResult {

    private final String reactionCode;
    private final LimitingReagentResult limitingReagentResult;
    private final String productCompoundCode;
    private final AmountOfSubstance theoreticalMoles;
    private final AmountOfSubstance theoreticalMolesLowerBound;
    private final AmountOfSubstance theoreticalMolesUpperBound;
    private final Mass theoreticalMass;
    private final Mass theoreticalMassLowerBound;
    private final Mass theoreticalMassUpperBound;
    private final List<ExcessReactantResult> excessReactants;
    private final Map<String, Mass> allProductYields;

    public TheoreticalYieldResult(
            String reactionCode,
            LimitingReagentResult limitingReagentResult,
            String productCompoundCode,
            AmountOfSubstance theoreticalMoles,
            AmountOfSubstance theoreticalMolesLowerBound,
            AmountOfSubstance theoreticalMolesUpperBound,
            Mass theoreticalMass,
            Mass theoreticalMassLowerBound,
            Mass theoreticalMassUpperBound,
            List<ExcessReactantResult> excessReactants,
            Map<String, Mass> allProductYields) {
        this.reactionCode = Objects.requireNonNull(reactionCode, "Reaction code must not be null");
        this.limitingReagentResult = Objects.requireNonNull(limitingReagentResult, "LimitingReagentResult must not be null");
        this.productCompoundCode = Objects.requireNonNull(productCompoundCode, "Product compound code must not be null");
        this.theoreticalMoles = Objects.requireNonNull(theoreticalMoles, "Theoretical moles must not be null");
        this.theoreticalMolesLowerBound = theoreticalMolesLowerBound;
        this.theoreticalMolesUpperBound = theoreticalMolesUpperBound;
        this.theoreticalMass = Objects.requireNonNull(theoreticalMass, "Theoretical mass must not be null");
        this.theoreticalMassLowerBound = theoreticalMassLowerBound;
        this.theoreticalMassUpperBound = theoreticalMassUpperBound;
        this.excessReactants = Collections.unmodifiableList(new ArrayList<>(Objects.requireNonNull(excessReactants, "Excess reactants must not be null")));
        this.allProductYields = Collections.unmodifiableMap(new HashMap<>(Objects.requireNonNull(allProductYields, "All product yields must not be null")));
    }

    public String getReactionCode() {
        return reactionCode;
    }

    public LimitingReagentResult getLimitingReagentResult() {
        return limitingReagentResult;
    }

    public String getProductCompoundCode() {
        return productCompoundCode;
    }

    public AmountOfSubstance getTheoreticalMoles() {
        return theoreticalMoles;
    }

    public Optional<AmountOfSubstance> getTheoreticalMolesLowerBound() {
        return Optional.ofNullable(theoreticalMolesLowerBound);
    }

    public Optional<AmountOfSubstance> getTheoreticalMolesUpperBound() {
        return Optional.ofNullable(theoreticalMolesUpperBound);
    }

    public Mass getTheoreticalMass() {
        return theoreticalMass;
    }

    public Optional<Mass> getTheoreticalMassLowerBound() {
        return Optional.ofNullable(theoreticalMassLowerBound);
    }

    public Optional<Mass> getTheoreticalMassUpperBound() {
        return Optional.ofNullable(theoreticalMassUpperBound);
    }

    public List<ExcessReactantResult> getExcessReactants() {
        return excessReactants;
    }

    public Map<String, Mass> getAllProductYields() {
        return allProductYields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TheoreticalYieldResult that = (TheoreticalYieldResult) o;
        return reactionCode.equals(that.reactionCode) &&
                productCompoundCode.equals(that.productCompoundCode) &&
                theoreticalMoles.equals(that.theoreticalMoles) &&
                theoreticalMass.equals(that.theoreticalMass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reactionCode, productCompoundCode, theoreticalMoles, theoreticalMass);
    }

    @Override
    public String toString() {
        return "TheoreticalYieldResult{" + productCompoundCode + ": " + theoreticalMass + " (" + theoreticalMoles + "), limiting=" + limitingReagentResult.getLimitingCompoundCodes() + '}';
    }
}
