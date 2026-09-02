package com.ailab.learning.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(name = "learning_user_attempts")
public class LearningUserAttemptEntity {

    @Id
    @Column(name = "id", length = 64)
    private String id;

    @Column(name = "client_attempt_id", length = 128)
    private String clientAttemptId;

    @Column(name = "user_id", length = 64)
    private String userId;

    @Column(name = "is_guest", nullable = false)
    private boolean isGuest = false;

    @Column(name = "is_preview", nullable = false)
    private boolean isPreview = false;

    @Column(name = "level_id", length = 64, nullable = false)
    private String levelId;

    @Column(name = "level_version", nullable = false)
    private long levelVersion;

    @Column(name = "workspace_id", length = 64, nullable = false)
    private String workspaceId;

    @Column(name = "experiment_id", length = 64, nullable = false)
    private String experimentId;

    @Column(name = "state_version", nullable = false)
    private long stateVersion = 1;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private AttemptStatus status = AttemptStatus.ACTIVE;

    @Column(name = "current_step_index", nullable = false)
    private int currentStepIndex = 0;

    @Column(name = "current_step_id", length = 64, nullable = false)
    private String currentStepId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "completed_steps", columnDefinition = "jsonb", nullable = false)
    private String completedStepsJson = "[]";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "hint_usage", columnDefinition = "jsonb", nullable = false)
    private String hintUsageJson = "[]";

    @Column(name = "idempotency_key", length = 128)
    private String idempotencyKey;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt = Instant.now();

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public LearningUserAttemptEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getClientAttemptId() { return clientAttemptId; }
    public void setClientAttemptId(String clientAttemptId) { this.clientAttemptId = clientAttemptId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public boolean isGuest() { return isGuest; }
    public void setGuest(boolean guest) { isGuest = guest; }

    public boolean isPreview() { return isPreview; }
    public void setPreview(boolean preview) { isPreview = preview; }

    public String getLevelId() { return levelId; }
    public void setLevelId(String levelId) { this.levelId = levelId; }

    public long getLevelVersion() { return levelVersion; }
    public void setLevelVersion(long levelVersion) { this.levelVersion = levelVersion; }

    public String getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(String workspaceId) { this.workspaceId = workspaceId; }

    public String getExperimentId() { return experimentId; }
    public void setExperimentId(String experimentId) { this.experimentId = experimentId; }

    public long getStateVersion() { return stateVersion; }
    public void setStateVersion(long stateVersion) { this.stateVersion = stateVersion; }

    public AttemptStatus getStatus() { return status; }
    public void setStatus(AttemptStatus status) { this.status = status; }

    public int getCurrentStepIndex() { return currentStepIndex; }
    public void setCurrentStepIndex(int currentStepIndex) { this.currentStepIndex = currentStepIndex; }

    public String getCurrentStepId() { return currentStepId; }
    public void setCurrentStepId(String currentStepId) { this.currentStepId = currentStepId; }

    public String getCompletedStepsJson() { return completedStepsJson; }
    public void setCompletedStepsJson(String completedStepsJson) { this.completedStepsJson = completedStepsJson; }

    public String getHintUsageJson() { return hintUsageJson; }
    public void setHintUsageJson(String hintUsageJson) { this.hintUsageJson = hintUsageJson; }

    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }

    public Instant getStartedAt() { return startedAt; }
    public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }

    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
