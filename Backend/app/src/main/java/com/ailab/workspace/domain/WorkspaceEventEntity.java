package com.ailab.workspace.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "workspace_events", uniqueConstraints = {
    @UniqueConstraint(name = "uk_workspace_client_event", columnNames = {"workspace_id", "user_id", "client_event_id"})
})
public class WorkspaceEventEntity {

    @Id
    private String id;

    @Column(name = "workspace_id", nullable = false)
    private String workspaceId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "client_event_id", nullable = false)
    private String clientEventId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "version", nullable = false)
    private long version;

    @Column(name = "payload_json", nullable = false, columnDefinition = "TEXT")
    private String payloadJson;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public WorkspaceEventEntity() {}

    public WorkspaceEventEntity(String id, String workspaceId, String userId, String clientEventId, String eventType, long version, String payloadJson) {
        this.id = id;
        this.workspaceId = workspaceId;
        this.userId = userId;
        this.clientEventId = clientEventId;
        this.eventType = eventType;
        this.version = version;
        this.payloadJson = payloadJson;
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(String workspaceId) { this.workspaceId = workspaceId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getClientEventId() { return clientEventId; }
    public void setClientEventId(String clientEventId) { this.clientEventId = clientEventId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public long getVersion() { return version; }
    public void setVersion(long version) { this.version = version; }

    public String getPayloadJson() { return payloadJson; }
    public void setPayloadJson(String payloadJson) { this.payloadJson = payloadJson; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
