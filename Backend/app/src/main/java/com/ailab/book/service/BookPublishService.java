package com.ailab.book.service;

import com.ailab.book.dto.BookDtos;

public interface BookPublishService {

    BookDtos.PublishResult publishBook(String bookId, BookDtos.PublishBookRequest request, String actorId, String actorName);

    BookDtos.PublishResult rollbackBook(String bookId, BookDtos.RollbackBookRequest request, String actorId, String actorName);
}
