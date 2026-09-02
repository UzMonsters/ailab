package com.ailab.book;

import com.ailab.book.domain.Book;
import com.ailab.book.domain.BookStatus;
import com.ailab.book.domain.Chapter;
import com.ailab.book.domain.Page;
import com.ailab.book.dto.BookDtos;
import com.ailab.book.repository.BookRepository;
import com.ailab.book.repository.ChapterRepository;
import com.ailab.book.repository.PageRepository;
import com.ailab.book.service.BookAdminServiceImpl;
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
class BookAdminServiceTest {

    @Mock
    BookRepository bookRepository;

    @Mock
    ChapterRepository chapterRepository;

    @Mock
    PageRepository pageRepository;

    @Mock
    BookValidationService validationService;

    @InjectMocks
    BookAdminServiceImpl adminService;

    private Book sampleBook;
    private Chapter sampleChapter;
    private Page samplePage;

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

        sampleChapter = new Chapter("ch_01", "book_chem_basics", 1, Map.of("ru", Map.of("title", "Глава 1")));
        samplePage = new Page("page_01", "ch_01", "book_chem_basics", "intro", 1, "single-page", Map.of("ru", Map.of("title", "Введение")));
    }

    @Test
    void testCreateBook() {
        when(bookRepository.existsBySlug("chemistry-basics")).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));
        when(chapterRepository.findByBookIdOrderByPositionAsc(any())).thenReturn(List.of());

        BookDtos.CreateBookRequest request = new BookDtos.CreateBookRequest(
                "chemistry-basics",
                "ru",
                Map.of("ru", Map.of("title", "Основы химии", "description", "Интерактивный учебник"))
        );

        BookDtos.BookEditorDocument result = adminService.createBook(request, "idem-key");

        assertThat(result.slug()).isEqualTo("chemistry-basics");
        assertThat(result.status()).isEqualTo(BookStatus.DRAFT);
        assertThat(result.draftVersion()).isEqualTo(1L);
    }

    @Test
    void testPatchBookOptimisticLockingSuccess() {
        when(bookRepository.findById("book_chem_basics")).thenReturn(Optional.of(sampleBook));
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));
        when(chapterRepository.findByBookIdOrderByPositionAsc("book_chem_basics")).thenReturn(List.of());

        BookDtos.PatchBookRequest patchReq = new BookDtos.PatchBookRequest("chemistry-basics-updated", null, null);
        BookDtos.BookEditorDocument result = adminService.patchBook("book_chem_basics", patchReq, "1");

        assertThat(result.slug()).isEqualTo("chemistry-basics-updated");
        assertThat(result.draftVersion()).isEqualTo(2L);
    }

    @Test
    void testPatchBookOptimisticLockingConflict() {
        when(bookRepository.findById("book_chem_basics")).thenReturn(Optional.of(sampleBook));

        BookDtos.PatchBookRequest patchReq = new BookDtos.PatchBookRequest("new-slug", null, null);

        assertThatThrownBy(() -> adminService.patchBook("book_chem_basics", patchReq, "99"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("VERSION_CONFLICT");
    }

    @Test
    void testSaveBlocksOptimisticLocking() {
        when(bookRepository.findById("book_chem_basics")).thenReturn(Optional.of(sampleBook));
        when(pageRepository.findByIdAndBookId("page_01", "book_chem_basics")).thenReturn(Optional.of(samplePage));
        when(pageRepository.save(any(Page.class))).thenAnswer(inv -> inv.getArgument(0));
        when(validationService.findPageMissingLocales(any(), any())).thenReturn(List.of());

        List<Map<String, Object>> blocks = List.of(
                Map.of(
                        "id", "block_intro",
                        "type", "RICH_TEXT",
                        "position", 1,
                        "data", Map.of("document", Map.of("type", "doc")),
                        "translations", Map.of("ru", Map.of("content", "Раствор"))
                )
        );

        BookDtos.SaveBlocksRequest request = new BookDtos.SaveBlocksRequest(1L, blocks);
        BookDtos.SaveBlocksResponse response = adminService.savePageBlocks("book_chem_basics", "page_01", request, null);

        assertThat(response.version()).isEqualTo(2L);
        assertThat(response.blocks()).hasSize(1);
    }

    @Test
    void testDeleteChapterRequiresConfirmation() {
        when(bookRepository.findById("book_chem_basics")).thenReturn(Optional.of(sampleBook));
        when(chapterRepository.findByIdAndBookId("ch_01", "book_chem_basics")).thenReturn(Optional.of(sampleChapter));

        assertThatThrownBy(() -> adminService.deleteChapter("book_chem_basics", "ch_01", 1L, false))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("CONFIRMATION_REQUIRED");
    }

    @Test
    void testDeleteChapterSuccess() {
        when(bookRepository.findById("book_chem_basics")).thenReturn(Optional.of(sampleBook));
        when(chapterRepository.findByIdAndBookId("ch_01", "book_chem_basics")).thenReturn(Optional.of(sampleChapter));

        adminService.deleteChapter("book_chem_basics", "ch_01", 1L, true);

        verify(pageRepository).deleteByChapterId("ch_01");
        verify(chapterRepository).delete(sampleChapter);
        verify(bookRepository).save(sampleBook);
    }
}
