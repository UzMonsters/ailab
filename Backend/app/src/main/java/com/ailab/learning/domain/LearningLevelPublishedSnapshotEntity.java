package com.ailab.learning.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(name = "learning_level_published_snapshots")
public class LearningLevelPublishedSnapshotEntity {

    @Id
    @Column(name = "id", length = 64)
    private String id;

    @Column(name = "level_id", length = 64, nullable = false)
    private String levelId;

    @Column(name = "version", nullable = false)
    private long version;

    @Column(name = "release_note", length = 500)
    private String releaseNote;

    @Column(name = "published_by_id", length = 64, nullable = false)
    private String publishedById;

    @Column(name = "published_by_name", length = 100, nullable = false)
    private String publishedByName;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "snapshot_data", columnDefinition = "jsonb", nullable = false)
    private String snapshotDataJson;

    @Column(name = "idempotency_key", length = 128)
    private String idempotencyKey;

    @Column(name = "published_at", nullable = false)
    private Instant publishedAt = Instant.now();

    public LearningLevelPublishedSnapshotEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getLevelId() { return levelId; }
    public void setLevelId(String levelId) { this.levelId = levelId; }

    public long getVersion() { return version; }
    public void setVersion(long version) { this.version = version; }

    public String getReleaseNote() { return releaseNote; }
    public void setReleaseNote(String releaseNote) { this.releaseNote = releaseNote; }

    public String getPublishedById() { return publishedById; }
    public void setPublishedById(String publishedById) { this.publishedById = publishedById; }

    public String getPublishedByName() { return publishedByName; }
    public void setPublishedByName(String publishedByName) { this.publishedByName = publishedByName; }

    public String getSnapshotDataJson() { return snapshotDataJson; }
    public void setSnapshotDataJson(String snapshotDataJson) { this.snapshotDataJson = snapshotDataJson; }

    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }

    public Instant getPublishedAt() { return publishedAt; }
    public void setPublishedAt(Instant publishedAt) { this.publishedAt = publishedAt; }
}
