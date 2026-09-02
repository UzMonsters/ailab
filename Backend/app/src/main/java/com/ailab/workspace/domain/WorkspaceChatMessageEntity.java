package com.ailab.workspace.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "workspace_chat_messages", uniqueConstraints = {
    @UniqueConstraint(name = "uk_workspace_chat_client", columnNames = {"workspace_id", "client_message_id"})
})
public class WorkspaceChatMessageEntity {

    @Id
    private String id;

    @Column(name = "client_message_id", nullable = false)
    private String clientMessageId;

    @Column(name = "workspace_id", nullable = false)
    private String workspaceId;

    @Column(name = "author_id", nullable = false)
    private String authorId;

    @Column(name = "author_name")
    private String authorName;

    @Column(name = "author_avatar", columnDefinition = "TEXT")
    private String authorAvatar;

    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(name = "reply_to_id")
    private String replyToId;

    @Column(name = "anchor_item_id")
    private String anchorItemId;

    @Column(name = "anchor_version")
    private Long anchorVersion;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "edited_at")
    private Instant editedAt;

    public WorkspaceChatMessageEntity() {}

    public WorkspaceChatMessageEntity(String id, String clientMessageId, String workspaceId, String authorId, String authorName, String authorAvatar, String body, String replyToId, String anchorItemId, Long anchorVersion) {
        this.id = id;
        this.clientMessageId = clientMessageId;
        this.workspaceId = workspaceId;
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorAvatar = authorAvatar;
        this.body = body;
        this.replyToId = replyToId;
        this.anchorItemId = anchorItemId;
        this.anchorVersion = anchorVersion;
        this.isDeleted = false;
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getClientMessageId() { return clientMessageId; }
    public void setClientMessageId(String clientMessageId) { this.clientMessageId = clientMessageId; }

    public String getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(String workspaceId) { this.workspaceId = workspaceId; }

    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getAuthorAvatar() { return authorAvatar; }
    public void setAuthorAvatar(String authorAvatar) { this.authorAvatar = authorAvatar; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getReplyToId() { return replyToId; }
    public void setReplyToId(String replyToId) { this.replyToId = replyToId; }

    public String getAnchorItemId() { return anchorItemId; }
    public void setAnchorItemId(String anchorItemId) { this.anchorItemId = anchorItemId; }

    public Long getAnchorVersion() { return anchorVersion; }
    public void setAnchorVersion(Long anchorVersion) { this.anchorVersion = anchorVersion; }

    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getEditedAt() { return editedAt; }
    public void setEditedAt(Instant editedAt) { this.editedAt = editedAt; }
}
