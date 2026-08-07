package com.ailab.chemistry.infrastructure.persistence.compound;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "compound_catalog_versions", schema = "chemistry")
public class CompoundCatalogVersionEntity {

    @Id
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "publication_date", nullable = false)
    private String publicationDate;

    public CompoundCatalogVersionEntity() {}

    public CompoundCatalogVersionEntity(String id, String name, String publicationDate) {
        this.id = id;
        this.name = name;
        this.publicationDate = publicationDate;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPublicationDate() { return publicationDate; }
    public void setPublicationDate(String publicationDate) { this.publicationDate = publicationDate; }
}
