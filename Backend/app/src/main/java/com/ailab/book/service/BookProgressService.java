package com.ailab.book.service;

import com.ailab.book.dto.BookDtos;

public interface BookProgressService {

    BookDtos.BookProgressDto getProgress(String userId, String bookId);

    BookDtos.BookProgressDto updateProgress(String userId, String bookId, BookDtos.UpdateBookProgressRequest request);
}
