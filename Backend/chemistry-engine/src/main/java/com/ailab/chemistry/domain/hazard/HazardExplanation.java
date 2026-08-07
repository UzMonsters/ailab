package com.ailab.chemistry.domain.hazard;

import java.util.List;
import java.util.Objects;

public final class HazardExplanation {
    private final String compoundCode;
    private final HazardSummaryFlag summaryFlag;
    private final List<HazardClassification> detailedClassifications;
    private final List<SupplementalLaboratoryHazard> supplementalHazards;
    private final String explanationText;

    public HazardExplanation(String compoundCode, HazardSummaryFlag summaryFlag, List<HazardClassification> detailedClassifications, List<SupplementalLaboratoryHazard> supplementalHazards, String explanationText) {
        Objects.requireNonNull(compoundCode, "Compound code cannot be null");
        Objects.requireNonNull(summaryFlag, "Summary flag cannot be null");
        Objects.requireNonNull(explanationText, "Explanation text cannot be null");
        this.compoundCode = compoundCode;
        this.summaryFlag = summaryFlag;
        this.detailedClassifications = detailedClassifications != null ? List.copyOf(detailedClassifications) : List.of();
        this.supplementalHazards = supplementalHazards != null ? List.copyOf(supplementalHazards) : List.of();
        this.explanationText = explanationText;
    }

    public String getCompoundCode() { return compoundCode; }
    public HazardSummaryFlag getSummaryFlag() { return summaryFlag; }
    public List<HazardClassification> getDetailedClassifications() { return detailedClassifications; }
    public List<SupplementalLaboratoryHazard> getSupplementalHazards() { return supplementalHazards; }
    public String getExplanationText() { return explanationText; }
}
