package com.ailab.chemistry.domain.hazard;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class HazardClassification {
    private final UUID id;
    private final HazardClassificationSystem classificationSystem;
    private final String revision;
    private final HazardJurisdiction jurisdiction;
    private final HazardScope scope;
    private final String hazardClassCode;
    private final String hazardCategoryCode;
    private final List<GhsPictogram> pictograms;
    private final SignalWord signalWord;
    private final List<HazardStatement> hazardStatements;
    private final List<PrecautionaryStatement> precautionaryStatements;
    private final HazardEvidenceStatus evidenceStatus;
    private final HazardSourceDocument sourceDocument;

    public HazardClassification(UUID id, HazardClassificationSystem classificationSystem, String revision, HazardJurisdiction jurisdiction, HazardScope scope, String hazardClassCode, String hazardCategoryCode, List<GhsPictogram> pictograms, SignalWord signalWord, List<HazardStatement> hazardStatements, List<PrecautionaryStatement> precautionaryStatements, HazardEvidenceStatus evidenceStatus, HazardSourceDocument sourceDocument) {
        Objects.requireNonNull(id, "ID cannot be null");
        Objects.requireNonNull(classificationSystem, "Classification system cannot be null");
        Objects.requireNonNull(hazardClassCode, "Hazard class code cannot be null");
        Objects.requireNonNull(hazardCategoryCode, "Hazard category code cannot be null");
        Objects.requireNonNull(sourceDocument, "Source document cannot be null");

        this.id = id;
        this.classificationSystem = classificationSystem;
        this.revision = revision != null ? revision : "UN-GHS-REV11-2025";
        this.jurisdiction = jurisdiction != null ? jurisdiction : HazardJurisdiction.INTERNATIONAL_REFERENCE;
        this.scope = scope;
        this.hazardClassCode = hazardClassCode;
        this.hazardCategoryCode = hazardCategoryCode;
        this.pictograms = pictograms != null ? List.copyOf(pictograms) : List.of();
        this.signalWord = signalWord != null ? signalWord : SignalWord.NONE;
        this.hazardStatements = hazardStatements != null ? List.copyOf(hazardStatements) : List.of();
        this.precautionaryStatements = precautionaryStatements != null ? List.copyOf(precautionaryStatements) : List.of();
        this.evidenceStatus = evidenceStatus != null ? evidenceStatus : HazardEvidenceStatus.AUTHORITATIVE_CLASSIFICATION;
        this.sourceDocument = sourceDocument;
    }

    public UUID getId() { return id; }
    public HazardClassificationSystem getClassificationSystem() { return classificationSystem; }
    public String getRevision() { return revision; }
    public HazardJurisdiction getJurisdiction() { return jurisdiction; }
    public HazardScope getScope() { return scope; }
    public String getHazardClassCode() { return hazardClassCode; }
    public String getHazardCategoryCode() { return hazardCategoryCode; }
    public List<GhsPictogram> getPictograms() { return pictograms; }
    public SignalWord getSignalWord() { return signalWord; }
    public List<HazardStatement> getHazardStatements() { return hazardStatements; }
    public List<PrecautionaryStatement> getPrecautionaryStatements() { return precautionaryStatements; }
    public HazardEvidenceStatus getEvidenceStatus() { return evidenceStatus; }
    public HazardSourceDocument getSourceDocument() { return sourceDocument; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HazardClassification that = (HazardClassification) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
