package com.ailab.chemistry.infrastructure.persistence.element;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "periodic_table_catalog_versions", schema = "chemistry")
public class CatalogVersionEntity {
    @Id
    @Column(length = 50, nullable = false)
    private String id;

    @Column(length = 50, nullable = false, unique = true)
    private String version;

    @Column(name = "generated_at")
    private Instant generatedAt;

    @Column(name = "data_sources", nullable = false)
    private String dataSources;

    @Column(name = "reference_conditions", nullable = false)
    private String referenceConditions;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public Instant getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(Instant generatedAt) { this.generatedAt = generatedAt; }
    public String getDataSources() { return dataSources; }
    public void setDataSources(String dataSources) { this.dataSources = dataSources; }
    public String getReferenceConditions() { return referenceConditions; }
    public void setReferenceConditions(String referenceConditions) { this.referenceConditions = referenceConditions; }
}
