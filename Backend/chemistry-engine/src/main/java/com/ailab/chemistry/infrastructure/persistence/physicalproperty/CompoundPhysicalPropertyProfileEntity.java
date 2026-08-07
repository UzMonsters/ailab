package com.ailab.chemistry.infrastructure.persistence.physicalproperty;

import com.ailab.chemistry.infrastructure.persistence.compound.CompoundEntity;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "compound_physical_property_profiles", schema = "chemistry")
public class CompoundPhysicalPropertyProfileEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compound_id", nullable = false)
    private CompoundEntity compound;

    @Column(name = "dataset_version_id", nullable = false)
    private String datasetVersionId;

    @Column(name = "created_at")
    private Instant createdAt;

    public CompoundPhysicalPropertyProfileEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public CompoundEntity getCompound() { return compound; }
    public void setCompound(CompoundEntity compound) { this.compound = compound; }
    public String getDatasetVersionId() { return datasetVersionId; }
    public void setDatasetVersionId(String datasetVersionId) { this.datasetVersionId = datasetVersionId; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
