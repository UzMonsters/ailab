package com.ailab.chemistry.infrastructure.persistence.classification;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "classification_definitions", schema = "chemistry")
public class ClassificationDefinitionEntity {

    @Id
    private UUID id;

    @Column(name = "taxonomy_version_id", nullable = false)
    private String taxonomyVersionId;

    @Column(name = "dimension", nullable = false)
    private String dimension;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "parent_code")
    private String parentCode;

    public ClassificationDefinitionEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTaxonomyVersionId() { return taxonomyVersionId; }
    public void setTaxonomyVersionId(String taxonomyVersionId) { this.taxonomyVersionId = taxonomyVersionId; }
    public String getDimension() { return dimension; }
    public void setDimension(String dimension) { this.dimension = dimension; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
    public String getParentCode() { return parentCode; }
    public void setParentCode(String parentCode) { this.parentCode = parentCode; }
}
