package com.ailab.book.service;

import com.ailab.book.domain.Book;
import com.ailab.book.domain.BookPublishedSnapshot;
import com.ailab.book.domain.BookStatus;
import com.ailab.book.dto.BookDtos;
import com.ailab.book.repository.BookPublishedSnapshotRepository;
import com.ailab.book.repository.BookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class BookReaderServiceImpl implements BookReaderService {

    private final BookRepository bookRepository;
    private final BookPublishedSnapshotRepository snapshotRepository;

    public BookReaderServiceImpl(BookRepository bookRepository,
                                 BookPublishedSnapshotRepository snapshotRepository) {
        this.bookRepository = bookRepository;
        this.snapshotRepository = snapshotRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public BookDtos.PublicBookManifest getManifest(String slug, String locale, Long version) {
        String normSlug = slug != null ? slug.trim().toLowerCase() : "";
        Book book = bookRepository.findBySlug(normSlug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BOOK_NOT_FOUND: Published book not found with slug " + slug));

        if (book.getStatus() != BookStatus.PUBLISHED || book.getPublishedVersion() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "BOOK_NOT_FOUND: Book is not published yet: " + slug);
        }

        BookPublishedSnapshot snapshot;
        if (version != null) {
            snapshot = snapshotRepository.findByBookIdAndVersion(book.getId(), version)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SNAPSHOT_NOT_FOUND: Published version " + version + " not found"));
        } else {
            snapshot = snapshotRepository.findByBookIdAndVersion(book.getId(), book.getPublishedVersion())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SNAPSHOT_NOT_FOUND: Latest published snapshot not found"));
        }

        Map<String, Object> data = snapshot.getSnapshotData();
        String defaultLocale = (String) data.getOrDefault("defaultLocale", book.getDefaultLocale() != null ? book.getDefaultLocale() : "ru");
        String requestedLocale = locale != null && !locale.isBlank() ? locale.trim().toLowerCase() : defaultLocale;

        @SuppressWarnings("unchecked")
        Map<String, Object> translations = data.get("translations") instanceof Map<?, ?> m ? (Map<String, Object>) m : Map.of();

        String fallbackLocale = null;
        if (!translations.containsKey(requestedLocale)) {
            fallbackLocale = defaultLocale;
        }

        String effectiveLocale = fallbackLocale != null ? fallbackLocale : requestedLocale;
        Map<String, Object> locBookData = extractLocaleMap(translations, effectiveLocale, defaultLocale);

        String title = String.valueOf(locBookData.getOrDefault("title", book.getSlug()));
        String description = String.valueOf(locBookData.getOrDefault("description", ""));

        List<String> missingLocales = calculateMissingLocales(translations);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rawChapters = data.get("chapters") instanceof List<?> l ? (List<Map<String, Object>>) l : List.of();
        List<BookDtos.PublicChapterSummary> chapters = new ArrayList<>();

        for (Map<String, Object> ch : rawChapters) {
            String chId = (String) ch.get("id");
            Integer pos = (Integer) ch.get("position");
            @SuppressWarnings("unchecked")
            Map<String, Object> chTrans = ch.get("translations") instanceof Map<?, ?> m ? (Map<String, Object>) m : Map.of();
            Map<String, Object> chLoc = extractLocaleMap(chTrans, effectiveLocale, defaultLocale);
            String chTitle = String.valueOf(chLoc.getOrDefault("title", "Chapter " + pos));

            @SuppressWarnings("unchecked")
            List<?> pages = ch.get("pages") instanceof List<?> pl ? pl : List.of();
            chapters.add(new BookDtos.PublicChapterSummary(chId, pos, chTitle, pages.size(), null));
        }

        return new BookDtos.PublicBookManifest(
                new BookDtos.PublicBookInfo(book.getId(), book.getSlug(), title, description),
                requestedLocale,
                fallbackLocale,
                missingLocales,
                snapshot.getVersion(),
                chapters
        );
    }

    @Override
    @Transactional(readOnly = true)
    public BookDtos.PublicChapterDetail getChapter(String slug, String chapterId, String locale, Integer page) {
        String normSlug = slug != null ? slug.trim().toLowerCase() : "";
        Book book = bookRepository.findBySlug(normSlug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BOOK_NOT_FOUND: Published book not found with slug " + slug));

        if (book.getStatus() != BookStatus.PUBLISHED || book.getPublishedVersion() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "BOOK_NOT_FOUND: Book is not published yet: " + slug);
        }

        BookPublishedSnapshot snapshot = snapshotRepository.findByBookIdAndVersion(book.getId(), book.getPublishedVersion())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SNAPSHOT_NOT_FOUND: Published snapshot not found"));

        Map<String, Object> data = snapshot.getSnapshotData();
        String defaultLocale = (String) data.getOrDefault("defaultLocale", book.getDefaultLocale() != null ? book.getDefaultLocale() : "ru");
        String requestedLocale = locale != null && !locale.isBlank() ? locale.trim().toLowerCase() : defaultLocale;

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rawChapters = data.get("chapters") instanceof List<?> l ? (List<Map<String, Object>>) l : List.of();

        Map<String, Object> targetChapter = rawChapters.stream()
                .filter(c -> Objects.equals(c.get("id"), chapterId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CHAPTER_NOT_FOUND: Chapter not found with id " + chapterId));

        @SuppressWarnings("unchecked")
        Map<String, Object> chTrans = targetChapter.get("translations") instanceof Map<?, ?> m ? (Map<String, Object>) m : Map.of();

        String fallbackLocale = null;
        if (!chTrans.containsKey(requestedLocale)) {
            fallbackLocale = defaultLocale;
        }

        String effectiveLocale = fallbackLocale != null ? fallbackLocale : requestedLocale;
        Map<String, Object> chLoc = extractLocaleMap(chTrans, effectiveLocale, defaultLocale);
        String chTitle = String.valueOf(chLoc.getOrDefault("title", "Chapter"));
        Integer chPos = (Integer) targetChapter.get("position");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rawPages = targetChapter.get("pages") instanceof List<?> pl ? (List<Map<String, Object>>) pl : List.of();

        List<BookDtos.PublicPageDetail> pages = new ArrayList<>();
        Set<String> missingLocalesSet = new HashSet<>();

        for (Map<String, Object> pg : rawPages) {
            String pgId = (String) pg.get("id");
            String pgSlug = (String) pg.get("slug");
            Integer pgPos = (Integer) pg.get("position");
            @SuppressWarnings("unchecked")
            Map<String, Object> pgTrans = pg.get("translations") instanceof Map<?, ?> m ? (Map<String, Object>) m : Map.of();
            Map<String, Object> pgLoc = extractLocaleMap(pgTrans, effectiveLocale, defaultLocale);
            String pgTitle = String.valueOf(pgLoc.getOrDefault("title", "Page " + pgPos));

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> rawBlocks = pg.get("blocks") instanceof List<?> bl ? (List<Map<String, Object>>) bl : List.of();
            List<Map<String, Object>> localizedBlocks = new ArrayList<>();

            for (Map<String, Object> block : rawBlocks) {
                Map<String, Object> locBlock = new HashMap<>(block);
                @SuppressWarnings("unchecked")
                Map<String, Object> bTrans = block.get("translations") instanceof Map<?, ?> bm ? (Map<String, Object>) bm : Map.of();
                Map<String, Object> bLoc = extractLocaleMap(bTrans, effectiveLocale, defaultLocale);

                locBlock.put("localized", bLoc);
                localizedBlocks.add(locBlock);

                for (String l : List.of("ru", "en", "uz")) {
                    if (!bTrans.containsKey(l)) {
                        missingLocalesSet.add(l);
                    }
                }
            }

            pages.add(new BookDtos.PublicPageDetail(pgId, pgSlug, pgPos, pgTitle, localizedBlocks));
        }

        if (page != null && page >= 0 && page < pages.size()) {
            pages = List.of(pages.get(page));
        }

        BookDtos.PublicChapterSummary chSummary = new BookDtos.PublicChapterSummary(
                chapterId,
                chPos,
                chTitle,
                rawPages.size(),
                null
        );

        return new BookDtos.PublicChapterDetail(
                chSummary,
                pages,
                fallbackLocale,
                new ArrayList<>(missingLocalesSet)
        );
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> extractLocaleMap(Map<String, Object> translations, String targetLocale, String defaultLocale) {
        if (translations == null || translations.isEmpty()) {
            return Map.of();
        }
        if (translations.containsKey(targetLocale) && translations.get(targetLocale) instanceof Map<?, ?> m) {
            return (Map<String, Object>) m;
        }
        if (translations.containsKey(defaultLocale) && translations.get(defaultLocale) instanceof Map<?, ?> m) {
            return (Map<String, Object>) m;
        }
        for (Object v : translations.values()) {
            if (v instanceof Map<?, ?> m) {
                return (Map<String, Object>) m;
            }
        }
        return Map.of();
    }

    private List<String> calculateMissingLocales(Map<String, Object> translations) {
        if (translations == null) {
            return List.of("ru", "en", "uz");
        }
        List<String> missing = new ArrayList<>();
        for (String loc : List.of("ru", "en", "uz")) {
            if (!translations.containsKey(loc)) {
                missing.add(loc);
            }
        }
        return missing;
    }
}
