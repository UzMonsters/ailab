package com.ailab.workspace.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "workspace_previews")
public class WorkspacePreviewEntity {

    @Id
    private String id;

    @Column(name = "workspace_id", nullable = false)
    private String workspaceId;

    @Column(name = "source_state_version", nullable = false)
    private long sourceStateVersion;

    @Column(name = "status", nullable = false)
    private String status = "READY"; // READY, PROCESSING, FALLBACK, ERROR

    @Column(name = "dark_url")
    private String darkUrl;

    @Column(name = "light_url")
    private String lightUrl;

    @Column(name = "fallback_key")
    private String fallbackKey = "chemistry-default-01";

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public WorkspacePreviewEntity() {}

    public WorkspacePreviewEntity(String id, String workspaceId, long sourceStateVersion, String status, String darkUrl, String lightUrl, String fallbackKey) {
        this.id = id;
        this.workspaceId = workspaceId;
        this.sourceStateVersion = sourceStateVersion;
        this.status = status != null ? status : "READY";
        this.darkUrl = darkUrl;
        this.lightUrl = lightUrl;
        this.fallbackKey = fallbackKey != null ? fallbackKey : "chemistry-default-01";
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(String workspaceId) { this.workspaceId = workspaceId; }

    public long getSourceStateVersion() { return sourceStateVersion; }
    public void setSourceStateVersion(long sourceStateVersion) { this.sourceStateVersion = sourceStateVersion; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDarkUrl() { return darkUrl; }
    public void setDarkUrl(String darkUrl) { this.darkUrl = darkUrl; }

    public String getLightUrl() { return lightUrl; }
    public void setLightUrl(String lightUrl) { this.lightUrl = lightUrl; }

    public String getFallbackKey() { return fallbackKey; }
    public void setFallbackKey(String fallbackKey) { this.fallbackKey = fallbackKey; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
