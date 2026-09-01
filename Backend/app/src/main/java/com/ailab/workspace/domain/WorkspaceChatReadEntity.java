package com.ailab.workspace.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "workspace_chat_reads")
@IdClass(WorkspaceMemberId.class)
public class WorkspaceChatReadEntity {

    @Id
    @Column(name = "workspace_id", nullable = false)
    private String workspaceId;

    @Id
    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "last_read_message_id")
    private String lastReadMessageId;

    @Column(name = "last_read_at", nullable = false)
    private Instant lastReadAt = Instant.now();

    public WorkspaceChatReadEntity() {}

    public WorkspaceChatReadEntity(String workspaceId, String userId, String lastReadMessageId) {
        this.workspaceId = workspaceId;
        this.userId = userId;
        this.lastReadMessageId = lastReadMessageId;
        this.lastReadAt = Instant.now();
    }

    public String getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(String workspaceId) { this.workspaceId = workspaceId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getLastReadMessageId() { return lastReadMessageId; }
    public void setLastReadMessageId(String lastReadMessageId) { this.lastReadMessageId = lastReadMessageId; }

    public Instant getLastReadAt() { return lastReadAt; }
    public void setLastReadAt(Instant lastReadAt) { this.lastReadAt = lastReadAt; }
}
