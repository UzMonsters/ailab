package com.ailab.book.service;

import com.ailab.book.domain.Book;
import com.ailab.book.domain.BookPublishedSnapshot;
import com.ailab.book.domain.BookStatus;
import com.ailab.book.domain.Chapter;
import com.ailab.book.domain.Page;
import com.ailab.book.dto.BookDtos;
import com.ailab.book.repository.BookPublishedSnapshotRepository;
import com.ailab.book.repository.BookRepository;
import com.ailab.book.repository.ChapterRepository;
import com.ailab.book.repository.PageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.*;

@Service
public class BookPublishServiceImpl implements BookPublishService {

    private final BookRepository bookRepository;
    private final ChapterRepository chapterRepository;
    private final PageRepository pageRepository;
    private final BookPublishedSnapshotRepository snapshotRepository;
    private final BookValidationService validationService;

    public BookPublishServiceImpl(BookRepository bookRepository,
                                  ChapterRepository chapterRepository,
                                  PageRepository pageRepository,
                                  BookPublishedSnapshotRepository snapshotRepository,
                                  BookValidationService validationService) {
        this.bookRepository = bookRepository;
        this.chapterRepository = chapterRepository;
        this.pageRepository = pageRepository;
        this.snapshotRepository = snapshotRepository;
        this.validationService = validationService;
    }

    @Override
    @Transactional
    public BookDtos.PublishResult publishBook(String bookId, BookDtos.PublishBookRequest request, String actorId, String actorName) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BOOK_NOT_FOUND: Book not found with id " + bookId));

        String idempotencyKey = request != null ? request.idempotencyKey() : null;
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            Optional<BookPublishedSnapshot> existing = snapshotRepository.findByIdempotencyKey(idempotencyKey);
            if (existing.isPresent()) {
                BookPublishedSnapshot snap = existing.get();
                return new BookDtos.PublishResult(
                        snap.getBookId(),
                        snap.getVersion(),
                        snap.getPublishedAt(),
                        new BookDtos.PublisherInfo(snap.getPublishedById(), snap.getPublishedByName()),
                        snap.getReleaseNote()
                );
            }
        }

        Long expectedVersion = request != null ? request.version() : null;
        BookDtos.ValidationReport report = validationService.validateBook(bookId, expectedVersion);
        if (!report.valid()) {
            String firstError = !report.errors().isEmpty() ? report.errors().get(0).message() : "Validation failed";
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "VALIDATION_ERROR: " + firstError);
        }

        long nextPublishedVersion = (book.getPublishedVersion() != null ? book.getPublishedVersion() : 0L) + 1L;

        Map<String, Object> snapshotData = buildSnapshotData(book, nextPublishedVersion);

        String snapshotId = "snap_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String safeActorId = actorId != null ? actorId : "admin";
        String safeActorName = actorName != null ? actorName : "Admin";
        String releaseNote = request != null ? request.releaseNote() : null;

        BookPublishedSnapshot snapshot = new BookPublishedSnapshot(
                snapshotId,
                book.getId(),
                nextPublishedVersion,
                releaseNote,
                safeActorId,
                safeActorName,
                snapshotData,
                idempotencyKey
        );
        snapshotRepository.save(snapshot);

        book.setStatus(BookStatus.PUBLISHED);
        book.setPublishedVersion(nextPublishedVersion);
        bookRepository.save(book);

        return new BookDtos.PublishResult(
                book.getId(),
                nextPublishedVersion,
                Instant.now(),
                new BookDtos.PublisherInfo(safeActorId, safeActorName),
                releaseNote
        );
    }

    @Override
    @Transactional
    public BookDtos.PublishResult rollbackBook(String bookId, BookDtos.RollbackBookRequest request, String actorId, String actorName) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BOOK_NOT_FOUND: Book not found with id " + bookId));

        if (request == null || request.targetPublishedVersion() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR: targetPublishedVersion is required");
        }

        BookPublishedSnapshot target = snapshotRepository.findByBookIdAndVersion(bookId, request.targetPublishedVersion())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SNAPSHOT_NOT_FOUND: Target published version " + request.targetPublishedVersion() + " not found"));

        long nextPublishedVersion = (book.getPublishedVersion() != null ? book.getPublishedVersion() : 0L) + 1L;

        Map<String, Object> clonedData = new HashMap<>(target.getSnapshotData());
        clonedData.put("publishedVersion", nextPublishedVersion);
        clonedData.put("rolledBackFromVersion", request.targetPublishedVersion());

        String snapshotId = "snap_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String safeActorId = actorId != null ? actorId : "admin";
        String safeActorName = actorName != null ? actorName : "Admin";
        String releaseNote = request.reason() != null ? request.reason() : ("Rollback to version " + request.targetPublishedVersion());

        BookPublishedSnapshot snapshot = new BookPublishedSnapshot(
                snapshotId,
                book.getId(),
                nextPublishedVersion,
                releaseNote,
                safeActorId,
                safeActorName,
                clonedData,
                null
        );
        snapshotRepository.save(snapshot);

        book.setStatus(BookStatus.PUBLISHED);
        book.setPublishedVersion(nextPublishedVersion);
        bookRepository.save(book);

        return new BookDtos.PublishResult(
                book.getId(),
                nextPublishedVersion,
                Instant.now(),
                new BookDtos.PublisherInfo(safeActorId, safeActorName),
                releaseNote
        );
    }

    private Map<String, Object> buildSnapshotData(Book book, long publishedVersion) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", book.getId());
        data.put("slug", book.getSlug());
        data.put("defaultLocale", book.getDefaultLocale());
        data.put("translations", book.getTranslations());
        data.put("publishedVersion", publishedVersion);

        List<Chapter> chapters = chapterRepository.findByBookIdOrderByPositionAsc(book.getId());
        List<Map<String, Object>> chapterList = new ArrayList<>();

        for (Chapter ch : chapters) {
            Map<String, Object> chData = new HashMap<>();
            chData.put("id", ch.getId());
            chData.put("position", ch.getPosition());
            chData.put("translations", ch.getTranslations());

            List<Page> pages = pageRepository.findByChapterIdOrderByPositionAsc(ch.getId());
            List<Map<String, Object>> pageList = new ArrayList<>();
            for (Page pg : pages) {
                Map<String, Object> pgData = new HashMap<>();
                pgData.put("id", pg.getId());
                pgData.put("slug", pg.getSlug());
                pgData.put("position", pg.getPosition());
                pgData.put("layout", pg.getLayout());
                pgData.put("translations", pg.getTranslations());
                pgData.put("blocks", pg.getBlocks());
                pageList.add(pgData);
            }
            chData.put("pages", pageList);
            chapterList.add(chData);
        }

        data.put("chapters", chapterList);
        return data;
    }
}
