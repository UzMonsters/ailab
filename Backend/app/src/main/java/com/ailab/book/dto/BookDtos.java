package com.ailab.book.dto;

import com.ailab.book.domain.AssetKind;
import com.ailab.book.domain.AssetStatus;
import com.ailab.book.domain.BookStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public final class BookDtos {

    private BookDtos() {
    }

    public record CreateBookRequest(
            String slug,
            String defaultLocale,
            Map<String, Object> translations
    ) {
    }

    public record PatchBookRequest(
            String slug,
            String defaultLocale,
            Map<String, Object> translations
    ) {
    }

    public record BookSummary(
            String id,
            String slug,
            BookStatus status,
            String defaultLocale,
            Map<String, Object> translations,
            Long draftVersion,
            Long publishedVersion,
            Instant createdAt,
            Instant updatedAt
    ) {
    }

    public record PageMetadata(
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {
    }

    public record BookListResponse(
            List<BookSummary> items,
            PageMetadata page
    ) {
    }

    public record CreateChapterRequest(
            Integer position,
            Map<String, Object> translations
    ) {
    }

    public record PatchChapterRequest(
            Integer position,
            Map<String, Object> translations,
            Long expectedVersion
    ) {
    }

    public record DeleteChapterRequest(
            Long expectedVersion,
            Boolean confirm
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ChapterResponse(
            String id,
            String bookId,
            Integer position,
            Map<String, Object> translations,
            String status,
            Instant createdAt,
            Instant updatedAt,
            List<PageResponse> pages
    ) {
    }

    public record CreatePageRequest(
            String chapterId,
            String slug,
            Integer position,
            String layout,
            Map<String, Object> translations
    ) {
    }

    public record PatchPageRequest(
            String slug,
            Integer position,
            String layout,
            Map<String, Object> translations,
            Long expectedVersion
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record PageResponse(
            String id,
            String chapterId,
            String bookId,
            String slug,
            Integer position,
            String layout,
            Map<String, Object> translations,
            List<Map<String, Object>> blocks,
            Long version,
            List<String> missingLocales,
            Instant createdAt,
            Instant updatedAt
    ) {
    }

    public record SaveBlocksRequest(
            Long version,
            List<Map<String, Object>> blocks
    ) {
    }

    public record SaveBlocksResponse(
            String pageId,
            Long version,
            List<Map<String, Object>> blocks,
            List<String> missingLocales,
            Instant updatedAt
    ) {
    }

    public record BookEditorDocument(
            String id,
            String slug,
            BookStatus status,
            String defaultLocale,
            Map<String, Object> translations,
            List<ChapterResponse> chapters,
            Long draftVersion,
            Long publishedVersion,
            Instant createdAt,
            Instant updatedAt
    ) {
    }

    public record ValidateBookRequest(
            Long version
    ) {
    }

    public record ValidationError(
            String path,
            String code,
            String message
    ) {
    }

    public record ValidationWarning(
            String path,
            String code,
            String message
    ) {
    }

    public record ValidationReport(
            boolean valid,
            List<ValidationError> errors,
            List<ValidationWarning> warnings
    ) {
    }

    public record PublishBookRequest(
            Long version,
            String idempotencyKey,
            String releaseNote
    ) {
    }

    public record PublisherInfo(
            String id,
            String displayName
    ) {
    }

    public record PublishResult(
            String bookId,
            Long publishedVersion,
            Instant publishedAt,
            PublisherInfo publishedBy,
            String releaseNote
    ) {
    }

    public record RollbackBookRequest(
            Long targetPublishedVersion,
            String reason
    ) {
    }

    public record FileUploadSpec(
            String name,
            String filename,
            String mimeType,
            String contentType,
            Long size,
            Long sizeBytes,
            String checksum,
            String kind,
            String theme
    ) {
    }

    public record AssetUploadTicket(
            String assetId,
            String fileId,
            String uploadUrl,
            String downloadUrl,
            Instant expiresAt
    ) {
    }

    public record AssetUploadUrlsResponse(
            List<AssetUploadTicket> uploads
    ) {
    }

    public record CompleteAssetRequest(
            String checksum,
            Map<String, Object> alt,
            Map<String, Object> caption,
            Map<String, Object> variants,
            Integer width,
            Integer height
    ) {
    }

    public record AssetResponse(
            String id,
            AssetKind kind,
            String mimeType,
            Long sizeBytes,
            String checksum,
            AssetStatus status,
            Map<String, Object> variants,
            Integer width,
            Integer height,
            Map<String, Object> alt,
            Map<String, Object> caption,
            String uploadUrl,
            String downloadUrl,
            Instant createdAt,
            Instant updatedAt
    ) {
    }

    public record PublicBookInfo(
            String id,
            String slug,
            String title,
            String description
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record PublicChapterSummary(
            String id,
            Integer position,
            String title,
            long pageCount,
            List<PublicPageSummary> pages
    ) {
    }

    public record PublicPageSummary(
            String id,
            String slug,
            Integer position,
            String title
    ) {
    }

    public record PublicBookManifest(
            PublicBookInfo book,
            String locale,
            String fallbackLocale,
            List<String> missingLocales,
            Long publishedVersion,
            List<PublicChapterSummary> chapters
    ) {
    }

    public record PublicPageDetail(
            String id,
            String slug,
            Integer position,
            String title,
            List<Map<String, Object>> blocks
    ) {
    }

    public record PublicChapterDetail(
            PublicChapterSummary chapter,
            List<PublicPageDetail> pages,
            String fallbackLocale,
            List<String> missingLocales
    ) {
    }

    public record BookProgressDto(
            String bookId,
            String pageId,
            String scrollAnchor,
            List<Object> bookmarks,
            Instant updatedAt
    ) {
    }

    public record UpdateBookProgressRequest(
            String pageId,
            String scrollAnchor,
            List<Object> bookmarks,
            Instant updatedAt
    ) {
    }
}
