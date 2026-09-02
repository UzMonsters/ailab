package com.ailab.book.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;

@Entity
@Table(name = "book_published_snapshots")
public class BookPublishedSnapshot {

    @Id
    @Column(length = 64, nullable = false, updatable = false)
    private String id;

    @Column(name = "book_id", length = 64, nullable = false)
    private String bookId;

    @Column(nullable = false)
    private Long version;

    @Column(name = "release_note", length = 500)
    private String releaseNote;

    @Column(name = "published_by_id", length = 64, nullable = false)
    private String publishedById;

    @Column(name = "published_by_name", length = 100, nullable = false)
    private String publishedByName;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "snapshot_data", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> snapshotData;

    @Column(name = "idempotency_key", length = 128)
    private String idempotencyKey;

    @CreationTimestamp
    @Column(name = "published_at", nullable = false, updatable = false)
    private Instant publishedAt;

    protected BookPublishedSnapshot() {
    }

    public BookPublishedSnapshot(String id, String bookId, Long version, String releaseNote,
                                 String publishedById, String publishedByName,
                                 Map<String, Object> snapshotData, String idempotencyKey) {
        this.id = id;
        this.bookId = bookId;
        this.version = version;
        this.releaseNote = releaseNote;
        this.publishedById = publishedById;
        this.publishedByName = publishedByName;
        this.snapshotData = snapshotData;
        this.idempotencyKey = idempotencyKey;
    }

    public String getId() {
        return id;
    }

    public String getBookId() {
        return bookId;
    }

    public Long getVersion() {
        return version;
    }

    public String getReleaseNote() {
        return releaseNote;
    }

    public String getPublishedById() {
        return publishedById;
    }

    public String getPublishedByName() {
        return publishedByName;
    }

    public Map<String, Object> getSnapshotData() {
        return snapshotData;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }
}
