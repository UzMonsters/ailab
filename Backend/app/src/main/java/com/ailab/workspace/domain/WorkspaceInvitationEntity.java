package com.ailab.workspace.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "workspace_invitations")
public class WorkspaceInvitationEntity {

    @Id
    private String id;

    @Column(name = "workspace_id", nullable = false)
    private String workspaceId;

    @Column(name = "inviter_id", nullable = false)
    private String inviterId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "role", nullable = false)
    private String role = "EDITOR"; // EDITOR, VIEWER

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "status", nullable = false)
    private String status = "PENDING"; // PENDING, ACCEPTED, REVOKED, EXPIRED

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public WorkspaceInvitationEntity() {}

    public WorkspaceInvitationEntity(String id, String workspaceId, String inviterId, String email, String role, String tokenHash, Instant expiresAt) {
        this.id = id;
        this.workspaceId = workspaceId;
        this.inviterId = inviterId;
        this.email = email;
        this.role = role != null ? role : "EDITOR";
        this.tokenHash = tokenHash;
        this.status = "PENDING";
        this.expiresAt = expiresAt;
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(String workspaceId) { this.workspaceId = workspaceId; }

    public String getInviterId() { return inviterId; }
    public void setInviterId(String inviterId) { this.inviterId = inviterId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getTokenHash() { return tokenHash; }
    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
