package com.ailab.book.service;

import com.ailab.book.domain.BookProgress;
import com.ailab.book.dto.BookDtos;
import com.ailab.book.repository.BookProgressRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
public class BookProgressServiceImpl implements BookProgressService {

    private final BookProgressRepository progressRepository;

    public BookProgressServiceImpl(BookProgressRepository progressRepository) {
        this.progressRepository = progressRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public BookDtos.BookProgressDto getProgress(String userId, String bookId) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED: User id is missing");
        }

        return progressRepository.findByUserIdAndBookId(userId, bookId)
                .map(this::toDto)
                .orElseGet(() -> new BookDtos.BookProgressDto(bookId, null, null, List.of(), Instant.now()));
    }

    @Override
    @Transactional
    public BookDtos.BookProgressDto updateProgress(String userId, String bookId, BookDtos.UpdateBookProgressRequest request) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED: User id is missing");
        }
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR: Request body is required");
        }

        String progressId = userId + "_" + bookId;
        BookProgress progress = progressRepository.findByUserIdAndBookId(userId, bookId)
                .orElseGet(() -> new BookProgress(progressId, userId, bookId, request.pageId(), request.scrollAnchor(), request.bookmarks()));

        if (request.pageId() != null) {
            progress.setPageId(request.pageId());
        }
        if (request.scrollAnchor() != null) {
            progress.setScrollAnchor(request.scrollAnchor());
        }
        if (request.bookmarks() != null) {
            progress.setBookmarks(request.bookmarks());
        }
        progress.setUpdatedAt(request.updatedAt() != null ? request.updatedAt() : Instant.now());

        BookProgress saved = progressRepository.save(progress);
        return toDto(saved);
    }

    private BookDtos.BookProgressDto toDto(BookProgress progress) {
        return new BookDtos.BookProgressDto(
                progress.getBookId(),
                progress.getPageId(),
                progress.getScrollAnchor(),
                progress.getBookmarks() != null ? progress.getBookmarks() : List.of(),
                progress.getUpdatedAt()
        );
    }
}
