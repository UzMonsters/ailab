package com.ailab.chemistry.domain.solution;

import com.ailab.chemistry.domain.measurement.Mass;
import com.ailab.chemistry.domain.measurement.Volume;

import java.util.Objects;
import java.util.Optional;

public final class SolventQuantity {

    private final String compoundCode;
    private final Mass mass;
    private final Volume volume;

    private SolventQuantity(String compoundCode, Mass mass, Volume volume) {
        if (compoundCode == null || compoundCode.isBlank()) {
            throw new SolutionException(SolutionErrorCode.COMPOUND_NOT_FOUND, "Solvent compound code must not be null or blank");
        }
        if (mass == null && volume == null) {
            throw new SolutionException(SolutionErrorCode.INVALID_QUANTITY, "Either mass or volume must be provided for solvent quantity");
        }
        this.compoundCode = compoundCode.trim();
        this.mass = mass;
        this.volume = volume;
    }

    public static SolventQuantity ofMass(String compoundCode, Mass mass) {
        return new SolventQuantity(compoundCode, Objects.requireNonNull(mass, "Mass must not be null"), null);
    }

    public static SolventQuantity ofVolume(String compoundCode, Volume volume) {
        return new SolventQuantity(compoundCode, null, Objects.requireNonNull(volume, "Volume must not be null"));
    }

    public static SolventQuantity ofBoth(String compoundCode, Mass mass, Volume volume) {
        return new SolventQuantity(compoundCode, Objects.requireNonNull(mass, "Mass must not be null"), Objects.requireNonNull(volume, "Volume must not be null"));
    }

    public String getCompoundCode() {
        return compoundCode;
    }

    public Optional<Mass> getMass() {
        return Optional.ofNullable(mass);
    }

    public Optional<Volume> getVolume() {
        return Optional.ofNullable(volume);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SolventQuantity that = (SolventQuantity) o;
        return compoundCode.equalsIgnoreCase(that.compoundCode) &&
                Objects.equals(mass, that.mass) &&
                Objects.equals(volume, that.volume);
    }

    @Override
    public int hashCode() {
        return Objects.hash(compoundCode.toUpperCase(), mass, volume);
    }

    @Override
    public String toString() {
        String qtyStr = mass != null ? mass.toString() : (volume != null ? volume.toString() : "");
        return compoundCode + ": " + qtyStr;
    }
}
