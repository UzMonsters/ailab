package com.ailab.chemistry.domain.classification;

import java.util.Objects;

public final class ClassificationAssignment {
    private final ClassificationCode code;
    private final ClassificationDimension dimension;
    private final ClassificationBasis basis;
    private final ClassificationEvidenceStatus evidenceStatus;
    private final ClassificationRuleCode ruleCode;
    private final ClassificationProvenance provenance;
    private final String explanatoryNote;

    public ClassificationAssignment(ClassificationCode code, ClassificationDimension dimension, ClassificationBasis basis, ClassificationEvidenceStatus evidenceStatus, ClassificationRuleCode ruleCode, ClassificationProvenance provenance, String explanatoryNote) {
        if (code == null) {
            throw new ClassificationException(ClassificationErrorCode.INVALID_CLASSIFICATION_CODE, "Assignment classification code cannot be null");
        }
        if (dimension == null) {
            throw new ClassificationException(ClassificationErrorCode.INVALID_CLASSIFICATION_CODE, "Assignment dimension cannot be null");
        }
        if (basis == null) {
            throw new ClassificationException(ClassificationErrorCode.INVALID_CLASSIFICATION_CODE, "Assignment basis cannot be null");
        }
        if (evidenceStatus == null) {
            throw new ClassificationException(ClassificationErrorCode.INVALID_CLASSIFICATION_CODE, "Assignment evidence status cannot be null");
        }

        if (basis == ClassificationBasis.SAFE_RULE_DERIVED && ruleCode == null) {
            throw new ClassificationException(ClassificationErrorCode.CLASSIFICATION_RULE_NOT_FOUND, "SAFE_RULE_DERIVED basis requires a valid ruleCode");
        }

        if (basis == ClassificationBasis.CURATED_REFERENCE && (provenance == null || provenance.getSourceIdentifier().equals("INTERNAL"))) {
            throw new ClassificationException(ClassificationErrorCode.CLASSIFICATION_PROVENANCE_MISSING, "CURATED_REFERENCE basis requires valid source provenance");
        }

        this.code = code;
        this.dimension = dimension;
        this.basis = basis;
        this.evidenceStatus = evidenceStatus;
        this.ruleCode = ruleCode;
        this.provenance = provenance != null ? provenance : (ruleCode != null ? ClassificationProvenance.derivedRule(ruleCode) : new ClassificationProvenance(null, null, null, null, null, null));
        this.explanatoryNote = explanatoryNote != null ? explanatoryNote.trim() : null;
    }

    public static ClassificationAssignment curated(ClassificationCode code, ClassificationDimension dimension, ClassificationProvenance provenance, String note) {
        return new ClassificationAssignment(code, dimension, ClassificationBasis.CURATED_REFERENCE, ClassificationEvidenceStatus.CURATED, null, provenance, note);
    }

    public static ClassificationAssignment derived(ClassificationCode code, ClassificationDimension dimension, ClassificationRuleCode ruleCode, String note) {
        return new ClassificationAssignment(code, dimension, ClassificationBasis.SAFE_RULE_DERIVED, ClassificationEvidenceStatus.DERIVED, ruleCode, ClassificationProvenance.derivedRule(ruleCode), note);
    }

    public ClassificationCode getCode() { return code; }
    public ClassificationDimension getDimension() { return dimension; }
    public ClassificationBasis getBasis() { return basis; }
    public ClassificationEvidenceStatus getEvidenceStatus() { return evidenceStatus; }
    public ClassificationRuleCode getRuleCode() { return ruleCode; }
    public ClassificationProvenance getProvenance() { return provenance; }
    public String getExplanatoryNote() { return explanatoryNote; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassificationAssignment that = (ClassificationAssignment) o;
        return Objects.equals(code, that.code) && Objects.equals(dimension, that.dimension);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, dimension);
    }

    @Override
    public String toString() {
        return code.getValue() + " [" + dimension + ", " + basis + "]";
    }
}
