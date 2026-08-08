package com.ailab.chemistry.infrastructure.persistence.physicalproperty;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "compound_physical_property_dataset_versions", schema = "chemistry")
public class PhysicalPropertyDatasetVersionEntity {

    @Id
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "publication_date", nullable = false)
    private String publicationDate;

    @Column(name = "created_at")
    private Instant createdAt;

    public PhysicalPropertyDatasetVersionEntity() {}

    public PhysicalPropertyDatasetVersionEntity(String id, String name, String publicationDate) {
        this.id = id;
        this.name = name;
        this.publicationDate = publicationDate;
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPublicationDate() { return publicationDate; }
    public void setPublicationDate(String publicationDate) { this.publicationDate = publicationDate; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
