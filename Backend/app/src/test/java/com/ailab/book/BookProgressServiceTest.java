package com.ailab.book;

import com.ailab.book.domain.BookProgress;
import com.ailab.book.dto.BookDtos;
import com.ailab.book.repository.BookProgressRepository;
import com.ailab.book.service.BookProgressServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookProgressServiceTest {

    @Mock
    BookProgressRepository progressRepository;

    @InjectMocks
    BookProgressServiceImpl progressService;

    @Test
    void testGetProgressExisting() {
        BookProgress progress = new BookProgress(
                "usr_01_book_1",
                "usr_01",
                "book_1",
                "page_3",
                "#section-2",
                List.of("bm_1", "bm_2")
        );

        when(progressRepository.findByUserIdAndBookId("usr_01", "book_1")).thenReturn(Optional.of(progress));

        BookDtos.BookProgressDto result = progressService.getProgress("usr_01", "book_1");

        assertThat(result.bookId()).isEqualTo("book_1");
        assertThat(result.pageId()).isEqualTo("page_3");
        assertThat(result.scrollAnchor()).isEqualTo("#section-2");
        assertThat(result.bookmarks()).containsExactly("bm_1", "bm_2");
    }

    @Test
    void testGetProgressInitial() {
        when(progressRepository.findByUserIdAndBookId("usr_01", "book_1")).thenReturn(Optional.empty());

        BookDtos.BookProgressDto result = progressService.getProgress("usr_01", "book_1");

        assertThat(result.bookId()).isEqualTo("book_1");
        assertThat(result.pageId()).isNull();
        assertThat(result.bookmarks()).isEmpty();
    }

    @Test
    void testUpdateProgress() {
        BookProgress existing = new BookProgress("usr_01_book_1", "usr_01", "book_1", "page_1", null, List.of());
        when(progressRepository.findByUserIdAndBookId("usr_01", "book_1")).thenReturn(Optional.of(existing));
        when(progressRepository.save(any(BookProgress.class))).thenAnswer(inv -> inv.getArgument(0));

        BookDtos.UpdateBookProgressRequest request = new BookDtos.UpdateBookProgressRequest(
                "page_5",
                "#para-4",
                List.of("bm_saved"),
                Instant.now()
        );

        BookDtos.BookProgressDto result = progressService.updateProgress("usr_01", "book_1", request);

        assertThat(result.pageId()).isEqualTo("page_5");
        assertThat(result.scrollAnchor()).isEqualTo("#para-4");
        assertThat(result.bookmarks()).containsExactly("bm_saved");
        verify(progressRepository).save(existing);
    }
}
