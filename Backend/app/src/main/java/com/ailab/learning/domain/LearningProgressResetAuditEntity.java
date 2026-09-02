package com.ailab.learning.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "learning_progress_reset_audit")
public class LearningProgressResetAuditEntity {

    @Id
    @Column(name = "id", length = 64)
    private String id;

    @Column(name = "user_id", length = 64, nullable = false)
    private String userId;

    @Column(name = "track_id", length = 64)
    private String trackId;

    @Column(name = "level_id", length = 64)
    private String levelId;

    @Column(name = "reason", length = 500, nullable = false)
    private String reason;

    @Column(name = "initiated_by", length = 64, nullable = false)
    private String initiatedBy;

    @Column(name = "status", length = 30, nullable = false)
    private String status = "QUEUED";

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public LearningProgressResetAuditEntity() {}

    public LearningProgressResetAuditEntity(String id, String userId, String trackId, String levelId, String reason, String initiatedBy) {
        this.id = id;
        this.userId = userId;
        this.trackId = trackId;
        this.levelId = levelId;
        this.reason = reason;
        this.initiatedBy = initiatedBy;
        this.status = "QUEUED";
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTrackId() { return trackId; }
    public void setTrackId(String trackId) { this.trackId = trackId; }

    public String getLevelId() { return levelId; }
    public void setLevelId(String levelId) { this.levelId = levelId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getInitiatedBy() { return initiatedBy; }
    public void setInitiatedBy(String initiatedBy) { this.initiatedBy = initiatedBy; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
