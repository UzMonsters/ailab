package com.ailab.book.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "book_user_progress")
public class BookProgress {

    @Id
    @Column(length = 128, nullable = false, updatable = false)
    private String id;

    @Column(name = "user_id", length = 64, nullable = false)
    private String userId;

    @Column(name = "book_id", length = 64, nullable = false)
    private String bookId;

    @Column(name = "page_id", length = 64)
    private String pageId;

    @Column(name = "scroll_anchor", length = 100)
    private String scrollAnchor;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private List<Object> bookmarks;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected BookProgress() {
    }

    public BookProgress(String id, String userId, String bookId, String pageId, String scrollAnchor, List<Object> bookmarks) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.pageId = pageId;
        this.scrollAnchor = scrollAnchor;
        this.bookmarks = bookmarks != null ? bookmarks : List.of();
        this.updatedAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getBookId() {
        return bookId;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getScrollAnchor() {
        return scrollAnchor;
    }

    public void setScrollAnchor(String scrollAnchor) {
        this.scrollAnchor = scrollAnchor;
    }

    public List<Object> getBookmarks() {
        return bookmarks;
    }

    public void setBookmarks(List<Object> bookmarks) {
        this.bookmarks = bookmarks != null ? bookmarks : List.of();
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
