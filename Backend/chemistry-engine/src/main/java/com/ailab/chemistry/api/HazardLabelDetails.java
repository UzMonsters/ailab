package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.hazard.SignalWord;

import java.util.List;

public class HazardLabelDetails {
    private final String compoundCode;
    private final String classificationSystem;
    private final String revision;
    private final String jurisdiction;
    private final SignalWord signalWord;
    private final List<String> pictogramCodes;
    private final List<String> hazardStatements;
    private final List<String> precautionaryStatements;
    private final String sourceDocumentId;

    public HazardLabelDetails(String compoundCode, String classificationSystem, String revision, String jurisdiction, SignalWord signalWord, List<String> pictogramCodes, List<String> hazardStatements, List<String> precautionaryStatements, String sourceDocumentId) {
        this.compoundCode = compoundCode;
        this.classificationSystem = classificationSystem;
        this.revision = revision;
        this.jurisdiction = jurisdiction;
        this.signalWord = signalWord;
        this.pictogramCodes = List.copyOf(pictogramCodes);
        this.hazardStatements = List.copyOf(hazardStatements);
        this.precautionaryStatements = List.copyOf(precautionaryStatements);
        this.sourceDocumentId = sourceDocumentId;
    }

    public String getCompoundCode() { return compoundCode; }
    public String getClassificationSystem() { return classificationSystem; }
    public String getRevision() { return revision; }
    public String getJurisdiction() { return jurisdiction; }
    public SignalWord getSignalWord() { return signalWord; }
    public List<String> getPictogramCodes() { return pictogramCodes; }
    public List<String> getHazardStatements() { return hazardStatements; }
    public List<String> getPrecautionaryStatements() { return precautionaryStatements; }
    public String getSourceDocumentId() { return sourceDocumentId; }
}
