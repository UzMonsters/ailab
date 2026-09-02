package com.ailab.book.controller;

import com.ailab.book.domain.BookStatus;
import com.ailab.book.dto.BookDtos;
import com.ailab.book.service.BookAdminService;
import com.ailab.book.service.BookPublishService;
import com.ailab.book.service.BookValidationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/books")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
public class AdminBookController {

    private final BookAdminService adminService;
    private final BookValidationService validationService;
    private final BookPublishService publishService;

    public AdminBookController(BookAdminService adminService,
                               BookValidationService validationService,
                               BookPublishService publishService) {
        this.adminService = adminService;
        this.validationService = validationService;
        this.publishService = publishService;
    }

    @GetMapping
    public BookDtos.BookListResponse listBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) BookStatus status,
            @RequestParam(defaultValue = "updatedAt,desc") String sort
    ) {
        return adminService.listBooks(page, size, q, status, sort);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDtos.BookEditorDocument createBook(
            @RequestBody BookDtos.CreateBookRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey
    ) {
        return adminService.createBook(request, idempotencyKey);
    }

    @GetMapping("/{bookId}")
    public BookDtos.BookEditorDocument getBook(
            @PathVariable String bookId,
            @RequestParam(defaultValue = "chapters,pages,translations,assets") String include
    ) {
        return adminService.getBookEditorDocument(bookId, include);
    }

    @PatchMapping("/{bookId}")
    public BookDtos.BookEditorDocument patchBook(
            @PathVariable String bookId,
            @RequestBody BookDtos.PatchBookRequest request,
            @RequestHeader(value = "If-Match", required = false) String ifMatch
    ) {
        return adminService.patchBook(bookId, request, ifMatch);
    }

    @PostMapping("/{bookId}/chapters")
    @ResponseStatus(HttpStatus.CREATED)
    public BookDtos.ChapterResponse createChapter(
            @PathVariable String bookId,
            @RequestBody BookDtos.CreateChapterRequest request
    ) {
        return adminService.createChapter(bookId, request);
    }

    @PatchMapping("/{bookId}/chapters/{chapterId}")
    public BookDtos.ChapterResponse patchChapter(
            @PathVariable String bookId,
            @PathVariable String chapterId,
            @RequestBody BookDtos.PatchChapterRequest request,
            @RequestHeader(value = "If-Match", required = false) String ifMatch
    ) {
        return adminService.patchChapter(bookId, chapterId, request, ifMatch);
    }

    @DeleteMapping("/{bookId}/chapters/{chapterId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChapter(
            @PathVariable String bookId,
            @PathVariable String chapterId,
            @RequestParam(required = false) Long expectedVersion,
            @RequestParam(defaultValue = "false") Boolean confirm,
            @RequestBody(required = false) BookDtos.DeleteChapterRequest requestBody
    ) {
        Long version = expectedVersion;
        Boolean isConfirm = confirm;
        if (requestBody != null) {
            if (requestBody.expectedVersion() != null) {
                version = requestBody.expectedVersion();
            }
            if (requestBody.confirm() != null) {
                isConfirm = requestBody.confirm();
            }
        }
        adminService.deleteChapter(bookId, chapterId, version, isConfirm);
    }

    @PostMapping("/{bookId}/pages")
    @ResponseStatus(HttpStatus.CREATED)
    public BookDtos.PageResponse createPage(
            @PathVariable String bookId,
            @RequestBody BookDtos.CreatePageRequest request
    ) {
        return adminService.createPage(bookId, request);
    }

    @PatchMapping("/{bookId}/pages/{pageId}")
    public BookDtos.PageResponse patchPage(
            @PathVariable String bookId,
            @PathVariable String pageId,
            @RequestBody BookDtos.PatchPageRequest request,
            @RequestHeader(value = "If-Match", required = false) String ifMatch
    ) {
        return adminService.patchPage(bookId, pageId, request, ifMatch);
    }

    @PutMapping("/{bookId}/pages/{pageId}/blocks")
    public BookDtos.SaveBlocksResponse saveBlocks(
            @PathVariable String bookId,
            @PathVariable String pageId,
            @RequestBody BookDtos.SaveBlocksRequest request,
            @RequestHeader(value = "If-Match", required = false) String ifMatch
    ) {
        return adminService.savePageBlocks(bookId, pageId, request, ifMatch);
    }

    @PostMapping("/{bookId}/validate")
    public BookDtos.ValidationReport validateBook(
            @PathVariable String bookId,
            @RequestBody(required = false) BookDtos.ValidateBookRequest request
    ) {
        Long version = request != null ? request.version() : null;
        return validationService.validateBook(bookId, version);
    }

    @PostMapping("/{bookId}/publish")
    @ResponseStatus(HttpStatus.CREATED)
    public BookDtos.PublishResult publishBook(
            @PathVariable String bookId,
            @RequestBody(required = false) BookDtos.PublishBookRequest request,
            @AuthenticationPrincipal String actorId
    ) {
        return publishService.publishBook(bookId, request, actorId, "Administrator");
    }

    @PostMapping("/{bookId}/rollback")
    @ResponseStatus(HttpStatus.CREATED)
    public BookDtos.PublishResult rollbackBook(
            @PathVariable String bookId,
            @RequestBody BookDtos.RollbackBookRequest request,
            @AuthenticationPrincipal String actorId
    ) {
        return publishService.rollbackBook(bookId, request, actorId, "Administrator");
    }
}
