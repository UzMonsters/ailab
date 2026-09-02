package com.ailab.book;

import com.ailab.book.domain.Book;
import com.ailab.book.domain.BookPublishedSnapshot;
import com.ailab.book.domain.BookStatus;
import com.ailab.book.dto.BookDtos;
import com.ailab.book.repository.BookPublishedSnapshotRepository;
import com.ailab.book.repository.BookRepository;
import com.ailab.book.service.BookReaderServiceImpl;
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
class BookReaderServiceTest {

    @Mock
    BookRepository bookRepository;

    @Mock
    BookPublishedSnapshotRepository snapshotRepository;

    @InjectMocks
    BookReaderServiceImpl readerService;

    private Book sampleBook;
    private BookPublishedSnapshot sampleSnapshot;

    @BeforeEach
    void setUp() {
        sampleBook = new Book(
                "book_chem_basics",
                "chemistry-basics",
                "ru",
                Map.of(
                        "ru", Map.of("title", "Основы химии", "description", "Интерактивный учебник"),
                        "en", Map.of("title", "Chemistry Basics", "description", "Interactive textbook")
                )
        );
        sampleBook.setStatus(BookStatus.PUBLISHED);
        sampleBook.setPublishedVersion(3L);

        Map<String, Object> snapshotData = Map.of(
                "id", "book_chem_basics",
                "slug", "chemistry-basics",
                "defaultLocale", "ru",
                "translations", Map.of(
                        "ru", Map.of("title", "Основы химии", "description", "Интерактивный учебник"),
                        "en", Map.of("title", "Chemistry Basics", "description", "Interactive textbook")
                ),
                "publishedVersion", 3L,
                "chapters", List.of(
                        Map.of(
                                "id", "ch_01",
                                "position", 1,
                                "translations", Map.of(
                                        "ru", Map.of("title", "Растворы"),
                                        "en", Map.of("title", "Solutions")
                                ),
                                "pages", List.of(
                                        Map.of(
                                                "id", "page_01",
                                                "slug", "page-solutions",
                                                "position", 1,
                                                "translations", Map.of("ru", Map.of("title", "Что такое раствор")),
                                                "blocks", List.of(
                                                        Map.of(
                                                                "id", "block_01",
                                                                "type", "CALLOUT",
                                                                "translations", Map.of(
                                                                        "ru", Map.of("content", "Запомните правило"),
                                                                        "en", Map.of("content", "Remember rule")
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );

        sampleSnapshot = new BookPublishedSnapshot(
                "snap_03",
                "book_chem_basics",
                3L,
                "Release 3",
                "usr_01",
                "Admin",
                snapshotData,
                null
        );
    }

    @Test
    void testGetManifestExactLocale() {
        when(bookRepository.findBySlug("chemistry-basics")).thenReturn(Optional.of(sampleBook));
        when(snapshotRepository.findByBookIdAndVersion("book_chem_basics", 3L)).thenReturn(Optional.of(sampleSnapshot));

        BookDtos.PublicBookManifest manifest = readerService.getManifest("chemistry-basics", "ru", null);

        assertThat(manifest.book().title()).isEqualTo("Основы химии");
        assertThat(manifest.locale()).isEqualTo("ru");
        assertThat(manifest.fallbackLocale()).isNull();
        assertThat(manifest.publishedVersion()).isEqualTo(3L);
        assertThat(manifest.chapters()).hasSize(1);
        assertThat(manifest.chapters().get(0).title()).isEqualTo("Растворы");
        assertThat(manifest.chapters().get(0).pageCount()).isEqualTo(1);
    }

    @Test
    void testGetManifestFallbackLocale() {
        when(bookRepository.findBySlug("chemistry-basics")).thenReturn(Optional.of(sampleBook));
        when(snapshotRepository.findByBookIdAndVersion("book_chem_basics", 3L)).thenReturn(Optional.of(sampleSnapshot));

        BookDtos.PublicBookManifest manifest = readerService.getManifest("chemistry-basics", "uz", null);

        assertThat(manifest.book().title()).isEqualTo("Основы химии");
        assertThat(manifest.locale()).isEqualTo("uz");
        assertThat(manifest.fallbackLocale()).isEqualTo("ru");
    }

    @Test
    void testGetChapterDetail() {
        when(bookRepository.findBySlug("chemistry-basics")).thenReturn(Optional.of(sampleBook));
        when(snapshotRepository.findByBookIdAndVersion("book_chem_basics", 3L)).thenReturn(Optional.of(sampleSnapshot));

        BookDtos.PublicChapterDetail detail = readerService.getChapter("chemistry-basics", "ch_01", "ru", null);

        assertThat(detail.chapter().title()).isEqualTo("Растворы");
        assertThat(detail.pages()).hasSize(1);
        assertThat(detail.pages().get(0).slug()).isEqualTo("page-solutions");
        assertThat(detail.pages().get(0).blocks()).hasSize(1);
    }

    @Test
    void testGetManifestNotFoundWhenDraft() {
        sampleBook.setStatus(BookStatus.DRAFT);
        when(bookRepository.findBySlug("chemistry-basics")).thenReturn(Optional.of(sampleBook));

        assertThatThrownBy(() -> readerService.getManifest("chemistry-basics", "ru", null))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("BOOK_NOT_FOUND");
    }
}
