package com.ailab.book;

import com.ailab.book.controller.PublicBookController;
import com.ailab.book.dto.BookDtos;
import com.ailab.book.service.BookReaderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublicBookControllerTest {

    @Mock
    BookReaderService readerService;

    @InjectMocks
    PublicBookController controller;

    @Test
    void testGetManifest() {
        BookDtos.PublicBookManifest manifest = new BookDtos.PublicBookManifest(
                new BookDtos.PublicBookInfo("b1", "chem", "Chemistry", "Desc"),
                "ru", null, List.of(), 1L, List.of()
        );

        when(readerService.getManifest("chem", "ru", null)).thenReturn(manifest);

        BookDtos.PublicBookManifest res = controller.getManifest("chem", "ru", null);
        assertThat(res.book().slug()).isEqualTo("chem");
    }

    @Test
    void testGetChapter() {
        BookDtos.PublicChapterDetail detail = new BookDtos.PublicChapterDetail(
                new BookDtos.PublicChapterSummary("ch_1", 1, "Ch1", 1, null),
                List.of(), null, List.of()
        );

        when(readerService.getChapter("chem", "ch_1", "ru", 0)).thenReturn(detail);

        BookDtos.PublicChapterDetail res = controller.getChapter("chem", "ch_1", "ru", 0);
        assertThat(res.chapter().id()).isEqualTo("ch_1");
    }
}
