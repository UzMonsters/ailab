package com.ailab.learning.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(name = "learning_levels")
public class LearningLevelEntity {

    @Id
    @Column(name = "id", length = 64)
    private String id;

    @Column(name = "track_id", length = 64, nullable = false)
    private String trackId;

    @Column(name = "level_number", nullable = false)
    private int levelNumber;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 1;

    @Column(name = "difficulty", length = 30, nullable = false)
    private String difficulty = "BEGINNER";

    @Column(name = "estimated_minutes", nullable = false)
    private int estimatedMinutes = 10;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private LearningStatus status = LearningStatus.DRAFT;

    @Column(name = "draft_version", nullable = false)
    private long draftVersion = 1;

    @Column(name = "published_version")
    private Long publishedVersion;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "prerequisites", columnDefinition = "jsonb", nullable = false)
    private String prerequisitesJson = "[]";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "requirements", columnDefinition = "jsonb", nullable = false)
    private String requirementsJson = "{}";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "available_equipment", columnDefinition = "jsonb", nullable = false)
    private String availableEquipmentJson = "[]";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "available_materials", columnDefinition = "jsonb", nullable = false)
    private String availableMaterialsJson = "[]";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "scenario", columnDefinition = "jsonb", nullable = false)
    private String scenarioJson = "{}";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "steps", columnDefinition = "jsonb", nullable = false)
    private String stepsJson = "[]";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "rewards", columnDefinition = "jsonb", nullable = false)
    private String rewardsJson = "{}";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "translations", columnDefinition = "jsonb", nullable = false)
    private String translationsJson = "{}";

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public LearningLevelEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTrackId() { return trackId; }
    public void setTrackId(String trackId) { this.trackId = trackId; }

    public int getLevelNumber() { return levelNumber; }
    public void setLevelNumber(int levelNumber) { this.levelNumber = levelNumber; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public int getEstimatedMinutes() { return estimatedMinutes; }
    public void setEstimatedMinutes(int estimatedMinutes) { this.estimatedMinutes = estimatedMinutes; }

    public LearningStatus getStatus() { return status; }
    public void setStatus(LearningStatus status) { this.status = status; }

    public long getDraftVersion() { return draftVersion; }
    public void setDraftVersion(long draftVersion) { this.draftVersion = draftVersion; }

    public Long getPublishedVersion() { return publishedVersion; }
    public void setPublishedVersion(Long publishedVersion) { this.publishedVersion = publishedVersion; }

    public String getPrerequisitesJson() { return prerequisitesJson; }
    public void setPrerequisitesJson(String prerequisitesJson) { this.prerequisitesJson = prerequisitesJson; }

    public String getRequirementsJson() { return requirementsJson; }
    public void setRequirementsJson(String requirementsJson) { this.requirementsJson = requirementsJson; }

    public String getAvailableEquipmentJson() { return availableEquipmentJson; }
    public void setAvailableEquipmentJson(String availableEquipmentJson) { this.availableEquipmentJson = availableEquipmentJson; }

    public String getAvailableMaterialsJson() { return availableMaterialsJson; }
    public void setAvailableMaterialsJson(String availableMaterialsJson) { this.availableMaterialsJson = availableMaterialsJson; }

    public String getScenarioJson() { return scenarioJson; }
    public void setScenarioJson(String scenarioJson) { this.scenarioJson = scenarioJson; }

    public String getStepsJson() { return stepsJson; }
    public void setStepsJson(String stepsJson) { this.stepsJson = stepsJson; }

    public String getRewardsJson() { return rewardsJson; }
    public void setRewardsJson(String rewardsJson) { this.rewardsJson = rewardsJson; }

    public String getTranslationsJson() { return translationsJson; }
    public void setTranslationsJson(String translationsJson) { this.translationsJson = translationsJson; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
