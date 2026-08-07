package com.ailab.chemistry.domain.stoichiometry;

import com.ailab.chemistry.domain.measurement.AmountOfSubstance;
import com.ailab.chemistry.domain.measurement.Mass;

import java.util.Objects;

public final class ReactionParticipantQuantity {

    private final String compoundCode;
    private final StoichiometricQuantity quantity;

    public ReactionParticipantQuantity(String compoundCode, StoichiometricQuantity quantity) {
        if (compoundCode == null || compoundCode.isBlank()) {
            throw new StoichiometryException(StoichiometryErrorCode.COMPOUND_NOT_FOUND, "Compound code must not be null or blank");
        }
        this.compoundCode = compoundCode.trim();
        this.quantity = Objects.requireNonNull(quantity, "StoichiometricQuantity must not be null");
    }

    public static ReactionParticipantQuantity ofMass(String compoundCode, Mass mass) {
        return new ReactionParticipantQuantity(compoundCode, StoichiometricQuantity.fromMass(mass));
    }

    public static ReactionParticipantQuantity ofMass(String compoundCode, Mass mass, Purity purity) {
        return new ReactionParticipantQuantity(compoundCode, StoichiometricQuantity.fromMass(mass, purity));
    }

    public static ReactionParticipantQuantity ofMoles(String compoundCode, AmountOfSubstance moles) {
        return new ReactionParticipantQuantity(compoundCode, StoichiometricQuantity.fromMoles(moles));
    }

    public static ReactionParticipantQuantity ofMoles(String compoundCode, AmountOfSubstance moles, Purity purity) {
        return new ReactionParticipantQuantity(compoundCode, StoichiometricQuantity.fromMoles(moles, purity));
    }

    public String getCompoundCode() {
        return compoundCode;
    }

    public StoichiometricQuantity getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReactionParticipantQuantity that = (ReactionParticipantQuantity) o;
        return compoundCode.equalsIgnoreCase(that.compoundCode) && quantity.equals(that.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(compoundCode.toUpperCase(), quantity);
    }

    @Override
    public String toString() {
        return compoundCode + ": " + quantity;
    }
}
