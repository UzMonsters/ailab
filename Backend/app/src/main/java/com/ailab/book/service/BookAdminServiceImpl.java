package com.ailab.book.service;

import com.ailab.book.domain.Book;
import com.ailab.book.domain.BookStatus;
import com.ailab.book.domain.Chapter;
import com.ailab.book.domain.Page;
import com.ailab.book.dto.BookDtos;
import com.ailab.book.repository.BookRepository;
import com.ailab.book.repository.ChapterRepository;
import com.ailab.book.repository.PageRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class BookAdminServiceImpl implements BookAdminService {

    private final BookRepository bookRepository;
    private final ChapterRepository chapterRepository;
    private final PageRepository pageRepository;
    private final BookValidationService validationService;

    public BookAdminServiceImpl(BookRepository bookRepository,
                                ChapterRepository chapterRepository,
                                PageRepository pageRepository,
                                BookValidationService validationService) {
        this.bookRepository = bookRepository;
        this.chapterRepository = chapterRepository;
        this.pageRepository = pageRepository;
        this.validationService = validationService;
    }

    @Override
    @Transactional(readOnly = true)
    public BookDtos.BookListResponse listBooks(int page, int size, String query, BookStatus status, String sort) {
        Sort sortObj = Sort.by(Sort.Direction.DESC, "updatedAt");
        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(",");
            String field = parts[0].trim();
            Sort.Direction direction = parts.length > 1 && "asc".equalsIgnoreCase(parts[1].trim())
                    ? Sort.Direction.ASC
                    : Sort.Direction.DESC;
            sortObj = Sort.by(direction, field);
        }

        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), sortObj);
        org.springframework.data.domain.Page<Book> bookPage = bookRepository.findAllWithFilter(status, query, pageable);

        List<BookDtos.BookSummary> summaries = bookPage.getContent().stream()
                .map(this::toBookSummary)
                .toList();

        BookDtos.PageMetadata pageMetadata = new BookDtos.PageMetadata(
                bookPage.getNumber(),
                bookPage.getSize(),
                bookPage.getTotalElements(),
                bookPage.getTotalPages()
        );

        return new BookDtos.BookListResponse(summaries, pageMetadata);
    }

    @Override
    @Transactional
    public BookDtos.BookEditorDocument createBook(BookDtos.CreateBookRequest request, String idempotencyKey) {
        if (request == null || request.slug() == null || request.slug().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR: Book slug is required");
        }

        String normalizedSlug = request.slug().trim().toLowerCase();
        if (bookRepository.existsBySlug(normalizedSlug)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "DUPLICATE_SLUG: Book slug already exists: " + normalizedSlug);
        }

        String bookId = "book_" + normalizedSlug.replace('-', '_');
        if (bookRepository.existsById(bookId)) {
            bookId = "book_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        }

        String defaultLocale = request.defaultLocale() != null && !request.defaultLocale().isBlank() ? request.defaultLocale() : "ru";
        Map<String, Object> translations = request.translations() != null ? request.translations() : Map.of();

        Book book = new Book(bookId, normalizedSlug, defaultLocale, translations);
        Book saved = bookRepository.save(book);

        return buildEditorDocument(saved, "chapters,pages,translations,assets");
    }

    @Override
    @Transactional(readOnly = true)
    public BookDtos.BookEditorDocument getBookEditorDocument(String bookId, String include) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BOOK_NOT_FOUND: Book not found with id " + bookId));

        return buildEditorDocument(book, include);
    }

    @Override
    @Transactional
    public BookDtos.BookEditorDocument patchBook(String bookId, BookDtos.PatchBookRequest request, String ifMatch) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BOOK_NOT_FOUND: Book not found with id " + bookId));

        validateIfMatch(ifMatch, book.getDraftVersion());

        if (request.slug() != null && !request.slug().isBlank()) {
            String newSlug = request.slug().trim().toLowerCase();
            if (!newSlug.equalsIgnoreCase(book.getSlug()) && bookRepository.existsBySlug(newSlug)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "DUPLICATE_SLUG: Book slug already exists: " + newSlug);
            }
            book.setSlug(newSlug);
        }

        if (request.defaultLocale() != null && !request.defaultLocale().isBlank()) {
            book.setDefaultLocale(request.defaultLocale());
        }

        if (request.translations() != null) {
            Map<String, Object> current = new HashMap<>(book.getTranslations() != null ? book.getTranslations() : Map.of());
            current.putAll(request.translations());
            book.setTranslations(current);
        }

        book.incrementDraftVersion();
        Book saved = bookRepository.save(book);

        return buildEditorDocument(saved, "chapters,pages,translations,assets");
    }

    @Override
    @Transactional
    public BookDtos.ChapterResponse createChapter(String bookId, BookDtos.CreateChapterRequest request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BOOK_NOT_FOUND: Book not found with id " + bookId));

        String chapterId = "ch_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        int position = request != null && request.position() != null ? request.position() : (int) (chapterRepository.findByBookIdOrderByPositionAsc(bookId).size() + 1);
        Map<String, Object> translations = request != null && request.translations() != null ? request.translations() : Map.of();

        Chapter chapter = new Chapter(chapterId, bookId, position, translations);
        Chapter saved = chapterRepository.save(chapter);

        book.incrementDraftVersion();
        bookRepository.save(book);

        return toChapterDto(saved, List.of());
    }

    @Override
    @Transactional
    public BookDtos.ChapterResponse patchChapter(String bookId, String chapterId, BookDtos.PatchChapterRequest request, String ifMatch) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BOOK_NOT_FOUND: Book not found with id " + bookId));

        Chapter chapter = chapterRepository.findByIdAndBookId(chapterId, bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CHAPTER_NOT_FOUND: Chapter not found with id " + chapterId));

        if (request != null && request.expectedVersion() != null) {
            validateIfMatch(String.valueOf(request.expectedVersion()), book.getDraftVersion());
        } else if (ifMatch != null) {
            validateIfMatch(ifMatch, book.getDraftVersion());
        }

        if (request != null && request.position() != null) {
            chapter.setPosition(request.position());
        }

        if (request != null && request.translations() != null) {
            Map<String, Object> current = new HashMap<>(chapter.getTranslations() != null ? chapter.getTranslations() : Map.of());
            current.putAll(request.translations());
            chapter.setTranslations(current);
        }

        Chapter saved = chapterRepository.save(chapter);
        book.incrementDraftVersion();
        bookRepository.save(book);

        List<Page> pages = pageRepository.findByChapterIdOrderByPositionAsc(chapterId);
        List<BookDtos.PageResponse> pageResponses = pages.stream().map(this::toPageDto).toList();

        return toChapterDto(saved, pageResponses);
    }

    @Override
    @Transactional
    public void deleteChapter(String bookId, String chapterId, Long expectedVersion, Boolean confirm) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BOOK_NOT_FOUND: Book not found with id " + bookId));

        Chapter chapter = chapterRepository.findByIdAndBookId(chapterId, bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CHAPTER_NOT_FOUND: Chapter not found with id " + chapterId));

        if (confirm == null || !confirm) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CONFIRMATION_REQUIRED: Explicit confirmation is required to delete chapter");
        }

        if (expectedVersion != null) {
            validateIfMatch(String.valueOf(expectedVersion), book.getDraftVersion());
        }

        pageRepository.deleteByChapterId(chapterId);
        chapterRepository.delete(chapter);

        book.incrementDraftVersion();
        bookRepository.save(book);
    }

    @Override
    @Transactional
    public BookDtos.PageResponse createPage(String bookId, BookDtos.CreatePageRequest request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BOOK_NOT_FOUND: Book not found with id " + bookId));

        if (request == null || request.chapterId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR: chapterId is required");
        }

        Chapter chapter = chapterRepository.findByIdAndBookId(request.chapterId(), bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CHAPTER_NOT_FOUND: Chapter not found with id " + request.chapterId()));

        String slug = request.slug() != null && !request.slug().isBlank() ? request.slug().trim().toLowerCase() : "page-" + UUID.randomUUID().toString().substring(0, 8);
        String pageId = "page_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        int position = request.position() != null ? request.position() : (int) (pageRepository.countByChapterId(chapter.getId()) + 1);
        String layout = request.layout() != null ? request.layout() : "single-page";
        Map<String, Object> translations = request.translations() != null ? request.translations() : Map.of();

        Page page = new Page(pageId, chapter.getId(), bookId, slug, position, layout, translations);
        Page saved = pageRepository.save(page);

        book.incrementDraftVersion();
        bookRepository.save(book);

        return toPageDto(saved);
    }

    @Override
    @Transactional
    public BookDtos.PageResponse patchPage(String bookId, String pageId, BookDtos.PatchPageRequest request, String ifMatch) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BOOK_NOT_FOUND: Book not found with id " + bookId));

        Page page = pageRepository.findByIdAndBookId(pageId, bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PAGE_NOT_FOUND: Page not found with id " + pageId));

        if (request != null && request.expectedVersion() != null) {
            validateIfMatch(String.valueOf(request.expectedVersion()), page.getVersion());
        } else if (ifMatch != null) {
            validateIfMatch(ifMatch, page.getVersion());
        }

        if (request != null && request.slug() != null && !request.slug().isBlank()) {
            page.setSlug(request.slug().trim().toLowerCase());
        }

        if (request != null && request.position() != null) {
            page.setPosition(request.position());
        }

        if (request != null && request.layout() != null) {
            page.setLayout(request.layout());
        }

        if (request != null && request.translations() != null) {
            Map<String, Object> current = new HashMap<>(page.getTranslations() != null ? page.getTranslations() : Map.of());
            current.putAll(request.translations());
            page.setTranslations(current);
        }

        page.incrementVersion();
        Page saved = pageRepository.save(page);

        book.incrementDraftVersion();
        bookRepository.save(book);

        return toPageDto(saved);
    }

    @Override
    @Transactional
    public BookDtos.SaveBlocksResponse savePageBlocks(String bookId, String pageId, BookDtos.SaveBlocksRequest request, String ifMatch) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR: Request body is required");
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BOOK_NOT_FOUND: Book not found with id " + bookId));

        Page page = pageRepository.findByIdAndBookId(pageId, bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PAGE_NOT_FOUND: Page not found with id " + pageId));

        if (request.version() != null) {
            validateIfMatch(String.valueOf(request.version()), page.getVersion());
        } else if (ifMatch != null) {
            validateIfMatch(ifMatch, page.getVersion());
        }

        List<Map<String, Object>> blocks = request.blocks() != null ? request.blocks() : List.of();

        List<BookDtos.ValidationError> errors = new ArrayList<>();
        List<BookDtos.ValidationWarning> warnings = new ArrayList<>();

        for (int i = 0; i < blocks.size(); i++) {
            validationService.validateBlockStructure(blocks.get(i), i, "blocks[" + i + "]", errors, warnings);
        }

        if (!errors.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "BLOCK_SCHEMA_INVALID: " + errors.get(0).message());
        }

        page.setBlocks(blocks);
        page.incrementVersion();
        Page saved = pageRepository.save(page);

        book.incrementDraftVersion();
        bookRepository.save(book);

        List<String> missingLocales = validationService.findPageMissingLocales(page.getTranslations(), blocks);

        return new BookDtos.SaveBlocksResponse(
                saved.getId(),
                saved.getVersion(),
                saved.getBlocks(),
                missingLocales,
                saved.getUpdatedAt()
        );
    }

    private BookDtos.BookEditorDocument buildEditorDocument(Book book, String include) {
        List<Chapter> chapters = chapterRepository.findByBookIdOrderByPositionAsc(book.getId());
        List<BookDtos.ChapterResponse> chapterResponses = new ArrayList<>();

        boolean includePages = include == null || include.contains("pages");

        for (Chapter chapter : chapters) {
            List<BookDtos.PageResponse> pageResponses = List.of();
            if (includePages) {
                List<Page> pages = pageRepository.findByChapterIdOrderByPositionAsc(chapter.getId());
                pageResponses = pages.stream().map(this::toPageDto).toList();
            }
            chapterResponses.add(toChapterDto(chapter, pageResponses));
        }

        return new BookDtos.BookEditorDocument(
                book.getId(),
                book.getSlug(),
                book.getStatus(),
                book.getDefaultLocale(),
                book.getTranslations(),
                chapterResponses,
                book.getDraftVersion(),
                book.getPublishedVersion(),
                book.getCreatedAt(),
                book.getUpdatedAt()
        );
    }

    private BookDtos.BookSummary toBookSummary(Book book) {
        return new BookDtos.BookSummary(
                book.getId(),
                book.getSlug(),
                book.getStatus(),
                book.getDefaultLocale(),
                book.getTranslations(),
                book.getDraftVersion(),
                book.getPublishedVersion(),
                book.getCreatedAt(),
                book.getUpdatedAt()
        );
    }

    private BookDtos.ChapterResponse toChapterDto(Chapter chapter, List<BookDtos.PageResponse> pages) {
        return new BookDtos.ChapterResponse(
                chapter.getId(),
                chapter.getBookId(),
                chapter.getPosition(),
                chapter.getTranslations(),
                chapter.getStatus(),
                chapter.getCreatedAt(),
                chapter.getUpdatedAt(),
                pages
        );
    }

    private BookDtos.PageResponse toPageDto(Page page) {
        List<String> missingLocales = validationService.findPageMissingLocales(page.getTranslations(), page.getBlocks());
        return new BookDtos.PageResponse(
                page.getId(),
                page.getChapterId(),
                page.getBookId(),
                page.getSlug(),
                page.getPosition(),
                page.getLayout(),
                page.getTranslations(),
                page.getBlocks(),
                page.getVersion(),
                missingLocales,
                page.getCreatedAt(),
                page.getUpdatedAt()
        );
    }

    private void validateIfMatch(String ifMatch, Long currentVersion) {
        if (ifMatch == null || ifMatch.isBlank() || "*".equals(ifMatch.trim())) {
            return;
        }

        String cleaned = ifMatch.replace("\"", "").replace("W/", "").trim();
        try {
            long expected = Long.parseLong(cleaned);
            if (currentVersion != null && expected != currentVersion) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "VERSION_CONFLICT: Version mismatch. Expected " + expected + " but current is " + currentVersion);
            }
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR: Invalid If-Match version header: " + ifMatch);
        }
    }
}
