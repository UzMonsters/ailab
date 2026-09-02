package com.ailab.book;

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
import com.ailab.book.service.BookPublishServiceImpl;
import com.ailab.book.service.BookValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookPublishServiceTest {

    @Mock
    BookRepository bookRepository;

    @Mock
    ChapterRepository chapterRepository;

    @Mock
    PageRepository pageRepository;

    @Mock
    BookPublishedSnapshotRepository snapshotRepository;

    @Mock
    BookValidationService validationService;

    @InjectMocks
    BookPublishServiceImpl publishService;

    private Book sampleBook;
    private Chapter sampleChapter;
    private Page samplePage;

    @BeforeEach
    void setUp() {
        sampleBook = new Book(
                "book_test",
                "test-book",
                "ru",
                Map.of("ru", Map.of("title", "Тест", "description", "Описание"))
        );

        sampleChapter = new Chapter("ch_01", "book_test", 1, Map.of("ru", Map.of("title", "Глава 1")));
        samplePage = new Page("page_01", "ch_01", "book_test", "p1", 1, "single-page", Map.of("ru", Map.of("title", "Страница 1")));
    }

    @Test
    void testPublishSuccess() {
        when(bookRepository.findById("book_test")).thenReturn(Optional.of(sampleBook));
        when(validationService.validateBook("book_test", 1L)).thenReturn(new BookDtos.ValidationReport(true, List.of(), List.of()));
        when(chapterRepository.findByBookIdOrderByPositionAsc("book_test")).thenReturn(List.of(sampleChapter));
        when(pageRepository.findByChapterIdOrderByPositionAsc("ch_01")).thenReturn(List.of(samplePage));

        BookDtos.PublishBookRequest request = new BookDtos.PublishBookRequest(1L, "idem-123", "First Release");
        BookDtos.PublishResult result = publishService.publishBook("book_test", request, "usr_admin", "Admin User");

        assertThat(result.bookId()).isEqualTo("book_test");
        assertThat(result.publishedVersion()).isEqualTo(1L);
        assertThat(result.releaseNote()).isEqualTo("First Release");
        assertThat(result.publishedBy().id()).isEqualTo("usr_admin");

        verify(snapshotRepository).save(any(BookPublishedSnapshot.class));
        verify(bookRepository).save(sampleBook);
        assertThat(sampleBook.getStatus()).isEqualTo(BookStatus.PUBLISHED);
        assertThat(sampleBook.getPublishedVersion()).isEqualTo(1L);
    }

    @Test
    void testPublishIdempotent() {
        BookPublishedSnapshot existingSnapshot = new BookPublishedSnapshot(
                "snap_01",
                "book_test",
                1L,
                "First Release",
                "usr_admin",
                "Admin User",
                Map.of("id", "book_test"),
                "idem-123"
        );

        when(bookRepository.findById("book_test")).thenReturn(Optional.of(sampleBook));
        when(snapshotRepository.findByIdempotencyKey("idem-123")).thenReturn(Optional.of(existingSnapshot));

        BookDtos.PublishBookRequest request = new BookDtos.PublishBookRequest(1L, "idem-123", "First Release");
        BookDtos.PublishResult result = publishService.publishBook("book_test", request, "usr_admin", "Admin User");

        assertThat(result.bookId()).isEqualTo("book_test");
        assertThat(result.publishedVersion()).isEqualTo(1L);
    }

    @Test
    void testPublishValidationFailed() {
        when(bookRepository.findById("book_test")).thenReturn(Optional.of(sampleBook));
        when(validationService.validateBook("book_test", 1L)).thenReturn(new BookDtos.ValidationReport(
                false,
                List.of(new BookDtos.ValidationError("slug", "VALIDATION_ERROR", "Slug cannot be empty")),
                List.of()
        ));

        BookDtos.PublishBookRequest request = new BookDtos.PublishBookRequest(1L, null, "Release");
        assertThatThrownBy(() -> publishService.publishBook("book_test", request, "usr_admin", "Admin User"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("VALIDATION_ERROR");
    }

    @Test
    void testRollbackSuccess() {
        sampleBook.setStatus(BookStatus.PUBLISHED);
        sampleBook.setPublishedVersion(2L);

        BookPublishedSnapshot v1Snapshot = new BookPublishedSnapshot(
                "snap_v1",
                "book_test",
                1L,
                "V1 Release",
                "usr_admin",
                "Admin User",
                Map.of("id", "book_test", "publishedVersion", 1L),
                null
        );

        when(bookRepository.findById("book_test")).thenReturn(Optional.of(sampleBook));
        when(snapshotRepository.findByBookIdAndVersion("book_test", 1L)).thenReturn(Optional.of(v1Snapshot));

        BookDtos.RollbackBookRequest request = new BookDtos.RollbackBookRequest(1L, "Reverting breaking changes");
        BookDtos.PublishResult result = publishService.rollbackBook("book_test", request, "usr_admin", "Admin User");

        assertThat(result.bookId()).isEqualTo("book_test");
        assertThat(result.publishedVersion()).isEqualTo(3L);
        assertThat(result.releaseNote()).isEqualTo("Reverting breaking changes");

        verify(snapshotRepository).save(any(BookPublishedSnapshot.class));
        assertThat(sampleBook.getPublishedVersion()).isEqualTo(3L);
    }
}
