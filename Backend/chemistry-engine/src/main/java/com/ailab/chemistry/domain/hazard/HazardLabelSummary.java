package com.ailab.chemistry.domain.hazard;

import java.util.List;
import java.util.Objects;

public final class HazardLabelSummary {
    private final String compoundCode;
    private final HazardClassificationSystem classificationSystem;
    private final String revision;
    private final HazardJurisdiction jurisdiction;
    private final HazardScope scope;
    private final SignalWord signalWord;
    private final List<GhsPictogram> pictograms;
    private final List<HazardStatement> hazardStatements;
    private final List<PrecautionaryStatement> precautionaryStatements;
    private final String sourceDocumentId;

    public HazardLabelSummary(String compoundCode, HazardClassificationSystem classificationSystem, String revision, HazardJurisdiction jurisdiction, HazardScope scope, SignalWord signalWord, List<GhsPictogram> pictograms, List<HazardStatement> hazardStatements, List<PrecautionaryStatement> precautionaryStatements, String sourceDocumentId) {
        Objects.requireNonNull(compoundCode, "Compound code cannot be null");
        Objects.requireNonNull(classificationSystem, "Classification system cannot be null");
        Objects.requireNonNull(sourceDocumentId, "Source document ID cannot be null");
        this.compoundCode = compoundCode;
        this.classificationSystem = classificationSystem;
        this.revision = revision != null ? revision : "UN-GHS-REV11-2025";
        this.jurisdiction = jurisdiction != null ? jurisdiction : HazardJurisdiction.INTERNATIONAL_REFERENCE;
        this.scope = scope;
        this.signalWord = signalWord != null ? signalWord : SignalWord.NONE;
        this.pictograms = pictograms != null ? List.copyOf(pictograms) : List.of();
        this.hazardStatements = hazardStatements != null ? List.copyOf(hazardStatements) : List.of();
        this.precautionaryStatements = precautionaryStatements != null ? List.copyOf(precautionaryStatements) : List.of();
        this.sourceDocumentId = sourceDocumentId;
    }

    public String getCompoundCode() { return compoundCode; }
    public HazardClassificationSystem getClassificationSystem() { return classificationSystem; }
    public String getRevision() { return revision; }
    public HazardJurisdiction getJurisdiction() { return jurisdiction; }
    public HazardScope getScope() { return scope; }
    public SignalWord getSignalWord() { return signalWord; }
    public List<GhsPictogram> getPictograms() { return pictograms; }
    public List<HazardStatement> getHazardStatements() { return hazardStatements; }
    public List<PrecautionaryStatement> getPrecautionaryStatements() { return precautionaryStatements; }
    public String getSourceDocumentId() { return sourceDocumentId; }
}
