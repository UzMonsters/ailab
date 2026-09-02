package com.ailab.book.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;

@Entity
@Table(name = "book_assets")
public class BookAsset {

    @Id
    @Column(length = 64, nullable = false, updatable = false)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AssetKind kind;

    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    @Column(name = "size_bytes", nullable = false)
    private Long sizeBytes;

    @Column(length = 128)
    private String checksum;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AssetStatus status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> variants;

    private Integer width;

    private Integer height;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> alt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> caption;

    @Column(name = "upload_url", length = 500)
    private String uploadUrl;

    @Column(name = "download_url", length = 500)
    private String downloadUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected BookAsset() {
    }

    public BookAsset(String id, AssetKind kind, String mimeType, Long sizeBytes, String checksum,
                     String uploadUrl, String downloadUrl) {
        this.id = id;
        this.kind = kind != null ? kind : AssetKind.IMAGE;
        this.mimeType = mimeType != null ? mimeType : "image/png";
        this.sizeBytes = sizeBytes != null ? sizeBytes : 0L;
        this.checksum = checksum;
        this.status = AssetStatus.READY;
        this.uploadUrl = uploadUrl;
        this.downloadUrl = downloadUrl;
    }

    public String getId() {
        return id;
    }

    public AssetKind getKind() {
        return kind;
    }

    public void setKind(AssetKind kind) {
        this.kind = kind;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(Long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public AssetStatus getStatus() {
        return status;
    }

    public void setStatus(AssetStatus status) {
        this.status = status;
    }

    public Map<String, Object> getVariants() {
        return variants;
    }

    public void setVariants(Map<String, Object> variants) {
        this.variants = variants;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Map<String, Object> getAlt() {
        return alt;
    }

    public void setAlt(Map<String, Object> alt) {
        this.alt = alt;
    }

    public Map<String, Object> getCaption() {
        return caption;
    }

    public void setCaption(Map<String, Object> caption) {
        this.caption = caption;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
