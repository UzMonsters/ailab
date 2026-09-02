package com.ailab.book.service;

import com.ailab.book.dto.BookDtos;

public interface BookReaderService {

    BookDtos.PublicBookManifest getManifest(String slug, String locale, Long version);

    BookDtos.PublicChapterDetail getChapter(String slug, String chapterId, String locale, Integer page);
}
