package com.ailab.chemistry.domain.hazard;

import com.ailab.chemistry.domain.compound.CompoundId;

import java.util.*;

public final class HazardProfile {
    private final CompoundId compoundId;
    private final String datasetVersion;
    private final Map<String, HazardAvailability> availabilityMap;
    private final List<HazardClassification> classifications;
    private final List<HazardLabelSummary> labelSummaries;
    private final Set<HazardSummaryFlag> summaryFlags;
    private final List<SupplementalLaboratoryHazard> supplementalHazards;
    private final List<SafetyInstruction> safetyInstructions;
    private final List<PersonalProtectiveEquipmentRecommendation> ppeRecommendations;
    private final List<HazardSourceDocument> sourceDocuments;
    private final HazardProvenance provenance;

    public HazardProfile(CompoundId compoundId, String datasetVersion, Map<String, HazardAvailability> availabilityMap, List<HazardClassification> classifications, List<HazardLabelSummary> labelSummaries, Set<HazardSummaryFlag> summaryFlags, List<SupplementalLaboratoryHazard> supplementalHazards, List<SafetyInstruction> safetyInstructions, List<PersonalProtectiveEquipmentRecommendation> ppeRecommendations, List<HazardSourceDocument> sourceDocuments, HazardProvenance provenance) {
        Objects.requireNonNull(compoundId, "Compound ID cannot be null");
        Objects.requireNonNull(datasetVersion, "Dataset version cannot be null");
        Objects.requireNonNull(provenance, "Provenance cannot be null");

        this.compoundId = compoundId;
        this.datasetVersion = datasetVersion;
        this.availabilityMap = availabilityMap != null ? Map.copyOf(availabilityMap) : Map.of();
        this.classifications = classifications != null ? List.copyOf(classifications) : List.of();
        this.labelSummaries = labelSummaries != null ? List.copyOf(labelSummaries) : List.of();
        this.summaryFlags = summaryFlags != null ? Set.copyOf(summaryFlags) : Set.of();
        this.supplementalHazards = supplementalHazards != null ? List.copyOf(supplementalHazards) : List.of();
        this.safetyInstructions = safetyInstructions != null ? List.copyOf(safetyInstructions) : List.of();
        this.ppeRecommendations = ppeRecommendations != null ? List.copyOf(ppeRecommendations) : List.of();
        this.sourceDocuments = sourceDocuments != null ? List.copyOf(sourceDocuments) : List.of();
        this.provenance = provenance;
    }

    public CompoundId getCompoundId() { return compoundId; }
    public String getDatasetVersion() { return datasetVersion; }
    public Map<String, HazardAvailability> getAvailabilityMap() { return availabilityMap; }
    public List<HazardClassification> getClassifications() { return classifications; }
    public List<HazardLabelSummary> getLabelSummaries() { return labelSummaries; }
    public Set<HazardSummaryFlag> getSummaryFlags() { return summaryFlags; }
    public List<SupplementalLaboratoryHazard> getSupplementalHazards() { return supplementalHazards; }
    public List<SafetyInstruction> getSafetyInstructions() { return safetyInstructions; }
    public List<PersonalProtectiveEquipmentRecommendation> getPpeRecommendations() { return ppeRecommendations; }
    public List<HazardSourceDocument> getSourceDocuments() { return sourceDocuments; }
    public HazardProvenance getProvenance() { return provenance; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HazardProfile that = (HazardProfile) o;
        return compoundId.equals(that.compoundId) && datasetVersion.equals(that.datasetVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(compoundId, datasetVersion);
    }
}
