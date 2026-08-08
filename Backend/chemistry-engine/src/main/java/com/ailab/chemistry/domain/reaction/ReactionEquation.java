package com.ailab.chemistry.domain.reaction;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class ReactionEquation {
    private final String originalEquation;
    private final String normalizedEquation;
    private final String canonicalBalancedEquation;
    private final String reactionSignature;

    public ReactionEquation(String originalEquation, String normalizedEquation, String canonicalBalancedEquation, String reactionSignature) {
        Objects.requireNonNull(originalEquation, "Original equation must not be null");
        Objects.requireNonNull(normalizedEquation, "Normalized equation must not be null");
        Objects.requireNonNull(canonicalBalancedEquation, "Canonical balanced equation must not be null");

        this.originalEquation = originalEquation.trim();
        this.normalizedEquation = normalizedEquation.trim();
        this.canonicalBalancedEquation = canonicalBalancedEquation.trim();
        this.reactionSignature = reactionSignature != null ? reactionSignature.trim() : generateSignatureFromEquation(canonicalBalancedEquation);
    }

    public static String generateSignature(List<ReactionTerm> terms, ReactionDirectionality directionality) {
        List<ReactionTerm> reactants = terms.stream()
                .filter(t -> t.getSide() == ReactionSide.REACTANT)
                .sorted((t1, t2) -> t1.getCompoundCode().compareTo(t2.getCompoundCode()))
                .collect(Collectors.toList());

        List<ReactionTerm> products = terms.stream()
                .filter(t -> t.getSide() == ReactionSide.PRODUCT)
                .sorted((t1, t2) -> t1.getCompoundCode().compareTo(t2.getCompoundCode()))
                .collect(Collectors.toList());

        String rStr = reactants.stream()
                .map(t -> t.getCoefficient() + "*" + t.getCompoundCode())
                .collect(Collectors.joining("+"));

        String pStr = products.stream()
                .map(t -> t.getCoefficient() + "*" + t.getCompoundCode())
                .collect(Collectors.joining("+"));

        return rStr + "->" + pStr + "[" + (directionality != null ? directionality.name() : "UNKNOWN") + "]";
    }

    private static String generateSignatureFromEquation(String eq) {
        return eq.replaceAll("\\s+", "");
    }

    public String getOriginalEquation() {
        return originalEquation;
    }

    public String getNormalizedEquation() {
        return normalizedEquation;
    }

    public String getCanonicalBalancedEquation() {
        return canonicalBalancedEquation;
    }

    public String getReactionSignature() {
        return reactionSignature;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReactionEquation that = (ReactionEquation) o;
        return Objects.equals(reactionSignature, that.reactionSignature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reactionSignature);
    }
}
