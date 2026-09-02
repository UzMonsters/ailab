package com.ailab.learning.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(name = "learning_user_progress")
public class LearningUserProgressEntity {

    @Id
    @Column(name = "id", length = 128)
    private String id;

    @Column(name = "user_id", length = 64, nullable = false)
    private String userId;

    @Column(name = "track_id", length = 64, nullable = false)
    private String trackId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "completed_level_ids", columnDefinition = "jsonb", nullable = false)
    private String completedLevelIdsJson = "[]";

    @Column(name = "current_level_id", length = 64)
    private String currentLevelId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "badges", columnDefinition = "jsonb", nullable = false)
    private String badgesJson = "[]";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "unlocked_equipment", columnDefinition = "jsonb", nullable = false)
    private String unlockedEquipmentJson = "[]";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "unlocked_materials", columnDefinition = "jsonb", nullable = false)
    private String unlockedMaterialsJson = "[]";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "unlocked_book_chapters", columnDefinition = "jsonb", nullable = false)
    private String unlockedBookChaptersJson = "[]";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "stats", columnDefinition = "jsonb", nullable = false)
    private String statsJson = "{}";

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public LearningUserProgressEntity() {}

    public LearningUserProgressEntity(String id, String userId, String trackId) {
        this.id = id;
        this.userId = userId;
        this.trackId = trackId;
        this.completedLevelIdsJson = "[]";
        this.badgesJson = "[]";
        this.unlockedEquipmentJson = "[]";
        this.unlockedMaterialsJson = "[]";
        this.unlockedBookChaptersJson = "[]";
        this.statsJson = "{}";
        this.updatedAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTrackId() { return trackId; }
    public void setTrackId(String trackId) { this.trackId = trackId; }

    public String getCompletedLevelIdsJson() { return completedLevelIdsJson; }
    public void setCompletedLevelIdsJson(String completedLevelIdsJson) { this.completedLevelIdsJson = completedLevelIdsJson; }

    public String getCurrentLevelId() { return currentLevelId; }
    public void setCurrentLevelId(String currentLevelId) { this.currentLevelId = currentLevelId; }

    public String getBadgesJson() { return badgesJson; }
    public void setBadgesJson(String badgesJson) { this.badgesJson = badgesJson; }

    public String getUnlockedEquipmentJson() { return unlockedEquipmentJson; }
    public void setUnlockedEquipmentJson(String unlockedEquipmentJson) { this.unlockedEquipmentJson = unlockedEquipmentJson; }

    public String getUnlockedMaterialsJson() { return unlockedMaterialsJson; }
    public void setUnlockedMaterialsJson(String unlockedMaterialsJson) { this.unlockedMaterialsJson = unlockedMaterialsJson; }

    public String getUnlockedBookChaptersJson() { return unlockedBookChaptersJson; }
    public void setUnlockedBookChaptersJson(String unlockedBookChaptersJson) { this.unlockedBookChaptersJson = unlockedBookChaptersJson; }

    public String getStatsJson() { return statsJson; }
    public void setStatsJson(String statsJson) { this.statsJson = statsJson; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
