package com.ailab.admin.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "admin_catalog_drafts")
public class AdminCatalogDraftEntity {

    @Id
    @Column(length = 64, nullable = false, updatable = false)
    private String id;

    @Column(name = "entity_type", length = 50, nullable = false)
    private String entityType;

    @Column(nullable = false, length = 100)
    private String code;

    @Column(nullable = false, length = 50)
    private String status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> data;

    @Column(nullable = false)
    private Long version;

    @Column(name = "idempotency_key", length = 100)
    private String idempotencyKey;

    @Column(name = "published_version")
    private Long publishedVersion;

    @Column(name = "published_at")
    private Instant publishedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected AdminCatalogDraftEntity() {
    }

    public AdminCatalogDraftEntity(String entityType, String code, String status, Map<String, Object> data) {
        this.id = "drf_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        this.entityType = entityType;
        this.code = code;
        this.status = status != null ? status : "DRAFT";
        this.data = data;
        this.version = 1L;
    }

    public String getId() { return id; }
    public String getEntityType() { return entityType; }
    public String getCode() { return code; }
    public String getStatus() { return status; }
    public Map<String, Object> getData() { return data; }
    public Long getVersion() { return version; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public Long getPublishedVersion() { return publishedVersion; }
    public Instant getPublishedAt() { return publishedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void updateData(Map<String, Object> data) {
        this.data = data;
        this.version++;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void publish(String idempotencyKey) {
        this.status = "PUBLISHED";
        this.publishedVersion = this.version;
        this.publishedAt = Instant.now();
        this.idempotencyKey = idempotencyKey;
    }
}
