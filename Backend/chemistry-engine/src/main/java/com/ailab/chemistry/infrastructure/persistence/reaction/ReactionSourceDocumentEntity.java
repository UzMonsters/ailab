package com.ailab.chemistry.infrastructure.persistence.reaction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "reaction_source_documents", schema = "chemistry")
public class ReactionSourceDocumentEntity {

    @Id
    @Column(name = "source_id")
    private String sourceId;

    @Column(name = "source_type", nullable = false)
    private String sourceType;

    @Column(name = "issuer")
    private String issuer;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "edition")
    private String edition;

    @Column(name = "publication_date")
    private String publicationDate;

    @Column(name = "access_date")
    private String accessDate;

    @Column(name = "coverage")
    private String coverage;

    @Column(name = "fields_supplied")
    private String fieldsSupplied;

    @Column(name = "language")
    private String language;

    @Column(name = "source_reference")
    private String sourceReference;

    @Column(name = "licensing_note")
    private String licensingNote;

    public ReactionSourceDocumentEntity() {}

    public String getSourceId() {
        return sourceId;
    }

    public String getSourceType() {
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

    public String getFieldsSupplied() {
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
}
