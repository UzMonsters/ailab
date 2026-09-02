package com.ailab.learning.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(name = "learning_tracks")
public class LearningTrackEntity {

    @Id
    @Column(name = "id", length = 64)
    private String id;

    @Column(name = "code", length = 64, nullable = false, unique = true)
    private String code;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 1;

    @Column(name = "default_locale", length = 10, nullable = false)
    private String defaultLocale = "ru";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "translations", columnDefinition = "jsonb", nullable = false)
    private String translationsJson;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private LearningStatus status = LearningStatus.DRAFT;

    @Column(name = "draft_version", nullable = false)
    private long draftVersion = 1;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public LearningTrackEntity() {}

    public LearningTrackEntity(String id, String code, int sortOrder, String defaultLocale, String translationsJson) {
        this.id = id;
        this.code = code;
        this.sortOrder = sortOrder;
        this.defaultLocale = defaultLocale != null ? defaultLocale : "ru";
        this.translationsJson = translationsJson != null ? translationsJson : "{}";
        this.status = LearningStatus.DRAFT;
        this.draftVersion = 1;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }

    public String getDefaultLocale() { return defaultLocale; }
    public void setDefaultLocale(String defaultLocale) { this.defaultLocale = defaultLocale; }

    public String getTranslationsJson() { return translationsJson; }
    public void setTranslationsJson(String translationsJson) { this.translationsJson = translationsJson; }

    public LearningStatus getStatus() { return status; }
    public void setStatus(LearningStatus status) { this.status = status; }

    public long getDraftVersion() { return draftVersion; }
    public void setDraftVersion(long draftVersion) { this.draftVersion = draftVersion; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
