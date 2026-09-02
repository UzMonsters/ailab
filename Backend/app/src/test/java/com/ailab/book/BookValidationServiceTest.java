package com.ailab.book;

import com.ailab.book.domain.Book;
import com.ailab.book.domain.Chapter;
import com.ailab.book.domain.Page;
import com.ailab.book.dto.BookDtos;
import com.ailab.book.repository.BookAssetRepository;
import com.ailab.book.repository.BookRepository;
import com.ailab.book.repository.ChapterRepository;
import com.ailab.book.repository.PageRepository;
import com.ailab.book.service.BookValidationServiceImpl;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookValidationServiceTest {

    @Mock
    BookRepository bookRepository;

    @Mock
    ChapterRepository chapterRepository;

    @Mock
    PageRepository pageRepository;

    @Mock
    BookAssetRepository assetRepository;

    @InjectMocks
    BookValidationServiceImpl validationService;

    private Book sampleBook;
    private Chapter sampleChapter;
    private Page samplePage;

    @BeforeEach
    void setUp() {
        sampleBook = new Book(
                "book_test",
                "test-book",
                "ru",
                Map.of(
                        "ru", Map.of("title", "Тестовая книга", "description", "Описание"),
                        "en", Map.of("title", "Test Book", "description", "Description"),
                        "uz", Map.of("title", "Test Kitob", "description", "Tavsif")
                )
        );

        sampleChapter = new Chapter(
                "ch_01",
                "book_test",
                1,
                Map.of(
                        "ru", Map.of("title", "Глава 1"),
                        "en", Map.of("title", "Chapter 1"),
                        "uz", Map.of("title", "1-bob")
                )
        );

        samplePage = new Page(
                "page_01",
                "ch_01",
                "book_test",
                "intro",
                1,
                "single-page",
                Map.of("ru", Map.of("title", "Введение"))
        );

        Map<String, Object> calloutBlock = Map.of(
                "id", "block_01",
                "type", "CALLOUT",
                "position", 1,
                "data", Map.of("variant", "REMEMBER", "icon", "brain"),
                "translations", Map.of(
                        "ru", Map.of("content", "Запомните правило"),
                        "en", Map.of("content", "Remember rule"),
                        "uz", Map.of("content", "Qoidani eslab qoling")
                )
        );

        samplePage.setBlocks(List.of(calloutBlock));
    }

    @Test
    void testValidateValidBook() {
        when(bookRepository.findById("book_test")).thenReturn(Optional.of(sampleBook));
        when(chapterRepository.findByBookIdOrderByPositionAsc("book_test")).thenReturn(List.of(sampleChapter));
        when(pageRepository.findByChapterIdOrderByPositionAsc("ch_01")).thenReturn(List.of(samplePage));

        BookDtos.ValidationReport report = validationService.validateBook("book_test", 1L);

        assertThat(report.valid()).isTrue();
        assertThat(report.errors()).isEmpty();
    }

    @Test
    void testValidateVersionMismatch() {
        when(bookRepository.findById("book_test")).thenReturn(Optional.of(sampleBook));

        assertThatThrownBy(() -> validationService.validateBook("book_test", 99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("VERSION_CONFLICT");
    }

    @Test
    void testValidateMissingChapters() {
        when(bookRepository.findById("book_test")).thenReturn(Optional.of(sampleBook));
        when(chapterRepository.findByBookIdOrderByPositionAsc("book_test")).thenReturn(List.of());

        BookDtos.ValidationReport report = validationService.validateBook("book_test", 1L);

        assertThat(report.valid()).isFalse();
        assertThat(report.errors()).anyMatch(e -> e.code().equals("VALIDATION_ERROR") && e.path().equals("chapters"));
    }

    @Test
    void testValidateInvalidBlockSchema() {
        Map<String, Object> invalidBlock = Map.of(
                "id", "block_bad",
                "type", "UNKNOWN_TYPE"
        );
        samplePage.setBlocks(List.of(invalidBlock));

        when(bookRepository.findById("book_test")).thenReturn(Optional.of(sampleBook));
        when(chapterRepository.findByBookIdOrderByPositionAsc("book_test")).thenReturn(List.of(sampleChapter));
        when(pageRepository.findByChapterIdOrderByPositionAsc("ch_01")).thenReturn(List.of(samplePage));

        BookDtos.ValidationReport report = validationService.validateBook("book_test", 1L);

        assertThat(report.valid()).isFalse();
        assertThat(report.errors()).anyMatch(e -> e.code().equals("BLOCK_SCHEMA_INVALID"));
    }
}
