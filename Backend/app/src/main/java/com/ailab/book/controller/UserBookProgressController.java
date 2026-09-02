package com.ailab.book.controller;

import com.ailab.book.dto.BookDtos;
import com.ailab.book.service.BookProgressService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/me/book-progress")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@SecurityRequirement(name = "bearerAuth")
public class UserBookProgressController {

    private final BookProgressService progressService;

    public UserBookProgressController(BookProgressService progressService) {
        this.progressService = progressService;
    }

    @GetMapping("/{bookId}")
    public BookDtos.BookProgressDto getProgress(
            @AuthenticationPrincipal String userId,
            @PathVariable String bookId
    ) {
        return progressService.getProgress(userId, bookId);
    }

    @PutMapping("/{bookId}")
    public BookDtos.BookProgressDto updateProgress(
            @AuthenticationPrincipal String userId,
            @PathVariable String bookId,
            @RequestBody BookDtos.UpdateBookProgressRequest request
    ) {
        return progressService.updateProgress(userId, bookId, request);
    }
}
