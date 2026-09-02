package com.ailab.learning.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(name = "learning_chapters")
public class LearningChapterEntity {

    @Id
    @Column(name = "id", length = 64)
    private String id;

    @Column(name = "track_id", length = 64, nullable = false)
    private String trackId;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 1;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "level_ids", columnDefinition = "jsonb", nullable = false)
    private String levelIdsJson = "[]";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "translations", columnDefinition = "jsonb", nullable = false)
    private String translationsJson = "{}";

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private LearningStatus status = LearningStatus.DRAFT;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public LearningChapterEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTrackId() { return trackId; }
    public void setTrackId(String trackId) { this.trackId = trackId; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }

    public String getLevelIdsJson() { return levelIdsJson; }
    public void setLevelIdsJson(String levelIdsJson) { this.levelIdsJson = levelIdsJson; }

    public String getTranslationsJson() { return translationsJson; }
    public void setTranslationsJson(String translationsJson) { this.translationsJson = translationsJson; }

    public LearningStatus getStatus() { return status; }
    public void setStatus(LearningStatus status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
