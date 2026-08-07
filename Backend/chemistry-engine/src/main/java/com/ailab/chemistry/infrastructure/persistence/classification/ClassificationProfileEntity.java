package com.ailab.chemistry.infrastructure.persistence.classification;

import com.ailab.chemistry.infrastructure.persistence.compound.CompoundEntity;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "compound_classification_profiles", schema = "chemistry")
public class ClassificationProfileEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compound_id", nullable = false)
    private CompoundEntity compound;

    @Column(name = "taxonomy_version_id", nullable = false)
    private String taxonomyVersionId;

    @Column(name = "created_at")
    private Instant createdAt;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ClassificationAssignmentEntity> assignments = new ArrayList<>();

    public ClassificationProfileEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public CompoundEntity getCompound() { return compound; }
    public void setCompound(CompoundEntity compound) { this.compound = compound; }
    public String getTaxonomyVersionId() { return taxonomyVersionId; }
    public void setTaxonomyVersionId(String taxonomyVersionId) { this.taxonomyVersionId = taxonomyVersionId; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public List<ClassificationAssignmentEntity> getAssignments() { return assignments; }
    public void setAssignments(List<ClassificationAssignmentEntity> assignments) { this.assignments = assignments; }
}
