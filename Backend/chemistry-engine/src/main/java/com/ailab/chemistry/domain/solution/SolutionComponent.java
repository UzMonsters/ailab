package com.ailab.chemistry.domain.solution;

import com.ailab.chemistry.domain.measurement.AmountOfSubstance;
import com.ailab.chemistry.domain.measurement.Mass;
import com.ailab.chemistry.domain.measurement.Volume;

import java.util.Objects;
import java.util.Optional;

public final class SolutionComponent {

    public enum ComponentRole {
        SOLUTE,
        SOLVENT
    }

    private final String compoundCode;
    private final ComponentRole role;
    private final Mass mass;
    private final AmountOfSubstance amount;
    private final Volume volume;

    public SolutionComponent(String compoundCode, ComponentRole role, Mass mass, AmountOfSubstance amount, Volume volume) {
        if (compoundCode == null || compoundCode.isBlank()) {
            throw new SolutionException(SolutionErrorCode.COMPOUND_NOT_FOUND, "Component compound code must not be null or blank");
        }
        this.compoundCode = compoundCode.trim();
        this.role = Objects.requireNonNull(role, "Role must not be null");
        this.mass = Objects.requireNonNull(mass, "Mass must not be null");
        this.amount = Objects.requireNonNull(amount, "Amount must not be null");
        this.volume = volume;
    }

    public String getCompoundCode() {
        return compoundCode;
    }

    public ComponentRole getRole() {
        return role;
    }

    public Mass getMass() {
        return mass;
    }

    public AmountOfSubstance getAmount() {
        return amount;
    }

    public Optional<Volume> getVolume() {
        return Optional.ofNullable(volume);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SolutionComponent that = (SolutionComponent) o;
        return compoundCode.equalsIgnoreCase(that.compoundCode) &&
                role == that.role &&
                mass.equals(that.mass) &&
                amount.equals(that.amount) &&
                Objects.equals(volume, that.volume);
    }

    @Override
    public int hashCode() {
        return Objects.hash(compoundCode.toUpperCase(), role, mass, amount, volume);
    }

    @Override
    public String toString() {
        return compoundCode + " (" + role + "): " + mass + ", " + amount;
    }
}
