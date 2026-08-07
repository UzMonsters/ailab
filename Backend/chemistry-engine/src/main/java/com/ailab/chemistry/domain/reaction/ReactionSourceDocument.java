package com.ailab.chemistry.domain.reaction;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class ReactionSourceDocument {
    private final String sourceId;
    private final SourceDocumentType sourceType;
    private final String issuer;
    private final String title;
    private final String edition;
    private final String publicationDate;
    private final String accessDate;
    private final String coverage;
    private final List<String> fieldsSupplied;
    private final String language;
    private final String sourceReference;
    private final String licensingNote;

    public ReactionSourceDocument(String sourceId, SourceDocumentType sourceType, String issuer, String title,
                                  String edition, String publicationDate, String accessDate, String coverage,
                                  List<String> fieldsSupplied, String language, String sourceReference, String licensingNote) {
        Objects.requireNonNull(sourceId, "Source ID must not be null");
        Objects.requireNonNull(sourceType, "Source type must not be null");
        Objects.requireNonNull(title, "Title must not be null");

        this.sourceId = sourceId.trim();
        this.sourceType = sourceType;
        this.issuer = issuer != null ? issuer.trim() : "";
        this.title = title.trim();
        this.edition = edition != null ? edition.trim() : "";
        this.publicationDate = publicationDate != null ? publicationDate.trim() : "";
        this.accessDate = accessDate != null ? accessDate.trim() : "";
        this.coverage = coverage != null ? coverage.trim() : "";
        this.fieldsSupplied = fieldsSupplied != null ? List.copyOf(fieldsSupplied) : Collections.emptyList();
        this.language = language != null ? language.trim() : "en";
        this.sourceReference = sourceReference != null ? sourceReference.trim() : "";
        this.licensingNote = licensingNote != null ? licensingNote.trim() : "";
    }

    public String getSourceId() {
        return sourceId;
    }

    public SourceDocumentType getSourceType() {
        return sourceType;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getTitle() {
        return title;
    }

    public String getEdition() {
        return edition;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public String getAccessDate() {
        return accessDate;
    }

    public String getCoverage() {
        return coverage;
    }

    public List<String> getFieldsSupplied() {
        return fieldsSupplied;
    }

    public String getLanguage() {
        return language;
    }

    public String getSourceReference() {
        return sourceReference;
    }

    public String getLicensingNote() {
        return licensingNote;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReactionSourceDocument that = (ReactionSourceDocument) o;
        return Objects.equals(sourceId, that.sourceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceId);
    }
}
