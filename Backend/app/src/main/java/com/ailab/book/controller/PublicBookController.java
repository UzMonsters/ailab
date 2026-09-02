package com.ailab.book.controller;

import com.ailab.book.dto.BookDtos;
import com.ailab.book.service.BookReaderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/books")
public class PublicBookController {

    private final BookReaderService readerService;

    public PublicBookController(BookReaderService readerService) {
        this.readerService = readerService;
    }

    @GetMapping("/{slug}/manifest")
    public BookDtos.PublicBookManifest getManifest(
            @PathVariable String slug,
            @RequestParam(required = false) String locale,
            @RequestParam(required = false) Long version
    ) {
        return readerService.getManifest(slug, locale, version);
    }

    @GetMapping("/{slug}/chapters/{chapterId}")
    public BookDtos.PublicChapterDetail getChapter(
            @PathVariable String slug,
            @PathVariable String chapterId,
            @RequestParam(required = false) String locale,
            @RequestParam(required = false) Integer page
    ) {
        return readerService.getChapter(slug, chapterId, locale, page);
    }
}
