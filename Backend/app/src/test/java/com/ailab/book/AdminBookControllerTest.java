package com.ailab.book;

import com.ailab.book.controller.AdminBookController;
import com.ailab.book.domain.BookStatus;
import com.ailab.book.dto.BookDtos;
import com.ailab.book.service.BookAdminService;
import com.ailab.book.service.BookPublishService;
import com.ailab.book.service.BookValidationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminBookControllerTest {

    @Mock
    BookAdminService adminService;

    @Mock
    BookValidationService validationService;

    @Mock
    BookPublishService publishService;

    @InjectMocks
    AdminBookController controller;

    @Test
    void testListBooks() {
        when(adminService.listBooks(0, 20, null, null, "updatedAt,desc")).thenReturn(
                new BookDtos.BookListResponse(List.of(), new BookDtos.PageMetadata(0, 20, 0, 0))
        );

        BookDtos.BookListResponse res = controller.listBooks(0, 20, null, null, "updatedAt,desc");
        assertThat(res.items()).isEmpty();
    }

    @Test
    void testCreateBook() {
        BookDtos.CreateBookRequest req = new BookDtos.CreateBookRequest("chem-basics", "ru", Map.of());
        BookDtos.BookEditorDocument doc = new BookDtos.BookEditorDocument(
                "book_1", "chem-basics", BookStatus.DRAFT, "ru", Map.of(), List.of(), 1L, null, Instant.now(), Instant.now()
        );

        when(adminService.createBook(req, "idem-1")).thenReturn(doc);

        BookDtos.BookEditorDocument result = controller.createBook(req, "idem-1");
        assertThat(result.id()).isEqualTo("book_1");
    }

    @Test
    void testValidate() {
        when(validationService.validateBook("book_1", 1L)).thenReturn(
                new BookDtos.ValidationReport(true, List.of(), List.of())
        );

        BookDtos.ValidationReport report = controller.validateBook("book_1", new BookDtos.ValidateBookRequest(1L));
        assertThat(report.valid()).isTrue();
    }

    @Test
    void testPublish() {
        BookDtos.PublishBookRequest req = new BookDtos.PublishBookRequest(1L, "idem-1", "note");
        when(publishService.publishBook("book_1", req, "usr_admin", "Administrator")).thenReturn(
                new BookDtos.PublishResult("book_1", 1L, Instant.now(), new BookDtos.PublisherInfo("usr_admin", "Administrator"), "note")
        );

        BookDtos.PublishResult result = controller.publishBook("book_1", req, "usr_admin");
        assertThat(result.publishedVersion()).isEqualTo(1L);
    }
}
