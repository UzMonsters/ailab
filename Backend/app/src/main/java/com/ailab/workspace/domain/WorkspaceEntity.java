package com.ailab.workspace.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "workspaces")
public class WorkspaceEntity {

    @Id
    private String id;

    @Column(name = "owner_id", nullable = false)
    private String ownerId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "science", nullable = false)
    private String science = "chemistry";

    @Column(name = "thumbnail", columnDefinition = "TEXT")
    private String thumbnail;

    @Column(name = "state_version", nullable = false)
    private long stateVersion = 1;

    @Column(name = "is_favorite", nullable = false)
    private boolean isFavorite = false;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Column(name = "experiment_session_id")
    private String experimentSessionId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public WorkspaceEntity() {}

    public WorkspaceEntity(String id, String ownerId, String name, String science, String experimentSessionId) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.science = science != null ? science : "chemistry";
        this.experimentSessionId = experimentSessionId;
        this.stateVersion = 1;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getScience() { return science; }
    public void setScience(String science) { this.science = science; }

    public String getThumbnail() { return thumbnail; }
    public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }

    public long getStateVersion() { return stateVersion; }
    public void setStateVersion(long stateVersion) { this.stateVersion = stateVersion; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }

    public String getExperimentSessionId() { return experimentSessionId; }
    public void setExperimentSessionId(String experimentSessionId) { this.experimentSessionId = experimentSessionId; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
