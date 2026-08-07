package com.ailab.chemistry.domain.reaction;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class ReactionExplanation {
    private final String reactionCode;
    private final String primaryName;
    private final String canonicalEquation;
    private final List<ReactionTypeAssignment> typeAssignments;
    private final String summaryNote;

    public ReactionExplanation(String reactionCode, String primaryName, String canonicalEquation,
                               List<ReactionTypeAssignment> typeAssignments, String summaryNote) {
        this.reactionCode = Objects.requireNonNull(reactionCode, "Reaction code must not be null");
        this.primaryName = Objects.requireNonNull(primaryName, "Primary name must not be null");
        this.canonicalEquation = Objects.requireNonNull(canonicalEquation, "Canonical equation must not be null");
        this.typeAssignments = typeAssignments != null ? List.copyOf(typeAssignments) : Collections.emptyList();
        this.summaryNote = summaryNote != null ? summaryNote : "";
    }

    public String getReactionCode() {
        return reactionCode;
    }

    public String getPrimaryName() {
        return primaryName;
    }

    public String getCanonicalEquation() {
        return canonicalEquation;
    }

    public List<ReactionTypeAssignment> getTypeAssignments() {
        return typeAssignments;
    }

    public String getSummaryNote() {
        return summaryNote;
    }
}
