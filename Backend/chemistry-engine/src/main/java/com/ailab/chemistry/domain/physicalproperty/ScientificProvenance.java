package com.ailab.chemistry.domain.physicalproperty;

import java.util.Objects;

public final class ScientificProvenance {
    private final String sourceIdentifier;
    private final String sourceTitle;
    private final String publisher;
    private final String sourceVersion;
    private final String publicationDate;
    private final String note;

    public ScientificProvenance(String sourceIdentifier, String sourceTitle, String publisher, String sourceVersion, String publicationDate, String note) {
        if (sourceIdentifier == null || sourceIdentifier.isBlank() || sourceTitle == null || sourceTitle.isBlank()) {
            throw new IllegalArgumentException("Provenance requires valid source identifier and title");
        }
        this.sourceIdentifier = sourceIdentifier.trim();
        this.sourceTitle = sourceTitle.trim();
        this.publisher = publisher != null ? publisher.trim() : "AI Laboratory Scientific Engine";
        this.sourceVersion = sourceVersion != null ? sourceVersion.trim() : "v1.0.0";
        this.publicationDate = publicationDate != null ? publicationDate.trim() : "2026-08-05";
        this.note = note != null ? note.trim() : null;
    }

    public static ScientificProvenance crcHandbook104th(String note) {
        return new ScientificProvenance(
                "CRC-HANDBOOK-104",
                "CRC Handbook of Chemistry and Physics, 104th Edition",
                "CRC Press / Taylor & Francis Group",
                "v1.0.0",
                "2026-08-05",
                note
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
        ScientificProvenance that = (ScientificProvenance) o;
        return Objects.equals(sourceIdentifier, that.sourceIdentifier) && Objects.equals(sourceTitle, that.sourceTitle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceIdentifier, sourceTitle);
    }
}
