package com.ailab.storage.upload;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "upload_tickets", indexes = {
        @Index(name = "idx_upload_tickets_token_hash", columnList = "token_hash", unique = true),
        @Index(name = "idx_upload_tickets_actor_scope", columnList = "actor_id, scope"),
        @Index(name = "idx_upload_tickets_asset_scope", columnList = "asset_id, scope"),
        @Index(name = "idx_upload_tickets_status", columnList = "status"),
        @Index(name = "idx_upload_tickets_expires_at", columnList = "expires_at")
})
public class UploadTicketEntity {
    @Id
    @Column(length = 80, nullable = false, updatable = false)
    private String id;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(name = "asset_id", nullable = false, length = 120)
    private String assetId;

    @Column(name = "actor_id", nullable = false, length = 120)
    private String actorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private UploadScope scope;

    @Column(name = "storage_key", nullable = false, length = 700)
    private String storageKey;

    @Column(name = "allowed_mime", nullable = false, length = 120)
    private String allowedMime;

    @Column(name = "max_size_bytes", nullable = false)
    private long maxSizeBytes;

    @Column(name = "expected_checksum", length = 120)
    private String expectedChecksum;

    @Column(name = "actual_checksum", length = 120)
    private String actualChecksum;

    @Column(name = "actual_size_bytes")
    private Long actualSizeBytes;

    @Column(name = "actual_mime", length = 120)
    private String actualMime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private UploadTicketStatus status;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "uploaded_at")
    private Instant uploadedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "workspace_id", length = 120)
    private String workspaceId;

    @Column(name = "preview_id", length = 120)
    private String previewId;

    @Column(length = 40)
    private String variant;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected UploadTicketEntity() {
    }

    public UploadTicketEntity(String id, String tokenHash, String assetId, String actorId, UploadScope scope,
                              String storageKey, String allowedMime, long maxSizeBytes, String expectedChecksum,
                              Instant expiresAt, String workspaceId, String previewId, String variant) {
        this.id = id;
        this.tokenHash = tokenHash;
        this.assetId = assetId;
        this.actorId = actorId;
        this.scope = scope;
        this.storageKey = storageKey;
        this.allowedMime = allowedMime;
        this.maxSizeBytes = maxSizeBytes;
        this.expectedChecksum = expectedChecksum;
        this.expiresAt = expiresAt;
        this.workspaceId = workspaceId;
        this.previewId = previewId;
        this.variant = variant;
        this.status = UploadTicketStatus.ISSUED;
    }

    public String getId() { return id; }
    public String getTokenHash() { return tokenHash; }
    public String getAssetId() { return assetId; }
    public String getActorId() { return actorId; }
    public UploadScope getScope() { return scope; }
    public String getStorageKey() { return storageKey; }
    public String getAllowedMime() { return allowedMime; }
    public long getMaxSizeBytes() { return maxSizeBytes; }
    public String getExpectedChecksum() { return expectedChecksum; }
    public String getActualChecksum() { return actualChecksum; }
    public Long getActualSizeBytes() { return actualSizeBytes; }
    public String getActualMime() { return actualMime; }
    public UploadTicketStatus getStatus() { return status; }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getUploadedAt() { return uploadedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public String getWorkspaceId() { return workspaceId; }
    public String getPreviewId() { return previewId; }
    public String getVariant() { return variant; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    void setStatus(UploadTicketStatus status) { this.status = status; }
    void setActualChecksum(String actualChecksum) { this.actualChecksum = actualChecksum; }
    void setActualSizeBytes(Long actualSizeBytes) { this.actualSizeBytes = actualSizeBytes; }
    void setActualMime(String actualMime) { this.actualMime = actualMime; }
    void setUploadedAt(Instant uploadedAt) { this.uploadedAt = uploadedAt; }
    void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
}
