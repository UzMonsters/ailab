package com.ailab.book.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "book_pages")
public class Page {

    @Id
    @Column(length = 64, nullable = false, updatable = false)
    private String id;

    @Column(name = "chapter_id", length = 64, nullable = false)
    private String chapterId;

    @Column(name = "book_id", length = 64, nullable = false)
    private String bookId;

    @Column(nullable = false, length = 120)
    private String slug;

    @Column(nullable = false)
    private Integer position;

    @Column(nullable = false, length = 50)
    private String layout;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> translations;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private List<Map<String, Object>> blocks;

    @Column(nullable = false)
    private Long version;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Page() {
    }

    public Page(String id, String chapterId, String bookId, String slug, Integer position, String layout, Map<String, Object> translations) {
        this.id = id;
        this.chapterId = chapterId;
        this.bookId = bookId;
        this.slug = slug != null ? slug : id;
        this.position = position != null ? position : 1;
        this.layout = layout != null && !layout.isBlank() ? layout : "single-page";
        this.translations = translations != null ? translations : Map.of();
        this.blocks = List.of();
        this.version = 1L;
    }

    public String getId() {
        return id;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public String getBookId() {
        return bookId;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public Map<String, Object> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<String, Object> translations) {
        this.translations = translations;
    }

    public List<Map<String, Object>> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Map<String, Object>> blocks) {
        this.blocks = blocks != null ? blocks : List.of();
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public void incrementVersion() {
        this.version = this.version != null ? this.version + 1 : 1L;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
