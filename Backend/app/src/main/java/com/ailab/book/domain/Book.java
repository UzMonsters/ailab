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
@Table(name = "books")
public class Book {

    @Id
    @Column(length = 64, nullable = false, updatable = false)
    private String id;

    @Column(nullable = false, unique = true, length = 120)
    private String slug;

    @Column(name = "default_locale", nullable = false, length = 10)
    private String defaultLocale;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> translations;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BookStatus status;

    @Column(name = "draft_version", nullable = false)
    private Long draftVersion;

    @Column(name = "published_version")
    private Long publishedVersion;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Book() {
    }

    public Book(String id, String slug, String defaultLocale, Map<String, Object> translations) {
        this.id = id;
        this.slug = slug;
        this.defaultLocale = defaultLocale != null && !defaultLocale.isBlank() ? defaultLocale : "ru";
        this.translations = translations != null ? translations : Map.of();
        this.status = BookStatus.DRAFT;
        this.draftVersion = 1L;
    }

    public String getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public Map<String, Object> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<String, Object> translations) {
        this.translations = translations;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public Long getDraftVersion() {
        return draftVersion;
    }

    public void incrementDraftVersion() {
        this.draftVersion = this.draftVersion != null ? this.draftVersion + 1 : 1L;
    }

    public Long getPublishedVersion() {
        return publishedVersion;
    }

    public void setPublishedVersion(Long publishedVersion) {
        this.publishedVersion = publishedVersion;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
