package com.ailab.chemistry.domain.classification;

import java.util.Objects;

public final class ClassificationProvenance {
    private final String sourceIdentifier;
    private final String sourceTitle;
    private final String publisher;
    private final String sourceVersion;
    private final String publicationDate;
    private final String note;

    public ClassificationProvenance(String sourceIdentifier, String sourceTitle, String publisher, String sourceVersion, String publicationDate, String note) {
        this.sourceIdentifier = sourceIdentifier != null ? sourceIdentifier.trim() : "INTERNAL";
        this.sourceTitle = sourceTitle != null ? sourceTitle.trim() : "Internal Classification Rule";
        this.publisher = publisher != null ? publisher.trim() : "AI Laboratory Chemistry Engine";
        this.sourceVersion = sourceVersion != null ? sourceVersion.trim() : "v1.0.0";
        this.publicationDate = publicationDate != null ? publicationDate.trim() : "2026-08-05";
        this.note = note != null ? note.trim() : null;
    }

    public static ClassificationProvenance curatedReference(String sourceIdentifier, String sourceTitle, String publisher, String sourceVersion, String publicationDate, String note) {
        if (sourceIdentifier == null || sourceIdentifier.isBlank() || sourceTitle == null || sourceTitle.isBlank()) {
            throw new ClassificationException(ClassificationErrorCode.CLASSIFICATION_PROVENANCE_MISSING, "Curated provenance requires valid source identifier and title");
        }
        return new ClassificationProvenance(sourceIdentifier, sourceTitle, publisher, sourceVersion, publicationDate, note);
    }

    public static ClassificationProvenance derivedRule(ClassificationRuleCode ruleCode) {
        if (ruleCode == null) {
            throw new ClassificationException(ClassificationErrorCode.CLASSIFICATION_RULE_NOT_FOUND, "Rule code cannot be null for derived provenance");
        }
        return new ClassificationProvenance(
                ruleCode.getCode(),
                ruleCode.getDescription(),
                "AI Laboratory Rule Engine",
                "v1.0.0",
                "2026-08-05",
                "Rule-derived classification"
        );
    }

    public String getSourceIdentifier() { return sourceIdentifier; }
    public String getSourceTitle() { return sourceTitle; }
    public String getPublisher() { return publisher; }
    public String getSourceVersion() { return sourceVersion; }
    public String getPublicationDate() { return publicationDate; }
    public String getNote() { return note; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassificationProvenance that = (ClassificationProvenance) o;
        return Objects.equals(sourceIdentifier, that.sourceIdentifier) && Objects.equals(sourceTitle, that.sourceTitle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceIdentifier, sourceTitle);
    }
}
