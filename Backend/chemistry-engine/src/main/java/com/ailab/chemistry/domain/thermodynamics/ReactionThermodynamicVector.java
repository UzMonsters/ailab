package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.equation.RationalNumber;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record ReactionThermodynamicVector(List<ReactionThermodynamicVectorTerm> terms) {

    public ReactionThermodynamicVector {
        terms = canonicalize(terms);
    }

    public static ReactionThermodynamicVector of(List<ReactionThermodynamicVectorTerm> terms) {
        return new ReactionThermodynamicVector(terms);
    }

    public static ReactionThermodynamicVector parse(String encoded) {
        if (encoded == null || encoded.isBlank()) {
            return new ReactionThermodynamicVector(List.of());
        }
        List<ReactionThermodynamicVectorTerm> parsed = new ArrayList<>();
        for (String part : encoded.split(";")) {
            String[] keyAndCoefficient = part.split(":");
            String[] compoundAndState = keyAndCoefficient[0].split("\\|");
            parsed.add(new ReactionThermodynamicVectorTerm(
                    compoundAndState[0],
                    MatterState.valueOf(compoundAndState[1]),
                    parseRational(keyAndCoefficient[1])));
        }
        return new ReactionThermodynamicVector(parsed);
    }

    public ReactionThermodynamicVector add(ReactionThermodynamicVector other) {
        List<ReactionThermodynamicVectorTerm> combined = new ArrayList<>(terms);
        combined.addAll(other.terms());
        return new ReactionThermodynamicVector(combined);
    }

    public ReactionThermodynamicVector scale(RationalNumber multiplier) {
        return new ReactionThermodynamicVector(terms.stream()
                .map(term -> new ReactionThermodynamicVectorTerm(term.compoundCode(), term.state(), term.coefficient().multiply(multiplier)))
                .toList());
    }

    public List<String> keys() {
        return terms.stream().map(ReactionThermodynamicVectorTerm::key).toList();
    }

    public Map<String, RationalNumber> asMap() {
        LinkedHashMap<String, RationalNumber> map = new LinkedHashMap<>();
        for (ReactionThermodynamicVectorTerm term : terms) {
            map.put(term.key(), term.coefficient());
        }
        return Map.copyOf(map);
    }

    public boolean hasOpposingUncancelledStates() {
        Map<String, List<ReactionThermodynamicVectorTerm>> byCompound = new LinkedHashMap<>();
        for (ReactionThermodynamicVectorTerm term : terms) {
            byCompound.computeIfAbsent(term.compoundCode(), ignored -> new ArrayList<>()).add(term);
        }
        for (List<ReactionThermodynamicVectorTerm> compoundTerms : byCompound.values()) {
            boolean hasPositive = compoundTerms.stream().anyMatch(term -> term.coefficient().compareTo(RationalNumber.ZERO) > 0);
            boolean hasNegative = compoundTerms.stream().anyMatch(term -> term.coefficient().compareTo(RationalNumber.ZERO) < 0);
            long stateCount = compoundTerms.stream().map(ReactionThermodynamicVectorTerm::state).distinct().count();
            if (hasPositive && hasNegative && stateCount > 1) {
                return true;
            }
        }
        return false;
    }

    private static List<ReactionThermodynamicVectorTerm> canonicalize(List<ReactionThermodynamicVectorTerm> input) {
        Objects.requireNonNull(input, "terms must not be null");
        Map<String, ReactionThermodynamicVectorTerm> byKey = new LinkedHashMap<>();
        for (ReactionThermodynamicVectorTerm term : input) {
            byKey.merge(term.key(), term, (left, right) -> new ReactionThermodynamicVectorTerm(
                    left.compoundCode(), left.state(), left.coefficient().add(right.coefficient())));
        }
        return byKey.values().stream()
                .filter(term -> !term.coefficient().isZero())
                .sorted(Comparator.comparing(ReactionThermodynamicVectorTerm::key))
                .toList();
    }

    private static RationalNumber parseRational(String value) {
        if (value.contains("/")) {
            String[] parts = value.split("/");
            return RationalNumber.of(new BigInteger(parts[0]), new BigInteger(parts[1]));
        }
        return RationalNumber.of(new BigInteger(value));
    }
}
