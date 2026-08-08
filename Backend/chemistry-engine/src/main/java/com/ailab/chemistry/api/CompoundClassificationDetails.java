package com.ailab.chemistry.api;

import java.util.List;
import java.util.UUID;

public class CompoundClassificationDetails {
    private final UUID compoundId;
    private final String compoundCode;
    private final String primaryName;
    private final String taxonomyVersion;
    private final List<AssignmentDetail> assignments;

    public CompoundClassificationDetails(UUID compoundId, String compoundCode, String primaryName, String taxonomyVersion, List<AssignmentDetail> assignments) {
        this.compoundId = compoundId;
        this.compoundCode = compoundCode;
        this.primaryName = primaryName;
        this.taxonomyVersion = taxonomyVersion;
        this.assignments = List.copyOf(assignments);
    }

    public UUID getCompoundId() { return compoundId; }
    public String getCompoundCode() { return compoundCode; }
    public String getPrimaryName() { return primaryName; }
    public String getTaxonomyVersion() { return taxonomyVersion; }
    public List<AssignmentDetail> getAssignments() { return assignments; }

    public static record AssignmentDetail(
            String code,
            String dimension,
            String basis,
            String evidenceStatus,
            String ruleCode,
            String sourceIdentifier,
            String sourceTitle,
            String explanatoryNote
    ) {}
}
