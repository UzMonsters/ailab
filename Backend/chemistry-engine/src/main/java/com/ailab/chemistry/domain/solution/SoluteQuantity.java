package com.ailab.chemistry.domain.solution;

import com.ailab.chemistry.domain.measurement.AmountOfSubstance;
import com.ailab.chemistry.domain.measurement.Mass;

import java.util.Objects;
import java.util.Optional;

public final class SoluteQuantity {

    private final String compoundCode;
    private final Mass mass;
    private final AmountOfSubstance amount;

    private SoluteQuantity(String compoundCode, Mass mass, AmountOfSubstance amount) {
        if (compoundCode == null || compoundCode.isBlank()) {
            throw new SolutionException(SolutionErrorCode.COMPOUND_NOT_FOUND, "Solute compound code must not be null or blank");
        }
        if (mass == null && amount == null) {
            throw new SolutionException(SolutionErrorCode.INVALID_QUANTITY, "Either mass or amount must be provided for solute quantity");
        }
        this.compoundCode = compoundCode.trim();
        this.mass = mass;
        this.amount = amount;
    }

    public static SoluteQuantity ofMass(String compoundCode, Mass mass) {
        return new SoluteQuantity(compoundCode, Objects.requireNonNull(mass, "Mass must not be null"), null);
    }

    public static SoluteQuantity ofAmount(String compoundCode, AmountOfSubstance amount) {
        return new SoluteQuantity(compoundCode, null, Objects.requireNonNull(amount, "Amount must not be null"));
    }

    public static SoluteQuantity ofBoth(String compoundCode, Mass mass, AmountOfSubstance amount) {
        return new SoluteQuantity(compoundCode, Objects.requireNonNull(mass, "Mass must not be null"), Objects.requireNonNull(amount, "Amount must not be null"));
    }

    public String getCompoundCode() {
        return compoundCode;
    }

    public Optional<Mass> getMass() {
        return Optional.ofNullable(mass);
    }

    public Optional<AmountOfSubstance> getAmount() {
        return Optional.ofNullable(amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SoluteQuantity that = (SoluteQuantity) o;
        return compoundCode.equalsIgnoreCase(that.compoundCode) &&
                Objects.equals(mass, that.mass) &&
                Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(compoundCode.toUpperCase(), mass, amount);
    }

    @Override
    public String toString() {
        String qtyStr = mass != null ? mass.toString() : (amount != null ? amount.toString() : "");
        return compoundCode + ": " + qtyStr;
    }
}
