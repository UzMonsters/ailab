package com.ailab.chemistry.domain.acidbase;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public record SpeciesDistribution(List<DistributionFraction> fractions) {
    public SpeciesDistribution {
        fractions = List.copyOf(Objects.requireNonNull(fractions, "fractions must not be null")).stream()
                .sorted(Comparator.comparingInt(DistributionFraction::protonsRemaining).reversed())
                .toList();
    }

    public List<DistributionFraction> getFractions() {
        return fractions;
    }

    public String dominantSpeciesCode() {
        return fractions.stream()
                .max((a, b) -> a.fraction().compareTo(b.fraction()))
                .orElseThrow()
                .speciesCode();
    }
}
