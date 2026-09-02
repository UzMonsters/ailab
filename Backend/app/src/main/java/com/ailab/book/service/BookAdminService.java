package com.ailab.book.service;

import com.ailab.book.domain.BookStatus;
import com.ailab.book.dto.BookDtos;

public interface BookAdminService {

    BookDtos.BookListResponse listBooks(int page, int size, String query, BookStatus status, String sort);

    BookDtos.BookEditorDocument createBook(BookDtos.CreateBookRequest request, String idempotencyKey);

    BookDtos.BookEditorDocument getBookEditorDocument(String bookId, String include);

    BookDtos.BookEditorDocument patchBook(String bookId, BookDtos.PatchBookRequest request, String ifMatch);

    BookDtos.ChapterResponse createChapter(String bookId, BookDtos.CreateChapterRequest request);

    BookDtos.ChapterResponse patchChapter(String bookId, String chapterId, BookDtos.PatchChapterRequest request, String ifMatch);

    void deleteChapter(String bookId, String chapterId, Long expectedVersion, Boolean confirm);

    BookDtos.PageResponse createPage(String bookId, BookDtos.CreatePageRequest request);

    BookDtos.PageResponse patchPage(String bookId, String pageId, BookDtos.PatchPageRequest request, String ifMatch);

    BookDtos.SaveBlocksResponse savePageBlocks(String bookId, String pageId, BookDtos.SaveBlocksRequest request, String ifMatch);
}
