package com.ailab.chemistry.infrastructure.persistence.reaction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "reaction_catalog_versions", schema = "chemistry")
public class ReactionCatalogVersionEntity {

    @Id
    @Column(name = "version_code")
    private String versionCode;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "publication_date", nullable = false)
    private LocalDate publicationDate;

    public ReactionCatalogVersionEntity() {}

    public ReactionCatalogVersionEntity(String versionCode, String description, LocalDate publicationDate) {
        this.versionCode = versionCode;
        this.description = description;
        this.publicationDate = publicationDate;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }
}
