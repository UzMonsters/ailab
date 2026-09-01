package com.ailab.workspace.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workspace_comment_threads")
public class WorkspaceCommentThreadEntity {

    @Id
    private String id;

    @Column(name = "workspace_id", nullable = false)
    private String workspaceId;

    @Column(name = "author_id", nullable = false)
    private String authorId;

    @Column(name = "author_name")
    private String authorName;

    @Column(name = "author_avatar", columnDefinition = "TEXT")
    private String authorAvatar;

    @Column(name = "anchor_item_id")
    private String anchorItemId;

    @Column(name = "anchor_point_x")
    private Double anchorPointX;

    @Column(name = "anchor_point_y")
    private Double anchorPointY;

    @Column(name = "anchor_version")
    private Long anchorVersion;

    @Column(name = "status", nullable = false)
    private String status = "OPEN"; // OPEN, RESOLVED

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @OneToMany(mappedBy = "thread", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("createdAt ASC")
    private List<WorkspaceCommentReplyEntity> replies = new ArrayList<>();

    public WorkspaceCommentThreadEntity() {}

    public WorkspaceCommentThreadEntity(String id, String workspaceId, String authorId, String authorName, String authorAvatar, String anchorItemId, Double anchorPointX, Double anchorPointY, Long anchorVersion) {
        this.id = id;
        this.workspaceId = workspaceId;
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorAvatar = authorAvatar;
        this.anchorItemId = anchorItemId;
        this.anchorPointX = anchorPointX;
        this.anchorPointY = anchorPointY;
        this.anchorVersion = anchorVersion;
        this.status = "OPEN";
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(String workspaceId) { this.workspaceId = workspaceId; }

    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getAuthorAvatar() { return authorAvatar; }
    public void setAuthorAvatar(String authorAvatar) { this.authorAvatar = authorAvatar; }

    public String getAnchorItemId() { return anchorItemId; }
    public void setAnchorItemId(String anchorItemId) { this.anchorItemId = anchorItemId; }

    public Double getAnchorPointX() { return anchorPointX; }
    public void setAnchorPointX(Double anchorPointX) { this.anchorPointX = anchorPointX; }

    public Double getAnchorPointY() { return anchorPointY; }
    public void setAnchorPointY(Double anchorPointY) { this.anchorPointY = anchorPointY; }

    public Long getAnchorVersion() { return anchorVersion; }
    public void setAnchorVersion(Long anchorVersion) { this.anchorVersion = anchorVersion; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public List<WorkspaceCommentReplyEntity> getReplies() { return replies; }
    public void setReplies(List<WorkspaceCommentReplyEntity> replies) { this.replies = replies; }
}
