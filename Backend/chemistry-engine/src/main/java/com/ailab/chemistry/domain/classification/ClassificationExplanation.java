package com.ailab.chemistry.domain.classification;

public final class ClassificationExplanation {
    private final String compoundCode;
    private final String primaryName;
    private final ClassificationCode classificationCode;
    private final String classificationName;
    private final ClassificationDimension dimension;
    private final ClassificationBasis basis;
    private final ClassificationEvidenceStatus evidenceStatus;
    private final String ruleCode;
    private final String sourceIdentifier;
    private final String sourceTitle;
    private final String explanationNote;

    public ClassificationExplanation(String compoundCode, String primaryName, ClassificationCode classificationCode, String classificationName, ClassificationDimension dimension, ClassificationBasis basis, ClassificationEvidenceStatus evidenceStatus, String ruleCode, String sourceIdentifier, String sourceTitle, String explanationNote) {
        this.compoundCode = compoundCode;
        this.primaryName = primaryName;
        this.classificationCode = classificationCode;
        this.classificationName = classificationName;
        this.dimension = dimension;
        this.basis = basis;
        this.evidenceStatus = evidenceStatus;
        this.ruleCode = ruleCode;
        this.sourceIdentifier = sourceIdentifier;
        this.sourceTitle = sourceTitle;
        this.explanationNote = explanationNote;
    }

    public String getCompoundCode() { return compoundCode; }
    public String getPrimaryName() { return primaryName; }
    public ClassificationCode getClassificationCode() { return classificationCode; }
    public String getClassificationName() { return classificationName; }
    public ClassificationDimension getDimension() { return dimension; }
    public ClassificationBasis getBasis() { return basis; }
    public ClassificationEvidenceStatus getEvidenceStatus() { return evidenceStatus; }
    public String getRuleCode() { return ruleCode; }
    public String getSourceIdentifier() { return sourceIdentifier; }
    public String getSourceTitle() { return sourceTitle; }
    public String getExplanationNote() { return explanationNote; }
}
