package com.ailab.workspace.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "workspace_states")
public class WorkspaceStateEntity {

    @Id
    @Column(name = "workspace_id")
    private String workspaceId;

    @Column(name = "state_version", nullable = false)
    private long stateVersion = 1;

    @Column(name = "viewport_json", columnDefinition = "TEXT")
    private String viewportJson;

    @Column(name = "grid_json", columnDefinition = "TEXT")
    private String gridJson;

    @Column(name = "items_json", columnDefinition = "TEXT")
    private String itemsJson;

    @Column(name = "connections_json", columnDefinition = "TEXT")
    private String connectionsJson;

    @Column(name = "log_json", columnDefinition = "TEXT")
    private String logJson;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public WorkspaceStateEntity() {}

    public WorkspaceStateEntity(String workspaceId, long stateVersion) {
        this.workspaceId = workspaceId;
        this.stateVersion = stateVersion;
        this.viewportJson = "{\"x\":0,\"y\":0,\"zoom\":1}";
        this.gridJson = "{\"enabled\":true,\"size\":20,\"snap\":true}";
        this.itemsJson = "[]";
        this.connectionsJson = "[]";
        this.logJson = "[]";
        this.updatedAt = Instant.now();
    }

    public String getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(String workspaceId) { this.workspaceId = workspaceId; }

    public long getStateVersion() { return stateVersion; }
    public void setStateVersion(long stateVersion) { this.stateVersion = stateVersion; }

    public String getViewportJson() { return viewportJson; }
    public void setViewportJson(String viewportJson) { this.viewportJson = viewportJson; }

    public String getGridJson() { return gridJson; }
    public void setGridJson(String gridJson) { this.gridJson = gridJson; }

    public String getItemsJson() { return itemsJson; }
    public void setItemsJson(String itemsJson) { this.itemsJson = itemsJson; }

    public String getConnectionsJson() { return connectionsJson; }
    public void setConnectionsJson(String connectionsJson) { this.connectionsJson = connectionsJson; }

    public String getLogJson() { return logJson; }
    public void setLogJson(String logJson) { this.logJson = logJson; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
